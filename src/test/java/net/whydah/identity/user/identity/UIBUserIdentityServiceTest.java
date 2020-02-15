package net.whydah.identity.user.identity;

import net.whydah.identity.Main;
import net.whydah.identity.application.ApplicationDao;
import net.whydah.identity.audit.AuditLogDao;
import net.whydah.identity.config.ApplicationMode;
import net.whydah.identity.dataimport.DatabaseMigrationHelper;
import net.whydah.identity.user.authentication.UserAdminHelper;
import net.whydah.identity.user.role.UserApplicationRoleEntryDao;
import net.whydah.identity.user.search.LuceneUserIndexer;
import net.whydah.identity.user.search.LuceneUserSearch;
import net.whydah.identity.util.FileUtils;
import net.whydah.identity.util.PasswordGenerator;
import net.whydah.sso.user.types.UserIdentity;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 02/04/14
 */
public class UIBUserIdentityServiceTest {
    private static LdapUserIdentityDao ldapUserIdentityDao;
    private static PasswordGenerator passwordGenerator;
    private static LuceneUserIndexer luceneIndexer;
    private static UserAdminHelper userAdminHelper;
    private static Directory index;

    private static Main main = null;


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


        String roleDBDirectory = configuration.evaluateToString("roledb.directory");
        String ldapPath = configuration.evaluateToString("ldap.embedded.directory");
 //       String ldapPath = "/tmp";
        String luceneUsersDir = configuration.evaluateToString("lucene.usersdirectory");
        FileUtils.deleteDirectories(ldapPath, roleDBDirectory, luceneUsersDir);

        main = new Main(configuration.evaluateToInt("service.port"));
        main.startEmbeddedDS(configuration.asMap());

        BasicDataSource dataSource = initBasicDataSource(configuration);
        new DatabaseMigrationHelper(dataSource).upgradeDatabase();


        String primaryLdapUrl = configuration.evaluateToString("ldap.primary.url");
        String primaryAdmPrincipal = configuration.evaluateToString("ldap.primary.admin.principal");
        String primaryAdmCredentials = configuration.evaluateToString("ldap.primary.admin.credentials");
        String primaryUidAttribute = configuration.evaluateToString("ldap.primary.uid.attribute");
        String primaryUsernameAttribute = configuration.evaluateToString("ldap.primary.username.attribute");
        String readonly = configuration.evaluateToString("ldap.primary.readonly");

        ldapUserIdentityDao = new LdapUserIdentityDao(primaryLdapUrl, primaryAdmPrincipal, primaryAdmCredentials, primaryUidAttribute, primaryUsernameAttribute, readonly);


        ApplicationDao configDataRepository = new ApplicationDao(dataSource);
        UserApplicationRoleEntryDao userApplicationRoleEntryDao = new UserApplicationRoleEntryDao(dataSource);

        index = new NIOFSDirectory(new File(luceneUsersDir));
        luceneIndexer = new LuceneUserIndexer(index);
        AuditLogDao auditLogDao = new AuditLogDao(dataSource);
        userAdminHelper = new UserAdminHelper(ldapUserIdentityDao, luceneIndexer, auditLogDao, userApplicationRoleEntryDao, configuration);
        passwordGenerator = new PasswordGenerator();

        /*
        int LDAP_PORT = 19389;
        String ldapUrl = "ldap://localhost:" + LDAP_PORT + "/dc=people,dc=whydah,dc=no";
        String readOnly = AppConfig.appConfig.getProperty("ldap.primary.readonly");
        ldapUserIdentityDao = new LdapUserIdentityDao(ldapUrl, "uid=admin,ou=system", "secret", "uid", "initials", readOnly);


        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:hsqldb:file:" + "target/" + UIBUserIdentityServiceTest.class.getSimpleName()  + "/hsqldb");

        new DatabaseMigrationHelper(dataSource).upgradeDatabase();


        String workDirPath = "target/" + UIBUserIdentityServiceTest.class.getSimpleName();
        File workDir = new File(workDirPath);
        FileUtils.deleteDirectory(workDir);
        if (!workDir.mkdirs()) {
            fail("Error creating working directory " + workDirPath);

        }

        luceneIndexer = new LuceneUserIndexer(index);

        // Create the server
        ads = new EmbeddedADS(workDir);
        ads.startServer(LDAP_PORT);
        Thread.sleep(1000);
        */


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

