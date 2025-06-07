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
import java.util.Map;

/**
 * Kestrel Engine Checkout Page Object
 * Handles payment and order completion on Demoblaze
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class CheckoutPage {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(id = "orderModal")
    private WebElement orderModal;
    
    @FindBy(id = "name")
    private WebElement nameField;
    
    @FindBy(id = "country")
    private WebElement countryField;
    
    @FindBy(id = "city")
    private WebElement cityField;
    
    @FindBy(id = "card")
    private WebElement cardField;
    
    @FindBy(id = "month")
    private WebElement monthField;
    
    @FindBy(id = "year")
    private WebElement yearField;
    
    @FindBy(xpath = "//button[text()='Purchase']")
    private WebElement purchaseButton;
    
    @FindBy(xpath = "//button[text()='Close']")
    private WebElement closeButton;
    
    // Success Elements
    @FindBy(className = "sweet-alert")
    private WebElement orderConfirmation;
    
    @FindBy(xpath = "//button[text()='OK']")
    private WebElement okButton;
    
    public CheckoutPage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        waitForModalToAppear();
    }
    
    private void waitForModalToAppear() {
        wait.until(ExpectedConditions.visibilityOf(orderModal));
    }
    
    public CheckoutPage fillCheckoutForm(Map<String, String> formData) {
        if (formData.containsKey("name")) {
            nameField.clear();
            nameField.sendKeys(formData.get("name"));
        }
        
        if (formData.containsKey("country")) {
            countryField.clear();
            countryField.sendKeys(formData.get("country"));
        }
        
        if (formData.containsKey("city")) {
            cityField.clear();
            cityField.sendKeys(formData.get("city"));
        }
        
        if (formData.containsKey("card")) {
            cardField.clear();
            cardField.sendKeys(formData.get("card"));
        }
        
        if (formData.containsKey("month")) {
            monthField.clear();
            monthField.sendKeys(formData.get("month"));
        }
        
        if (formData.containsKey("year")) {
            yearField.clear();
            yearField.sendKeys(formData.get("year"));
        }
        
        logger.info("📝 Checkout form filled");
        return this;
    }
    
    public OrderConfirmationPage completePurchase() {
        wait.until(ExpectedConditions.elementToBeClickable(purchaseButton));
        purchaseButton.click();
        
        // Wait for success message
        wait.until(ExpectedConditions.visibilityOf(orderConfirmation));
        
        return new OrderConfirmationPage();
    }
    
    public boolean attemptPurchase() {
        try {
            purchaseButton.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isModalVisible() {
        try {
            return orderModal.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isPurchaseButtonEnabled() {
        try {
            return purchaseButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    public void closeModal() {
        closeButton.click();
    }
}