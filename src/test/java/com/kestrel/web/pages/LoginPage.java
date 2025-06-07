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
 * Kestrel Engine Login Page Object
 * Handles authentication operations on Demoblaze
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class LoginPage {
    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Login Modal Elements
    @FindBy(id = "logInModal")
    private WebElement loginModal;
    
    @FindBy(id = "loginusername")
    private WebElement usernameField;
    
    @FindBy(id = "loginpassword")
    private WebElement passwordField;
    
    @FindBy(xpath = "//button[text()='Log in']")
    private WebElement loginSubmitButton;
    
    @FindBy(xpath = "//div[@id='logInModal']//button[@class='close']")
    private WebElement loginCloseButton;
    
    @FindBy(xpath = "//div[@id='logInModal']//span[text()='×']")
    private WebElement loginCloseX;
    
    /**
     * Constructor - Initialize page elements
     */
    public LoginPage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        waitForModalToAppear();
        logger.debug("🦅 LoginPage initialized");
    }
    
    /**
     * Wait for login modal to appear
     */
    private void waitForModalToAppear() {
        wait.until(ExpectedConditions.visibilityOf(loginModal));
        wait.until(ExpectedConditions.visibilityOf(usernameField));
        wait.until(ExpectedConditions.visibilityOf(passwordField));
        logger.debug("✅ Login modal appeared");
    }
    
    /**
     * Enter username
     * @param username Username to enter
     * @return Current LoginPage instance
     */
    public LoginPage enterUsername(String username) {
        wait.until(ExpectedConditions.elementToBeClickable(usernameField));
        usernameField.clear();
        usernameField.sendKeys(username);
        logger.info("👤 Username entered: {}", username);
        return this;
    }
    
    /**
     * Enter password
     * @param password Password to enter
     * @return Current LoginPage instance
     */
    public LoginPage enterPassword(String password) {
        wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        passwordField.clear();
        passwordField.sendKeys(password);
        logger.info("🔑 Password entered: [HIDDEN]");
        return this;
    }
    
    /**
     * Click login submit button
     * @return HomePage instance
     */
    public HomePage clickLoginSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(loginSubmitButton));
        loginSubmitButton.click();
        logger.info("✅ Login submit clicked");
        
        // Wait for modal to disappear (successful login)
        wait.until(ExpectedConditions.invisibilityOf(loginModal));
        
        return new HomePage();
    }
    
    /**
     * Perform complete login operation
     * @param username Username
     * @param password Password
     * @return HomePage instance
     */
    public HomePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginSubmit();
    }
    
    /**
     * Close login modal
     * @return HomePage instance
     */
    public HomePage closeModal() {
        wait.until(ExpectedConditions.elementToBeClickable(loginCloseButton));
        loginCloseButton.click();
        logger.info("❌ Login modal closed");
        return new HomePage();
    }
    
    /**
     * Check if login modal is visible
     * @return true if modal is displayed
     */
    public boolean isModalVisible() {
        try {
            return loginModal.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get username field value
     * @return Current username field value
     */
    public String getUsernameValue() {
        return usernameField.getAttribute("value");
    }
    
    /**
     * Get password field value
     * @return Current password field value
     */
    public String getPasswordValue() {
        return passwordField.getAttribute("value");
    }
    
    /**
     * Clear all form fields
     * @return Current LoginPage instance
     */
    public LoginPage clearForm() {
        usernameField.clear();
        passwordField.clear();
        logger.debug("🧹 Login form cleared");
        return this;
    }
    
    /**
     * Check if login button is enabled
     * @return true if login button is clickable
     */
    public boolean isLoginButtonEnabled() {
        try {
            return loginSubmitButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Attempt login and wait for result (success or error)
     * @param username Username
     * @param password Password
     * @return true if login successful, false if failed
     */
    public boolean attemptLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        loginSubmitButton.click();
        
        try {
            // Wait for modal to disappear (success) or error alert
            wait.until(ExpectedConditions.invisibilityOf(loginModal));
            logger.info("✅ Login successful");
            return true;
        } catch (Exception e) {
            logger.warn("❌ Login failed");
            return false;
        }
    }
}