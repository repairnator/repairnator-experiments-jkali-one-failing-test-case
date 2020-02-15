/*
 * This file is part of ClassGraph.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/lukehutch/fast-classpath-scanner
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.classgraph.issues.issue166;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.junit.Test;

import io.github.classgraph.ClassGraph;

public class Issue166Test {
    @Test
    public void issue166Test() {
        final URL jarURL = Issue166Test.class.getClassLoader().getResource("issue166-jar-without-extension");
        assertThat(new ClassGraph().overrideClasspath(jarURL).scan().getAllResources().getPaths())
                .containsOnly("Issue166.txt");
    }

    @Test
    public void testNonJarFileOnClasspath() {
        final URL nonJarURL = Issue166Test.class.getClassLoader().getResource("file-content-test.txt");
        assertThat(new ClassGraph().overrideClasspath(nonJarURL).scan().getAllResources().getPaths()).isEmpty();
    }

    @Test
    public void testNonExistentJarFileOnClasspath() {
        final URL nonJarURL = Issue166Test.class.getClassLoader().getResource("file-content-test.txt");
        final String nonExistentURL = nonJarURL.toString() + "-file-that-does-not-exist";
        assertThat(new ClassGraph().overrideClasspath(nonExistentURL).scan().getAllResources().getPaths())
                .isEmpty();
    }
}
