package learnositysdk.request;

import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.http.client.config.RequestConfig;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DataApiIT
	extends TestCase {

	static private String consumerKey = "yis0TYCu7U9V4o7M";
	static private String consumerSecret = "74c5fd430cf1242a527f6223aebd42d30464be22";

	private String baseUrl;
	static Map<String,String> securityMap;
	private RequestConfig requestConfig;
	private Map request;
	private DataApi dataApi;
	static JSONObject response;
	private JSONObject responseJson;

	@Override
	protected void setUp()
		throws java.lang.Exception
	{
		String testEnv = System.getenv("ENV");
		String testVersion = System.getenv("VERSION");

		baseUrl = buildBaseUrl(testEnv, testVersion);

		securityMap = new HashMap<String, String>();
		securityMap.put("consumer_key", consumerKey);
		securityMap.put("domain","localhost");

		requestConfig = RequestConfig.custom().setSocketTimeout(40000).build();

		request = new HashMap<String,String>();
	}

	public void testGetActivitites()
			throws java.lang.Exception
		{
			String endpoint = baseUrl + "/itembank/activities";
			System.out.println("Testing Data API call to " + endpoint + " with SET request");

			JSONArray items = new JSONArray();
			items.put("item_2");
			items.put("item_3");

			JSONObject data = new JSONObject();
			data.put("items", items);

			JSONObject activity = new JSONObject();
			activity.put("status","published");
			activity.put("description","My test description Title √");
			activity.put("data", data);
			activity.put("reference", UUID.randomUUID().toString());

			JSONArray activities = new JSONArray();
			activities.put(activity);

			request.put("activities", activities);

			DataApi dataApi = new DataApi(endpoint, securityMap, consumerSecret, request, "set");
			response = dataApi.requestJSONObject();
			responseJson = new JSONObject(response.getString("body"));

			assertConsistentResponse(response, responseJson);
		}

	public void testGetItemsEmptyRemote()
			throws java.lang.Exception
		{
			String endpoint = baseUrl + "/itembank/items";
			System.out.println("Testing Data API call to " + endpoint + " with Remote object");

			dataApi = new DataApi(endpoint, securityMap, consumerSecret);
			response = dataApi.requestJSONObject();

			Remote remote = dataApi.request();
			String body = remote.getBody();
			responseJson = new JSONObject(body);

			assertConsistentResponseRemote(remote, responseJson);
		}

	public void testGetItemsEmpty()
			throws java.lang.Exception
		{
			String endpoint = baseUrl + "/itembank/items";
			System.out.println("Testing Data API call to " + endpoint + " with empty implicit GET request");

			dataApi = new DataApi(endpoint, securityMap, consumerSecret);
			response = dataApi.requestJSONObject();
			responseJson = new JSONObject(response.getString("body"));

			assertConsistentResponse(response, responseJson);
		}

	public void testExplicitGetItemsEmpty()
			throws java.lang.Exception
		{
			String endpoint = baseUrl + "/itembank/items";
			System.out.println("Testing Data API call to " + endpoint + " with empty explicit GET request");

			dataApi = new DataApi(endpoint, securityMap, consumerSecret, "get");
			response = dataApi.requestJSONObject();
			responseJson = new JSONObject(response.getString("body"));

			assertConsistentResponse(response, responseJson);
		}

	public void testGetItemsLimit()
			throws java.lang.Exception
		{
			String endpoint = baseUrl + "/itembank/items";
			System.out.println("Testing Data API call to " + endpoint + " with limited GET request");

			request.put("limit", "10");
			dataApi = new DataApi(endpoint, securityMap, consumerSecret, request, "get");
			response = dataApi.requestJSONObject();
			responseJson = new JSONObject(response.getString("body"));

			assertConsistentResponse(response, responseJson);
		}

	public void testGetItemsRecursive()
			throws java.lang.Exception
		{
			String endpoint = baseUrl + "/itembank/items";
			System.out.println("Testing Data API call to " + endpoint + " with recursive GET request");

			request.put("item_pool_id", "DoNotChange_ForIntegrationTest");
			request.put("limit", "1");
			dataApi = new DataApi(baseUrl + "/itembank/items", securityMap, consumerSecret, request, "get");
			dataApi.requestRecursive(new DataApiITCallback());
		}

	public void assertConsistentResponse(JSONObject response, JSONObject responseJson)
			throws org.json.JSONException
		{
			assertConsistentResponseCodeStatus(
					response.getInt("statusCode"),
					responseJson.getJSONObject("meta").getBoolean("status")
					);
		}

	public void assertConsistentResponseRemote(Remote responseRemote, JSONObject responseJson)
			throws org.json.JSONException
		{
			assertConsistentResponseCodeStatus(
					responseRemote.getStatusCode(),
					responseJson.getJSONObject("meta").getBoolean("status")
					);
		}

	private void assertConsistentResponseCodeStatus(int statusCode, boolean status)
			throws org.json.JSONException
		{
			assertTrue("Inconsistent HTTP status cand and meta status",
					(statusCode == 200 && status)
					|| (statusCode != 200 && !status));
		}

	public String buildBaseUrl(String env, String version)
	{
		String envDomain = "";
		if (env != null && !env.equals("prod")) {
			envDomain = "." + env;
		}

		String versionPath = "v1";
		if (env != null && env.equals("vg")) {
			versionPath = "latest";
		} else if (version != null) {
			versionPath = version;
		} else {
		}

		return "https://data" + envDomain + ".learnosity.com/" + versionPath;
	}
}
