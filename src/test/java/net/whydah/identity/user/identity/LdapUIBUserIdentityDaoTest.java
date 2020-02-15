package net.whydah.identity.user.identity;

import net.whydah.identity.Main;
import net.whydah.identity.config.ApplicationMode;
import net.whydah.identity.util.FileUtils;
import net.whydah.sso.user.types.UserIdentity;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import static org.junit.Assert.*;
import static org.testng.Assert.assertEquals;

public class LdapUIBUserIdentityDaoTest {
    private static Main main = null;

    private static LdapUserIdentityDao ldapUserIdentityDao;
    private static LdapAuthenticator ldapAuthenticator;

    @BeforeClass
    public static void setUp() throws Exception {
        //System.setProperty(AppConfig.IAM_MODE_KEY, AppConfig.IAM_MODE_DEV);
        //System.setProperty(ConfigTags.CONSTRETTO_TAGS, ConfigTags.DEV_MODE);
        FileUtils.deleteDirectory(new File("target/data/"));

        ApplicationMode.setCIMode();
        final ConstrettoConfiguration configuration = new ConstrettoBuilder()
                .createPropertiesStore()
                .addResource(Resource.create("classpath:useridentitybackend.properties"))
                .addResource(Resource.create("classpath:useridentitybackend-test.properties"))
                .done()
                .getConfiguration();

        String ldapPath = configuration.evaluateToString("ldap.embedded.directory");
        FileUtils.deleteDirectories(ldapPath);

        main = new Main(configuration.evaluateToInt("service.port"));
        main.startEmbeddedDS(configuration.asMap());

        String primaryLdapUrl = configuration.evaluateToString("ldap.primary.url");
        String primaryAdmPrincipal = configuration.evaluateToString("ldap.primary.admin.principal");
        String primaryAdmCredentials = configuration.evaluateToString("ldap.primary.admin.credentials");
        String primaryUidAttribute = configuration.evaluateToString("ldap.primary.uid.attribute");
        String primaryUsernameAttribute = configuration.evaluateToString("ldap.primary.username.attribute");
        String readonly = configuration.evaluateToString("ldap.primary.readonly");
        ldapUserIdentityDao = new LdapUserIdentityDao(primaryLdapUrl, primaryAdmPrincipal, primaryAdmCredentials, primaryUidAttribute, primaryUsernameAttribute, readonly);
        ldapAuthenticator = new LdapAuthenticator(primaryLdapUrl, primaryAdmPrincipal, primaryAdmCredentials, primaryUidAttribute, primaryUsernameAttribute);

        /*
        int LDAP_PORT = new Integer(AppConfig.appConfig.getProperty("ldap.embedded.port"));
        int LDAP_PORT = 18389;
        String ldapUrl = "ldap://localhost:" + LDAP_PORT + "/dc=people,dc=whydah,dc=no";
        String readOnly = AppConfig.appConfig.getProperty("ldap.primary.readonly");
        ldapUserIdentityDao = new LdapUserIdentityDao(ldapUrl, "uid=admin,ou=system", "secret", "uid", "initials", readOnly);
        ldapAuthenticator = new LdapAuthenticator(ldapUrl, "uid=admin,ou=system", "secret", "uid", "initials");

        String workDirPath = "target/" + LdapUIBUserIdentityDaoTest.class.getSimpleName();
        File workDir = new File(workDirPath);
        FileUtils.deleteDirectory(workDir);
        if (!workDir.mkdirs()) {
            fail("Error creating working directory " + workDirPath);
        }
        // Create the server
        ads = new EmbeddedADS(workDir);
        ads.startServer(LDAP_PORT);
        Thread.sleep(1000);
        */
    }

    @AfterClass
    public static void stop() {
        if (main != null) {
            main.stop();
        }
        FileUtils.deleteDirectory(new File("target/data/"));

    }


    @Test
    public void testAddUser() throws Exception {
        String uid = "staven@hotmail.com";
        String username = "jan";
        String firstName = "Oddvar";
        String lastName = "jensen";
        String email = "staven@hotmail.com";
        String password = "passpuss";
        String cellPhone = "+4798765432";
        String personRef = "some@email.dk";
        LDAPUserIdentity user = new LDAPUserIdentity(uid, username, firstName, lastName, email, password, cellPhone, personRef);
        ldapUserIdentityDao.addUserIdentity(user);
        LDAPUserIdentity gotUser = ldapUserIdentityDao.getUserIndentity("jan");
        assertNotNull(gotUser);
        assertEquals(gotUser.getUid(), uid);
        assertEquals(gotUser.getUsername(), username);
        assertEquals(gotUser.getFirstName(), firstName);
        assertEquals(gotUser.getLastName(), lastName);
        assertEquals(gotUser.getEmail(), email);
        //assertEquals(gotUser.getPassword(), password);
        assertEquals(gotUser.getCellPhone(), cellPhone);
        assertEquals(gotUser.getPersonRef(), personRef);
    }

