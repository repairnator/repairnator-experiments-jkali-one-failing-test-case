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
package customhttpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.impl.CustomHttpClient;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.symphony.clients.AuthenticationClient;
import org.symphonyoss.symphony.clients.model.SymExtensionAppAuth;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.client.Client;
import java.util.HashSet;
import java.util.Set;


/**
 * Simple example of the ChatService.
 * <p>
 * It will send a message to a call.home.user and listen/create new Chat sessions.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dtruststore.file=
 * -Dtruststore.password=password
 * -Dsessionauth.url=https://(hostname)/sessionauth
 * -Dkeyauth.url=https://(hostname)/keyauth
 * -Duser.call.home=frank.tarsillo@markit.com
 * -Duser.cert.password=password
 * -Duser.cert.file=bot.user2.p12
 * -Duser.email=bot.user2@domain.com
 * -Dpod.url=https://(pod host)/pod
 * -Dagent.url=https://(agent server host)/agent
 * -Dreceiver.email=bot.user2@markit.com or bot user email
 *
 * @author  Frank Tarsillo
 */
//NOSONAR
public class CustomHttpClientExample implements ChatListener, ChatServiceListener {


    private final Logger logger = LoggerFactory.getLogger(CustomHttpClientExample.class);

    private SymphonyClient symClient;

    public CustomHttpClientExample() {


        init();


    }

    public static void main(String[] args) {

        new CustomHttpClientExample();

    }

    public void init() {


        try {

            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig(false);

            //Create an initialized client
            symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4);

            try {

                //You can also set specific properties on each
                Client podHttpClient = CustomHttpClient.getClient(
                        symphonyClientConfig.get(SymphonyClientConfigID.USER_CERT_FILE),
                        symphonyClientConfig.get(SymphonyClientConfigID.USER_CERT_PASSWORD),
                        symphonyClientConfig.get(SymphonyClientConfigID.TRUSTSTORE_FILE),
                        symphonyClientConfig.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));

                //You can also set specific properties on each
                Client agentHttpClient = CustomHttpClient.getClient(
                        symphonyClientConfig.get(SymphonyClientConfigID.USER_CERT_FILE),
                        symphonyClientConfig.get(SymphonyClientConfigID.USER_CERT_PASSWORD),
                        symphonyClientConfig.get(SymphonyClientConfigID.TRUSTSTORE_FILE),
                        symphonyClientConfig.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));



                //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
                //the same fqdn but different URLs.
                AuthenticationClient authClient = new AuthenticationClient(
                        symphonyClientConfig.get(SymphonyClientConfigID.SESSIONAUTH_URL),
                        symphonyClientConfig.get(SymphonyClientConfigID.KEYAUTH_URL),
                        podHttpClient);



                symClient.init(podHttpClient,agentHttpClient, symphonyClientConfig);

            } catch (AuthenticationException e) {
                logger.error("error", e);
            } catch (Exception e) {
                logger.error("General exception thrown", e);
            }



            //Will notify the bot of new Chat conversations.
            symClient.getChatService().addListener(this);

            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();

            //V4 will wrap the text in a PresentationMl div.
            aMessage.setMessageText("Hello master, I'm alive again....");


            //Creates a Chat session with that will receive the online message.
            Chat chat = new Chat();
            chat.setLocalUser(symClient.getLocalUser());
            Set<SymUser> remoteUsers = new HashSet<>();
            remoteUsers.add(symClient.getUsersClient().getUserFromEmail(symphonyClientConfig.get(SymphonyClientConfigID.RECEIVER_EMAIL)));
            chat.setRemoteUsers(remoteUsers);
            chat.addListener(this);


            //Add the chat to the chat service, in case the "master" continues the conversation.
            symClient.getChatService().addChat(chat);


            //Send a message to the master user.
            symClient.getMessageService().sendMessage(chat, aMessage);


            symClient.shutdown();

            logger.info("Finished");




        } catch (MessagesException | UsersClientException  e) {
            logger.error("error", e);
        }

    }



    //Chat sessions callback method.
    @Override
    public void onChatMessage(SymMessage message) {
        if (message == null)
            return;

        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                message.getMessage(),
                message.getMessageType());

        Chat chat = symClient.getChatService().getChatByStream(message.getStreamId());

        if(chat!=null)
            logger.debug("New message is related to chat with users: {}", remoteUsersString(chat.getRemoteUsers()));




    }

    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(this);

        logger.debug("New chat session detected on stream {} with {}", chat.getStream().getStreamId(), remoteUsersString(chat.getRemoteUsers()));


    }

    @Override
    public void onRemovedChat(Chat chat) {

    }

    private  String remoteUsersString(Set<SymUser> symUsers){

        String output = "";
        for(SymUser symUser: symUsers){
            output += "[" + symUser.getId() + ":" + symUser.getDisplayName() + "] ";

        }

        return output;
    }

}
