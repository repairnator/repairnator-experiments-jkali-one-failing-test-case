/*
 * Jenkins Plugin for SonarQube, open source software quality management tool.
 * mailto:contact AT sonarsource DOT com
 *
 * Jenkins Plugin for SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Jenkins Plugin for SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package hudson.plugins.sonar.configurationslicing;

import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.Project;
import hudson.plugins.sonar.SonarRunnerBuilder;

@Extension(optional = true)
public class SonarRunnerBuilderJdkSlicer extends UnorderedStringSlicer<Project<?, ?>> {

  public SonarRunnerBuilderJdkSlicer() {
    super(new SonarRunnerBuilderJdkSlicerSpec());
  }

  protected static class SonarRunnerBuilderJdkSlicerSpec extends AbstractSonarRunnerBuilderSlicerSpec {

    @Override
    public String getName() {
      return "SonarQube (Build Step) - JDK Slicer";
    }

    @Override
    public String getUrl() {
      return "sqRunnerBuilderJdk";
    }

    @Override
    protected String doGetValue(SonarRunnerBuilder builder) {
      return defaultValueIfBlank(builder.getJdk());
    }

    @Override
    protected void doSetValue(SonarRunnerBuilder builder, String value) {
      builder.setJdk(nullIfDefaultValue(value));
    }

    @Override
    protected String getDefaultValue() {
      return "(Inherit From Job)";
    }
  }
}
