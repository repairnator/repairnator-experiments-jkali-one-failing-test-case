package net.whydah.identity.application;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import net.whydah.identity.Main;
import net.whydah.identity.config.ApplicationMode;
import net.whydah.identity.dataimport.DatabaseMigrationHelper;
import net.whydah.identity.util.FileUtils;
import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.types.Application;
import org.apache.commons.dbcp.BasicDataSource;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.Resource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

/**
 * End-to-end test against the exposed HTTP endpoint and down to the in-mem HSQLDB.
 *
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-02-01
 */
public class ApplicationsResourceTest {
    private final String appToken1 = "appToken1";
    private final String userToken1 = "userToken1";
    private Main main;
    
    ConstrettoConfiguration configuration = new ConstrettoBuilder()
            .createPropertiesStore()
            .addResource(Resource.create("classpath:useridentitybackend.properties"))
            .addResource(Resource.create("file:./useridentitybackend_override.properties"))
            .done()
            .getConfiguration();
    BasicDataSource dataSource;

    @BeforeClass
    public void startServer() {
    
    	 ApplicationMode.setTags(ApplicationMode.CI_MODE, ApplicationMode.NO_SECURITY_FILTER);
         final ConstrettoConfiguration configuration = new ConstrettoBuilder()
                 .createPropertiesStore()
                 .addResource(Resource.create("classpath:useridentitybackend.properties"))
                 .addResource(Resource.create("file:./useridentitybackend_override.properties"))
                 .done()
                 .getConfiguration();

         removeLuceneAppData();
         
         String roleDBDirectory = configuration.evaluateToString("roledb.directory");
         FileUtils.deleteDirectory(roleDBDirectory);
         dataSource = initBasicDataSource(configuration);
         DatabaseMigrationHelper dbHelper = new DatabaseMigrationHelper(dataSource);
         dbHelper.cleanDatabase();
         dbHelper.upgradeDatabase();

         main = new Main(6645);
         main.startJetty();
         RestAssured.port = main.getPort();
         RestAssured.basePath = Main.CONTEXT_PATH;
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
    public void stop() {
        if (main != null) {
            main.stop();
        }
        try {
        	if(!dataSource.isClosed()) {
        		dataSource.close();
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    void removeLuceneAppData() {
    	String luceneApplicationDirectory = configuration.evaluateToString("lucene.applicationsdirectory");
    	FileUtils.deleteDirectory(luceneApplicationDirectory);
    }

    @Test
    public void testGetApplicationsEmptyList() throws Exception {
        String path = "/{applicationtokenid}/applications";
        Response response = given()
                .log().everything()
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(path, appToken1);

        String jsonResponse = response.body().asString();
        List<Application> applications = ApplicationMapper.fromJsonList(jsonResponse);
        for(Application app: applications) {
        	System.out.println("Application found :" + app.toString());
        }
        assertEquals(applications.size(), 0);
    }

    @Test(dependsOnMethods = "testGetApplicationsEmptyList", enabled = false)
    public void testGetApplicationsOK() throws Exception {
        //Add applications
        int nrOfApplications = 4;
        String createPath = "/{applicationtokenid}/{userTokenId}/application";
        String json;
        for (int i = 0; i < nrOfApplications; i++) {

            Application app = new Application("ignoredId", "appName" + i);
            app.getSecurity().setSecret("secret1");
            app.getSecurity().setUserTokenFilter("false");

            json = ApplicationMapper.toJson(app);
            given()
                    .body(json)
                    .contentType(ContentType.JSON)
                    .log().everything()
                    .expect()
                    .statusCode(200)
                    .log().ifError()
                    .when()
                    .post(createPath, appToken1, userToken1);
        }

        //GET
        String path = "/{applicationtokenid}/applications";
        Response response = given()
                .log().everything()
                .expect()
                .statusCode(200)
                .log().ifError()
                .when()
                .get(path, appToken1);

        String jsonResponse = response.body().asString();
        List<Application> applications = ApplicationMapper.fromJsonList(jsonResponse);
        assertEquals(applications.size(), nrOfApplications);
    }
}
