package com.linkedin.pages;

import com.linkedin.AbstractPage;
import com.linkedin.ProfilePage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Iterator;
import java.util.List;

public class AdvancedSearchPage extends AbstractPage {

    public AdvancedSearchPage(WebDriver driver) {
        super(driver);;
    }

    @FindBy(xpath = "//input[@id='advs-keywords']")
    private WebElement keywordsInput;

    @FindBy(xpath = "//input[@class='submit-advs']")
    private WebElement searchButton;

    @FindBy(xpath = "//a[@class='result-image']")
    private  List<WebElement> resultLinks;

    @FindBy(xpath = "//a[@rel='next']")
    private  WebElement nextPageLink;




    public void submitAdvancedSearchForm(String keywordsList){
        keywordsInput.sendKeys(keywordsList);
        searchButton.click();
        searchButton.sendKeys(Keys.ENTER);
    }

    public void getResultLinksList() {
        Iterator<WebElement> i = resultLinks.iterator();
        while(i.hasNext()) {
            WebElement resultLink = i.next();
            System.out.println(resultLink.getAttribute("href"));
        }
        nextPageLink.click();
        waitForSpinner();
        Iterator<WebElement> i2 = resultLinks.iterator();
        while(i2.hasNext()) {
            WebElement resultLink = i2.next();
            System.out.println(resultLink.getAttribute("href"));
        }
    }

}

