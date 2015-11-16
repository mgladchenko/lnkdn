package com.linkedin;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

public class RegistrationTests {
    private LoginRegistrationPage loginRegistrationPage;
    @Test
    public void securityVerification() {
        loginRegistrationPage = PageFactory.initElements(new FirefoxDriver(), LoginRegistrationPage.class);
        loginRegistrationPage.open();
        String uniqueEmail = "lnkdn_"+System.currentTimeMillis()+"@mailinator.com";
        loginRegistrationPage.submitRegistrationForm("First", "Last", uniqueEmail, "P@ssword123");
        //assert securityVerification form displayed
        loginRegistrationPage.close();
    }

}
