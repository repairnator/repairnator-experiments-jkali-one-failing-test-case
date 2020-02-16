/*
 *
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.symphonyoss.client;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.model.CacheType;
import org.symphonyoss.client.services.SymUserCache;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.concurrent.TimeUnit;

/**
 * This test will simulate and verify cache performance for user requests.
 *
 * @author Frank Tarsillo
 */
public class SymUserCacheIT {

    private static SymphonyClient sjcTestClient;
    private static final Logger logger = LoggerFactory.getLogger(SymUserCacheIT.class);


    private final static String MP_USER_EMAIL = System.getProperty("mp.user.email","Frank.Tarsillo@ihsmarkit.com");

    private static boolean responded;

    private final String botEmail = System.getProperty("bot.user.email", "sjc.testbot");

    @BeforeClass
    public static void setupBeforeClass() throws Exception {


        try {

            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();
            symphonyClientConfig.set(SymphonyClientConfigID.USER_CERT_FILE,System.getProperty("sender.user.cert.file"));
            symphonyClientConfig.set(SymphonyClientConfigID.USER_CERT_PASSWORD,System.getProperty("sender.user.cert.password"));
            symphonyClientConfig.set(SymphonyClientConfigID.TRUSTSTORE_FILE,System.getProperty("truststore.file"));
            symphonyClientConfig.set(SymphonyClientConfigID.TRUSTSTORE_PASSWORD,System.getProperty("truststore.password"));
            symphonyClientConfig.set(SymphonyClientConfigID.USER_EMAIL, System.getProperty("sender.user.email", "sjc.testclient"));


            sjcTestClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.BASIC, symphonyClientConfig);


        } catch (Exception e) {

            logger.error("Could not init symphony test client", e);


        }

        Assume.assumeTrue(sjcTestClient != null);


    }

    @Before
    public void setupBefore() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        if (sjcTestClient != null)
            sjcTestClient.shutdown();


    }


    @Test
    public void verifyUserCache() throws Exception {
        //init it.
        try {
            ((SymUserCache) sjcTestClient.getCache(CacheType.USER)).getUserById( (long) 1000000);
        } catch (Exception e) {
            logger.debug("Good thing...");
        }


        long start = System.currentTimeMillis();
        SymUser symUser = ((SymUserCache) sjcTestClient.getCache(CacheType.USER)).getUserByEmail( MP_USER_EMAIL);
        logger.info("Lookup {} took: {}", MP_USER_EMAIL, System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        ((SymUserCache) sjcTestClient.getCache(CacheType.USER)).getUserByEmail( MP_USER_EMAIL);
        logger.info("Lookup {} took: {}", MP_USER_EMAIL, System.currentTimeMillis() - start);


        start = System.currentTimeMillis();
        ((SymUserCache) sjcTestClient.getCache(CacheType.USER)).getUserById( symUser.getId());
        logger.info("Lookup {} took: {}", symUser.getId(), System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        ((SymUserCache) sjcTestClient.getCache(CacheType.USER)).getUserByName( symUser.getUsername());
        logger.info("Lookup {} took: {}", symUser.getDisplayName(), System.currentTimeMillis() - start);


    }


    public void pause() {

        //Can use properties to override default time wait
        try {

            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e1) {
            logger.error("Interrupt.. ", e1);
            Thread.currentThread().interrupt();
        }

    }

}