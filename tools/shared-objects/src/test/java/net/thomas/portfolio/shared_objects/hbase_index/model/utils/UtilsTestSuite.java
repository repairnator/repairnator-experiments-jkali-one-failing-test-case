package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.schema.IdCalculatorUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DateConverterUnitTest.class, UidConverterUnitTest.class, IdCalculatorUnitTest.class })
public class UtilsTestSuite {
}