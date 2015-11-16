package com.linkedin;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    protected WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    @FindBy(xpath = "//a[contains(text(),'Profile')]")
    private WebElement profileMenuLink;

    public ProfilePage openProfilePage(){
        profileMenuLink.click();
        return PageFactory.initElements(driver, ProfilePage.class);
    }

    public boolean isPageLoaded(){
        if (driver.getTitle().contentEquals("Welcome! | LinkedIn")) {
            return true;
        }
        return false;
    }

    public void close() {
        driver.quit();
    }
}
