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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.symphonyoss.util;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;
import javax.ws.rs.client.ClientBuilder;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Frank Tarsillo on 6/19/2016.
 */
public class TestFactory {
    private static final SymphonyClient symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC);
    private static final SymAuth symAuth = new SymAuth();

    static {

        Token token = new Token();
        token.setName("TOKEN_NAME");
        token.setToken("TOKEN_VALUE");
        symAuth.setKeyToken(token);
        symAuth.setSessionToken(token);
        symClient.setSymAuth(symAuth);
        SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();

        symphonyClientConfig.set(SymphonyClientConfigID.POD_URL,"POD_URL");
        symphonyClientConfig.set(SymphonyClientConfigID.AGENT_URL,"AGENT_URL");
        symphonyClientConfig.set(SymphonyClientConfigID.USER_EMAIL,"USER_EMAIL");
        symphonyClientConfig.set(SymphonyClientConfigID.TRUSTSTORE_FILE,"TRUSTSTORE_FILE");
        symphonyClientConfig.set(SymphonyClientConfigID.TRUSTSTORE_PASSWORD,"TRUSTSTORE_PASSWORD");
        symphonyClientConfig.set(SymphonyClientConfigID.USER_CERT_PASSWORD,"USER_CERT_PASSWORD");
        symphonyClientConfig.set(SymphonyClientConfigID.USER_CERT_FILE, "USER_CERT_FILE");
        symClient.setConfig(symphonyClientConfig);

        symClient.setAgentHttpClient(ClientBuilder.newBuilder().build());
        symClient.setPodHttpClient(ClientBuilder.newBuilder().build());

    }

    public static SymphonyClient getSymClient() {
        return symClient;
    }

    public static SymAuth getSymAuth() {
        return symAuth;
    }

    public static Chat getChat(){

        Chat chat = new Chat();
        chat.setLocalUser(symClient.getLocalUser());
        Set<SymUser> remoteUsers = new HashSet<>();
        SymUser aUser = new SymUser();
        aUser.setId((long)1234567890);
        aUser.setEmailAddress("test.user@domain.com");
        remoteUsers.add(aUser);

        chat.setRemoteUsers(remoteUsers);

        SymStream stream = new SymStream();
        stream.setStreamId("ABCDEFGHIJKLMNOPQRSTUV");
        chat.setStream(stream);

        return chat;

    }
}
