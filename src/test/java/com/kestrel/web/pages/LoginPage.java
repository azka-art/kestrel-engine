package com.kestrel.web.pages;

import com.kestrel.utils.DriverManager;
import com.kestrel.utils.WaitUtils;
import com.kestrel.utils.AlertHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 🦅 Kestrel Engine Login Page Object - Ultimate Enhanced Version
 * Handles authentication operations on Demoblaze with surgical precision
 * 
 * Features:
 * - Enhanced wait strategies for modal handling
 * - Comprehensive alert management integration
 * - Advanced error handling and recovery
 * - Performance optimized element interactions
 * - Detailed logging and debugging support
 * - Smart retry mechanisms for unstable elements
 * - Thread-safe operation support
 * - Validation and verification utilities
 * 
 * @author Kestrel Engine
 * @version 2.0.0 (Ultimate Enhanced Edition)
 */
public class LoginPage {
    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Enhanced timeout constants
    private static final int DEFAULT_TIMEOUT = 15;
    private static final int MODAL_TIMEOUT = 10;
    private static final int ELEMENT_TIMEOUT = 8;
    private static final int QUICK_TIMEOUT = 3;
    
    // Login Modal Elements - Enhanced with multiple locator strategies
    @FindBy(id = "logInModal")
    private WebElement loginModal;
    
    @FindBy(xpath = "//div[@id='logInModal'][@style='display: block;']")
    private WebElement visibleLoginModal;
    
    @FindBy(id = "loginusername")
    private WebElement usernameField;
    
    @FindBy(id = "loginpassword")
    private WebElement passwordField;
    
    @FindBy(xpath = "//button[text()='Log in']")
    private WebElement loginSubmitButton;
    
    @FindBy(xpath = "//div[@id='logInModal']//button[contains(@class,'btn-primary')]")
    private WebElement loginSubmitButtonAlt;
    
    @FindBy(xpath = "//div[@id='logInModal']//button[@class='close']")
    private WebElement loginCloseButton;
    
    @FindBy(xpath = "//div[@id='logInModal']//span[text()='×']")
    private WebElement loginCloseX;
    
    @FindBy(xpath = "//div[@id='logInModal']//button[@data-dismiss='modal']")
    private WebElement loginCancelButton;
    
    // Modal title and validation elements
    @FindBy(xpath = "//div[@id='logInModal']//h4[@class='modal-title']")
    private WebElement modalTitle;
    
    @FindBy(xpath = "//div[@id='logInModal']//form")
    private WebElement loginForm;
    
    // Enhanced validation elements
    @FindBy(xpath = "//div[@id='logInModal']//div[@class='modal-header']")
    private WebElement modalHeader;
    
    @FindBy(xpath = "//div[@id='logInModal']//div[@class='modal-body']")
    private WebElement modalBody;
    
    @FindBy(xpath = "//div[@id='logInModal']//div[@class='modal-footer']")
    private WebElement modalFooter;
    
    /**
     * Constructor - Enhanced initialization with comprehensive validation
     */
    public LoginPage() {
        initializePage();
    }
    
    /**
     * Enhanced page initialization with retry logic
     */
    private void initializePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
        
        logger.info("🦅 Initializing LoginPage with enhanced validation");
        