    @Test
    public void testUpdateUser() throws Exception {
        String uid = UUID.randomUUID().toString();
        String username = "nalle";
        UserIdentity user = createValidUser(uid, username, "Nalle", "Puhh", "nalle@hotmail.com");
        ldapUserIdentityDao.addUserIdentity(new LDAPUserIdentity(user, "pass"));
        UserIdentity gotUser = ldapUserIdentityDao.getUserIndentity(username);
        assertNull(gotUser.getCellPhone());

        String cellPhone = "32323232";
        String personRef = "abc/123";
        gotUser.setCellPhone(cellPhone);
        gotUser.setPersonRef(personRef);
        ldapUserIdentityDao.updateUserIdentityForUsername(username, new LDAPUserIdentity(gotUser, "pass"));
        LDAPUserIdentity gotUpdatedUser = ldapUserIdentityDao.getUserIndentity(username);
        assertEquals(cellPhone, gotUpdatedUser.getCellPhone());
        assertEquals(personRef, gotUpdatedUser.getPersonRef());

        gotUpdatedUser.setCellPhone(null);
        String firstName = "Emil";
        personRef = "some@email.com";
        gotUpdatedUser.setFirstName(firstName);
        gotUpdatedUser.setPersonRef(personRef);
        ldapUserIdentityDao.updateUserIdentityForUsername(username, gotUpdatedUser);
        gotUpdatedUser = ldapUserIdentityDao.getUserIndentity(username);
        assertEquals(firstName, gotUpdatedUser.getFirstName());
        assertNull(gotUpdatedUser.getCellPhone());
        assertEquals(personRef, gotUpdatedUser.getPersonRef());
    }

    @Test
    public void testDeleteUser() throws Exception {
        String uid = UUID.randomUUID().toString();
        String username = "usernameToBeDeleted";
        UserIdentity user = createValidUser(uid, username, "Trevor", "Treske", "tretre@hotmail.com");
        ldapUserIdentityDao.addUserIdentity(new LDAPUserIdentity(user, "pass"));
        UserIdentity gotUser = ldapUserIdentityDao.getUserIndentity(user.getUsername());
        assertNotNull(gotUser);

        boolean deleteSuccessful = ldapUserIdentityDao.deleteUserIdentity(username);
        assertTrue(deleteSuccessful);

        UserIdentity gotUser2 = ldapUserIdentityDao.getUserIndentity(username);
        assertNull("Expected user to be deleted. " + (gotUser2 != null ? gotUser2.toString() : "null"), gotUser2);
    }

    @Test
    public void testChangePassword() throws Exception {
        String username = "stoven@hotmail.com";
        String firstPassword = "firstpass";
        String uid = username;
        UserIdentity user = createValidUser(uid, username, "Oddvar", "Bravo", "stoven@hotmail.com");
        ldapUserIdentityDao.addUserIdentity(new LDAPUserIdentity(user, firstPassword));

        assertNotNull(ldapAuthenticator.authenticateWithTemporaryPassword(username, firstPassword));
        String secondPassword = "snafssnufs";
        assertNull(ldapAuthenticator.authenticate(username, secondPassword));

        ldapUserIdentityDao.changePassword(username, secondPassword);
        assertNull(ldapAuthenticator.authenticate(username, firstPassword));
        assertNotNull(ldapAuthenticator.authenticate(username, secondPassword));
    }
    

    @Test
    public void testGetAllUsers() throws Exception {
    	
    	//{uid='uibadmin', username='uibadmin', firstName='uibadmin', lastName='uibadmin', personRef='null', email='null', cellPhone='null'}
//
//    	 String uid = "uibadmin";
//         String username = "uibadmin";
//         String firstName = "uibadmin";
//         String lastName = "uibadmin";
//         String email = "mail@gmail.com";
//         String password = null;
//         String cellPhone = null;
//         String personRef = null;
//         LDAPUserIdentity user = new LDAPUserIdentity(uid, username, firstName, lastName, email, password, cellPhone, personRef);
//         ldapUserIdentityDao.addUserIdentity(user);
//         LDAPUserIdentity gotUser = ldapUserIdentityDao.getUserIndentity("uibadmin");
//         assertTrue(gotUser!=null);
    	 assertTrue(ldapUserIdentityDao.getAllUsers().size()>0);
    	
    	
    }
    

    private static UserIdentity createValidUser(String uid, String username, String firstName, String lastName, String email) {
        return new UserIdentity(uid, username, firstName, lastName, null, email, null);
    }
}