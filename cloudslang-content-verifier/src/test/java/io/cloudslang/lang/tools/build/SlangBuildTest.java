/*
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package io.cloudslang.lang.tools.build;

import io.cloudslang.lang.api.Slang;
import io.cloudslang.lang.compiler.SlangCompiler;
import io.cloudslang.lang.compiler.modeller.model.Executable;
import io.cloudslang.lang.entities.bindings.Input;
import io.cloudslang.lang.tools.build.tester.RunTestsResults;
import io.cloudslang.lang.tools.build.tester.SlangTestRunner;
import io.cloudslang.lang.tools.build.tester.TestRun;
import io.cloudslang.lang.tools.build.tester.parse.SlangTestCase;
import io.cloudslang.lang.tools.build.tester.parse.TestCasesYamlParser;
import io.cloudslang.lang.tools.build.verifier.SlangContentVerifier;
import junit.framework.Assert;
import io.cloudslang.lang.compiler.SlangSource;
import io.cloudslang.lang.compiler.scorecompiler.ScoreCompiler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import io.cloudslang.score.api.ExecutionPlan;
import io.cloudslang.lang.compiler.modeller.model.Flow;
import io.cloudslang.lang.entities.CompilationArtifact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yaml.snakeyaml.Yaml;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;

/*
 * Created by stoneo on 2/11/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SlangBuildTest.Config.class)
public class SlangBuildTest {

	private static final CompilationArtifact emptyCompilationArtifact = new CompilationArtifact(new ExecutionPlan(), new HashMap<String, ExecutionPlan>(), new ArrayList<Input>(), new ArrayList<Input>());
	private static final Flow emptyExecutable = new Flow(null, null, null, "no_dependencies", "empty_flow", null, null, null, new HashSet<String>());

    @Autowired
    private SlangBuilder slangBuilder;

    @Autowired
    private SlangCompiler slangCompiler;

    @Autowired
    private ScoreCompiler scoreCompiler;

    @Autowired
    private SlangTestRunner slangTestRunner;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void resetMocks() {
        Mockito.reset(slangCompiler);
        Mockito.reset(scoreCompiler);
        Mockito.reset(slangTestRunner);
    }

    @Test
    public void testNullDirPath() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("path");
        slangBuilder.buildSlangContent(null, null, null, null);
    }

    @Test
    public void testEmptyDirPath() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("path");
        slangBuilder.buildSlangContent("", "content", null, null);
    }

    @Test
    public void testIllegalDirPath() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("c/h/j");
        exception.expectMessage("directory");
        slangBuilder.buildSlangContent("c/h/j", "c/h/j/content", null, null);
    }

    @Test
    public void testPreCompileIllegalSlangFile() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenThrow(new RuntimeException());
        exception.expect(RuntimeException.class);
        slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
    }

    @Test
    public void testNotAllSlangFilesWerePreCompiled() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(null);
        exception.expect(RuntimeException.class);
        exception.expectMessage("1");
        exception.expectMessage("0");
        exception.expectMessage("compiled");
        slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
    }

    @Test
    public void testCompileValidSlangFileNoDependencies() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(emptyExecutable);
        Mockito.when(scoreCompiler.compile(emptyExecutable, new HashSet<Executable>())).thenReturn(emptyCompilationArtifact);
        SlangBuildResults buildResults = slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
        int numberOfCompiledSlangFiles = buildResults.getNumberOfCompiledSources();
        Assert.assertEquals("Did not compile all Slang files. Expected to compile: 1, but compiled: " + numberOfCompiledSlangFiles, numberOfCompiledSlangFiles, 1);
    }

    @Test
    public void testCompileInvalidSlangFile() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(emptyExecutable);
        Mockito.when(scoreCompiler.compile(emptyExecutable, new HashSet<Executable>())).thenThrow(new RuntimeException());
        exception.expect(RuntimeException.class);
        slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
    }

    @Test
    public void testNotAllSlangFilesWereCompiled() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(emptyExecutable);
        Mockito.when(scoreCompiler.compile(emptyExecutable, new HashSet<Executable>())).thenReturn(null);
        exception.expect(RuntimeException.class);
        exception.expectMessage("1");
        exception.expectMessage("0");
        exception.expectMessage("compile");
        exception.expectMessage("models");
        slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
    }

    @Test
    public void testCompileValidSlangFileWithMissingDependencies() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Set<String> flowDependencies = new HashSet<>();
        flowDependencies.add("dep1");
        Flow newExecutable = new Flow(null, null, null, "no_dependencies", "empty_flow", null, null, null, flowDependencies);
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(newExecutable);
        Mockito.when(scoreCompiler.compile(newExecutable, new HashSet<Executable>())).thenReturn(emptyCompilationArtifact);
        exception.expect(RuntimeException.class);
        exception.expectMessage("dependency");
        exception.expectMessage("dep1");
        slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
    }

    @Test
    public void testCompileValidSlangFileWithDependencies() throws Exception {
        URI resource = getClass().getResource("/dependencies").toURI();
        Set<String> flowDependencies = new HashSet<>();
        flowDependencies.add("dependencies.dependency");
        Flow emptyFlowExecutable = new Flow(null, null, null, "dependencies", "empty_flow", null, null, null, flowDependencies);
        Mockito.when(slangCompiler.preCompile(new SlangSource("", "empty_flow.sl"))).thenReturn(emptyFlowExecutable);
        Flow dependencyExecutable = new Flow(null, null, null, "dependencies", "dependency", null, null, null, new HashSet<String>());
        Mockito.when(slangCompiler.preCompile(new SlangSource("", "dependency.sl"))).thenReturn(dependencyExecutable);
        HashSet<Executable> dependencies = new HashSet<>();
        dependencies.add(dependencyExecutable);
        Mockito.when(scoreCompiler.compile(emptyFlowExecutable, dependencies)).thenReturn(emptyCompilationArtifact);
        Mockito.when(scoreCompiler.compile(dependencyExecutable, new HashSet<Executable>())).thenReturn(emptyCompilationArtifact);
        SlangBuildResults buildResults = slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
        int numberOfCompiledSlangFiles = buildResults.getNumberOfCompiledSources();
        Assert.assertEquals("Did not compile all Slang files. Expected to compile: 2, but compiled: " + numberOfCompiledSlangFiles, numberOfCompiledSlangFiles, 2);
    }

    @Test
    public void testInvalidNamespaceFlow() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Flow newExecutable = new Flow(null, null, null, "wrong.namespace", "empty_flow", null, null, null, new HashSet<String>());
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(newExecutable);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Namespace");
        exception.expectMessage("wrong.namespace");
        slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
    }

    @Test
    public void testInvalidFlowName() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Flow newExecutable = new Flow(null, null, null, "no_dependencies", "wrong_name", null, null, null, new HashSet<String>());
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(newExecutable);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Name");
        exception.expectMessage("wrong_name");
        slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
    }

    @Test
    public void testValidFlowNameAndNamespace() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(emptyExecutable);
        Mockito.when(scoreCompiler.compile(emptyExecutable, new HashSet<Executable>())).thenReturn(emptyCompilationArtifact);
        SlangBuildResults buildResults = slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
        int numberOfCompiledSlangFiles = buildResults.getNumberOfCompiledSources();
        Assert.assertEquals("Did not compile all Slang files. Expected to compile: 1, but compiled: " + numberOfCompiledSlangFiles, numberOfCompiledSlangFiles, 1);
    }

    @Test
    public void testValidFlowNamespaceWithAllValidCharsTypes() throws Exception {
        URI resource = getClass().getResource("/no_dependencies-0123456789").toURI();
        Flow executable = new Flow(null, null, null, "no_dependencies-0123456789", "empty_flow", null, null, null, new HashSet<String>());
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(executable);
        Mockito.when(scoreCompiler.compile(executable, new HashSet<Executable>())).thenReturn(emptyCompilationArtifact);
        SlangBuildResults buildResults = slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
        int numberOfCompiledSlangFiles = buildResults.getNumberOfCompiledSources();
        Assert.assertEquals("Did not compile all Slang files. Expected to compile: 1, but compiled: " + numberOfCompiledSlangFiles, numberOfCompiledSlangFiles, 1);
    }

    @Test
    public void testValidFlowNamespaceCaseInsensitive() throws Exception {
        URI resource = getClass().getResource("/no_dependencies").toURI();
        Flow executable = new Flow(null, null, null, "No_Dependencies", "empty_flow", null, null, null, new HashSet<String>());
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(executable);
        Mockito.when(scoreCompiler.compile(executable, new HashSet<Executable>())).thenReturn(emptyCompilationArtifact);
        SlangBuildResults buildResults = slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
        int numberOfCompiledSlangFiles = buildResults.getNumberOfCompiledSources();
        Assert.assertEquals("Did not compile all Slang files. Expected to compile: 1, but compiled: " + numberOfCompiledSlangFiles, numberOfCompiledSlangFiles, 1);
    }

    @Test
    public void testNamespaceWithInvalidCharsFlow() throws Exception {
        URI resource = getClass().getResource("/invalid-chars$").toURI();
        Flow newExecutable = new Flow(null, null, null, "invalid-chars$", "empty_flow", null, null, null, new HashSet<String>());
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(newExecutable);
        exception.expect(RuntimeException.class);
        exception.expectMessage("invalid-chars$");
        exception.expectMessage("alphanumeric");
        slangBuilder.buildSlangContent(resource.getPath(), resource.getPath(), null, null);
    }

    @Test
    public void testCompileSlangFileAndRunTests() throws Exception {
        URI contentResource = getClass().getResource("/no_dependencies").toURI();
        URI testResource = getClass().getResource("/test/valid").toURI();
        Mockito.when(slangCompiler.preCompile(any(SlangSource.class))).thenReturn(emptyExecutable);
        Mockito.when(scoreCompiler.compile(emptyExecutable, new HashSet<Executable>())).thenReturn(emptyCompilationArtifact);
        RunTestsResults runTestsResults = new RunTestsResults();
        runTestsResults.addFailedTest("test1", new TestRun(new SlangTestCase("test1", "", null, null, null, null, null, null, null), "message"));
        Mockito.when(slangTestRunner.runAllTests(any(String.class), anyMap(), anyMap(), anyList())).thenReturn(runTestsResults);
        SlangBuildResults buildResults = slangBuilder.buildSlangContent(contentResource.getPath(), contentResource.getPath(), testResource.getPath(), null);
        int numberOfCompiledSlangFiles = buildResults.getNumberOfCompiledSources();
        RunTestsResults actualRunTestsResults = buildResults.getRunTestsResults();
        Assert.assertEquals("Did not compile all Slang files. Expected to compile: 1, but compiled: " + numberOfCompiledSlangFiles, numberOfCompiledSlangFiles, 1);
        Assert.assertEquals("1 test case should fail", 1, actualRunTestsResults.getFailedTests().size());
    }

    @Configuration
    static class Config {

        @Bean
        public SlangCompiler slangCompiler() {
            return mock(SlangCompiler.class);
        }

        @Bean
        public ScoreCompiler scoreCompiler() {
            return mock(ScoreCompiler.class);
        }

        @Bean
        public SlangBuilder slangBuild() {
            return new SlangBuilder();
        }

        @Bean
        public SlangContentVerifier slangContentVerifier() {
            return new SlangContentVerifier();
        }

        @Bean
        public SlangTestRunner slangTestRunner() {
            return mock(SlangTestRunner.class);
        }

        @Bean
        public TestCasesYamlParser testCasesYamlParser(){
            return mock(TestCasesYamlParser.class);
        }

        @Bean
        public Yaml yaml(){
            return mock(Yaml.class);
        }

        @Bean
        public Slang slang(){
            return mock(Slang.class);
        }
    }
}
