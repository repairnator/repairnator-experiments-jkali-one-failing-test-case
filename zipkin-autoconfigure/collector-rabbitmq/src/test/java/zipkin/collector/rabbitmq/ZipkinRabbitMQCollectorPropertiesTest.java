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
package zipkin.collector.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import org.junit.Test;
import zipkin.autoconfigure.collector.rabbitmq.ZipkinRabbitMQCollectorProperties;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipkinRabbitMQCollectorPropertiesTest {

  @Test
  public void uriProperlyParsedAndIgnoresOtherProperties_whenUriSet() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
    ZipkinRabbitMQCollectorProperties properties = new ZipkinRabbitMQCollectorProperties();
    properties.setUri(URI.create("amqp://admin:admin@localhost:5678/myv"));
    properties.setAddresses(Collections.singletonList("will_not^work!"));
    properties.setUsername("bob");
    properties.setPassword("letmein");
    properties.setVirtualHost("drwho");

    ConnectionFactory connFactory = properties.toBuilder().connectionFactory;
    assertThat(connFactory.getHost()).isEqualTo("localhost");
    assertThat(connFactory.getPort()).isEqualTo(5678);
    assertThat(connFactory.getUsername()).isEqualTo("admin");
    assertThat(connFactory.getPassword()).isEqualTo("admin");
    assertThat(connFactory.getVirtualHost()).isEqualTo("myv");
  }
}
