/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.autoconfigure.ui;

import java.io.IOException;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.internal.matchers.ThrowableCauseMatcher.hasCause;
import static org.springframework.boot.test.util.EnvironmentTestUtils.addEnvironment;

public class ZipkinUiAutoConfigurationTest {

  AnnotationConfigApplicationContext context;

  @After
  public void close() {
    if (context != null) {
      context.close();
    }
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void indexHtmlFromClasspath() {
    context = createContext();

    assertThat(context.getBean(ZipkinUiAutoConfiguration.class).indexHtml)
      .isNotNull();
  }

  @Test
  public void indexContentType() throws IOException {
    context = createContext();
    assertThat(
      context.getBean(ZipkinUiAutoConfiguration.class).serveIndex().getHeaders().getContentType())
      .isEqualTo(MediaType.TEXT_HTML);
  }

  @Test
  public void invalidIndexHtml() throws IOException {
    // I failed to make Jsoup barf, even on nonsense like: "<head wait no I changed my mind this HTML is totally invalid <<<<<<<<<<<"
    // So let's just run with a case where the file doesn't exist
    context = createContextWithOverridenProperty("zipkin.ui.basepath:/foo/bar");
    ZipkinUiAutoConfiguration ui = context.getBean(ZipkinUiAutoConfiguration.class);
    ui.indexHtml = new ClassPathResource("does-not-exist.html");

    thrown.expect(BeanCreationException.class);
    // There's a BeanInstantiationException nested in between BeanCreationException and IOException,
    // so we go one level deeper about causes. There's no `expectRootCause`.
    thrown.expectCause(hasCause(isA(IOException.class)));
    ui.serveIndex();
  }

  @Test
  public void canOverridesProperty_defaultLookback() {
    context = createContextWithOverridenProperty("zipkin.ui.defaultLookback:100");

    assertThat(context.getBean(ZipkinUiProperties.class).getDefaultLookback())
      .isEqualTo(100);
  }

  @Test
  public void canOverrideProperty_logsUrl() {
    final String url = "http://mycompany.com/kibana";
    context = createContextWithOverridenProperty("zipkin.ui.logs-url:" + url);

    assertThat(context.getBean(ZipkinUiProperties.class).getLogsUrl()).isEqualTo(url);
  }

  @Test
  public void logsUrlIsNullIfOverridenByEmpty() {
    context = createContextWithOverridenProperty("zipkin.ui.logs-url:");

    assertThat(context.getBean(ZipkinUiProperties.class).getLogsUrl()).isNull();
  }

  @Test
  public void logsUrlIsNullByDefault() {
    context = createContext();

    assertThat(context.getBean(ZipkinUiProperties.class).getLogsUrl()).isNull();
  }

  @Test(expected = NoSuchBeanDefinitionException.class)
  public void canOverridesProperty_disable() {
    context = createContextWithOverridenProperty("zipkin.ui.enabled:false");

    context.getBean(ZipkinUiProperties.class);
  }

  @Test
  public void canOverridesProperty_searchEnabled() {
    context = createContextWithOverridenProperty("zipkin.ui.search-enabled:false");

    assertThat(context.getBean(ZipkinUiProperties.class).isSearchEnabled()).isFalse();
  }

  @Test
  public void canOverrideProperty_dependencyLowErrorRate() {
    context = createContextWithOverridenProperty("zipkin.ui.dependency.low-error-rate:0.1");

    assertThat(context.getBean(ZipkinUiProperties.class).getDependency().getLowErrorRate())
      .isEqualTo(0.1f);
  }

  @Test
  public void canOverrideProperty_dependencyHighErrorRate() {
    context = createContextWithOverridenProperty("zipkin.ui.dependency.high-error-rate:0.1");

    assertThat(context.getBean(ZipkinUiProperties.class).getDependency().getHighErrorRate())
      .isEqualTo(0.1f);
  }

  @Test
  public void defaultBaseUrl_doesNotChangeResource() throws IOException {
    context = createContext();
    Resource index =
      (Resource) context.getBean(ZipkinUiAutoConfiguration.class).serveIndex().getBody();

    assertThat(index.getInputStream())
      .hasSameContentAs(getClass().getResourceAsStream("/zipkin-ui/index.html"));
  }

  @Test
  public void canOverideProperty_basePath() throws IOException {
    context = createContextWithOverridenProperty("zipkin.ui.basepath:/foo/bar");

    assertThat(context.getBean(ZipkinUiAutoConfiguration.class).serveIndex().getBody().toString())
      .contains("<base href=\"/foo/bar/\">");
  }

  @Test
  public void canOverideProperty_specialCaseRoot() throws IOException {
    context = createContextWithOverridenProperty("zipkin.ui.basepath:/");

    assertThat(context.getBean(ZipkinUiAutoConfiguration.class).serveIndex().getBody().toString())
      .contains("<base href=\"/\">");
  }

  private static AnnotationConfigApplicationContext createContext() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.register(PropertyPlaceholderAutoConfiguration.class, ZipkinUiAutoConfiguration.class);
    context.refresh();
    return context;
  }

  private static AnnotationConfigApplicationContext createContextWithOverridenProperty(
    String pair) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    addEnvironment(context, pair);
    context.register(PropertyPlaceholderAutoConfiguration.class, ZipkinUiAutoConfiguration.class);
    context.refresh();
    return context;
  }
}
