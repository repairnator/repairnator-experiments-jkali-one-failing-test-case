package net.whydah.identity.user.resource;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.identity.Main;
import net.whydah.identity.config.ApplicationMode;
import net.whydah.identity.dataimport.DatabaseMigrationHelper;
import net.whydah.identity.dataimport.IamDataImporter;
import net.whydah.identity.security.SecurityFilter;
import net.whydah.identity.user.search.LuceneUserIndexer;
import net.whydah.identity.util.FileUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.Resource;
import org.glassfish.jersey.client.ClientResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class UserAdminTest {
    private static Client client = ClientBuilder.newClient();
    private static WebTarget baseResource;
    private static WebTarget logonResource;
    private static Main main;
    String luceneUsersDir;
    BasicDataSource dataSource;
    
    @Before
    public void init() throws Exception {
        FileUtils.deleteDirectory(new File("target/data/lucene"));
        FileUtils.deleteDirectory(new File("data/lucene"));
        //ApplicationMode.setCIMode();
        ApplicationMode.setTags(ApplicationMode.CI_MODE, ApplicationMode.NO_SECURITY_FILTER);
        SecurityFilter.setCIFlag(true);
        final ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createPropertiesStore()
                .addResource(Resource.create("classpath:useridentitybackend.properties"))
                .addResource(Resource.create("classpath:useridentitybackend-test.properties"))
                .done()
                .getConfiguration();

        String ldapPath = configuration.evaluateToString("ldap.embedded.directory");
        luceneUsersDir = configuration.evaluateToString("lucene.usersdirectory");
        FileUtils.deleteDirectories(ldapPath, "target/bootstrapdata/", luceneUsersDir);
        
        main = new Main(configuration.evaluateToInt("service.port"));
        main.startEmbeddedDS(configuration.asMap());

        dataSource = initBasicDataSource(configuration);
        DatabaseMigrationHelper dbHelper =  new DatabaseMigrationHelper(dataSource);
        dbHelper.cleanDatabase();
        dbHelper.upgradeDatabase();

        new IamDataImporter(dataSource, configuration).importIamData();

        //String requiredRoleName = AppConfig.appConfig.getProperty("useradmin.requiredrolename");
        //main.startHttpServer(requiredRoleName);   //TODO
        main.startJetty();

        URI baseUri = UriBuilder.fromUri("http://localhost/uib/uib/useradmin/").port(main.getPort()).build();
        URI logonUri = UriBuilder.fromUri("http://localhost/uib/").port(main.getPort()).build();
        //String authentication = "usrtk1";
        baseResource = client.target(baseUri)/*.path(authentication + '/')*/;
        logonResource = client.target(logonUri);
    }

    private static BasicDataSource initBasicDataSource(ConstrettoConfiguration configuration) {
        String jdbcdriver = configuration.evaluateToString("roledb.jdbc.driver");
        String jdbcurl = configuration.evaluateToString("roledb.jdbc.url");
        String roledbuser = configuration.evaluateToString("roledb.jdbc.user");
        String roledbpasswd = configuration.evaluateToString("roledb.jdbc.password");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(jdbcdriver);
        dataSource.setUrl(jdbcurl);
        dataSource.setUsername(roledbuser);
        dataSource.setPassword(roledbpasswd);
        return dataSource;
    }

    @After
    public void teardown() throws InterruptedException {
        main.stop();
        
        try {
        	if(!dataSource.isClosed()) {
        		dataSource.close();
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        FileUtils.deleteDirectory(new File("target/data/lucene"));
        FileUtils.deleteDirectory(new File("data/lucene"));
        FileUtils.deleteDirectory(new File(luceneUsersDir));
    }

    @Test
    public void testFind() {
    	System.out.println("==================test Find()======================");
        WebTarget webResource = baseResource.path("users/find/useradmin");
        Response response = webResource.request().get(Response.class);
        String entity = response.readEntity(String.class);
        assertTrue(entity.contains("\"firstName\":\"User\""));
    }

    @Test
    public void getUser() {
    	System.out.println("==================getUser()======================");
        WebTarget webResource = baseResource.path("user/useradmin");
        String s = webResource.request().get(String.class);
        System.out.println("===>" + s);
        assertTrue(s.contains("\"firstName\":\"User"));
    }

    @Test
    public void getNonExistingUser() {
    	System.out.println("==================getNonExistingUser()======================");
        FileUtils.deleteDirectory(new File("target/data/lucene"));
        FileUtils.deleteDirectory(new File("data/lucene"));
        WebTarget webResource = baseResource.path("user/");
        webResource.path("useradmin").request().get(String.class); // verify that path works with existing user
        try {
            String s = webResource.path("pettersmart@gmail.com").request().get(String.class);
            fail("Expected 404, got " + s);
        } catch (NotFoundException e) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void modifyUser() {
    	System.out.println("==================modifyUser()======================");
        String uid = doAddUser("1231312", "siqula", "Hoytahl", "Goffse", "siqula@midget.orj", "12121212");

        String s = baseResource.path("user/" + uid).request().get(String.class);
        assertTrue(s.contains("siqula@midget.orj"));
        assertTrue(s.contains("Hoytahl"));
        assertTrue(s.contains("12121212"));

        String updateduserjson = "{\n" +
                " \"uid\":\"" + uid + "\",\n" +
                " \"personRef\":\"1231312\",\n" +
                " \"username\":\"siqula\",\n" +
                " \"firstName\":\"Harald\",\n" +
                " \"lastName\":\"Goffse\",\n" +
                " \"email\":\"siqula@midget.orj\",\n" +
                " \"cellPhone\":\"35353535\"\n" +
                "}";


        //baseResource.path("user/" + uid).type("application/json").put(String.class, updateduserjson);
        baseResource.path("user/" + uid).request().put(Entity.json(updateduserjson));


        s = baseResource.path("user/" + uid).request().get(String.class);
        assertTrue(s.contains("siqula@midget.orj"));
        assertTrue(s.contains("Harald"));
        assertFalse(s.contains("Hoytahl"));
        assertTrue(s.contains("35353535"));
        assertFalse(s.contains("12121212"));
    }

    @Test
    public void deleteUserOK() {
    	System.out.println("==================deleteUserOK()======================");
        String uid = doAddUser("rubblebeard", "frustaalstrom", "Frustaal", "Strom", "frustaalstrom@gmail.com", "12121212");

        Response response = baseResource.path("user/" + uid).request().delete(Response.class);
        //deleteResponse.getClientResponseStatus().getFamily().equals(Response.Status.Family.SUCCESSFUL);
        assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.NO_CONTENT.getStatusCode());

        try {
            String s = baseResource.path(uid).request().get(String.class);
            fail("Expected 404, got " + s);
        } catch (NotFoundException e) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void deleteUserNotFound() {
    	System.out.println("==================deleteUserNotFound()======================");
        WebTarget webResource = baseResource.path("users/dededede@hotmail.com/delete");
        try {
            String s = webResource.request().get(String.class);
            fail("Expected 404, got " + s);
        } catch (NotFoundException e) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }

    }


    @Test
    public void getuserroles() {

    	System.out.println("==================getuserroles()======================");
        String uid = doAddUser("riffraff", "snyper", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        String roleId1 = doAddUserRole(uid, "testappId1", "0005", "KK", "test");
        String roleId2 = doAddUserRole(uid, "testappIdX", "0005", "NN", "another");
        String roleId3 = doAddUserRole(uid, "testappIdX", "0005", "MM", "yetanother");

        List<Map<String, Object>> roles = doGetUserRoles(uid);
        assertEquals(3, roles.size());

        Map<String, Object> testRole1 = doGetUserRole(uid, roleId1);
        assertEquals("testappId1", testRole1.get("applicationId"));
        assertEquals("0005", testRole1.get("orgName"));
        assertEquals("KK", testRole1.get("roleName"));
        assertEquals("test", testRole1.get("roleValue"));

        Map<String, Object> testRole2 = doGetUserRole(uid, roleId2);
        assertEquals("testappIdX", testRole2.get("applicationId"));
        assertEquals("0005", testRole2.get("orgName"));
        assertEquals("NN", testRole2.get("roleName"));
        assertEquals("another", testRole2.get("roleValue"));

        Map<String, Object> testRole3 = doGetUserRole(uid, roleId3);
        assertEquals("testappIdX", testRole3.get("applicationId"));
        assertEquals("0005", testRole3.get("orgName"));
        assertEquals("MM", testRole3.get("roleName"));
        assertEquals("yetanother", testRole3.get("roleValue"));
        FileUtils.deleteDirectory(new File("target/data/lucene"));
        FileUtils.deleteDirectory(new File("data/lucene"));

    }

    @Test
    public void adduserrole() {
    	System.out.println("==================adduserrole()======================");
        String uid = doAddUser("riffraff", "snyper", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        List<Map<String, Object>> rolesBefore = doGetUserRoles(uid);
        assertTrue(rolesBefore.isEmpty());

        String roleId = doAddUserRole(uid, "testappId", "0005", "KK", "test");

        List<Map<String, Object>> rolesAfter = doGetUserRoles(uid);
        assertEquals(1, rolesAfter.size());

        assertEquals(roleId, rolesAfter.get(0).get("id"));
    }

    @Test
    public void adduserroleNoJson() {
    	System.out.println("==================adduserroleNoJson()======================");
        String uid = doAddUser("riffraff", "snyper", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        try {
            //String s = baseResource.path("user/" + uid + "/role").type("application/json").post(String.class, "");
            String s = baseResource.path("user/" + uid + "/role").request().post(Entity.json(""), String.class);
            fail("Expected 400, got " + s);
        } catch (ClientErrorException e) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void adduserroleBadJson() {
    	System.out.println("==================adduserroleBadJson()======================");
        String uid = doAddUser("riffraff", "snyper", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        try {
            //String s = baseResource.path("user/" + uid + "/role").type("application/json").post(String.class, "{ dilldall }");
            String s = baseResource.path("user/" + uid + "/role").request().post(Entity.json("{ dilldall }"), String.class);
            fail("Expected 400, got " + s);
        } catch (ClientErrorException e) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        }
    }


    @Test
    public void addExistingUserrole() {
    	System.out.println("==================addExistingUserrole()======================");
        String uid = doAddUser("riffraff", "snyper", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        doAddUserRole(uid, "testappId", "0005", "KK", "test");
        try {
            String failedRoleId = doAddUserRole(uid, "testappId", "0005", "KK", "test");
            fail("Expected exception with 409, got roleId " + failedRoleId);
        } catch (ClientErrorException e) {
            assertEquals(Response.Status.CONFLICT.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test(expected = NotFoundException.class)
    public void addRoleNonExistingUser() {
        
        String uid = "nonExistingUserId";
        WebTarget webResource = baseResource.path("user/" + uid + "/role");
        String payload = "{\"organizationName\": \"" + "0005" + "\",\n" +
                "        \"applicationId\": \"" + "testappId" + "\",\n" +
                "        \"applicationRoleName\": \"" + "KK" + "\",\n" +
                "        \"applicationRoleValue\": \"" + "test" + "\"}";

        String postResponseJson = webResource.request().post(Entity.json(payload), String.class);
    }

    @Test
    public void deleteuserrole() {
    	System.out.println("==================deleteuserrole()======================");
        String uid = doAddUser("riffraff", "snyper", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        String roleId1 = doAddUserRole(uid, "testappId", "0005", "KK", "test");
        String roleId2 = doAddUserRole(uid, "testappId", "0005", "NN", "tjohei");

        assertEquals(2, doGetUserRoles(uid).size());
        assertNotNull(doGetUserRole(uid, roleId1));
        baseResource.path("user/" + uid + "/role/" + roleId1).request().delete();
        assertEquals(1, doGetUserRoles(uid).size());

        assertNotNull(doGetUserRole(uid, roleId2));
        baseResource.path("user/" + uid + "/role/" + roleId2).request().delete();
        assertEquals(0, doGetUserRoles(uid).size());
    }


    @Test
    public void modifyuserrole() {
    	System.out.println("==================modifyuserrole()======================");
        String uid = doAddUser("riffraff", "snyper", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        String roleId = doAddUserRole(uid, "testappId", "0005", "KK", "test");

        Map<String, Object> roleBeforeModification = doGetUserRole(uid, roleId);
        assertEquals("testappId", roleBeforeModification.get("applicationId"));
        assertEquals("0005", roleBeforeModification.get("orgName"));
        assertEquals("KK", roleBeforeModification.get("roleName"));
        assertEquals("test", roleBeforeModification.get("roleValue"));

        String modifiedUserRoleJsonRequest = "{\"orgName\": \"0005\",\n" +
                "        \"userId\": \"" + uid + "\",\n" +
                "        \"id\": \"" + roleId + "\",\n" +
                "        \"applicationId\": \"testappId\",\n" +
                "        \"roleName\": \"KK\",\n" +
                "        \"roleValue\": \"test modified\"}";

        //String s = baseResource.path("user/" + uid + "/role/" + roleId).type("application/json").put(String.class, modifiedUserRoleJsonRequest);
        String s = baseResource.path("user/" + uid + "/role/" + roleId).request().put(Entity.json(modifiedUserRoleJsonRequest), String.class);

        Map<String, Object> roleAfterModification = doGetUserRole(uid, roleId);
        assertEquals("testappId", roleAfterModification.get("applicationId"));
        assertEquals("0005", roleAfterModification.get("orgName"));
        assertEquals("KK", roleAfterModification.get("roleName"));
        assertEquals("test modified", roleAfterModification.get("roleValue"));
    }


    @Test
    public void testGetExistingUser() {
    	System.out.println("==================testGetExistingUser()======================");
        String uid = doAddUser("1231312", "siqula", "Hoytahl", "Goffse", "siqula@midget.orj", "12121212");
        String s = baseResource.path("user/" + uid).request().get(String.class);
        assertTrue(s.contains("Hoytahl"));
    }


    @Test
    public void testGetNonExistingUser() {
    	System.out.println("==================testGetNonExistingUser()======================");
        doAddUser("1231312", "siqula", "Hoytahl", "Goffse", "siqula@midget.orj", "12121212");
        String uid = "non-existent-uid";
        try {
            String s = baseResource.path("user/" + uid).request().get(String.class);
            fail("Expected 404 NOT FOUND");
        } catch (NotFoundException e) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }
    }


    @Test
    public void testAddUserWillRespondWithConflictWhenUsernameAlreadyExists() {
    	System.out.println("==================testAddUserWillRespondWithConflictWhenUsernameAlreadyExists()======================");
        doAddUser("riffraff", "snyper", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        try {
            doAddUser("tifftaff", "snyper", "Another", "Wanderer", "wanderer@midget.orj", "34343434");
            fail("Expected 409 CONFLICT");
        } catch (ClientErrorException e) {
            assertEquals(Response.Status.CONFLICT.getStatusCode(), e.getResponse().getStatus());
            String entity = e.getResponse().readEntity(String.class);
            assertNotNull(entity);
        }
    }

    /*
    @Test
    public void testAddUserWillRespondWithConflictWhenEmailIsAlreadyInUseByAnotherUser() {
        doAddUser("riffraff", "another", "Edmund", "Goffse", "snyper@midget.orj", "12121212");
        try {
            doAddUser("tifftaff", "iamatestuser", "Another", "Wanderer", "snyper@midget.orj", "34343434");
            fail("Expected 409 CONFLICT");
        } catch (ClientErrorException e) {
            assertEquals(Response.Status.CONFLICT.getStatusCode(), e.getResponse().getStatus());
            String entity = e.getResponse().readEntity(String.class);
            assertNotNull(entity);
        }
    }
    */


    @Test
    public void addUser() {
    	System.out.println("==================addUser()======================");
        String uid = doAddUser("riffraff", "snyper", "Edmund", "Gøæøåffse", "snyper@midget.orj", "12121212");
        assertNotNull(uid);

        String s = baseResource.path("user/" + uid).request().get(String.class);
        assertTrue(s.contains("snyper@midget.orj"));
        assertTrue(s.contains("Edmund"));

        Response findresult = baseResource.path("users/find/snyper").request().get(Response.class);
        String entity = findresult.readEntity(String.class);
        assertTrue(entity.contains("snyper@midget.orj"));
        assertTrue(entity.contains("Edmund"));
    }

    @Test
    public void addUserAllowMissingPersonRef() {
    	System.out.println("==================addUserAllowMissingPersonRef()======================");
        String uid = doAddUser(null, "tsnyper", "tEdmund", "tGoffse", "tsnyper@midget.orj", "12121212");
        baseResource.path("user/" + uid).request().get(String.class);
    }

    @Test
    public void addUserAllowMissingFirstName() {
    	System.out.println("==================addUserAllowMissingFirstName()======================");
        doAddUser("triffraff", "tsnyper", null, "tGoffse", "tsnyper@midget.orj", "12121212");
    }

    @Test
    public void addUserAllowMissingLastName() {
    	System.out.println("==================addUserAllowMissingLastName()======================");
        doAddUser("triffraff", "tsnyper", "tEdmund", null, "tsnyper@midget.orj", "12121212");
    }

    @Test
    public void thatAddUserDoesNotAllowMissingEmail() {
    	System.out.println("==================thatAddUserDoesNotAllowMissingEmail()======================");
       try {
            doAddUser("triffraff", "tsnyper", "tEdmund", "tGoffse", null, "12121212");
            fail("Expected 400 BAD_REQUEST");
        } catch (ClientErrorException e) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void addUserWithMissingPhoneNumber() {
    	System.out.println("==================addUserWithMissingPhoneNumber()======================");
        String uid = doAddUser("triffraff", "tsnyper", "tEdmund", "tGoffse", "tsnyper@midget.orj", null);
        baseResource.path("user/" + uid).request().get(String.class);
    }

    /*  //TODO not sure what this test is supposed to verify
    @Test
    public void addUserWithCodesInCellPhoneNumber() {
        // TODO: Apache DS does not allow phone number with non-number letters
        String uid = doAddUser("triffraff", "tsnyper", "tEdmund", "lastname", "tsnyper@midget.orj", "12121-bb-212");
        baseResource.path("user/" + uid).get(String.class);
    }
    */

    @Test
    public void testAddUserWithLettersInPhoneNumberIsNotAllowed() {
    	System.out.println("==================testAddUserWithLettersInPhoneNumberIsNotAllowed()======================");
        // Apache DS does not allow phone number with non-number letters
        try {
            doAddUser("triffraff", "tsnyper", "tEdmund", "lastname", "tsnyper@midget.orj", "12121-bb-212");
            fail("Expected 400 BAD_REQUEST");
        } catch (ClientErrorException e) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        }
    }


    @Test
    @Ignore
    public void resetAndChangePassword() {
    	System.out.println("==================resetAndChangePassword()======================");
        String uid = doAddUser("123123123", "sneile", "Effert", "Huffse", "sneile@midget.orj", "21212121");

        //baseResource.path("user/sneile/resetpassword").type("application/json").post(ClientResponse.class);
        baseResource.path("user/sneile/resetpassword").request().post(Entity.json(""), ClientResponse.class);

        // TODO somehow replace PasswordSender with MockMail in guice context.
        //String token = main.getInjector().getInstance(MockMail.class).getToken(uid);
        String token = new MockMail().getToken(uid);
        assertNotNull(token);

        //ClientResponse response = baseResource.path("user/sneile/newpassword/" + token).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, "{\"newpassword\":\"naLLert\"}");
        ClientResponse response = baseResource.path("user/sneile/newpassword/" + token).request().post(Entity.json("{\"newpassword\":\"naLLert\"}"), ClientResponse.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String payload = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><auth><username>sneile</username><password>naLLert</password></auth>";
        //response = logonResource.path("logon").type("application/xml").post(ClientResponse.class, payload);
        response = logonResource.path("logon").request().post(Entity.json(payload), ClientResponse.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        //String identity = response.getEntity(String.class);
        String identity = response.readEntity(String.class);
        assertTrue(identity.contains("identity"));
        assertTrue(identity.contains("sneile"));
    }

    @Test
    public void thatEmailCanBeUsedAsUsername() {
    	System.out.println("==================thatEmailCanBeUsedAsUsername()======================");
        String uid = doAddUser("riffraff", "snyper@midget.orj", "Edmund", "Goffse", "somotheremail@midget.orj", "12121212");
        String s = baseResource.path("user/" + uid).request().get(String.class);
        assertTrue(s.contains("snyper@midget.orj"));
    }

    private String doAddUser(String userjson) {
        WebTarget webResource = baseResource.path("user");
        //String postResponseJson = webResource.type(MediaType.APPLICATION_JSON).post(String.class, userjson);
        String postResponseJson = webResource.request().post(Entity.json(userjson), String.class);

        Map<String, Object> createdUser;
        try {
            createdUser = new ObjectMapper().readValue(postResponseJson, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (String) createdUser.get("uid");
    }

    private String doAddUser(String personRef, String username, String firstName, String lastName, String email, String cellPhone) {
        String userJson = buildUserJson(personRef, username, firstName, lastName, email, cellPhone);
        return doAddUser(userJson);
    }

    private String buildUserJson(String personRef, String username, String firstName, String lastName, String email, String cellPhone) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(" \"username\":\"").append(username).append("\"");
        if (personRef != null) {
            sb.append(",\n").append(" \"personRef\":\"").append(personRef).append("\"");
        }
        if (firstName != null) {
            sb.append(",\n").append(" \"firstName\":\"").append(firstName).append("\"");
        }
        if (lastName != null) {
            sb.append(",\n").append(" \"lastName\":\"").append(lastName).append("\"");
        }
        if (email != null) {
            sb.append(",\n").append(" \"email\":\"").append(email).append("\"");
        }
        if (cellPhone != null) {
            sb.append(",\n").append(" \"cellPhone\":\"").append(cellPhone).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private List<Map<String, Object>> doGetUserRoles(String uid) {
        String postResponseJson = baseResource.path("user/" + uid + "/roles").request().get(String.class);
        List<Map<String, Object>> roles = null;
        try {
            roles = new ObjectMapper().readValue(postResponseJson, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return roles;
    }

    private Map<String, Object> doGetUserRole(String uid, String roleId) {
        String postResponseJson = baseResource.path("user/" + uid + "/role/" + roleId).request().get(String.class);
        Map<String, Object> roles = null;
        try {
            roles = new ObjectMapper().readValue(postResponseJson, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return roles;
    }

    private String doAddUserRole(String uid, String applicationId, String organizationName, String applicationRoleName, String applicationRoleValue) {
        WebTarget webResource = baseResource.path("user/" + uid + "/role");
        String payload = "{\"orgName\": \"" + organizationName + "\",\n" +
                "        \"applicationId\": \"" + applicationId + "\",\n" +
                "        \"roleName\": \"" + applicationRoleName + "\",\n" +
                "        \"roleValue\": \"" + applicationRoleValue + "\"}";

        //String postResponseJson = webResource.type("application/json").post(String.class, payload);
        String postResponseJson = webResource.request().post(Entity.json(payload), String.class);

        Map<String, Object> createdUser = null;
        try {
            createdUser = new ObjectMapper().readValue(postResponseJson, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (String) createdUser.get("id");
    }

}