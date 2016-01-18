package com.linkedin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AbstractPage {
    protected WebDriver driver;
    protected int timeoutInSeconds = 15;


    public AbstractPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isPageLoaded(String pageTitle){
        if (driver.getTitle().contentEquals(pageTitle)) {
            return true;
        }
        return false;
    }

    public void waitForText(String text){
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(),'"+text+"')]")));
    }

    public void waitForSpinner(){
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@class='loading']")));
    }

    public void close() {
        driver.quit();
    }

}
