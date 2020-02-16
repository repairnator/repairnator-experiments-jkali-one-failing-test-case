/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.execution.resourceGroups.db;

import com.facebook.presto.Session;
import com.facebook.presto.execution.QueryManager;
import com.facebook.presto.execution.QueryState;
import com.facebook.presto.execution.TestingSessionFactory;
import com.facebook.presto.execution.resourceGroups.ResourceGroupManager;
import com.facebook.presto.resourceGroups.db.DbResourceGroupConfig;
import com.facebook.presto.resourceGroups.db.H2DaoProvider;
import com.facebook.presto.resourceGroups.db.H2ResourceGroupsDao;
import com.facebook.presto.spi.Plugin;
import com.facebook.presto.spi.QueryId;
import com.facebook.presto.spi.resourceGroups.ResourceGroupId;
import com.facebook.presto.spi.resourceGroups.ResourceGroupInfo;
import com.facebook.presto.spi.resourceGroups.ResourceGroupSelector;
import com.facebook.presto.sql.parser.SqlParserOptions;
import com.facebook.presto.tests.DistributedQueryRunner;
import com.facebook.presto.tests.tpch.TpchQueryRunner;
import com.facebook.presto.tpch.TpchPlugin;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.facebook.presto.execution.QueryState.FAILED;
import static com.facebook.presto.execution.QueryState.QUEUED;
import static com.facebook.presto.execution.QueryState.RUNNING;
import static com.facebook.presto.execution.QueryState.TERMINAL_QUERY_STATES;
import static com.facebook.presto.spi.StandardErrorCode.EXCEEDED_MEMORY_LIMIT;
import static com.facebook.presto.spi.StandardErrorCode.QUERY_REJECTED;
import static com.facebook.presto.testing.TestingSession.testSessionBuilder;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.testng.Assert.assertEquals;

// run single threaded to avoid creating multiple query runners at once
@Test(singleThreaded = true)
public class TestQueues
{
    // Copy of TestQueues with tests for db reconfiguration of resource groups
    private static final String NAME = "h2";
    private static final String LONG_LASTING_QUERY = "SELECT COUNT(*) FROM lineitem";
    private static final String HUGE_MEMORY_QUERY = "SELECT COUNT(*) FROM lineitem a join lineitem b on a.orderkey = b.orderkey";

    @Test(timeOut = 60_000)
    public void testRunningQuery()
            throws Exception
    {
        try (DistributedQueryRunner queryRunner = getSimpleQueryRunner()) {
            queryRunner.execute("SELECT COUNT(*), clerk FROM orders GROUP BY clerk");
            while (true) {
                ResourceGroupInfo global = queryRunner.getCoordinator().getResourceGroupManager().get().getResourceGroupInfo(new ResourceGroupId(new ResourceGroupId("global"), "bi-user"));
                if (global.getSoftMemoryLimit().toBytes() > 0) {
                    break;
                }
                TimeUnit.SECONDS.sleep(2);
            }
        }
    }

    @Test(timeOut = 240_000)
    public void testBasic()
            throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        try (DistributedQueryRunner queryRunner = createQueryRunner(dbConfigUrl, dao)) {
            QueryManager queryManager = queryRunner.getCoordinator().getQueryManager();
            // submit first "dashboard" query
            QueryId firstDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);

