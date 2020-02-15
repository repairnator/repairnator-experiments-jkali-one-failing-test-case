package net.whydah.identity.user.resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.identity.user.InvalidRoleModificationException;
import net.whydah.identity.user.NonExistentRoleException;
import net.whydah.identity.user.UserAggregateService;
import net.whydah.identity.user.identity.InvalidUserIdentityFieldException;
import net.whydah.identity.user.identity.LDAPUserIdentity;
import net.whydah.identity.user.identity.UserIdentityService;
import net.whydah.sso.user.mappers.UserRoleMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.List;

/**
 * Administration of users and their data.
 */
@Component
@Path("/{applicationtokenid}/{userTokenId}/user")
public class UserResource {
    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserIdentityService userIdentityService;
    private final UserAggregateService userAggregateService;
    private final ObjectMapper mapper;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserResource(UserIdentityService userIdentityService, UserAggregateService userAggregateService, ObjectMapper mapper) {
        this.userIdentityService = userIdentityService;
        this.userAggregateService = userAggregateService;
        this.mapper = mapper;
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        log.trace("Started: UserResource");
    }

    /**
     * Expectations to input:
     * no UID
     * no password
     * <p/>
     * Output:
     * uid is included
     * no password
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserIdentity(String userIdentityJson) {
        log.debug("addUserIdentity, userIdentityJson={}", userIdentityJson);

        UserIdentity representation;
        try {
            representation = mapper.readValue(userIdentityJson, UserIdentity.class);
        } catch (IOException e) {
            String msg = "addUserIdentity, invalid json";
            log.info(msg + ". userIdentityJson={}", userIdentityJson, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }

        UserIdentity userIdentity;
        try {
            userIdentity = userIdentityService.addUserIdentityWithGeneratedPassword(representation);
        } catch (IllegalStateException conflictException) {
            Response response = Response.status(Response.Status.CONFLICT).entity(conflictException.getMessage()).build();
            log.info("addUserIdentity returned {} {} because {}. \njson {}",
                    response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase(), conflictException.getMessage(), userIdentityJson);
            return response;
        } catch (IllegalArgumentException|InvalidUserIdentityFieldException badRequestException) {
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(badRequestException.getMessage()).build();
            log.info("addUserIdentity returned {} {} because {}. \njson {}",
                    response.getStatusInfo().getStatusCode(), response.getStatusInfo().getReasonPhrase(), badRequestException.getMessage(), userIdentityJson);
            return response;
        } catch (RuntimeException e) {
            log.error("addUserIdentity-RuntimeExeption ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }


        try {
            String newUserAsJson;
            newUserAsJson = mapper.writeValueAsString(userIdentity);
            //TODO Ensure password is not returned. Expect UserAdminService to trigger resetPassword.
            return Response.status(Response.Status.CREATED).entity(newUserAsJson).build();
        } catch (IOException e) {
            log.error("Error converting to json. {}", userIdentity.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserIdentity(@PathParam("uid") String uid) {
        log.trace("getUserIdentity for uid={}", uid);

        LDAPUserIdentity userIdentity;
        try {
            userIdentity = userIdentityService.getUserIdentityForUid(uid);
            log.trace("getUserIdentity for uid={} found user={}", uid, (userIdentity != null ? userIdentity.toString() : "null"));
        } catch (NamingException e) {
            throw new RuntimeException("getUserIdentityForUid, uid=" + uid, e);
        }
        if (userIdentity == null) {
            log.trace("getUserIdentityForUid could not find user with uid={}", uid);
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        String json;
        try {
            json = mapper.writeValueAsString(userIdentity);
        } catch (IOException e) {
            log.error("Error converting to json. {}", userIdentity.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(json).build();
    }

    @PUT
    @Path("/{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserIdentity(@PathParam("uid") String uid, String userIdentityJson) {
        log.trace("updateUserIdentity: uid={}, userIdentityJson={}", uid, userIdentityJson);

        LDAPUserIdentity userIdentity;
        try {
            userIdentity = mapper.readValue(userIdentityJson, LDAPUserIdentity.class);
        } catch (IOException e) {
            log.error("updateUserIdentity failed for uid={}, invalid json. userIdentityJson={}", uid, userIdentityJson, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            userIdentityService.updateUserIdentity(uid, userIdentity);
            try {
                String json = mapper.writeValueAsString(userIdentity);
                return Response.ok(json).build();
            } catch (IOException e) {
                log.error("Error converting to json. {}", userIdentity.toString(), e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } catch (InvalidUserIdentityFieldException iuife) {
            log.warn("updateUserIdentity returned {} because {}.", Response.Status.BAD_REQUEST.toString(), iuife.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(iuife.getMessage()).build();
        } catch (IllegalArgumentException iae) {
            log.info("updateUserIdentity: Invalid json={}", userIdentityJson, iae);
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid json").build();
        } catch (RuntimeException e) {
            log.error("updateUserIdentity: RuntimeError json={}", userIdentityJson, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{uid}")
    public Response deleteUserIdentityAndRoles(@PathParam("uid") String uid) {
        log.debug("deleteUserIdentityAndRoles: uid={}", uid);

        try {
            userAggregateService.deleteUserAggregateByUid(uid);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (IllegalArgumentException iae) {
            log.error("deleteUserIdentity failed username={}", uid + ". " + iae.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"user not found\"}'").build();
        } catch (RuntimeException e) {
            log.error("", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }




    // ROLES


    @POST
    @Path("/{uid}/role/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRole(@PathParam("uid") String uid, String roleJson) {
        log.trace("addRole for uid={}, roleJson={}", uid, roleJson);

        LDAPUserIdentity user;
        String msg = "addRole failed. No user with uid=" + uid;
        try {
            user = userIdentityService.getUserIdentityForUid(uid);
        } catch (NamingException e) {
            log.info(msg, e);
            return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        }
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        }

        UserApplicationRoleEntry request;
        try {
            request = mapper.readValue(roleJson, UserApplicationRoleEntry.class);
        } catch (IOException e) {
            log.error("addRole, invalid json. roleJson={}", roleJson, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            UserApplicationRoleEntry updatedRole = userAggregateService.addUserApplicationRoleEntry(uid, request);
            String json = UserRoleMapper.toJson(updatedRole);
            return Response.status(Response.Status.CREATED).entity(json).build();
        } catch (WebApplicationException ce) {
            log.error("addRole-Conflict. {}", roleJson, ce);
            //return Response.status(Response.Status.CONFLICT).build();
            return ce.getResponse();
        } catch (RuntimeException e) {
            log.error("addRole-RuntimeException. {}", roleJson, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{uid}/roles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoles(@PathParam("uid") String uid) {
        log.trace("getRoles, uid={}", uid);

        List<UserApplicationRoleEntry> roles = userAggregateService.getUserApplicationRoleEntries(uid);

        String json = UserRoleMapper.toJson(roles);
        return Response.ok(json).build();
    }

    @GET
    @Path("/{uid}/role/{roleid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRole(@PathParam("uid") String uid, @PathParam("roleid") String roleid) {
        log.trace("getRole, uid={}, roleid={}", uid, roleid);

        UserApplicationRoleEntry role = userAggregateService.getUserApplicationRoleEntry(uid, roleid);
        if (role == null) {
            log.trace("getRole could not find role with roleid={}", roleid);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String json = UserRoleMapper.toJson(role);
        return Response.ok(json).build();
    }

    @PUT
    @Path("/{uid}/role/{roleid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRole(@PathParam("uid") String uid, @PathParam("roleid") String roleid, String roleJson) {
        log.trace("updateRole, uid={}, roleid={}", uid, roleid);

        UserApplicationRoleEntry role = UserRoleMapper.fromJson(roleJson);

        try {

            UserApplicationRoleEntry updatedRole = userAggregateService.updateRole(uid, roleid, role);
            String json= UserRoleMapper.toJson(updatedRole);
            return Response.ok(json).build();
        } catch (NonExistentRoleException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (InvalidRoleModificationException e) {
            return Response.status(Response.Status.fromStatusCode(422)).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            log.error("updateRole-RuntimeException. {}", roleJson, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{uid}/role/{roleid}")
    public Response deleteRole(@PathParam("uid") String uid, @PathParam("roleid") String roleid) {
        log.trace("deleteRoleByRoleID, uid={}, roleid={}", uid, roleid);

        try {
            userAggregateService.deleteRole(uid, roleid);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (RuntimeException e) {
            log.error("deleteRoleByRoleID-RuntimeException. roleId {}", roleid, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
