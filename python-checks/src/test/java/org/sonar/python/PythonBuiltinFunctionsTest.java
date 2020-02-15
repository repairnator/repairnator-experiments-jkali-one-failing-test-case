/*
 * SonarQube Python Plugin
 * Copyright (C) 2011-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.python;

import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PythonBuiltinFunctionsTest {

  @Test
  public void test() throws Exception {
    assertThat(PythonBuiltinFunctions.contains("abs")).isTrue();
    assertThat(PythonBuiltinFunctions.contains("ascii")).isTrue(); // python3
    assertThat(PythonBuiltinFunctions.contains("basestring")).isTrue(); // python2
    assertThat(PythonBuiltinFunctions.contains("xxx")).isFalse();
  }

  @Test(expected = IllegalStateException.class)
  public void unreadable_inputstream() throws Exception {
    PythonBuiltinFunctions.loadBuiltinNames(new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("Can't read!");
      }
    });
  }

}
