package org.symphonyoss.symphony.clients;

import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.symphony.pod.model.MembershipList;

/**
 * Supports adding/removing members from a room.  Also provides members lookup for a given stream
 *
 * @author  Frank Tarsillo
 */
public interface RoomMembershipClient {

    /**
     * Provides room membership
     * @param roomStreamId
     *          - stream-id of the chat room you want to add the member to
     * @return
     *        {@link MembershipList}
     * @throws SymException Exceptions from API calls into Symphony
     */
    MembershipList getRoomMembership(String roomStreamId) throws SymException;
    
    /**
     * Call this method to add a member to a chat room. Pass in two parameters - chat-room stream-id and user-id
     * 
     * @param roomStreamId
     *            - stream-id of the chat room you want to add the member to
     * @param userId
     *            userId for the user in Symphony
     * @throws SymException
     *             throws an {@link org.symphonyoss.symphony.pod.invoker.ApiException} if there were any issues while invoking the endpoint,
     *             {@link IllegalArgumentException} if the arguments were wrong, {@link IllegalStateException} if the
     *             session-token is null
     */
    @SuppressWarnings("unused")
    void addMemberToRoom(String roomStreamId, long userId) throws SymException;


    /**
     * Call this method to remove a member from a chat room. Pass in two parameters - chat-room stream-id and user-id
     *
     * @param roomStreamId - stream-id of the chat room you want to add the member to
     * @param userId       userId for the user in Symphony
     * @throws SymException throws an {@link org.symphonyoss.symphony.pod.invoker.ApiException} if there were any issues while invoking the endpoint,
     *                   {@link IllegalArgumentException} if the arguments were wrong, {@link IllegalStateException} if the
     *                   session-token is null
     */
    @SuppressWarnings("unused")
    void removeMemberFromRoom(String roomStreamId, long userId) throws SymException;
}
