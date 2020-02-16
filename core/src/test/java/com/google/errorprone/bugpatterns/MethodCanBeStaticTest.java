/*
 * Copyright 2016 The Error Prone Authors.
 *
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

package com.google.errorprone.bugpatterns;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** {@link MethodCanBeStatic}Test */
@RunWith(JUnit4.class)
public class MethodCanBeStaticTest {
  private final CompilationTestHelper testHelper =
      CompilationTestHelper.newInstance(MethodCanBeStatic.class, getClass());

  @Test
  public void positive() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test {",
            "  // BUG: Diagnostic contains: private static int add(",
            "  private int add(int x, int y) {",
            "    return x + y;",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negative() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test {",
            "  int base = 0;",
            "  private int f(int x, int y) {",
            "    return base++ + x + y;",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveTypeParameter() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test<T> {",
            "  // BUG: Diagnostic contains: private static <T> T f(",
            "  private <T> T f(int x, int y) {",
            "    return null;",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeTypeParameter() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test<T> {",
            "  private T f(int x, int y) {",
            "    return null;",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeConstructor() {
    testHelper
        .addSourceLines(
            "Test.java", //
            "class Test {",
            "  Test() {",
            "}",
            "}")
        .doTest();
  }

  @Test
  public void negativePublic() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test {",
            "  public String toString() {",
            "    return \"\";",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeSuppressed() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test {",
            "  @SuppressWarnings(\"MethodCanBeStatic\")",
            "  private String f() {",
            "    return \"\";",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeSuppressedAlt() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test {",
            "  @SuppressWarnings(\"static-method\")",
            "  private String f() {",
            "    return \"\";",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeOverride() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test {",
            "  @Override",
            "  public String toString() {",
            "    return \"\";",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeMethodCall() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test {",
            "  int x;",
            "  int f() {",
            "    return x++;",
            "  }",
            "  private int g() {",
            "    return f();",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void nativeMethod() {
    testHelper
        .addSourceLines(
            "Test.java", //
            "class Test {",
            "  private native int f();",
            "}")
        .doTest();
  }

  @Test
  public void innerClass() {
    testHelper
        .addSourceLines(
            "Test.java", //
            "class Test {",
            "  class Inner {",
            "    private int incr(int x) {",
            "      return x + 1;",
            "    }",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeEnum() {
    testHelper
        .addSourceLines(
            "Test.java", //
            "enum Test {",
            "  VALUE {",
            "    private void foo() {}",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeAnonymous() {
    testHelper
        .addSourceLines(
            "Test.java", //
            "class Test {",
            "  static void foo() {",
            "    new Object() {",
            "      private void foo() {}",
            "    };",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeLocal() {
    testHelper
        .addSourceLines(
            "Test.java", //
            "class Test {",
            "  static void foo() {",
            "    class Local {",
            "      private void foo() {}",
            "    }",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negative_referencesTypeVariable() {
    testHelper
        .addSourceLines(
            "Test.java",
            "class Test<T> {",
            "  private int add(int x, int y) {",
            "    T t = null;",
            "    return x + y;",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void serialization() {
    testHelper
        .addSourceLines(
            "Test.java", //
            "import java.io.ObjectInputStream;",
            "import java.io.ObjectOutputStream;",
            "import java.io.ObjectStreamException;",
            "import java.io.IOException;",
            "import java.io.Serializable;",
            "class Test implements Serializable {",
            "  private void readObject(",
            "    ObjectInputStream stream) throws IOException, ClassNotFoundException {}",
            "  private void writeObject(",
            "    ObjectOutputStream stream) throws IOException {}",
            "  private void readObjectNoData() throws ObjectStreamException {}",
            "  private Object readResolve() { return null; }",
            "  private Object writeReplace() { return null; }",
            "}")
        .doTest();
  }

  @Test
  public void methodReference() throws Exception {
    BugCheckerRefactoringTestHelper.newInstance(new MethodCanBeStatic(), getClass())
        .addInputLines(
            "in/Test.java",
            "import java.util.function.ToIntBiFunction;",
            "class Test {",
            "  private int add(int x, int y) {",
            "    return x + y;",
            "  }",
            "  ToIntBiFunction<Integer, Integer> f = this::add;",
            "  ToIntBiFunction<Integer, Integer> g = (x, y) -> this.add(x, y);",
            "}")
        .addOutputLines(
            "out/Test.java",
            "import java.util.function.ToIntBiFunction;",
            "class Test {",
            "  private static int add(int x, int y) {",
            "    return x + y;",
            "  }",
            "  ToIntBiFunction<Integer, Integer> f = Test::add;",
            "  ToIntBiFunction<Integer, Integer> g = (x, y) -> Test.add(x, y);",
            "}")
        .doTest();
  }
}
