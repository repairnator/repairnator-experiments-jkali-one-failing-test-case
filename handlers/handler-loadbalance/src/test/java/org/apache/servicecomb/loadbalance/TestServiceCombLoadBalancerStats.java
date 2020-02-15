/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.loadbalance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.servicecomb.core.Transport;
import org.apache.servicecomb.serviceregistry.api.registry.MicroserviceInstance;
import org.apache.servicecomb.serviceregistry.cache.CacheEndpoint;
import org.junit.Assert;
import org.junit.Test;

import mockit.Injectable;

public class TestServiceCombLoadBalancerStats {
  @Test
  public void testSimpleThread(@Injectable Transport transport) {
    long time = System.currentTimeMillis();
    MicroserviceInstance instance = new MicroserviceInstance();
    instance.setInstanceId("instance1");
    ServiceCombServer serviceCombServer = new ServiceCombServer(transport,
        new CacheEndpoint("rest://localhost:8080", instance));
    ServiceCombLoadBalancerStats.INSTANCE.markFailure(serviceCombServer);
    ServiceCombLoadBalancerStats.INSTANCE.markFailure(serviceCombServer);
    Assert.assertEquals(
        ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getCountinuousFailureCount(), 2);
    ServiceCombLoadBalancerStats.INSTANCE.markSuccess(serviceCombServer);
    Assert.assertEquals(
        ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getCountinuousFailureCount(), 0);
    ServiceCombLoadBalancerStats.INSTANCE.markSuccess(serviceCombServer);
    Assert
        .assertEquals(ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getTotalRequests(), 4);
    Assert.assertEquals(ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getFailedRate(), 50);
    Assert.assertEquals(ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getSuccessRate(), 50);
    Assert.assertTrue(
        ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getLastVisitTime() <= System
            .currentTimeMillis()
            && ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getLastVisitTime() >= time);
  }

  @Test
  public void testMiltiThread(@Injectable Transport transport) throws Exception {
    long time = System.currentTimeMillis();
    MicroserviceInstance instance = new MicroserviceInstance();
    instance.setInstanceId("instance2");
    ServiceCombServer serviceCombServer = new ServiceCombServer(transport,
        new CacheEndpoint("rest://localhost:8080", instance));

    CountDownLatch latch = new CountDownLatch(10);
    for (int i = 0; i < 10; i++) {
      new Thread() {
        public void run() {
          ServiceCombLoadBalancerStats.INSTANCE.markFailure(serviceCombServer);
          ServiceCombLoadBalancerStats.INSTANCE.markFailure(serviceCombServer);
          ServiceCombLoadBalancerStats.INSTANCE.markSuccess(serviceCombServer);
          ServiceCombLoadBalancerStats.INSTANCE.markSuccess(serviceCombServer);
          latch.countDown();
        }
      }.start();
    }
    latch.await(30, TimeUnit.SECONDS);
    Assert.assertEquals(ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getTotalRequests(),
        4 * 10);
    Assert.assertEquals(ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getFailedRate(), 50);
    Assert.assertEquals(ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getSuccessRate(), 50);
    Assert.assertTrue(
        ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getLastVisitTime() <= System
            .currentTimeMillis()
            && ServiceCombLoadBalancerStats.INSTANCE.getServiceCombServerStats(instance).getLastVisitTime() >= time);
  }
}
