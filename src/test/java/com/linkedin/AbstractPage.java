package com.linkedin;

import org.openqa.selenium.WebDriver;

public class AbstractPage {
    protected WebDriver driver;

    public AbstractPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isPageLoaded(String pageTitle){
        if (driver.getTitle().contentEquals(pageTitle)) {
            return true;
        }
        return false;
    }

}
