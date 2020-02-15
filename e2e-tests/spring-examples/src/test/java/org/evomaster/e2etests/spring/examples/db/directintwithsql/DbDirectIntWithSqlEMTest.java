package org.evomaster.e2etests.spring.examples.db.directintwithsql;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.evomaster.core.Main;
import org.evomaster.core.database.DbAction;
import org.evomaster.core.problem.rest.HttpVerb;
import org.evomaster.core.problem.rest.RestCallAction;
import org.evomaster.core.problem.rest.RestCallResult;
import org.evomaster.core.problem.rest.RestIndividual;
import org.evomaster.core.problem.rest.service.RestSampler;
import org.evomaster.core.search.EvaluatedAction;
import org.evomaster.core.search.EvaluatedIndividual;
import org.evomaster.core.search.FitnessValue;
import org.evomaster.core.search.Solution;
import org.evomaster.core.search.gene.IntegerGene;
import org.evomaster.core.search.service.FitnessFunction;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbDirectIntWithSqlEMTest extends DbDirectIntWithSqlTestBase{


    @Disabled
    @Test
    public void testRunEM() {

        String[] args = new String[]{
                "--createTests", "true",
                "--seed", "42",
                "--sutControllerPort", "" + controllerPort,
                "--maxActionEvaluations", "20000",
                "--stoppingCriterion", "FITNESS_EVALUATIONS",
                "--heuristicsForSQL", "true",
                "--generateSqlData", "true",
                "--maxTestSize", "1", //TODO Remove?
                "--maxSqlInitActionsPerMissingData", "1" //TODO Remove?
        };

        Solution<RestIndividual> solution = (Solution<RestIndividual>) Main.initAndRun(args);

        assertTrue(solution.getIndividuals().size() >= 1);

        //the POST is deactivated in the controller
        assertNone(solution, HttpVerb.POST, 200);

        assertHasAtLeastOne(solution, HttpVerb.GET, 400, "/api/db/directint/{x}/{y}", null);
        assertHasAtLeastOne(solution, HttpVerb.GET, 200, "/api/db/directint/{x}/{y}", null);
        assertInsertionIntoTable(solution, "DB_DIRECT_INT_ENTITY");
    }


    @Test
    public void testSteps() {

        String[] args = new String[]{
                "--createTests", "true",
                "--seed", "42",
                "--sutControllerPort", "" + controllerPort,
                "--maxActionEvaluations", "1",
                "--stoppingCriterion", "FITNESS_EVALUATIONS",
                "--heuristicsForSQL", "true",
                "--generateSqlData", "true",
                "--maxTestSize", "1"
        };

        Injector injector = Main.init(args);

        //start from creating and evaluating a random individual
        RestSampler sampler = injector.getInstance(RestSampler.class);
        RestIndividual ind = sampler.sampleAtRandom();


        FitnessFunction<RestIndividual> ff = injector.getInstance(Key.get(
                new TypeLiteral<FitnessFunction<RestIndividual>>(){}));
        EvaluatedIndividual ei = ff.calculateCoverage(ind);
        assertNotNull(ei);

        FitnessValue noDataFV = ei.getFitness();

        //as no data in database, should get worst heuristic value
        assertEquals(Double.MAX_VALUE, noDataFV.averageExtraDistancesToMinimize(0));

        RestCallResult result = (RestCallResult) ((EvaluatedAction)ei.evaluatedActions().get(0)).getResult();
        assertEquals(400, result.getStatusCode().intValue());



        //now, try to execute an action in which as well we add SQL data

        List<DbAction> insertions =  sampler.sampleSqlInsertion("DB_DIRECT_INT_ENTITY", Collections.singleton("*"));
        assertEquals(1, insertions.size());

        //extract the x/y values from the random call
        RestCallAction first = (RestCallAction) ind.getActions().iterator().next();
        int x = first.getParameters().stream()
                .filter(p -> p.getName().equalsIgnoreCase("x"))
                .map(p -> Integer.parseInt(p.getGene().getValueAsRawString()))
                .findAny().get();
        int y = first.getParameters().stream()
                .filter(p -> p.getName().equalsIgnoreCase("y"))
                .map(p -> Integer.parseInt(p.getGene().getValueAsRawString()))
                .findAny().get();

        //update SQL insertion with values close to the requested x/y
        insertions.stream()
                .flatMap(a -> a.seeGenes().stream())
                .forEach(g -> {
                    if(g.getName().equalsIgnoreCase("x")) {
                        IntegerGene gene = (IntegerGene) g;
                        gene.setValue(x + 1);
                    } else if(g.getName().equalsIgnoreCase("y")) {
                        IntegerGene gene = (IntegerGene) g;
                        gene.setValue(y + 1);
                    }
                });

        RestIndividual withSQL = new RestIndividual(ind.getActions(), ind.getSampleType(), insertions);

        ei = ff.calculateCoverage(withSQL);
        assertNotNull(ei);

        //should have better heuristic
        FitnessValue closeDataFV = ei.getFitness();

        assertTrue(closeDataFV.averageExtraDistancesToMinimize(0) <
                noDataFV.averageExtraDistancesToMinimize(0));

        for(int target : noDataFV.getViewOfData().keySet()) {
            assertTrue(closeDataFV.compareExtraToMinimize(target, noDataFV) >= 0);
        }

        //but still not reaching target
        result = (RestCallResult) ((EvaluatedAction)ei.evaluatedActions().get(0)).getResult();
        assertEquals(400, result.getStatusCode().intValue());


        //finally, with correct data
        insertions.stream()
                .flatMap(a -> a.seeGenes().stream())
                .forEach(g -> {
                    if(g.getName().equalsIgnoreCase("x")) {
                        IntegerGene gene = (IntegerGene) g;
                        gene.setValue(x);
                    } else if(g.getName().equalsIgnoreCase("y")) {
                        IntegerGene gene = (IntegerGene) g;
                        gene.setValue(y);
                    }
                });

        ei = ff.calculateCoverage(withSQL);
        assertNotNull(ei);

        //As SQL data is returned, we get no heuristic, and so worst value
//        FitnessValue rightDataFV = ei.getFitness();
//
//        for(int target : closeDataFV.getViewOfData().keySet()) {
//            assertTrue(rightDataFV.compareExtraToMinimize(target, closeDataFV) >= 0);
//        }

        result = (RestCallResult) ((EvaluatedAction)ei.evaluatedActions().get(0)).getResult();
        assertEquals(200, result.getStatusCode().intValue());
    }


}
