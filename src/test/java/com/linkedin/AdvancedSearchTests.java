package com.linkedin;

import com.linkedin.pages.AdvancedSearchPage;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AdvancedSearchTests {
    private LoginRegistrationPage loginRegistrationPage;
    private String userPassword = "Nataly123";
    private String userEmail = "soul@ukr.net";

    @BeforeMethod
    public void initLoginPage(){
        loginRegistrationPage = PageFactory.initElements(new FirefoxDriver(), LoginRegistrationPage.class);
        loginRegistrationPage.open();
    }

    @Test
    public void advancedSearchTest() {
        HomePage homePage = loginRegistrationPage.loginUser(userEmail, userPassword);
        AdvancedSearchPage advancedSearchPage = homePage.advancedSearchLinkClick();
        advancedSearchPage.waitForText("Advanced People Search");
        advancedSearchPage.submitAdvancedSearchForm("HR");
        advancedSearchPage.waitForText("Some search results have been filtered to improve relevance");
        advancedSearchPage.getResultLinksList();
    }
}
