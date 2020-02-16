/*
 * Copyright (C) 2016, 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 7. February 2016 by Aaron Johnson
 */
package com.thoughtworks.acceptance;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Aaron Johnson
 * @author J&ouml;rg Schaible
 */
public class Extended17TypesTest extends AbstractAcceptanceTest {

    public void testPathOfDefaultFileSystem() {
        assertBothWays(Paths.get("../a/relative/path"), "<path>../a/relative/path</path>");
        assertBothWays(Paths.get("/an/absolute/path"), "<path>/an/absolute/path</path>");

        String absolutePathName = Paths.get("target").toAbsolutePath().toString();
        if (File.separatorChar != '/') {
            absolutePathName = absolutePathName.replace(File.separatorChar, '/');
        }
        final URI uri = URI.create("file:" + absolutePathName);
        assertBothWays(Paths.get(uri), "<path>" + absolutePathName + "</path>");
    }

    public void testPathWithSpecialCharacters() {
        assertBothWays(Paths.get("with space"), "<path>with space</path>");
        assertBothWays(Paths.get("with+plus"), "<path>with+plus</path>");
        assertBothWays(Paths.get("with&ampersand"), "<path>with&amp;ampersand</path>");
    }

    public void testPathOfNonDefaultFileSystem() throws IOException {
        final Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        final URI uri = URI.create("jar:"
            + Paths.get("target/lib/proxytoys-0.2.1.jar").toAbsolutePath().toUri().toString());

        FileSystem zipfs = null;
        try {
            zipfs = FileSystems.newFileSystem(uri, env);
            final String entry = "/com/thoughtworks/proxy/kit/SimpleReference.class";
            final Path path = zipfs.getPath(entry);
            assertBothWays(path, "<path>" + uri.toString() + "!" + entry + "</path>");
        } finally {
            if (zipfs != null) {
                zipfs.close();
            }
        }
    }

    public void testPathIsImmutable() {
        Path[] array = new Path[2];
        array[0] = array[1] = Paths.get("same");
        assertBothWays(array, "" //
            + "<path-array>\n" //
            + "  <path>same</path>\n" //
            + "  <path>same</path>\n" //
            + "</path-array>");
    }
}
