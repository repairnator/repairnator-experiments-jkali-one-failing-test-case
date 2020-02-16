/*
 * Copyright 2007-2018 The jdeb developers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vafer.jdeb.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import org.apache.tools.ant.util.ReaderInputStream;
import org.vafer.jdeb.debian.BinaryPackageControlFile;

public final class FilteredFileTestCase extends Assert {

    private VariableResolver variableResolver;

    @Before
    public void setUp() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("artifactId", "jdeb");
        map.put("myProperty1", "custom1");
        map.put("myProperty2", "custom2");
        variableResolver = new MapVariableResolver(map);
    }

    @Test
    public void testTokenSubstitution() throws Exception {
        InputStream in = new ReaderInputStream(new StringReader("#!/bin/sh\ncat [[artifactId]][[myProperty1]] \necho '[[myProperty2]]'\n"));

        FilteredFile placeHolder = new FilteredFile(in, variableResolver);

        String actual = placeHolder.toString();
        assertEquals("#!/bin/sh\ncat jdebcustom1 \necho 'custom2'\n", actual);
    }

    @Test
    public void testTokenSubstitutionWithinOpenCloseTokens() throws Exception {
        // The line below is a extract from my postinst script.
        InputStream in = new ReaderInputStream(new StringReader("#!/usr/bin/python3 -B\nline = line.replace(\'@ALLOW_BATCH_FILTER@\', config[self.client][\'ALLOW_BATCH_FILTER\'])"));

        // This method should only replace values enclosed inside "[[ ]]" tags, however it is incorrectly mutating the string above
        // config[self.client]['ALLOW_BATCH_FILTER'] ----> config[self.client[]'ALLOW_BATCH_FILTER']  (THIS IS SHOULD NOT HAPPEN !!!)
        FilteredFile placeHolder = new FilteredFile(in, variableResolver);
        String actual = placeHolder.toString();
        assertEquals("", "#!/usr/bin/python3 -B\nline = line.replace(\'@ALLOW_BATCH_FILTER@\', config[self.client][\'ALLOW_BATCH_FILTER\'])", actual);
    }

    @Test
    public void testVariableSubstitution() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("VERSION", "1.2");
        map.put("MAINTAINER", "Torsten Curdt <tcurdt@vafer.org>");

        String controlFile =
                "Version: [[VERSION]]\n"
                + "Maintainer: [[MAINTAINER]]\n"
                + "NoResolve1: test[[test\n"
                + "NoResolve2: [[test]]\n";

        FilteredFile filteredFile = new FilteredFile(new ByteArrayInputStream(controlFile.getBytes()), new MapVariableResolver(map));

        BinaryPackageControlFile d = new BinaryPackageControlFile(filteredFile.toString());

        assertEquals("1.2", d.get("Version"));
        assertEquals("Torsten Curdt <tcurdt@vafer.org>", d.get("Maintainer"));
        assertEquals("test[[test", d.get("NoResolve1"));
        assertEquals("[[test]]", d.get("NoResolve2"));
    }
}
