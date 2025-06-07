package com.kestrel.web.pages;

import com.kestrel.utils.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Kestrel Engine Cart Page Object
 * Handles shopping cart operations on Demoblaze
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class CartPage {
    private static final Logger logger = LoggerFactory.getLogger(CartPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(id = "tbodyid")
    private WebElement cartContainer;
    
    @FindBy(xpath = "//button[text()='Place Order']")
    private WebElement placeOrderButton;
    
    @FindBy(id = "totalp")
    private WebElement totalPrice;
    
    @FindBy(xpath = "//tr[@class='success']")
    private List<WebElement> cartItems;
    
    public CartPage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        waitForPageLoad();
    }
    
    private void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(cartContainer));
    }
    
    public boolean isProductInCart(String productName) {
        try {
            for (WebElement item : cartItems) {
                if (item.getText().contains(productName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isCartEmpty() {
        try {
            return cartItems.isEmpty();
        } catch (Exception e) {
            return true;
        }
    }
    
    public int getProductCount() {
        try {
            return cartItems.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public String getTotalPrice() {
        try {
            return totalPrice.getText();
        } catch (Exception e) {
            return "0";
        }
    }
    
    public void removeProduct(String productName) {
        for (WebElement item : cartItems) {
            if (item.getText().contains(productName)) {
                WebElement deleteButton = item.findElement(By.linkText("Delete"));
                deleteButton.click();
                break;
            }
        }
    }
    
    public void clearCart() {
        while (!cartItems.isEmpty()) {
            WebElement deleteButton = cartItems.get(0).findElement(By.linkText("Delete"));
            deleteButton.click();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public CheckoutPage clickPlaceOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderButton));
        placeOrderButton.click();
        return new CheckoutPage();
    }
    
    public boolean isPageLoaded() {
        try {
            return cartContainer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}