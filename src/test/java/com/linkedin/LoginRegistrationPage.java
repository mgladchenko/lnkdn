package com.linkedin;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginRegistrationPage {
    protected WebDriver driver;

    public LoginRegistrationPage(WebDriver driver) {
        this.driver = driver;
    }

    //Registration elements
    @FindBy(id ="first-name")
    private WebElement firstNameInput;

    @FindBy(id ="last-name")
    private WebElement lastNameInput;

    @FindBy(id ="join-email")
    private WebElement emailInput;

    @FindBy(id ="join-password")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement joinButton;

    //Login elements:
    @FindBy(id = "login-email")
    private WebElement loginEmailInput;

    @FindBy(id = "login-password")
    private WebElement loginPasswordInput;

    @FindBy(xpath = "//input[@name='submit']")
    private WebElement loginSubmitButton;

    @FindBy(xpath = "//div[@class='alert error']//strong")
    private WebElement alertErrorMessageBox;

    public String getAlertErrorMessageText(){
        return alertErrorMessageBox.getText();
    }

    public HomePage loginUser(String userEmail, String userPassword){
        loginEmailInput.sendKeys(userEmail);
        loginPasswordInput.sendKeys(userPassword);
        loginSubmitButton.click();
        return PageFactory.initElements(driver, HomePage.class);
    }

    public void submitRegistrationForm(String firstName, String lastName, String email, String password){
        firstNameInput.sendKeys(firstName);
        lastNameInput.sendKeys(lastName);
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        joinButton.click();
    }

    public void open() {
        driver.get("https://www.linkedin.com/");
    }

    public void close() {
        driver.quit();
    }





}