            // wait for the first "dashboard" query to start
            waitForQueryState(queryRunner, firstDashboardQuery, RUNNING);
            waitForRunningQueryCount(queryRunner, 1);
            // submit second "dashboard" query
            QueryId secondDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            MILLISECONDS.sleep(2000);
            // wait for the second "dashboard" query to be queued ("dashboard.${USER}" queue strategy only allows one "dashboard" query to be accepted for execution)
            waitForQueryState(queryRunner, secondDashboardQuery, QUEUED);
            waitForRunningQueryCount(queryRunner, 1);
            // Update db to allow for 1 more running query in dashboard resource group
            dao.updateResourceGroup(3, "user-${USER}", "1MB", "1GB", 3, 4, null, null, null, null, null, null, null, 1L);
            dao.updateResourceGroup(5, "dashboard-${USER}", "1MB", "1GB", 1, 2, null, null, null, null, null, null, null, 3L);
            waitForQueryState(queryRunner, secondDashboardQuery, RUNNING);
            QueryId thirdDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, thirdDashboardQuery, QUEUED);
            waitForRunningQueryCount(queryRunner, 2);
            // submit first non "dashboard" query
            QueryId firstNonDashboardQuery = createQuery(queryRunner, newSession(), LONG_LASTING_QUERY);
            // wait for the first non "dashboard" query to start
            waitForQueryState(queryRunner, firstNonDashboardQuery, RUNNING);
            waitForRunningQueryCount(queryRunner, 3);
            // submit second non "dashboard" query
            QueryId secondNonDashboardQuery = createQuery(queryRunner, newSession(), LONG_LASTING_QUERY);
            // wait for the second non "dashboard" query to start
            waitForQueryState(queryRunner, secondNonDashboardQuery, RUNNING);
            waitForRunningQueryCount(queryRunner, 4);
            // cancel first "dashboard" query, the second "dashboard" query and second non "dashboard" query should start running
            cancelQuery(queryRunner, firstDashboardQuery);
            waitForQueryState(queryRunner, firstDashboardQuery, FAILED);
            waitForQueryState(queryRunner, thirdDashboardQuery, RUNNING);
            waitForRunningQueryCount(queryRunner, 4);
            waitForCompleteQueryCount(queryRunner, 1);
        }
    }

    @Test(timeOut = 240_000)
    public void testTwoQueriesAtSameTime()
            throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        try (DistributedQueryRunner queryRunner = createQueryRunner(dbConfigUrl, dao)) {
            QueryId firstDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            QueryId secondDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);

            ImmutableSet<QueryState> queuedOrRunning = ImmutableSet.of(QUEUED, RUNNING);
            waitForQueryState(queryRunner, firstDashboardQuery, RUNNING);
            waitForQueryState(queryRunner, secondDashboardQuery, QUEUED);
        }
    }

    @Test(timeOut = 240_000)
    public void testTooManyQueries()
            throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        try (DistributedQueryRunner queryRunner = createQueryRunner(dbConfigUrl, dao)) {
            QueryId firstDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, firstDashboardQuery, RUNNING);

            QueryId secondDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, secondDashboardQuery, QUEUED);

            QueryId thirdDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, thirdDashboardQuery, FAILED);

            // Allow one more query to run and resubmit third query
            dao.updateResourceGroup(3, "user-${USER}", "1MB", "1GB", 3, 4, null, null, null, null, null, null, null, 1L);
            dao.updateResourceGroup(5, "dashboard-${USER}", "1MB", "1GB", 1, 2, null, null, null, null, null, null, null, 3L);
            waitForQueryState(queryRunner, secondDashboardQuery, RUNNING);
            thirdDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, thirdDashboardQuery, QUEUED);

            // Lower running queries in dashboard resource groups and wait until groups are reconfigured
            dao.updateResourceGroup(5, "dashboard-${USER}", "1MB", "1GB", 1, 1, null, null, null, null, null, null, null, 3L);
            ResourceGroupManager manager = queryRunner.getCoordinator().getResourceGroupManager().get();
            while (manager.getResourceGroupInfo(
                    new ResourceGroupId(new ResourceGroupId(new ResourceGroupId("global"), "user-user"), "dashboard-user")).getMaxRunningQueries() != 1) {
                MILLISECONDS.sleep(500);
            }
            // Cancel query and verify that third query is still queued
            cancelQuery(queryRunner, firstDashboardQuery);
            waitForQueryState(queryRunner, firstDashboardQuery, FAILED);
            MILLISECONDS.sleep(2000);
            waitForQueryState(queryRunner, thirdDashboardQuery, QUEUED);
        }
    }

    @Test(timeOut = 240_000)
    public void testRejection()
            throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        try (DistributedQueryRunner queryRunner = createQueryRunner(dbConfigUrl, dao)) {
            // Verify the query cannot be submitted
            QueryId queryId = createQuery(queryRunner, newRejectionSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, queryId, FAILED);
            QueryManager queryManager = queryRunner.getCoordinator().getQueryManager();
            assertEquals(queryManager.getQueryInfo(queryId).getErrorCode(), QUERY_REJECTED.toErrorCode());
            int selectorCount = getSelectors(queryRunner).size();
            dao.insertSelector(4, "user.*", "(?i).*reject.*");
            assertEquals(dao.getSelectors().size(), selectorCount + 1);
            while (getSelectors(queryRunner).size() == selectorCount) {
                MILLISECONDS.sleep(500);
            }
            // Verify the query can be submitted
            queryId = createQuery(queryRunner, newRejectionSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, queryId, RUNNING);
            dao.deleteSelector(4, "user.*", "(?i).*reject.*");
            while (getSelectors(queryRunner).size() != selectorCount) {
                MILLISECONDS.sleep(500);
            }
            // Verify the query cannot be submitted
            queryId = createQuery(queryRunner, newRejectionSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, queryId, FAILED);
        }
    }

    @Test(timeOut = 240_000)
    public void testRunningTimeout()
            throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        try (DistributedQueryRunner queryRunner = createQueryRunner(dbConfigUrl, dao)) {
            dao.updateResourceGroup(5, "dashboard-${USER}", "1MB", "1GB", 1, 1, null, null, null, null, null, null, "3s", 3L);
            QueryId firstDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, firstDashboardQuery, FAILED);
        }
    }

    @Test(timeOut = 240_000)
    public void testQueuedTimeout()
            throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        try (DistributedQueryRunner queryRunner = createQueryRunner(dbConfigUrl, dao)) {
            dao.updateResourceGroup(5, "dashboard-${USER}", "1MB", "1GB", 1, 1, null, null, null, null, null, "5s", null, 3L);
            QueryId firstDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, firstDashboardQuery, RUNNING);
            QueryId secondDashboardQuery = createQuery(queryRunner, newDashboardSession(), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, secondDashboardQuery, QUEUED);
            waitForQueryState(queryRunner, secondDashboardQuery, FAILED);
        }
    }

    @Test(timeOut = 240_000)
    public void testHardMemoryLimit()
        throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        try (DistributedQueryRunner queryRunner = createQueryRunner(dbConfigUrl, dao)) {
            dao.updateResourceGroup(5, "dashboard-${USER}", "8kB", "10kB", 1, 1, null, null, null, null, null, "5s", null, 3L);
            QueryId firstDashboardQuery = createQuery(queryRunner, newDashboardSession(), HUGE_MEMORY_QUERY);
            waitForQueryState(queryRunner, firstDashboardQuery, RUNNING);
            waitForQueryState(queryRunner, firstDashboardQuery, FAILED);
            assertEquals(EXCEEDED_MEMORY_LIMIT.toErrorCode(), queryRunner.getCoordinator().getQueryManager().getQueryInfo(firstDashboardQuery).getErrorCode());
        }
    }

    @Test(timeOut = 240_000)
    public void testWeightedFifoSchedulingPolicy()
            throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        try (DistributedQueryRunner queryRunner = createQueryRunner(dbConfigUrl, dao)) {
            setupWeightedFifo(queryRunner, dao);
            // Create 2 filler queries so other queries can be queued
            QueryId filler1 = createQuery(queryRunner, newSessionWithSource("etl-hi"), LONG_LASTING_QUERY);
            QueryId filler2 = createQuery(queryRunner, newSessionWithSource("etl-hi"), LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, filler1, RUNNING);
            waitForQueryState(queryRunner, filler2, RUNNING);
            // Create 2 queries for each resource group
            QueryId etlLo1 = createQuery(queryRunner, newSessionWithSource("etl-lo"), LONG_LASTING_QUERY);
            QueryId etlLo2 = createQuery(queryRunner, newSessionWithSource("etl-lo"), LONG_LASTING_QUERY);
            QueryId etlMed1 = createQuery(queryRunner, newSessionWithSource("etl-med"), LONG_LASTING_QUERY);
            QueryId etlMed2 = createQuery(queryRunner, newSessionWithSource("etl-med"), LONG_LASTING_QUERY);
            QueryId etlHi1 = createQuery(queryRunner, newSessionWithSource("etl-hi"), LONG_LASTING_QUERY);
            QueryId etlHi2 = createQuery(queryRunner, newSessionWithSource("etl-hi"), LONG_LASTING_QUERY);
            // Wait for filler queries to run
            waitForQueryState(queryRunner, filler1, RUNNING);
            waitForQueryState(queryRunner, filler2, RUNNING);
            // Verify all other queries are queued
            waitForQueryState(queryRunner, etlLo1, QUEUED);
            waitForQueryState(queryRunner, etlLo2, QUEUED);
            waitForQueryState(queryRunner, etlMed1, QUEUED);
            waitForQueryState(queryRunner, etlMed2, QUEUED);
            waitForQueryState(queryRunner, etlHi1, QUEUED);
            waitForQueryState(queryRunner, etlHi2, QUEUED);
            //Cancel filler queries and wait for etlHi to begin
            cancelQuery(queryRunner, filler1);
            cancelQuery(queryRunner, filler2);
            waitForQueryState(queryRunner, filler1, FAILED);
            waitForQueryState(queryRunner, filler2, FAILED);
            waitForQueryState(queryRunner, etlHi1, RUNNING);
            waitForQueryState(queryRunner, etlHi2, RUNNING);
            // Verify other queries are queued
            waitForQueryState(queryRunner, etlLo1, QUEUED);
            waitForQueryState(queryRunner, etlLo2, QUEUED);
            waitForQueryState(queryRunner, etlMed1, QUEUED);
            waitForQueryState(queryRunner, etlMed2, QUEUED);
            // Cancel etlHi queries
            cancelQuery(queryRunner, etlHi1);
            cancelQuery(queryRunner, etlHi2);
            waitForQueryState(queryRunner, etlHi1, FAILED);
            waitForQueryState(queryRunner, etlHi2, FAILED);
            // Wait for etlMed queries to run
            waitForQueryState(queryRunner, etlMed1, RUNNING);
            waitForQueryState(queryRunner, etlMed2, RUNNING);
            // Verify etlLo queries are still queued
            waitForQueryState(queryRunner, etlLo1, QUEUED);
            waitForQueryState(queryRunner, etlLo2, QUEUED);
            // Cancel etlMed queries
            cancelQuery(queryRunner, etlMed1);
            cancelQuery(queryRunner, etlMed2);
            waitForQueryState(queryRunner, etlMed1, FAILED);
            waitForQueryState(queryRunner, etlMed2, FAILED);
            // Wait for etlLo queries to run
            waitForQueryState(queryRunner, etlLo1, RUNNING);
            waitForQueryState(queryRunner, etlLo2, RUNNING);
        }
    }

    private static void setupWeightedFifo(DistributedQueryRunner queryRunner, H2ResourceGroupsDao dao)
            throws Exception
    {
        dao.insertResourceGroup(6, "bi", "1MB", "1GB", 100, 2, "weighted_fifo", null, null, null, null, null, null, null);
        dao.insertResourceGroup(7, "etl-hi", "1MB", "1GB", 100, 2, "weighted_fifo", 100, null, null, null, null, null, 6L);
        dao.insertResourceGroup(8, "etl-med", "1MB", "1GB", 100, 2, "weighted_fifo", 50, null, null, null, null, null, 6L);
        dao.insertResourceGroup(9, "etl-lo", "1MB", "1GB", 100, 2, "weighted_fifo", 20, null, null, null, null, null, 6L);
        dao.insertSelector(7, "user.*", "etl-hi");
        dao.insertSelector(8, "user.*", "etl-med");
        dao.insertSelector(9, "user.*", "etl-lo");
        // Selectors are loaded last
        do {
            MILLISECONDS.sleep(500);
        } while (getSelectors(queryRunner).size() != 6);
    }

    private static Session newSession()
    {
        return testSessionBuilder()
                .setCatalog("tpch")
                .setSchema("sf100000")
                .setSource("adhoc")
                .build();
    }

    private static Session newDashboardSession()
    {
        return testSessionBuilder()
                .setCatalog("tpch")
                .setSchema("sf100000")
                .setSource("dashboard")
                .build();
    }

    private static Session newRejectionSession()
    {
        return testSessionBuilder()
                .setCatalog("tpch")
                .setSchema("sf100000")
                .setSource("reject")
                .build();
    }

    private static Session newSessionWithSource(String source)
    {
        return testSessionBuilder()
                .setCatalog("tpch")
                .setSchema("sf100000")
                .setSource(source)
                .build();
    }

    private static QueryId createQuery(DistributedQueryRunner queryRunner, Session session, String sql)
    {
        return queryRunner.getCoordinator().getQueryManager().createQuery(new TestingSessionFactory(session), sql).getQueryId();
    }

    private static void cancelQuery(DistributedQueryRunner queryRunner, QueryId queryId)
    {
        queryRunner.getCoordinator().getQueryManager().cancelQuery(queryId);
    }

    private static void waitForCompleteQueryCount(DistributedQueryRunner queryRunner, int expectedCount)
            throws InterruptedException
    {
        waitForQueryCount(queryRunner, TERMINAL_QUERY_STATES, expectedCount);
    }

    private static void waitForRunningQueryCount(DistributedQueryRunner queryRunner, int expectedCount)
            throws InterruptedException
    {
        waitForQueryCount(queryRunner, ImmutableSet.of(RUNNING), expectedCount);
    }

    private static void waitForQueryCount(DistributedQueryRunner queryRunner, Set<QueryState> countingStates, int expectedCount)
            throws InterruptedException
    {
        QueryManager queryManager = queryRunner.getCoordinator().getQueryManager();
        while (queryManager.getAllQueryInfo().stream().filter(q -> countingStates.contains(q.getState())).count() != expectedCount) {
            MILLISECONDS.sleep(500);
        }
    }

    private static void waitForQueryState(DistributedQueryRunner queryRunner, QueryId queryId, QueryState expectedQueryState)
            throws InterruptedException
    {
        waitForQueryState(queryRunner, queryId, ImmutableSet.of(expectedQueryState));
    }

    private static void waitForQueryState(DistributedQueryRunner queryRunner, QueryId queryId, Set<QueryState> expectedQueryStates)
            throws InterruptedException
    {
        QueryManager queryManager = queryRunner.getCoordinator().getQueryManager();
        while (!expectedQueryStates.contains(queryManager.getQueryInfo(queryId).getState())) {
            MILLISECONDS.sleep(500);
        }
    }

    private static String getDbConfigUrl()
    {
        Random rnd = new Random();
        return "jdbc:h2:mem:test_" + Math.abs(rnd.nextLong());
    }

    private static H2ResourceGroupsDao getDao(String url)
    {
        DbResourceGroupConfig dbResourceGroupConfig = new DbResourceGroupConfig()
                .setConfigDbUrl(url);
        H2ResourceGroupsDao dao = new H2DaoProvider(dbResourceGroupConfig).get();
        dao.createResourceGroupsTable();
        dao.createSelectorsTable();
        dao.createResourceGroupsGlobalPropertiesTable();
        return dao;
    }

    private static DistributedQueryRunner createQueryRunner(String dbConfigUrl, H2ResourceGroupsDao dao)
            throws Exception
    {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("experimental.resource-groups-enabled", "true");
        Map<String, String> properties = builder.build();
        DistributedQueryRunner queryRunner = new DistributedQueryRunner(testSessionBuilder().build(), 2, ImmutableMap.of(), properties, new SqlParserOptions());
        try {
            Plugin h2ResourceGroupManagerPlugin = new H2ResourceGroupManagerPlugin();
            queryRunner.installPlugin(h2ResourceGroupManagerPlugin);
            queryRunner.getCoordinator().getResourceGroupManager().get()
                    .setConfigurationManager(NAME, ImmutableMap.of("resource-groups.config-db-url", dbConfigUrl));
            queryRunner.installPlugin(new TpchPlugin());
            queryRunner.createCatalog("tpch", "tpch");
            setup(queryRunner, dao);
            return queryRunner;
        }
        catch (Exception e) {
            queryRunner.close();
            throw e;
        }
    }

    static DistributedQueryRunner getSimpleQueryRunner()
            throws Exception
    {
        String dbConfigUrl = getDbConfigUrl();
        H2ResourceGroupsDao dao = getDao(dbConfigUrl);
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("experimental.resource-groups-enabled", "true");
        Map<String, String> properties = builder.build();
        DistributedQueryRunner queryRunner = TpchQueryRunner.createQueryRunner(properties);
        Plugin h2ResourceGroupManagerPlugin = new H2ResourceGroupManagerPlugin();
        queryRunner.installPlugin(h2ResourceGroupManagerPlugin);
        queryRunner.getCoordinator().getResourceGroupManager().get()
                .setConfigurationManager(NAME, ImmutableMap.of("resource-groups.config-db-url", dbConfigUrl));
        setup(queryRunner, dao);
        return queryRunner;
    }

    private static void setup(DistributedQueryRunner queryRunner, H2ResourceGroupsDao dao)
            throws InterruptedException
    {
        dao.insertResourceGroupsGlobalProperties("cpu_quota_period", "1h");
        dao.insertResourceGroup(1, "global", "1MB", "1GB", 100, 1000, null, null, null, null, null, null, null, null);
        dao.insertResourceGroup(2, "bi-${USER}", "1MB", "1GB", 3, 2, null, null, null, null, null, null, null, 1L);
        dao.insertResourceGroup(3, "user-${USER}", "1MB", "1GB", 3, 3, null, null, null, null, null, null, null, 1L);
        dao.insertResourceGroup(4, "adhoc-${USER}", "1MB", "1GB", 3, 3, null, null, null, null, null, null, null, 3L);
        dao.insertResourceGroup(5, "dashboard-${USER}", "1MB", "1GB", 1, 1, null, null, null, null, null, null, null, 3L);
        dao.insertSelector(2, "user.*", "test");
        dao.insertSelector(4, "user.*", "(?i).*adhoc.*");
        dao.insertSelector(5, "user.*", "(?i).*dashboard.*");
        // Selectors are loaded last
        while (getSelectors(queryRunner).size() != 3) {
            MILLISECONDS.sleep(500);
        }
    }

    private static List<ResourceGroupSelector> getSelectors(DistributedQueryRunner queryRunner)
    {
        return queryRunner.getCoordinator().getResourceGroupManager().get().getConfigurationManager().getSelectors();
    }
}
