/*
 * Copyright (c) 2014 - 2017 the original author or authors.
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
package com.github.ferstl.depgraph.dependency;

import org.junit.Test;

import static com.github.ferstl.depgraph.dependency.VersionAbbreviator.abbreviateVersion;
import static org.junit.Assert.assertEquals;

public class VersionAbbreviatorTest {

  @Test
  public void abbreviateRegularVersion() {
    assertEquals("1.0.0", abbreviateVersion("1.0.0"));
  }

  @Test
  public void abbreviateSnapshotVersion() {
    assertEquals("1.0.0-S.", abbreviateVersion("1.0.0-SNAPSHOT"));
  }
}
