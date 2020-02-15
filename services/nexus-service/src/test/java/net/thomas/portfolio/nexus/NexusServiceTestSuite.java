package net.thomas.portfolio.nexus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgumentParametizedUnitTest;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataProxiesTestSuite;
import net.thomas.portfolio.nexus.service.NexusServiceControllerServiceAdaptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DataProxiesTestSuite.class, GraphQlArgumentParametizedUnitTest.class, NexusServiceControllerServiceAdaptorTest.class })
public class NexusServiceTestSuite {
}