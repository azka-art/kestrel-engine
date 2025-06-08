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
 * 🦅 Kestrel Engine Sign Up Page Object - Enhanced for WebStepDefinitions Integration
 * Handles user registration operations on Demoblaze with comprehensive getter methods
 * 
 * Features:
 * - Enhanced wait strategies for modal handling
 * - Alert management integration for signup results
 * - Comprehensive getter methods for WebStepDefinitions
 * - Advanced error handling and validation
 * - Enhanced logging and debugging support
 * 
 * @author Kestrel Engine
 * @version 2.0.0 (Enhanced for Compilation Fix)
 */
public class SignUpPage {
    private static final Logger logger = LoggerFactory.getLogger(SignUpPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Enhanced timeout constants
    private static final int DEFAULT_TIMEOUT = 15;
    private static final int MODAL_TIMEOUT = 10;
    private static final int ELEMENT_TIMEOUT = 8;
    private static final int QUICK_TIMEOUT = 3;
    
    // Sign Up Modal Elements - Enhanced with multiple locator strategies
    @FindBy(id = "signInModal")
    private WebElement signUpModal;
    
    @FindBy(xpath = "//div[@id='signInModal'][@style='display: block;']")
    private WebElement visibleSignUpModal;
    
    @FindBy(id = "sign-username")
    private WebElement usernameField;
    
    @FindBy(id = "sign-password")
    private WebElement passwordField;
    
    @FindBy(xpath = "//button[text()='Sign up']")
    private WebElement signUpButton;
    
    @FindBy(xpath = "//div[@id='signInModal']//button[contains(@class,'btn-primary')]")
    private WebElement signUpButtonAlt;
    
    @FindBy(xpath = "//div[@id='signInModal']//button[@class='close']")
    private WebElement signUpCloseButton;
    
    @FindBy(xpath = "//div[@id='signInModal']//span[text()='×']")
    private WebElement signUpCloseX;
    
    @FindBy(xpath = "//div[@id='signInModal']//button[@data-dismiss='modal']")
    private WebElement signUpCancelButton;
    
    // Modal validation elements
    @FindBy(xpath = "//div[@id='signInModal']//h4[@class='modal-title']")
    private WebElement modalTitle;
    
    @FindBy(xpath = "//div[@id='signInModal']//form")
    private WebElement signUpForm;
    
    @FindBy(xpath = "//div[@id='signInModal']//div[@class='modal-header']")
    private WebElement modalHeader;
    
    @FindBy(xpath = "//div[@id='signInModal']//div[@class='modal-body']")
    private WebElement modalBody;
    
    @FindBy(xpath = "//div[@id='signInModal']//div[@class='modal-footer']")
    private WebElement modalFooter;
    
    /**
     * Constructor - Enhanced initialization with comprehensive validation
     */
    public SignUpPage() {
        initializePage();
    }
    
    /**
     * Enhanced page initialization with retry logic
     */
    private void initializePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
        
        logger.info("🦅 Initializing SignUpPage with enhanced validation");
        
        // Enhanced modal appearance waiting with retry
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                waitForModalToAppear();
                logger.info("✅ SignUpPage initialized successfully on attempt {}", attempt);
                return;
            } catch (Exception e) {
                logger.warn("⚠️ SignUpPage initialization attempt {} failed: {}", attempt, e.getMessage());
                if (attempt == 3) {
                    throw new RuntimeException("Failed to initialize SignUpPage after 3 attempts", e);
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
        logger.debug("🔍 Waiting for signup modal to appear...");
        
        try {
            // Primary wait - modal visibility
            wait.until(ExpectedConditions.visibilityOf(signUpModal));
            logger.debug("✅ Signup modal is visible");
            
            // Secondary validation - modal is actually displayed
            wait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOf(signUpModal),
                ExpectedConditions.attributeContains(signUpModal, "style", "block")
            ));
            logger.debug("✅ Signup modal style validated");
            
            // Form elements validation
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            wait.until(ExpectedConditions.visibilityOf(passwordField));
            wait.until(ExpectedConditions.elementToBeClickable(signUpButton));
            
            logger.debug("✅ Signup modal form elements ready");
            
            // Additional validation - modal content
            if (modalTitle.isDisplayed()) {
                String title = modalTitle.getText();
                logger.debug("📝 Modal title: {}", title);
            }
            
            // Brief stabilization wait
            Thread.sleep(500);
            
        } catch (TimeoutException e) {
            logger.error("❌ Signup modal failed to appear within timeout");
            throw new RuntimeException("Signup modal did not appear", e);
        } catch (Exception e) {
            logger.error("❌ Error waiting for signup modal: {}", e.getMessage());
            throw new RuntimeException("Signup modal validation failed", e);
        }
    }
    
    /**
     * Enhanced username entry with validation and error handling
     * @param username Username to enter
     * @return Current SignUpPage instance
     */
    public SignUpPage enterUsername(String username) {
        logger.info("👤 Entering signup username: {}", username);
        
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
            
            logger.debug("✅ Signup username entered and verified: {}", username);
            
        } catch (Exception e) {
            logger.error("❌ Failed to enter signup username: {}", e.getMessage());
            throw new RuntimeException("Signup username entry failed", e);
        }
        
        return this;
    }
    
    /**
     * Enhanced password entry with validation and security
     * @param password Password to enter
     * @return Current SignUpPage instance
     */
    public SignUpPage enterPassword(String password) {
        logger.info("🔑 Entering signup password: [HIDDEN]");
        
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
            
            logger.debug("✅ Signup password entered and verified (length: {})", password.length());
            
        } catch (Exception e) {
            logger.error("❌ Failed to enter signup password: {}", e.getMessage());
            throw new RuntimeException("Signup password entry failed", e);
        }
        
        return this;
    }
    
    /**
     * Enhanced signup submit with comprehensive result handling
     * @return HomePage instance
     */
    public HomePage clickSignUpSubmit() {
        logger.info("🎯 Clicking signup submit button");
        
        try {
            // Enhanced button readiness validation
            WaitUtils.waitForElementClickable(driver, signUpButton, ELEMENT_TIMEOUT);
            
            // Click with retry mechanism
            boolean clickSuccessful = false;
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    signUpButton.click();
                    clickSuccessful = true;
                    logger.debug("✅ Signup button clicked successfully on attempt {}", attempt);
                    break;
                } catch (Exception e) {
                    logger.warn("⚠️ Signup button click attempt {} failed: {}", attempt, e.getMessage());
                    if (attempt == 2) {
                        throw e;
                    }
                    Thread.sleep(500);
                }
            }
            
            if (!clickSuccessful) {
                throw new RuntimeException("Failed to click signup button after retries");
            }
            
            // Handle signup result with timeout
            return handleSignUpResult();
            
        } catch (Exception e) {
            logger.error("❌ Signup submit failed: {}", e.getMessage());
            throw new RuntimeException("Signup submission failed", e);
        }
    }
    
    /**
     * Enhanced signup result handling with alert management
     * @return HomePage instance
     */
    private HomePage handleSignUpResult() {
        logger.debug("🔍 Handling signup result...");
        
        try {
            // Handle signup alert with detailed tracking
            String alertText = AlertHandler.acceptAlert(driver, 10);
            
            if (alertText != null) {
                if (alertText.toLowerCase().contains("successful")) {
                    logger.info("✅ Signup successful: {}", alertText);
                } else {
                    logger.warn("⚠️ Signup failed or had issues: {}", alertText);
                }
            }
            
            // Wait for modal to disappear (successful signup) or stay visible (failure)
            boolean modalDisappeared = WaitUtils.waitForElementInvisible(driver, signUpModal, MODAL_TIMEOUT);
            
            if (modalDisappeared) {
                logger.info("✅ Signup completed - modal disappeared");
            } else {
                logger.warn("⚠️ Signup modal still visible - possible failure or validation error");
            }
            
            return new HomePage();
            
        } catch (Exception e) {
            logger.warn("⚠️ Error handling signup result: {}", e.getMessage());
            // Return HomePage anyway - let calling code handle validation
            return new HomePage();
        }
    }
    
    /**
     * Enhanced complete signup operation with comprehensive error handling
     * @param username Username
     * @param password Password
     * @return HomePage instance
     */
    public HomePage signUp(String username, String password) {
        logger.info("📝 Performing signup operation for user: {}", username);
        
        try {
            // Enhanced signup sequence with validation
            enterUsername(username);
            
            // Brief pause between fields for stability
            Thread.sleep(200);
            
            enterPassword(password);
            
            // Brief pause before submit for form validation
            Thread.sleep(300);
            
            return clickSignUpSubmit();
            
        } catch (Exception e) {
            logger.error("❌ Signup operation failed for user {}: {}", username, e.getMessage());
            
            // Attempt cleanup
            try {
                AlertHandler.cleanupAnyAlerts(driver);
                clearForm();
            } catch (Exception cleanup) {
                logger.warn("⚠️ Cleanup failed: {}", cleanup.getMessage());
            }
            
            throw new RuntimeException("Signup operation failed", e);
        }
    }
    
    /**
     * Enhanced attempt signup with detailed result tracking
     * @param username Username
     * @param password Password
     * @return true if signup successful, false if failed
     */
    public boolean attemptSignUp(String username, String password) {
        logger.info("🎯 Attempting signup for user: {}", username);
        
        try {
            enterUsername(username);
            Thread.sleep(200);
            enterPassword(password);
            Thread.sleep(300);
            
            // Click submit and evaluate result
            WaitUtils.waitForElementClickable(driver, signUpButton, ELEMENT_TIMEOUT);
            signUpButton.click();
            
            // Enhanced result evaluation
            String alertText = AlertHandler.acceptAlert(driver, 10);
            
            if (alertText != null) {
                if (alertText.toLowerCase().contains("successful")) {
                    logger.info("✅ Signup attempt successful: {}", alertText);
                    return true;
                } else {
                    logger.info("❌ Signup attempt failed: {}", alertText);
                    return false;
                }
            }
            
            // Check modal visibility as alternative success indicator
            boolean modalGone = WaitUtils.waitForElementInvisible(driver, signUpModal, MODAL_TIMEOUT);
            
            if (modalGone) {
                logger.info("✅ Signup attempt successful - modal disappeared");
                return true;
            } else {
                logger.info("❌ Signup attempt failed - modal still visible");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("❌ Signup attempt error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced modal closing with validation
     * @return HomePage instance
     */
    public HomePage closeModal() {
        logger.info("❌ Closing signup modal");
        
        try {
            // Try multiple close methods
            if (WaitUtils.waitForElementClickable(driver, signUpCloseButton, QUICK_TIMEOUT)) {
                signUpCloseButton.click();
            } else if (WaitUtils.waitForElementClickable(driver, signUpCloseX, QUICK_TIMEOUT)) {
                signUpCloseX.click();
            } else if (WaitUtils.waitForElementClickable(driver, signUpCancelButton, QUICK_TIMEOUT)) {
                signUpCancelButton.click();
            } else {
                logger.warn("⚠️ No close button found, trying ESC key");
                signUpModal.sendKeys(org.openqa.selenium.Keys.ESCAPE);
            }
            
            // Verify modal closed
            boolean modalClosed = WaitUtils.waitForElementInvisible(driver, signUpModal, MODAL_TIMEOUT);
            
            if (modalClosed) {
                logger.info("✅ Signup modal closed successfully");
            } else {
                logger.warn("⚠️ Signup modal may not have closed properly");
            }
            
            return new HomePage();
            
        } catch (Exception e) {
            logger.error("❌ Failed to close signup modal: {}", e.getMessage());
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
            boolean basicVisibility = signUpModal.isDisplayed();
            boolean styleCheck = signUpModal.getAttribute("style").contains("block");
            boolean formVisible = signUpForm.isDisplayed();
            boolean fieldsVisible = usernameField.isDisplayed() && passwordField.isDisplayed();
            
            boolean fullyVisible = basicVisibility && styleCheck && formVisible && fieldsVisible;
            
            logger.debug("🔍 Signup modal visibility check: basic={}, style={}, form={}, fields={}, overall={}", 
                        basicVisibility, styleCheck, formVisible, fieldsVisible, fullyVisible);
            
            return fullyVisible;
            
        } catch (Exception e) {
            logger.debug("🔍 Signup modal visibility check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced form validation and clearing
     * @return Current SignUpPage instance
     */
    public SignUpPage clearForm() {
        logger.debug("🧹 Clearing signup form");
        
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
            
            logger.debug("✅ Signup form cleared successfully");
            
        } catch (Exception e) {
            logger.warn("⚠️ Error clearing form: {}", e.getMessage());
        }
        
        return this;
    }
    
    // ===== REQUIRED GETTER METHODS FOR WEBSTEPDEFINITIONS INTEGRATION =====
    
    /**
     * Get the signup modal element for external wait operations
     * Required by WebStepDefinitions for WaitUtils integration
     * @return WebElement representing the signup modal
     */
    public WebElement getSignUpModal() {
        return signUpModal;
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
     * Get the signup button element for external operations
     * Required by WebStepDefinitions for clickability checks
     * @return WebElement representing the signup button
     */
    public WebElement getSignUpButton() {
        return signUpButton;
    }
    
    /**
     * Get the close button element for modal closing operations
     * @return WebElement representing the close button
     */
    public WebElement getCloseButton() {
        return signUpCloseButton;
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
     * Get the signup form element for form validation
     * @return WebElement representing the signup form
     */
    public WebElement getSignUpForm() {
        return signUpForm;
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
     * Enhanced signup button state check
     * @return true if signup button is enabled and clickable
     */
    public boolean isSignUpButtonEnabled() {
        try {
            return signUpButton.isEnabled() && 
                   signUpButton.isDisplayed() &&
                   WaitUtils.waitForElementClickable(driver, signUpButton, 1);
        } catch (Exception e) {
            logger.debug("🔍 Signup button not enabled: {}", e.getMessage());
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
            boolean buttonReady = isSignUpButtonEnabled();
            
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
        state.append("SignUpPage State: ");
        state.append("modal_visible=").append(isModalVisible());
        state.append(", form_ready=").append(isFormReady());
        state.append(", username_length=").append(getUsernameValue().length());
        state.append(", password_length=").append(getPasswordLength());
        state.append(", button_enabled=").append(isSignUpButtonEnabled());
        
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
                   signUpButton.isDisplayed();
        } catch (Exception e) {
            logger.debug("🔍 Page load check failed: {}", e.getMessage());
            return false;
        }
    }
}
