package com.linkedin.rally;

import com.google.gson.JsonObject;

import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.response.GetResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class RallyAPI {
    public static final String TEST_RAIL_USER = "mykola.gladchenko@elementumapps.com";
    public static final String TEST_RAIL_PASS = "Nataly123";

    public static void setTestResult(int runID, String caseID, String testComment, String testStatus) throws URISyntaxException, IOException {

        //Create and configure a new instance of RallyRestApi
        RallyRestApi restApi = new RallyRestApi(new URI("https://rally1.rallydev.com"), TEST_RAIL_USER, TEST_RAIL_PASS);
        restApi.setApplicationName("setTestResult");

        try {
            //Get TestCase ObjectID
            GetRequest getRequest = new GetRequest("/testcase");
            getRequest.addParam("query","(FormattedID = \""+caseID+"\")");
            GetResponse getResponse = restApi.get(getRequest);
            JsonObject obj = getResponse.getObject();
            long tcObjectID = obj.getAsJsonArray("Results").get(0).getAsJsonObject().get("ObjectID").getAsLong();

            //Set TestCase result
            JsonObject newTCResult = new JsonObject();
            newTCResult.addProperty("Build", "version resource is missing");
            newTCResult.addProperty("Tester", 9939844830L);
            newTCResult.addProperty("Date", getCurrentDate());
            newTCResult.addProperty("TestCase", tcObjectID);
            newTCResult.addProperty("Verdict", testStatus);
            newTCResult.addProperty("Notes", testComment);

            CreateRequest createRequest = new CreateRequest("testcaseresult", newTCResult);
            restApi.create(createRequest);

        } finally {
            //Release all resources
            restApi.close();
        }
    }

    private static String getCurrentDate() {
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        return fmt.print(dt);
    }

    //temporary not needed
 /*   private static String getBuildVersion() {
        Response responseOfGet = ResponseManager.executeGET(URI_BUILD_VERSION, "user", "pass");
        String revision =  "revision: "+new JsonParser().parse(responseOfGet.readEntity(String.class))
                .getAsJsonObject().get("revision").getAsString();
        System.out.println(revision);
        return revision;
    }*/
}