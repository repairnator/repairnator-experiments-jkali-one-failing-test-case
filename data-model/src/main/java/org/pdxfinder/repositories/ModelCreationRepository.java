package org.pdxfinder.repositories;

import org.pdxfinder.dao.ModelCreation;
import org.pdxfinder.dao.MolecularCharacterization;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Interface describing operations for adding/finding/deleting PDX strain records
 *
 * @author csaba
 */
@Repository
public interface ModelCreationRepository extends Neo4jRepository<ModelCreation, Long> {


    ModelCreation findBySourcePdxId(@Param("modelId") String modelId);

    @Query("MATCH (model:ModelCreation) WHERE model.dataSource = {dataSource} AND model.sourcePdxId = {modelId} " +
            "WITH model " +
            "OPTIONAL MATCH (model)-[spr:SPECIMENS]-(sp:Specimen)-[hsr:HOST_STRAIN]-(hs:HostStrain) " +
            "OPTIONAL MATCH (model)-[qar:QUALITY_ASSURED_BY]-(qa:QualityAssurance) "+
            "WITH model, spr, sp, hsr, hs, qar, qa " +
            "OPTIONAL MATCH (sp)-[itr:IMPLANTATION_TYPE]-(it:ImplantationType) "+
            "OPTIONAL MATCH (sp)-[isr:IMPLANTATION_SITE]-(is:ImplantationSite) "+
            "RETURN model, spr, sp, hsr, hs, itr, isr, it, is, qar, qa")
    ModelCreation findByDataSourceAndSourcePdxId(@Param("dataSource") String dataSource, @Param("modelId") String modelId);

    @Query("MATCH (model:ModelCreation) WHERE model.sourcePdxId = {modelId} AND model.dataSource = {dataSource} RETURN model ")
    ModelCreation findBySourcePdxIdAndDataSource(@Param("modelId") String modelId, @Param("dataSource") String dataSource);

    @Query("MATCH (model:ModelCreation) return count(model) ")
    int countAllModels();


    @Query("MATCH (s:Sample)-[i:IMPLANTED_IN]-(mod:ModelCreation) " +
            "WHERE s.sourceSampleId = {sampleId} " +
            "RETURN mod")
    ModelCreation findBySampleId(@Param("sampleId") String sampleId);


    //disable filtering on markers
    @Query("MATCH (humSample:Sample)-[i:IMPLANTED_IN]-(mod:ModelCreation)" +
            "WHERE (toLower(humSample.diagnosis) CONTAINS toLower({diag}) OR {diag}='') " +
            "AND (humSample.dataSource IN {dataSource} OR {dataSource}=[] )"+
            "WITH mod, humSample, i " +

            "MATCH (humSample)-[o:ORIGIN_TISSUE]-(t:Tissue) " +
            "MATCH (humSample)-[ot:OF_TYPE]-(tt:TumorType) " +
            "WHERE (tt.name IN {tumorType} OR {tumorType}=[])  " +
            "OPTIONAL MATCH (humSample)-[mto:MAPPED_TO]-(oterm:OntologyTerm) " +
            "RETURN humSample, i, mod, o, t, ot, tt, mto, oterm ")
    Collection<ModelCreation> findByMultipleFilters(@Param("diag") String diag, @Param("markers") String[] markers,
                                                    @Param("dataSource") String[] dataSource, @Param("tumorType") String[] tumorType);




    //Ontology powered search: returns less data to improve performance
    @Query("MATCH (term:OntologyTerm)<-[*0..]-(child:OntologyTerm)-[mapp:MAPPED_TO]-(humSample:Sample)-[i:IMPLANTED_IN]-(mod:ModelCreation) " +
            "        WHERE term.label = {query} " +
            "        AND (humSample.dataSource IN {dataSource} OR {dataSource}=[]) " +
            "        WITH humSample,i,mod,mapp,child " +

            "        MATCH (humSample)-[o:ORIGIN_TISSUE]-(t:Tissue) " +
            "        MATCH (humSample)-[ot:OF_TYPE]-(tt:TumorType) " +
            "        WHERE (tt.name IN {tumorType} OR {tumorType}=[])  " +
            "        return humSample, i, mod, o, t, ot, tt,mapp,child ")
    Collection<ModelCreation> findByOntology(@Param("query") String query, @Param("markers") String[] markers,
                                             @Param("dataSource") String[] dataSource, @Param("tumorType") String[] tumorType);

    @Query("MATCH (n:ModelCreation) RETURN n")
    Collection<ModelCreation> getAllModels();

