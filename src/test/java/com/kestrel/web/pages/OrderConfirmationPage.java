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
 * Kestrel Engine Order Confirmation Page Object
 * Handles order confirmation and success messages on Demoblaze
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class OrderConfirmationPage {
    private static final Logger logger = LoggerFactory.getLogger(OrderConfirmationPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(className = "sweet-alert")
    private WebElement confirmationModal;
    
    @FindBy(xpath = "//h2[text()='Thank you for your purchase!']")
    private WebElement thankYouMessage;
    
    @FindBy(className = "lead")
    private WebElement orderDetails;
    
    @FindBy(xpath = "//button[text()='OK']")
    private WebElement okButton;
    
    public OrderConfirmationPage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        waitForConfirmation();
    }
    
    private void waitForConfirmation() {
        wait.until(ExpectedConditions.visibilityOf(confirmationModal));
    }
    
    public boolean isOrderConfirmed() {
        try {
            return thankYouMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getOrderDetails() {
        try {
            return orderDetails.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    public String getOrderId() {
        String details = getOrderDetails();
        // Extract order ID from details text
        if (details.contains("Id:")) {
            return details.substring(details.indexOf("Id:") + 3).trim().split("\\s+")[0];
        }
        return "";
    }
    
    public HomePage clickOk() {
        wait.until(ExpectedConditions.elementToBeClickable(okButton));
        okButton.click();
        return new HomePage();
    }
    
    public boolean isConfirmationVisible() {
        try {
            return confirmationModal.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}