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

package org.symphonyoss.symphony.clients.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.client.events.SymEvent;
import org.symphonyoss.client.exceptions.DataFeedException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.agent.api.DatafeedApi;
import org.symphonyoss.symphony.agent.api.FirehoseApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.Datafeed;
import org.symphonyoss.symphony.agent.model.V4Event;
import org.symphonyoss.symphony.agent.model.V5EventList;
import org.symphonyoss.symphony.agent.model.V5Firehose;
import org.symphonyoss.symphony.clients.DataFeedClient;
import org.symphonyoss.symphony.clients.FirehoseClient;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymFirehose;
import org.symphonyoss.symphony.clients.model.SymFirehoseRequest;

import javax.ws.rs.client.Client;
import java.util.List;


/**
 * Provides access to datafeed in order to stream all message events (messages) through blocking calls.
 *
 * @author Frank Tarsillo
 */
public class FirehoseClientImpl implements FirehoseClient {

    private final ApiClient apiClient;
    private final SymAuth symAuth;
    private Logger logger = LoggerFactory.getLogger(FirehoseClientImpl.class);


    /**
     * Init
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param config     Config containing the Agent URL used to access API
     *
     */

    public FirehoseClientImpl(SymAuth symAuth, SymphonyClientConfig config) {

        this(symAuth, config, null);
    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param config     Config containing the Agent URL used to access API
     * @param httpClient Custom HTTP client
     */

    public FirehoseClientImpl(SymAuth symAuth, SymphonyClientConfig config, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();

        if (httpClient != null)
            apiClient.setHttpClient(httpClient);

        apiClient.setBasePath(config.get(SymphonyClientConfigID.AGENT_URL));

    }


    @Override
    public SymFirehose createFirehose() throws DataFeedException {


        FirehoseApi firehoseApi = new FirehoseApi(apiClient);


        try {


            return SymFirehose.toSymFirehose(firehoseApi.v5FirehoseCreatePost(symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken()));


        } catch (ApiException e) {
            throw new DataFeedException("Could not start firehose..",
                    firehoseApi.getApiClient().getBasePath(), e.getCode(), e);
        } catch (RuntimeException ef) {
            throw new DataFeedException("Could not start firehose due to network issue..",
                    firehoseApi.getApiClient().getBasePath(), 500, ef);
        }
    }


    @Override
    public List<SymEvent> getEventsFromFirehose(SymFirehose symFirehose, SymFirehoseRequest symFirehoseRequest) throws DataFeedException {

        FirehoseApi firehoseApi = new FirehoseApi(apiClient);



        try {


            V5EventList v5EventList = firehoseApi.v5FirehoseIdReadPost(symFirehose.getId(), symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(),SymFirehoseRequest.toV5FirehoseRequest(symFirehoseRequest));

            return SymEvent.toSymEvent(v5EventList.getEvents());


        } catch (ApiException e) {
            throw new DataFeedException("Failed to retrieve messages from firehose...",
                    firehoseApi.getApiClient().getBasePath(), e.getCode(), e);
        } catch (RuntimeException ef) {
            throw new DataFeedException("Failed to retrieve messages due to network issue..",
                    firehoseApi.getApiClient().getBasePath(), 500, ef);
        }


    }



}
