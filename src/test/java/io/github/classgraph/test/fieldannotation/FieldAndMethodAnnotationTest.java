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
package io.github.classgraph.test.fieldannotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.test.external.ExternalAnnotation;

public class FieldAndMethodAnnotationTest {
    public int publicFieldWithAnnotation;

    @ExternalAnnotation
    private int privateFieldWithAnnotation;

    public int fieldWithoutAnnotation;

    @Test
    public void getNamesOfClassesWithFieldAnnotation() throws Exception {
        final List<String> testClasses = new ClassGraph()
                .whitelistPackages(FieldAndMethodAnnotationTest.class.getPackage().getName()).enableFieldInfo()
                .enableAnnotationInfo().scan().getClassesWithFieldAnnotation(ExternalAnnotation.class.getName())
                .getNames();
        assertThat(testClasses).isEmpty();
    }

    @Test
    public void getNamesOfClassesWithFieldAnnotationIgnoringVisibility() throws Exception {
        final List<String> testClasses = new ClassGraph()
                .whitelistPackages(FieldAndMethodAnnotationTest.class.getPackage().getName()).enableFieldInfo()
                .ignoreFieldVisibility().enableAnnotationInfo().scan()
                .getClassesWithFieldAnnotation(ExternalAnnotation.class.getName()).getNames();
        assertThat(testClasses).containsOnly(FieldAndMethodAnnotationTest.class.getName());
    }

    @Test
    @ExternalAnnotation
    public void getNamesOfClassesWithMethodAnnotation() throws Exception {
        final List<String> testClasses = new ClassGraph()
                .whitelistPackages(FieldAndMethodAnnotationTest.class.getPackage().getName()).enableMethodInfo()
                .enableAnnotationInfo().scan().getClassesWithMethodAnnotation(ExternalAnnotation.class.getName())
                .getNames();
        assertThat(testClasses).containsOnly(FieldAndMethodAnnotationTest.class.getName());
    }
}
