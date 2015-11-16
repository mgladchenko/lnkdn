package com.linkedin;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTests {
    private LoginRegistrationPage loginRegistrationPage;
    private HomePage homePage;
    private ProfilePage profilePage;
    String userEmail = "testautomation.acc@gmail.com";

    @BeforeMethod
    public void initLoginPage(){
        loginRegistrationPage = PageFactory.initElements(new FirefoxDriver(), LoginRegistrationPage.class);
    }

    @AfterMethod
    public void closeBrowser(){
        homePage.close();
    }

    @DataProvider(name = "userLoginTestData")
    public Object[][] createdata() {
        return new Object[][] {
                new Object[] { "Testautomation123" },
                new Object[] { "1234"}};
    }

    @Test(dataProvider = "userLoginTestData")
    public void userLoginGenericTest(String userPassword) {
        loginRegistrationPage.open();
        homePage = loginRegistrationPage.loginUser(userEmail,userPassword);
        switch(userPassword) {
            case "Testautomation123":
                Assert.assertTrue(homePage.isPageLoaded());
                break;
            case "1234":
                String errorMsg = "There were one or more errors in your submission. Please correct the marked fields below.";
                Assert.assertEquals(loginRegistrationPage.getAlertErrorMessageText(),errorMsg,"Expected error message not found");
                Assert.assertFalse(homePage.isPageLoaded());
                break;
            default:
                break;
        }
    }

    @Test
    public void loadProfilePageTest(){
        String userPassword = "Testautomation123";
        loginRegistrationPage = PageFactory.initElements(new FirefoxDriver(), LoginRegistrationPage.class);
        loginRegistrationPage.open();
        homePage = loginRegistrationPage.loginUser(userEmail,userPassword);
        profilePage = homePage.openProfilePage();
        //Assert that profile page is loaded
        profilePage.close();
    }

}