    @Query("MATCH (mc:ModelCreation)<-[ir:IMPLANTED_IN]-(s:Sample)-[sfr:SAMPLED_FROM]-(ps:PatientSnapshot)-[pr:PATIENT]-(p:Patient) " +
            "WITH mc, ir, s, sfr, ps, pr, p " +
            "MATCH (c:Tissue)-[cr:SAMPLE_SITE]-(s)-[ttr:OF_TYPE]-(tt:TumorType) " +
            "WITH mc, ir, s, sfr, ps, pr, p, cr, c, ttr, tt " +
            "MATCH (t:Tissue)-[tr:ORIGIN_TISSUE]-(s)-[otm:MAPPED_TO]-(ot:OntologyTerm)-[ottm:SUBCLASS_OF *1..]->(term:OntologyTerm) " +
            "RETURN mc, ir, s, sfr, ps, pr, p, cr, c, ttr, tt, tr, t, otm, ot, ottm, term")
    Collection<ModelCreation> getModelsWithPatientData();

    @Query("MATCH " +
            "(mc:ModelCreation)-[msr:MODEL_SAMPLE_RELATION]-(s:Sample)" +
            "-[cbr:CHARACTERIZED_BY]-(molChar:MolecularCharacterization)" +
            "-[pur:PLATFORM_USED]-(p:Platform) " +
            "RETURN mc, msr, s, cbr, molChar, pur, p")
    Collection<ModelCreation> getAllModelsPlatforms();



    @Query("MATCH (mc:ModelCreation)--(psamp:Sample)-[char:CHARACTERIZED_BY]-(molch:MolecularCharacterization)-[assoc:ASSOCIATED_WITH]->(mAss:MarkerAssociation)-[aw:MARKER]-(m:Marker) " +
            "            WHERE  mc.dataSource = {dataSource}  " +
            "            AND    mc.sourcePdxId = {modelId}  " +
            "WITH mc, psamp, char, molch, assoc, mAss, aw, m " +
            "MATCH (molch)-[pu:PLATFORM_USED]-(pl:Platform) " +

            "            WHERE (pl.name = {tech}  OR {tech} = '' ) " +

            "            OR toLower(m.symbol) CONTAINS toLower({search}) " +
            "            OR any( property in keys(mAss) where toLower(mAss[property]) CONTAINS toLower({search}) )  " +
            "            RETURN count(*) ")
    Integer variationCountByDataSourceAndPdxIdAndPlatform(@Param("dataSource") String dataSource,
                                                          @Param("modelId") String modelId,
                                                          @Param("tech") String tech,
                                                          @Param("search") String search);


    @Query("MATCH (mc:ModelCreation)--(psamp:Sample)-[char:CHARACTERIZED_BY]-(molch:MolecularCharacterization)-[assoc:ASSOCIATED_WITH]->(mAss:MarkerAssociation)-[aw:MARKER]-(m:Marker) " +

            "            WHERE  mc.dataSource = {dataSource}  " +
            "            AND    mc.sourcePdxId = {modelId}  " +
            "WITH mc, psamp, char, molch, assoc, mAss, aw, m " +
            "MATCH (molch)-[pu:PLATFORM_USED]-(pl:Platform) " +

            "            WHERE (pl.name = {tech}  OR {tech} = '' ) " +


            "            OR toLower(m.symbol) CONTAINS toLower({search})" +
            "            OR any( property in keys(mAss) where toLower(mAss[property]) CONTAINS toLower({search}) )  " +

            "            RETURN mc,psamp,char,molch,mAss,m, pu, pl SKIP {skip} LIMIT {lim} ")
    ModelCreation findVariationBySourcePdxIdAndPlatform(@Param("dataSource") String dataSource,
                                                        @Param("modelId") String modelId,
                                                        @Param("tech") String tech,
                                                        @Param("search") String search,
                                                        @Param("skip") int skip,
                                                        @Param("lim") int lim);

    @Query("MATCH (mc)-[msr:MODEL_SAMPLE_RELATION]-(s:Sample)-[cbr:CHARACTERIZED_BY]-(molChar:MolecularCharacterization)-[pur:PLATFORM_USED]-(p:Platform) " +
            "WHERE mc.sourcePdxId={sourcePdxId}  AND p.name={platform} " +
            "WITH mc, msr, s, cbr, molChar, pur, p " +

            "OPTIONAL MATCH (molChar)-[assW:ASSOCIATED_WITH]-(mAss:MarkerAssociation) " +
            "RETURN count(mAss) ")
    Integer countMarkerAssociationBySourcePdxId(@Param("sourcePdxId") String sourcePdxId,
                                                @Param("platform") String platform);


    @Query("MATCH (model:ModelCreation)--(s:Sample)--(molch:MolecularCharacterization) " +
            "WHERE id(molch) = {mc} " +
            "RETURN model")
    ModelCreation findByMolChar(@Param("mc") MolecularCharacterization mc);


    @Query("MATCH (mod:ModelCreation) WHERE toLower(mod.dataSource) = toLower({dataSource}) " +
            "WITH mod " +
            "MATCH (mod)-[msr:MODEL_SAMPLE_RELATION]-(s:Sample)-[cbr:CHARACTERIZED_BY]-(mc:MolecularCharacterization)-[pur:PLATFORM_USED]-(pl:Platform) " +
            "RETURN mod, msr, s, cbr, mc, pur, pl")
    Collection<ModelCreation> getModelsWithMolCharBySource(@Param("dataSource") String dataSource);

}
