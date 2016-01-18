package com.linkedin;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.linkedin.rally.Rally;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;

public class TestRailRESTTests {

    @BeforeClass
    public void beforeClass(){
        RestAssured.baseURI = "https://arylovnikov.testrail.net";
        RestAssured.requestContentType(ContentType.JSON);
        //RestAssured.authentication = basic("mykola.gladchenko@gmail.com", "Soulme123");

    }


    @Test
    public void getTestCaseTest(){
        int testCaseId = 1;
        Response response = given().header("Authorization", "Basic bXlrb2xhLmdsYWRjaGVua29AZ21haWwuY29tOlNvdWxtZTEyMw==")
                .when().get("/index.php?/api/v2/get_case/{t}",testCaseId);
        System.out.println(response.asString());
        String testCaseTitle = "Verify successful user login";
        Assert.assertTrue(response.asString().contains(testCaseTitle), "Test case title not found in output");
        Assert.assertEquals(200, response.getStatusCode(), "Status code is wrong");
    }

    @Test
    @Rally(runID = "testcase1", caseID = "passed")
    public void test2(){
        given()
                .header("Authorization", "Basic bXlrb2xhLmdsYWRjaGVua29AZ21haWwuY29tOlNvdWxtZTEyMw==")
                .expect()
                .statusCode(200)
                .response()
                .when()
                .get("/index.php?/api/v2/get_case/1");
    }

    @Test
    public void SetTestCaseStatusTest() throws JSONException {
        int testCaseId = 1;
        int testCaseStatus = 4;
        JSONObject obj = new JSONObject();
        obj.put("status_id", testCaseStatus);

        Response response = given().header("Authorization", "Basic bXlrb2xhLmdsYWRjaGVua29AZ21haWwuY29tOlNvdWxtZTEyMw==")
                .body(obj.toString())
                .when().post("/index.php?/api/v2/add_result/{t}",testCaseId);
        System.out.println(response.asString());
        Assert.assertTrue(response.asString().contains("\"status_id\":"+testCaseStatus), "status id was not set");
        Assert.assertEquals(200, response.getStatusCode(), "Status code is wrong");
    }
}
