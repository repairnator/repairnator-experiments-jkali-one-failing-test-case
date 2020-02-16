/*******************************************************************************
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package io.cloudslang.lang.tools.build.commands;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.CommaParameterSplitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationArgs {

    @Parameter
    public List<String> parameters = new ArrayList<>();

    @Parameter(names = { "--project-root", "-pr"},
            description = "Project root directory")
    public String projectRoot;

    @Parameter(names = { "--content-root", "-cr"},
            description = "Content root directory")
    public String contentRoot;

    @Parameter(names = { "--test-root", "-tr"},
            description = "Test root directory")
    public String testRoot;

    @Parameter(names = {"--test-suites", "-ts"},
            description = "Comma-separated list of group names to be run",
            splitter = CommaParameterSplitter.class)
    public List<String> testSuites;

    @Parameter(names = {"--coverage", "-cov"},
            description = "Whether or not test coverage data should be outputted")
    public boolean coverage = false;


    @Parameter(names = {"--help", "-h"}, help = true,
            description = "Print this message")
    private boolean help;

    @DynamicParameter(names = "-D", description = "Dynamic parameters go here")
    public Map<String, String> dynamicParams = new HashMap<>();

    public List<String> getParameters() {
        return parameters;
    }

    public String getProjectRoot() {
        return projectRoot;
    }

    public String getContentRoot() {
        return contentRoot;
    }

    public String getTestRoot() {
        return testRoot;
    }

    public List<String> getTestSuites() {
        return testSuites;
    }

    public Boolean shouldOutputCoverage() {
        return coverage;
    }

    public boolean isHelp() {
        return help;
    }

    public Map<String, String> getDynamicParams() {
        return dynamicParams;
    }
}
