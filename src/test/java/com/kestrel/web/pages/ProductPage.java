package com.kestrel.web.pages;

import com.kestrel.utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Kestrel Engine Product Page Object
 * Handles product details and cart operations on Demoblaze
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class ProductPage {
    private static final Logger logger = LoggerFactory.getLogger(ProductPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(xpath = "//h2[@class='name']")
    private WebElement productTitle;
    
    @FindBy(xpath = "//h3[@class='price-container']")
    private WebElement productPrice;
    
    @FindBy(id = "more-information")
    private WebElement productDescription;
    
    @FindBy(xpath = "//a[text()='Add to cart']")
    private WebElement addToCartButton;
    
    @FindBy(xpath = "//img[contains(@class,'img-responsive')]")
    private WebElement productImage;
    
    public ProductPage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        waitForPageLoad();
    }
    
    private void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(productTitle));
        wait.until(ExpectedConditions.visibilityOf(addToCartButton));
    }
    
    public void clickAddToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
        addToCartButton.click();
        
        // Handle alert if it appears
        try {
            Thread.sleep(1000);
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // No alert appeared
        }
    }
    
    public String getProductPrice() {
        return productPrice.getText();
    }
    
    public String getProductDescription() {
        try {
            return productDescription.getText();
        } catch (Exception e) {
            return "Description not available";
        }
    }
    
    public boolean isImageLoaded() {
        try {
            return productImage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isAddToCartButtonEnabled() {
        try {
            return addToCartButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isPageLoaded() {
        try {
            return productTitle.isDisplayed() && addToCartButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getProductTitle() {
        return productTitle.getText();
    }
}