        // Enhanced modal appearance waiting with retry
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                waitForModalToAppear();
                logger.info("✅ LoginPage initialized successfully on attempt {}", attempt);
                return;
            } catch (Exception e) {
                logger.warn("⚠️ LoginPage initialization attempt {} failed: {}", attempt, e.getMessage());
                if (attempt == 3) {
                    throw new RuntimeException("Failed to initialize LoginPage after 3 attempts", e);
                }
                
                try {
                    Thread.sleep(1000 * attempt); // Progressive delay
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    /**
     * Enhanced modal appearance waiting with multiple validation points
     */
    private void waitForModalToAppear() {
        logger.debug("🔍 Waiting for login modal to appear...");
        
        try {
            // Primary wait - modal visibility
            wait.until(ExpectedConditions.visibilityOf(loginModal));
            logger.debug("✅ Login modal is visible");
            
            // Secondary validation - modal is actually displayed
            wait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOf(loginModal),
                ExpectedConditions.attributeContains(loginModal, "style", "block")
            ));
            logger.debug("✅ Login modal style validated");
            
            // Form elements validation
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            wait.until(ExpectedConditions.visibilityOf(passwordField));
            wait.until(ExpectedConditions.elementToBeClickable(loginSubmitButton));
            
            logger.debug("✅ Login modal form elements ready");
            
            // Additional validation - modal content
            if (modalTitle.isDisplayed()) {
                String title = modalTitle.getText();
                logger.debug("📝 Modal title: {}", title);
            }
            
            // Brief stabilization wait
            Thread.sleep(500);
            
        } catch (TimeoutException e) {
            logger.error("❌ Login modal failed to appear within timeout");
            throw new RuntimeException("Login modal did not appear", e);
        } catch (Exception e) {
            logger.error("❌ Error waiting for login modal: {}", e.getMessage());
            throw new RuntimeException("Login modal validation failed", e);
        }
    }
    
    /**
     * Enhanced username entry with validation and error handling
     * @param username Username to enter
     * @return Current LoginPage instance
     */
    public LoginPage enterUsername(String username) {
        logger.info("👤 Entering username: {}", username);
        
        try {
            // Enhanced element readiness validation
            WaitUtils.waitForElementReady(driver, usernameField, ELEMENT_TIMEOUT);
            
            // Clear field with validation
            usernameField.clear();
            
            // Verify field is actually cleared
            WaitUtils.waitForCondition(
                driver -> usernameField.getAttribute("value").isEmpty(),
                QUICK_TIMEOUT
            );
            
            // Enter username with validation
            usernameField.sendKeys(username);
            
            // Verify username was entered correctly
            String enteredValue = usernameField.getAttribute("value");
            if (!username.equals(enteredValue)) {
                logger.warn("⚠️ Username verification failed. Expected: {}, Got: {}", username, enteredValue);
                // Retry once
                usernameField.clear();
                Thread.sleep(200);
                usernameField.sendKeys(username);
            }
            
            logger.debug("✅ Username entered and verified: {}", username);
            
        } catch (Exception e) {
            logger.error("❌ Failed to enter username: {}", e.getMessage());
            throw new RuntimeException("Username entry failed", e);
        }
        
        return this;
    }
    
    /**
     * Enhanced password entry with validation and security
     * @param password Password to enter
     * @return Current LoginPage instance
     */
    public LoginPage enterPassword(String password) {
        logger.info("🔑 Entering password: [HIDDEN]");
        
        try {
            // Enhanced element readiness validation
            WaitUtils.waitForElementReady(driver, passwordField, ELEMENT_TIMEOUT);
            
            // Clear field with validation
            passwordField.clear();
            
            // Verify field is actually cleared
            WaitUtils.waitForCondition(
                driver -> passwordField.getAttribute("value").isEmpty(),
                QUICK_TIMEOUT
            );
            
            // Enter password with validation
            passwordField.sendKeys(password);
            
            // Verify password was entered (without logging actual value)
            String enteredValue = passwordField.getAttribute("value");
            if (enteredValue.length() != password.length()) {
                logger.warn("⚠️ Password length verification failed. Expected: {}, Got: {}", password.length(), enteredValue.length());
                // Retry once
                passwordField.clear();
                Thread.sleep(200);
                passwordField.sendKeys(password);
            }
            
            logger.debug("✅ Password entered and verified (length: {})", password.length());
            
        } catch (Exception e) {
            logger.error("❌ Failed to enter password: {}", e.getMessage());
            throw new RuntimeException("Password entry failed", e);
        }
        
        return this;
    }
    
    /**
     * Enhanced login submit with comprehensive result handling
     * @return HomePage instance if successful
     */
    public HomePage clickLoginSubmit() {
        logger.info("🎯 Clicking login submit button");
        
        try {
            // Enhanced button readiness validation
            WaitUtils.waitForElementClickable(driver, loginSubmitButton, ELEMENT_TIMEOUT);
            
            // Click with retry mechanism
            boolean clickSuccessful = false;
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    loginSubmitButton.click();
                    clickSuccessful = true;
                    logger.debug("✅ Login button clicked successfully on attempt {}", attempt);
                    break;
                } catch (Exception e) {
                    logger.warn("⚠️ Login button click attempt {} failed: {}", attempt, e.getMessage());
                    if (attempt == 2) {
                        throw e;
                    }
                    Thread.sleep(500);
                }
            }
            
            if (!clickSuccessful) {
                throw new RuntimeException("Failed to click login button after retries");
            }
            
            // Handle login result with timeout
            return handleLoginResult();
            
        } catch (Exception e) {
            logger.error("❌ Login submit failed: {}", e.getMessage());
            throw new RuntimeException("Login submission failed", e);
        }
    }
    
    /**
     * Enhanced login result handling with alert management
     * @return HomePage instance if successful
     */
    private HomePage handleLoginResult() {
        logger.debug("🔍 Handling login result...");
        
        try {
            // First, handle any alerts that might appear
            String alertText = AlertHandler.acceptAlert(driver, QUICK_TIMEOUT);
            
            if (alertText != null) {
                // Alert appeared - likely login failure
                logger.warn("⚠️ Login alert received: {}", alertText);
                
                if (alertText.toLowerCase().contains("wrong") || 
                    alertText.toLowerCase().contains("user") ||
                    alertText.toLowerCase().contains("password")) {
                    // Login failed - modal should still be visible
                    logger.info("❌ Login failed: {}", alertText);
                    return new HomePage(); // Return HomePage but login failed
                }
            }
            
            // No alert or success alert - wait for modal to disappear
            boolean modalDisappeared = WaitUtils.waitForElementInvisible(driver, loginModal, MODAL_TIMEOUT);
            
            if (modalDisappeared) {
                logger.info("✅ Login successful - modal disappeared");
                return new HomePage();
            } else {
                logger.warn("⚠️ Login modal still visible - possible failure");
                return new HomePage();
            }
            
        } catch (Exception e) {
            logger.warn("⚠️ Error handling login result: {}", e.getMessage());
            // Return HomePage anyway - let calling code handle validation
            return new HomePage();
        }
    }
    
    /**
     * Enhanced complete login operation with comprehensive error handling
     * @param username Username
     * @param password Password
     * @return HomePage instance
     */
    public HomePage login(String username, String password) {
        logger.info("🔐 Performing login operation for user: {}", username);
        
        try {
            // Enhanced login sequence with validation
            enterUsername(username);
            
            // Brief pause between fields for stability
            Thread.sleep(200);
            
            enterPassword(password);
            
            // Brief pause before submit for form validation
            Thread.sleep(300);
            
            return clickLoginSubmit();
            
        } catch (Exception e) {
            logger.error("❌ Login operation failed for user {}: {}", username, e.getMessage());
            
            // Attempt cleanup
            try {
                AlertHandler.cleanupAnyAlerts(driver);
                clearForm();
            } catch (Exception cleanup) {
                logger.warn("⚠️ Cleanup failed: {}", cleanup.getMessage());
            }
            
            throw new RuntimeException("Login operation failed", e);
        }
    }
    
    /**
     * Enhanced attempt login with detailed result tracking
     * @param username Username
     * @param password Password
     * @return true if login successful, false if failed
     */
    public boolean attemptLogin(String username, String password) {
        logger.info("🎯 Attempting login for user: {}", username);
        
        try {
            enterUsername(username);
            Thread.sleep(200);
            enterPassword(password);
            Thread.sleep(300);
            
            // Click submit and evaluate result
            WaitUtils.waitForElementClickable(driver, loginSubmitButton, ELEMENT_TIMEOUT);
            loginSubmitButton.click();
            
            // Enhanced result evaluation
            String alertText = AlertHandler.acceptAlert(driver, 5);
            
            if (alertText != null) {
                // Alert appeared
                if (alertText.toLowerCase().contains("wrong") || 
                    alertText.toLowerCase().contains("user") ||
                    alertText.toLowerCase().contains("password")) {
                    logger.info("❌ Login failed with alert: {}", alertText);
                    return false;
                } else {
                    logger.info("ℹ️ Unexpected alert during login: {}", alertText);
                }
            }
            
            // Check modal visibility
            boolean modalGone = WaitUtils.waitForElementInvisible(driver, loginModal, MODAL_TIMEOUT);
            
            if (modalGone) {
                logger.info("✅ Login attempt successful - modal disappeared");
                return true;
            } else {
                logger.info("❌ Login attempt failed - modal still visible");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("❌ Login attempt error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced modal closing with validation
     * @return HomePage instance
     */
    public HomePage closeModal() {
        logger.info("❌ Closing login modal");
        
        try {
            // Try multiple close methods
            if (WaitUtils.waitForElementClickable(driver, loginCloseButton, QUICK_TIMEOUT)) {
                loginCloseButton.click();
            } else if (WaitUtils.waitForElementClickable(driver, loginCloseX, QUICK_TIMEOUT)) {
                loginCloseX.click();
            } else if (WaitUtils.waitForElementClickable(driver, loginCancelButton, QUICK_TIMEOUT)) {
                loginCancelButton.click();
            } else {
                logger.warn("⚠️ No close button found, trying ESC key");
                loginModal.sendKeys(org.openqa.selenium.Keys.ESCAPE);
            }
            
            // Verify modal closed
            boolean modalClosed = WaitUtils.waitForElementInvisible(driver, loginModal, MODAL_TIMEOUT);
            
            if (modalClosed) {
                logger.info("✅ Login modal closed successfully");
            } else {
                logger.warn("⚠️ Login modal may not have closed properly");
            }
            
            return new HomePage();
            
        } catch (Exception e) {
            logger.error("❌ Failed to close login modal: {}", e.getMessage());
            return new HomePage();
        }
    }
    
    /**
     * Enhanced modal visibility check with multiple validation points
     * @return true if modal is displayed and functional
     */
    public boolean isModalVisible() {
        try {
            // Multi-point validation
            boolean basicVisibility = loginModal.isDisplayed();
            boolean styleCheck = loginModal.getAttribute("style").contains("block");
            boolean formVisible = loginForm.isDisplayed();
            boolean fieldsVisible = usernameField.isDisplayed() && passwordField.isDisplayed();
            
            boolean fullyVisible = basicVisibility && styleCheck && formVisible && fieldsVisible;
            
            logger.debug("🔍 Modal visibility check: basic={}, style={}, form={}, fields={}, overall={}", 
                        basicVisibility, styleCheck, formVisible, fieldsVisible, fullyVisible);
            
            return fullyVisible;
            
        } catch (Exception e) {
            logger.debug("🔍 Modal visibility check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced form validation and clearing
     * @return Current LoginPage instance
     */
    public LoginPage clearForm() {
        logger.debug("🧹 Clearing login form");
        
        try {
            if (WaitUtils.waitForElementVisible(driver, usernameField, QUICK_TIMEOUT)) {
                usernameField.clear();
            }
            
            if (WaitUtils.waitForElementVisible(driver, passwordField, QUICK_TIMEOUT)) {
                passwordField.clear();
            }
            
            // Verify fields are cleared
            Thread.sleep(200);
            
            String usernameValue = usernameField.getAttribute("value");
            String passwordValue = passwordField.getAttribute("value");
            
            if (!usernameValue.isEmpty() || !passwordValue.isEmpty()) {
                logger.warn("⚠️ Form not fully cleared, retrying...");
                usernameField.clear();
                passwordField.clear();
            }
            
            logger.debug("✅ Login form cleared successfully");
            
        } catch (Exception e) {
            logger.warn("⚠️ Error clearing form: {}", e.getMessage());
        }
        
        return this;
    }
    
    // ===== ENHANCED GETTER METHODS FOR WEBSTEPDEFINITIONS INTEGRATION =====
    
    /**
     * Get the login modal element for external wait operations
     * Required by WebStepDefinitions for WaitUtils integration
     * @return WebElement representing the login modal
     */
    public WebElement getLoginModal() {
        return loginModal;
    }
    
    /**
     * Get the username field element for external operations
     * Required by WebStepDefinitions for enhanced wait strategies
     * @return WebElement representing the username field
     */
    public WebElement getUsernameField() {
        return usernameField;
    }
    
    /**
     * Get the password field element for external operations
     * Required by WebStepDefinitions for validation and waits
     * @return WebElement representing the password field
     */
    public WebElement getPasswordField() {
        return passwordField;
    }
    
    /**
     * Get the login button element for external operations
     * Required by WebStepDefinitions for clickability checks
     * @return WebElement representing the login button
     */
    public WebElement getLoginButton() {
        return loginSubmitButton;
    }
    
    /**
     * Get the close button element for modal closing operations
     * @return WebElement representing the close button
     */
    public WebElement getCloseButton() {
        return loginCloseButton;
    }
    
    /**
     * Get the modal header element for validation
     * @return WebElement representing the modal header
     */
    public WebElement getModalHeader() {
        return modalHeader;
    }
    
    /**
     * Get the modal body element for content validation
     * @return WebElement representing the modal body
     */
    public WebElement getModalBody() {
        return modalBody;
    }
    
    /**
     * Get the modal footer element for button validation
     * @return WebElement representing the modal footer
     */
    public WebElement getModalFooter() {
        return modalFooter;
    }
    
    /**
     * Get the login form element for form validation
     * @return WebElement representing the login form
     */
    public WebElement getLoginForm() {
        return loginForm;
    }
    
    /**
     * Enhanced username field value retrieval
     * @return Current username field value
     */
    public String getUsernameValue() {
        try {
            return usernameField.getAttribute("value");
        } catch (Exception e) {
            logger.warn("⚠️ Could not get username value: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Enhanced password field value retrieval (for validation only)
     * @return Current password field value length (for security)
     */
    public int getPasswordLength() {
        try {
            return passwordField.getAttribute("value").length();
        } catch (Exception e) {
            logger.warn("⚠️ Could not get password length: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Enhanced login button state check
     * @return true if login button is enabled and clickable
     */
    public boolean isLoginButtonEnabled() {
        try {
            return loginSubmitButton.isEnabled() && 
                   loginSubmitButton.isDisplayed() &&
                   WaitUtils.waitForElementClickable(driver, loginSubmitButton, 1);
        } catch (Exception e) {
            logger.debug("🔍 Login button not enabled: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get modal title text
     * @return Modal title text
     */
    public String getModalTitle() {
        try {
            return modalTitle.getText();
        } catch (Exception e) {
            logger.warn("⚠️ Could not get modal title: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Enhanced form validation check
     * @return true if form is ready for submission
     */
    public boolean isFormReady() {
        try {
            boolean usernameReady = !getUsernameValue().isEmpty();
            boolean passwordReady = getPasswordLength() > 0;
            boolean buttonReady = isLoginButtonEnabled();
            
            boolean formReady = usernameReady && passwordReady && buttonReady;
            
            logger.debug("🔍 Form readiness: username={}, password={}, button={}, overall={}", 
                        usernameReady, passwordReady, buttonReady, formReady);
            
            return formReady;
            
        } catch (Exception e) {
            logger.debug("🔍 Form readiness check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get comprehensive page state information
     * @return Page state description
     */
    public String getPageState() {
        StringBuilder state = new StringBuilder();
        state.append("LoginPage State: ");
        state.append("modal_visible=").append(isModalVisible());
        state.append(", form_ready=").append(isFormReady());
        state.append(", username_length=").append(getUsernameValue().length());
        state.append(", password_length=").append(getPasswordLength());
        state.append(", button_enabled=").append(isLoginButtonEnabled());
        
        return state.toString();
    }
    
    /**
     * Enhanced page load validation
     * @return true if page is fully loaded and functional
     */
    public boolean isPageLoaded() {
        try {
            return isModalVisible() && 
                   usernameField.isDisplayed() && 
                   passwordField.isDisplayed() && 
                   loginSubmitButton.isDisplayed();
        } catch (Exception e) {
            logger.debug("🔍 Page load check failed: {}", e.getMessage());
            return false;
        }
    }
}