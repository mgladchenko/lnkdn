package com.linkedin;

import com.linkedin.pages.AdvancedSearchPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends AbstractPage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//a[contains(text(),'Profile')]")
    private WebElement profileMenuLink;

    @FindBy(xpath = "//a[@id='advanced-search']")
    private WebElement advancedSearchLink;

    public ProfilePage openProfilePage(){
        profileMenuLink.click();
        return PageFactory.initElements(driver, ProfilePage.class);
    }

    public AdvancedSearchPage advancedSearchLinkClick (){
        advancedSearchLink.click();
        return PageFactory.initElements(driver, AdvancedSearchPage.class);
    }

}