    @AfterClass
    public static void stop() {
        if (main != null) {
            main.stop();
        }
        luceneIndexer.closeIndexWriter();
        main=null;
        luceneIndexer = null;
        FileUtils.deleteDirectory(new File("target/data/"));
    }

    @Test
    public void testAddUserToLdap() throws Exception {
        UserIdentityService userIdentityService =
                new UserIdentityService(null, ldapUserIdentityDao, null, passwordGenerator, luceneIndexer, Mockito.mock(LuceneUserSearch.class));

        String username = "username123";
        LDAPUserIdentity userIdentity = new LDAPUserIdentity("uidvalue", username, "firstName", "lastName", "test@test.no", "password", "12345678", "personRef"
        );
        userAdminHelper.addUser(userIdentity);

        UserIdentity fromLdap = userIdentityService.getUserIdentity(username);

        assertEquals(userIdentity, fromLdap);
        Response response = userAdminHelper.addUser(userIdentity);
        assertTrue("Expected ConflictException because user should already exist.",
                response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void testAddTestUserToLdap() throws Exception {
        UserIdentityService userIdentityService =
                new UserIdentityService(null, ldapUserIdentityDao, null, passwordGenerator, luceneIndexer, Mockito.mock(LuceneUserSearch.class));

        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        LDAPUserIdentity userIdentity = new LDAPUserIdentity("uidvalue",
                "us" + UUID.randomUUID().toString().replace("-", "").replace("_", ""),
                "Mt Test",
                "Testesen",
                UUID.randomUUID().toString().replace("-", "").replace("_", "") + "@getwhydah.com",
                "47" + Integer.toString(rand.nextInt(100000000)),
                null,
                "pref");

        userAdminHelper.addUser(userIdentity);

        UserIdentity fromLdap = userIdentityService.getUserIdentity(userIdentity.getUsername());

        assertEquals(userIdentity, fromLdap);

    }

    @Test
    public void testAddUserStrangeCellPhone() throws Exception {
        UserIdentityService userIdentityService =
                new UserIdentityService(null, ldapUserIdentityDao, null, passwordGenerator, luceneIndexer, Mockito.mock(LuceneUserSearch.class));

        String username = "username1234";
        LDAPUserIdentity userIdentity = new LDAPUserIdentity("uid2", username, "firstName2", "lastName2", "test2@test.no", "password2", "+47 123 45 678", "personRef2"
        );
        userAdminHelper.addUser(userIdentity);

        UserIdentity fromLdap = userIdentityService.getUserIdentity(username);

        assertEquals(userIdentity, fromLdap);
        Response response = userAdminHelper.addUser(userIdentity);
        assertTrue("Expected ConflictException because user should already exist.", response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode());
    }

    @Test
    public void testPersistenceAddTestUserToLdap() throws Exception {
        UserIdentityService userIdentityService =
                new UserIdentityService(null, ldapUserIdentityDao, null, passwordGenerator, luceneIndexer, Mockito.mock(LuceneUserSearch.class));

        Random rand = new Random();
        rand.setSeed(new java.util.Date().getTime());
        LDAPUserIdentity userIdentity = new LDAPUserIdentity("uidvalue",
                "us" + UUID.randomUUID().toString().replace("-", "").replace("_", ""),
                "Mt Test",
                "Testesen",
                UUID.randomUUID().toString().replace("-", "").replace("_", "") + "@getwhydah.com",
                "47" + Integer.toString(rand.nextInt(100000000)),
                null,
                "pref");

        userAdminHelper.addUser(userIdentity);

        UserIdentity fromLdap = userIdentityService.getUserIdentity(userIdentity.getUsername());

        assertEquals(userIdentity, fromLdap);
        stop();
        setUp();
        UserIdentityService userIdentityService2 =
                new UserIdentityService(null, ldapUserIdentityDao, null, passwordGenerator, luceneIndexer, Mockito.mock(LuceneUserSearch.class));
        UserIdentity fromLdap2 = userIdentityService2.getUserIdentity(userIdentity.getUsername());

        // TODO: Still not working
        // assertEquals(userIdentity, fromLdap2);

    }
}
