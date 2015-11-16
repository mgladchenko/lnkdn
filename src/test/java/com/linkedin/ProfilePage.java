package com.linkedin;

import org.openqa.selenium.WebDriver;

public class ProfilePage {
    protected WebDriver driver;

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
    }

    public void close() {
        driver.quit();
    }
}
