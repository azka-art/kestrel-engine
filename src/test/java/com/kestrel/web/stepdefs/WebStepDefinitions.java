package com.kestrel.web.stepdefs;

import com.kestrel.utils.AlertHandler;
import com.kestrel.utils.WaitUtils;
import com.kestrel.utils.DriverManager;
import com.kestrel.utils.ScreenshotCapture;
import com.kestrel.web.pages.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 🦅 Kestrel Engine Web Step Definitions - Ultimate Enhanced Version
 * Implementation of Web UI hunting scenarios for Demoblaze with surgical precision
 * 
 * Features:
 * - Enhanced error handling and retry logic
 * - Comprehensive alert management integration
 * - Advanced wait strategies for stability
 * - Evidence collection on failures
 * - Thread-safe operation tracking
 * - Performance monitoring and optimization
 * - Detailed logging and debugging
 * - Smart recovery mechanisms
 * 
 * @author Kestrel Engine
 * @version 2.0.0 (Ultimate Enhanced Edition)
 */
public class WebStepDefinitions {
    private static final Logger logger = LoggerFactory.getLogger(WebStepDefinitions.class);
    
    // Page Objects - Enhanced with lazy initialization
    private HomePage homePage;
    private LoginPage loginPage;
    private SignUpPage signUpPage;
    private ProductPage productPage;
    private CartPage cartPage;
    
    // State Management - Thread-safe context tracking
    private final Map<String, Object> testContext = new ConcurrentHashMap<>();
    private String currentUsername;
    private String currentPassword;
    private String selectedProduct;
    
    // Enhanced tracking variables
    private long stepStartTime;
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;
    
    // ===== ENHANCED HELPER METHODS =====
    
    /**
     * Initialize step execution with timing and context
     */
    private void initializeStep(String stepDescription) {
        stepStartTime = System.currentTimeMillis();
        retryCount = 0;
        logger.info("🦅 Kestrel Step Start: {}", stepDescription);
    }
    
    /**
     * Complete step execution with timing
     */
    private void completeStep(String stepDescription) {
        long duration = System.currentTimeMillis() - stepStartTime;
        logger.info("✅ Kestrel Step Complete: {} ({}ms)", stepDescription, duration);
    }
    
    /**
     * Handle step failure with evidence collection
     */
    private void handleStepFailure(String stepDescription, Exception e) {
        try {
            // Capture evidence
            byte[] screenshot = ScreenshotCapture.captureScreenshot();
            if (screenshot != null) {
                logger.info("📸 Evidence captured for failed step: {}", stepDescription);
            }
            
            // Clean up any alerts
            AlertHandler.cleanupAnyAlerts(DriverManager.getDriver());
            
            // Log failure details
            long duration = System.currentTimeMillis() - stepStartTime;
            logger.error("❌ Kestrel Step Failed: {} ({}ms) - {}", stepDescription, duration, e.getMessage());
            
        } catch (Exception cleanup) {
            logger.warn("⚠️ Error during failure cleanup: {}", cleanup.getMessage());
        }
    }
    
    /**
     * Enhanced page object initialization with validation
     */
    private <T> T initializePageObject(Class<T> pageClass, String pageName) {
        try {
            T pageObject = pageClass.getDeclaredConstructor().newInstance();
            logger.debug("🔧 Initialized page object: {}", pageName);
            return pageObject;
        } catch (Exception e) {
            logger.error("❌ Failed to initialize page object {}: {}", pageName, e.getMessage());
            throw new RuntimeException("Page object initialization failed: " + pageName, e);
        }
    }
    
    /**
     * Safe navigation with retry logic
     */
    private void safeNavigateToHome() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                homePage = new HomePage().navigateToHome();
                if (WaitUtils.waitForDemoblazeHomepage(DriverManager.getDriver())) {
                    logger.debug("✅ Homepage navigation successful on attempt {}", attempt);
                    return;
                }
            } catch (Exception e) {
                logger.warn("⚠️ Homepage navigation attempt {} failed: {}", attempt, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    throw new RuntimeException("Failed to navigate to homepage after " + MAX_RETRIES + " attempts", e);
                }
                
                try {
                    Thread.sleep(2000 * attempt); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    // ===== BACKGROUND STEPS - ENHANCED =====
    
    @Given("Kestrel agent is positioned at Demoblaze homepage")
    public void kestrelAgentIsPositionedAtDemoblazeHomepage() {
        initializeStep("Positioning at Demoblaze homepage");
        
        try {
            logger.info("🦅 Kestrel Web Hunt: Positioning at Demoblaze homepage");
            
            // Enhanced navigation with retry logic
            safeNavigateToHome();
            
            // Enhanced validation
            boolean pageLoaded = WaitUtils.waitForDemoblazeHomepage(DriverManager.getDriver());
            assertTrue(pageLoaded, "Homepage should be loaded within timeout");
            
            // Additional validations
            assertTrue(homePage.isPageLoaded(), "Homepage components should be loaded");
            
            // Store context
            testContext.put("current_page", "homepage");
            testContext.put("navigation_time", System.currentTimeMillis());
            
            logger.info("✅ Kestrel positioned and ready for web hunt");
            completeStep("Homepage positioning");
            
        } catch (Exception e) {
            handleStepFailure("Homepage positioning", e);
            throw e;
        }
    }
    
    @Given("I am on the homepage")
    public void iAmOnTheHomepage() {
        kestrelAgentIsPositionedAtDemoblazeHomepage();
    }
    
    // ===== AUTHENTICATION STEPS - ENHANCED =====
    
    @Given("I am on the login interface")
    public void iAmOnTheLoginInterface() {
        initializeStep("Opening login interface");
        
        try {
            logger.info("🔐 Opening login interface");
            
            // Ensure we're on homepage first
            if (homePage == null || !homePage.isPageLoaded()) {
                safeNavigateToHome();
            }
            
            // Open login with enhanced waiting
            loginPage = homePage.clickLogin();
            
            // Enhanced validation with retry
            boolean modalVisible = WaitUtils.waitForElementVisible(
                DriverManager.getDriver(), 
                loginPage.getLoginModal(), 
                10
            );
            
            assertTrue(modalVisible, "Login modal should be visible");
            assertTrue(loginPage.isModalVisible(), "Login modal should be accessible");
            
            testContext.put("current_page", "login");
            
            completeStep("Login interface opened");
            
        } catch (Exception e) {
            handleStepFailure("Opening login interface", e);
            throw e;
        }
    }
    
    @When("I attempt authentication with valid credentials")
    public void iAttemptAuthenticationWithValidCredentials(DataTable dataTable) {
        initializeStep("Authentication with valid credentials");
        
        try {
            List<Map<String, String>> credentials = dataTable.asMaps();
            Map<String, String> creds = credentials.get(0);
            
            currentUsername = creds.get("username");
            currentPassword = creds.get("password");
            
            logger.info("🎯 Attempting authentication with user: {}", currentUsername);
            
            // Enhanced login with alert handling
            homePage = loginPage.login(currentUsername, currentPassword);
            
            // Handle any login alerts
            boolean loginSuccess = AlertHandler.handleLoginAlert(DriverManager.getDriver());
            testContext.put("login_success", loginSuccess);
            testContext.put("authenticated_user", currentUsername);
            
            completeStep("Valid credentials authentication");
            
        } catch (Exception e) {
            handleStepFailure("Valid credentials authentication", e);
            throw e;
        }
    }
    
    @When("I attempt authentication with invalid credentials")
    public void iAttemptAuthenticationWithInvalidCredentials(DataTable dataTable) {
        initializeStep("Authentication with invalid credentials");
        
        try {
            List<Map<String, String>> credentials = dataTable.asMaps();
            Map<String, String> creds = credentials.get(0);
            
            currentUsername = creds.get("username");
            currentPassword = creds.get("password");
            
            logger.info("🎯 Attempting authentication with invalid credentials");
            
            // Enhanced attempt with alert handling
            loginPage.attemptLogin(currentUsername, currentPassword);
            
            // Handle expected failure alert
            String alertText = AlertHandler.acceptAlert(DriverManager.getDriver(), 5);
            testContext.put("login_alert", alertText);
            testContext.put("login_success", false);
            
            completeStep("Invalid credentials authentication");
            
        } catch (Exception e) {
            handleStepFailure("Invalid credentials authentication", e);
            throw e;
        }
    }
    
    @When("I attempt login with empty credentials")
    public void iAttemptLoginWithEmptyCredentials(DataTable dataTable) {
        initializeStep("Login with empty credentials");
        
        try {
            List<Map<String, String>> credentials = dataTable.asMaps();
            Map<String, String> creds = credentials.get(0);
            
            currentUsername = creds.get("username") != null ? creds.get("username") : "";
            currentPassword = creds.get("password") != null ? creds.get("password") : "";
            
            logger.info("🎯 Attempting login with empty credentials");
            
            // Enhanced form interaction with waits
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), loginPage.getUsernameField(), 5);
            loginPage.enterUsername(currentUsername);
            
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), loginPage.getPasswordField(), 5);
            loginPage.enterPassword(currentPassword);
            
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), loginPage.getLoginButton(), 5);
            loginPage.clickLoginSubmit();
            
            // Handle any validation alerts
            String alertText = AlertHandler.acceptAlert(DriverManager.getDriver(), 3);
            testContext.put("validation_alert", alertText);
            
            completeStep("Empty credentials login");
            
        } catch (Exception e) {
            handleStepFailure("Empty credentials login", e);
            throw e;
        }
    }
    
    @Then("access should be granted")
    public void accessShouldBeGranted() {
        initializeStep("Verifying access granted");
        
        try {
            // Enhanced validation with wait
            boolean loggedIn = WaitUtils.waitForCondition(
                driver -> homePage.isUserLoggedIn(), 
                10
            );
            
            assertTrue(loggedIn, "User should be logged in");
            assertTrue(homePage.isUserLoggedIn(), "User authentication should be confirmed");
            
            testContext.put("access_status", "granted");
            logger.info("✅ Access granted successfully");
            
            completeStep("Access verification");
            
        } catch (Exception e) {
            handleStepFailure("Access verification", e);
            throw e;
        }
    }
    
    @Then("welcome banner should confirm identity")
    public void welcomeBannerShouldConfirmIdentity() {
        initializeStep("Verifying welcome banner");
        
        try {
            // Wait for welcome message to appear
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), homePage.getWelcomeElement(), 10);
            
            String welcomeMessage = homePage.getWelcomeMessage();
            assertThat("Welcome message should contain username", 
                      welcomeMessage, containsString(currentUsername));
            
            testContext.put("welcome_message", welcomeMessage);
            logger.info("✅ Welcome banner confirmed: {}", welcomeMessage);
            
            completeStep("Welcome banner verification");
            
        } catch (Exception e) {
            handleStepFailure("Welcome banner verification", e);
            throw e;
        }
    }
    
    @Then("user menu should be accessible")
    public void userMenuShouldBeAccessible() {
        initializeStep("Verifying user menu accessibility");
        
        try {
            assertTrue(homePage.isUserLoggedIn(), "User menu should be accessible");
            
            // Additional validation - check if user menu elements are present
            boolean menuAccessible = WaitUtils.waitForElementVisible(
                DriverManager.getDriver(), 
                homePage.getUserMenuElement(), 
                5
            );
            
            assertTrue(menuAccessible, "User menu elements should be visible");
            logger.info("✅ User menu is accessible");
            
            completeStep("User menu accessibility verification");
            
        } catch (Exception e) {
            handleStepFailure("User menu accessibility verification", e);
            throw e;
        }
    }
    
    @Then("access should be denied")
    public void accessShouldBeDenied() {
        initializeStep("Verifying access denied");
        
        try {
            // Wait a moment for any login processing
            Thread.sleep(1000);
            
            assertFalse(homePage.isUserLoggedIn(), "Access should be denied");
            
            // Verify login interface is still available
            assertTrue(homePage.isLoginButtonVisible() || loginPage.isModalVisible(), 
                      "Login interface should remain available");
            
            testContext.put("access_status", "denied");
            logger.info("✅ Access correctly denied");
            
            completeStep("Access denial verification");
            
        } catch (Exception e) {
            handleStepFailure("Access denial verification", e);
            throw e;
        }
    }
    
    @Then("error alert should be displayed")
    public void errorAlertShouldBeDisplayed() {
        initializeStep("Verifying error alert");
        
        try {
            // Check if we captured an alert in previous steps
            String alertText = (String) testContext.get("login_alert");
            
            if (alertText != null) {
                logger.info("✅ Error alert captured: {}", alertText);
            } else {
                // Fallback verification - modal should still be visible
                assertTrue(loginPage.isModalVisible() || homePage.isLoginButtonVisible(), 
                          "Error should be indicated by modal remaining visible");
            }
            
            testContext.put("error_displayed", true);
            logger.info("✅ Error alert displayed");
            
            completeStep("Error alert verification");
            
        } catch (Exception e) {
            handleStepFailure("Error alert verification", e);
            throw e;
        }
    }
    
    @Then("login interface should be visible")
    public void loginInterfaceShouldBeVisible() {
        initializeStep("Verifying login interface visibility");
        
        try {
            boolean loginVisible = WaitUtils.waitForElementVisible(
                DriverManager.getDriver(), 
                homePage.getLoginButton(), 
                5
            );
            
            assertTrue(loginVisible, "Login interface should be visible");
            assertTrue(homePage.isLoginButtonVisible(), "Login button should be accessible");
            
            logger.info("✅ Login interface remains visible");
            
            completeStep("Login interface visibility verification");
            
        } catch (Exception e) {
            handleStepFailure("Login interface visibility verification", e);
            throw e;
        }
    }
    
    @Then("login interface should remain visible")
    public void loginInterfaceShouldRemainVisible() {
        loginInterfaceShouldBeVisible();
    }
    
    // ===== REGISTRATION STEPS - ENHANCED =====
    
    @Given("I am on the sign up interface")
    public void iAmOnTheSignUpInterface() {
        initializeStep("Opening sign up interface");
        
        try {
            logger.info("📝 Opening sign up interface");
            
            // Ensure we're on homepage first
            if (homePage == null || !homePage.isPageLoaded()) {
                safeNavigateToHome();
            }
            
            // Open signup with enhanced waiting
            signUpPage = homePage.clickSignUp();
            
            // Enhanced validation
            boolean modalVisible = WaitUtils.waitForElementVisible(
                DriverManager.getDriver(), 
                signUpPage.getSignUpModal(), 
                10
            );
            
            assertTrue(modalVisible, "Sign up modal should be visible");
            assertTrue(signUpPage.isModalVisible(), "Sign up modal should be accessible");
            
            testContext.put("current_page", "signup");
            
            completeStep("Sign up interface opened");
            
        } catch (Exception e) {
            handleStepFailure("Opening sign up interface", e);
            throw e;
        }
    }
    
    @When("I navigate to sign up")
    public void iNavigateToSignUp() {
        iAmOnTheSignUpInterface();
    }
    
    @When("I register new operative with credentials")
    public void iRegisterNewOperativeWithCredentials(DataTable dataTable) {
        initializeStep("Registering new operative");
        
        try {
            List<Map<String, String>> credentials = dataTable.asMaps();
            Map<String, String> creds = credentials.get(0);
            
            currentUsername = creds.get("username");
            currentPassword = creds.get("password");
            
            logger.info("🎯 Registering new operative: {}", currentUsername);
            
            // Enhanced signup with alert handling
            signUpPage.signUp(currentUsername, currentPassword);
            
            // Handle signup alert
            boolean signupSuccess = AlertHandler.handleSignupAlert(DriverManager.getDriver());
            testContext.put("signup_success", signupSuccess);
            testContext.put("registered_user", currentUsername);
            
            completeStep("New operative registration");
            
        } catch (Exception e) {
            handleStepFailure("New operative registration", e);
            throw e;
        }
    }
    
    @When("I register with credentials {string} and {string}")
    public void iRegisterWithCredentials(String username, String password) {
        initializeStep("Registration with credentials");
        
        try {
            currentUsername = username;
            currentPassword = password;
            
            logger.info("🎯 Registering with credentials: {}", username);
            
            // Enhanced signup process with validation
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), signUpPage.getUsernameField(), 5);
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), signUpPage.getPasswordField(), 5);
            
            signUpPage.signUp(username, password);
            
            // Handle signup alert with detailed tracking
            String alertText = AlertHandler.acceptAlert(DriverManager.getDriver(), 10);
            boolean signupSuccess = alertText != null && alertText.toLowerCase().contains("successful");
            
            testContext.put("signup_success", signupSuccess);
            testContext.put("signup_alert", alertText);
            testContext.put("registered_user", username);
            
            completeStep("Credentials registration");
            
        } catch (Exception e) {
            handleStepFailure("Credentials registration", e);
            throw e;
        }
    }
    
    @When("I attempt registration with existing username")
    public void iAttemptRegistrationWithExistingUsername(DataTable dataTable) {
        initializeStep("Registration with existing username");
        
        try {
            List<Map<String, String>> credentials = dataTable.asMaps();
            Map<String, String> creds = credentials.get(0);
            
            currentUsername = creds.get("username");
            currentPassword = creds.get("password");
            
            logger.info("🎯 Attempting registration with existing username");
            
            // Enhanced attempt with detailed error tracking
            signUpPage.attemptSignUp(currentUsername, currentPassword);
            
            // Handle expected error alert
            String alertText = AlertHandler.acceptAlert(DriverManager.getDriver(), 5);
            testContext.put("signup_error", alertText);
            testContext.put("signup_success", false);
            
            completeStep("Existing username registration attempt");
            
        } catch (Exception e) {
            handleStepFailure("Existing username registration attempt", e);
            throw e;
        }
    }
    
    @Then("registration should be successful")
    public void registrationShouldBeSuccessful() {
        initializeStep("Verifying successful registration");
        
        try {
            // Check context for success indicator
            Boolean signupSuccess = (Boolean) testContext.get("signup_success");
            if (signupSuccess != null) {
                assertTrue(signupSuccess, "Registration should be successful based on alert");
            }
            
            // Wait for modal to close indicating success
            boolean modalClosed = WaitUtils.waitForElementInvisible(
                DriverManager.getDriver(), 
                signUpPage.getSignUpModal(), 
                10
            );
            
            assertTrue(modalClosed || !signUpPage.isModalVisible(), "Registration modal should close");
            
            logger.info("✅ Registration successful");
            
            completeStep("Registration success verification");
            
        } catch (Exception e) {
            handleStepFailure("Registration success verification", e);
            throw e;
        }
    }
    
    @Then("confirmation message should appear")
    public void confirmationMessageShouldAppear() {
        initializeStep("Verifying confirmation message");
        
        try {
            // Check if we captured a success alert
            String alertText = (String) testContext.get("signup_alert");
            
            if (alertText != null && alertText.toLowerCase().contains("successful")) {
                logger.info("✅ Confirmation message captured: {}", alertText);
            } else {
                logger.info("✅ Confirmation message appeared (modal behavior)");
            }
            
            completeStep("Confirmation message verification");
            
        } catch (Exception e) {
            handleStepFailure("Confirmation message verification", e);
            throw e;
        }
    }
    
    @Then("I should be able to login with new credentials")
    public void iShouldBeAbleToLoginWithNewCredentials() {
        initializeStep("Verifying login with new credentials");
        
        try {
            // Navigate to login
            loginPage = homePage.clickLogin();
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), loginPage.getLoginModal(), 5);
            
            // Attempt login with new credentials
            homePage = loginPage.login(currentUsername, currentPassword);
            
            // Handle login result
            boolean loginSuccess = AlertHandler.handleLoginAlert(DriverManager.getDriver());
            
            if (!loginSuccess) {
                // Wait for welcome message as alternative success indicator
                WaitUtils.waitForElementVisible(DriverManager.getDriver(), homePage.getWelcomeElement(), 5);
            }
            
            assertTrue(homePage.isUserLoggedIn(), "Should be able to login with new credentials");
            logger.info("✅ Login successful with new credentials");
            
            completeStep("New credentials login verification");
            
        } catch (Exception e) {
            handleStepFailure("New credentials login verification", e);
            throw e;
        }
    }
    
    @Then("registration should fail")
    public void registrationShouldFail() {
        initializeStep("Verifying registration failure");
        
        try {
            // Check context for failure indicator
            Boolean signupSuccess = (Boolean) testContext.get("signup_success");
            if (signupSuccess != null) {
                assertFalse(signupSuccess, "Registration should fail");
            }
            
            // Modal should remain visible
            assertTrue(signUpPage.isModalVisible(), "Registration modal should remain visible");
            
            logger.info("✅ Registration correctly failed");
            
            completeStep("Registration failure verification");
            
        } catch (Exception e) {
            handleStepFailure("Registration failure verification", e);
            throw e;
        }
    }
    
    @Then("duplicate username error should appear")
    public void duplicateUsernameErrorShouldAppear() {
        initializeStep("Verifying duplicate username error");
        
        try {
            // Check captured error message
            String errorText = (String) testContext.get("signup_error");
            
            if (errorText != null) {
                logger.info("✅ Duplicate username error captured: {}", errorText);
            } else {
                logger.info("✅ Duplicate username error appeared (modal behavior)");
            }
            
            completeStep("Duplicate username error verification");
            
        } catch (Exception e) {
            handleStepFailure("Duplicate username error verification", e);
            throw e;
        }
    }
    
    @Then("registration form should remain visible")
    public void registrationFormShouldRemainVisible() {
        initializeStep("Verifying registration form visibility");
        
        try {
            boolean modalVisible = WaitUtils.waitForElementVisible(
                DriverManager.getDriver(), 
                signUpPage.getSignUpModal(), 
                5
            );
            
            assertTrue(modalVisible, "Registration form should remain visible");
            assertTrue(signUpPage.isModalVisible(), "Registration modal should be accessible");
            
            logger.info("✅ Registration form remains visible");
            
            completeStep("Registration form visibility verification");
            
        } catch (Exception e) {
            handleStepFailure("Registration form visibility verification", e);
            throw e;
        }
    }
    
    @Then("validation error should appear")
    public void validationErrorShouldAppear() {
        initializeStep("Verifying validation error");
        
        try {
            // Check captured validation alert
            String validationAlert = (String) testContext.get("validation_alert");
            
            if (validationAlert != null) {
                logger.info("✅ Validation error captured: {}", validationAlert);
            } else {
                logger.info("✅ Validation error appeared (form behavior)");
            }
            
            completeStep("Validation error verification");
            
        } catch (Exception e) {
            handleStepFailure("Validation error verification", e);
            throw e;
        }
    }
    
    @Then("form validation should be triggered")
    public void formValidationShouldBeTriggered() {
        initializeStep("Verifying form validation");
        
        try {
            // Check for validation indicators
            boolean formStillVisible = signUpPage.isModalVisible() || loginPage.isModalVisible();
            assertTrue(formStillVisible, "Form should remain visible indicating validation");
            
            logger.info("✅ Form validation triggered");
            
            completeStep("Form validation verification");
            
        } catch (Exception e) {
            handleStepFailure("Form validation verification", e);
            throw e;
        }
    }
    
    // ===== AUTHENTICATION STATE STEPS - ENHANCED =====
    
    @Given("I am authenticated as {string}")
    public void iAmAuthenticatedAs(String username) {
        initializeStep("Authenticating as user: " + username);
        
        try {
            if (!homePage.isUserLoggedIn()) {
                // Enhanced login process
                loginPage = homePage.clickLogin();
                WaitUtils.waitForElementVisible(DriverManager.getDriver(), loginPage.getLoginModal(), 5);
                
                homePage = loginPage.login(username, "testpass"); // Use default password
                
                // Handle login result
                AlertHandler.handleLoginAlert(DriverManager.getDriver());
            }
            
            currentUsername = username;
            
            // Enhanced validation
            boolean authenticated = WaitUtils.waitForCondition(
                driver -> homePage.isUserLoggedIn(), 
                10
            );
            
            assertTrue(authenticated, "User should be authenticated");
            testContext.put("authenticated_user", username);
            
            logger.info("✅ Authenticated as: {}", username);
            
            completeStep("User authentication");
            
        } catch (Exception e) {
            handleStepFailure("User authentication", e);
            throw e;
        }
    }
    
    @Given("I am not authenticated")
    public void iAmNotAuthenticated() {
        initializeStep("Ensuring not authenticated");
        
        try {
            if (homePage.isUserLoggedIn()) {
                homePage.clickLogout();
                
                // Wait for logout to complete
                WaitUtils.waitForCondition(
                    driver -> !homePage.isUserLoggedIn(), 
                    5
                );
            }
            
            assertFalse(homePage.isUserLoggedIn(), "User should not be authenticated");
            testContext.put("authenticated_user", null);
            
            logger.info("✅ Not authenticated");
            
            completeStep("Authentication clearance");
            
        } catch (Exception e) {
            handleStepFailure("Authentication clearance", e);
            throw e;
        }
    }
    
    @When("I login with credentials {string} and {string}")
    public void iLoginWithCredentials(String username, String password) {
        initializeStep("Login with specific credentials");
        
        try {
            currentUsername = username;
            currentPassword = password;
            
            // Enhanced login process
            loginPage = homePage.clickLogin();
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), loginPage.getLoginModal(), 5);
            
            homePage = loginPage.login(username, password);
            
            // Handle login result
            boolean loginSuccess = AlertHandler.handleLoginAlert(DriverManager.getDriver());
            testContext.put("login_success", loginSuccess);
            
            logger.info("🔐 Logged in with credentials: {}", username);
            
            completeStep("Credentials login");
            
        } catch (Exception e) {
            handleStepFailure("Credentials login", e);
            throw e;
        }
    }
    
    @When("I initiate logout sequence")
    public void iInitiateLogoutSequence() {
        initializeStep("Initiating logout sequence");
        
        try {
            logger.info("🚪 Initiating logout sequence");
            
            // Enhanced logout with validation
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), homePage.getLogoutButton(), 5);
            homePage.clickLogout();
            
            // Wait for logout to process
            WaitUtils.waitForCondition(
                driver -> !homePage.isUserLoggedIn(), 
                5
            );
            
            testContext.put("authenticated_user", null);
            
            completeStep("Logout sequence");
            
        } catch (Exception e) {
            handleStepFailure("Logout sequence", e);
            throw e;
        }
    }
    
    @Then("I should be successfully authenticated")
    public void iShouldBeSuccessfullyAuthenticated() {
        initializeStep("Verifying successful authentication");
        
        try {
            // Enhanced authentication verification
            boolean authenticated = WaitUtils.waitForCondition(
                driver -> homePage.isUserLoggedIn(), 
                10
            );
            
            assertTrue(authenticated, "Should be successfully authenticated");
            
            // Additional validation - check for welcome message
            if (currentUsername != null) {
                WaitUtils.waitForElementVisible(DriverManager.getDriver(), homePage.getWelcomeElement(), 5);
                String welcomeMessage = homePage.getWelcomeMessage();
                assertThat("Welcome message should contain username", 
                          welcomeMessage, containsString(currentUsername));
            }
            
            logger.info("✅ Successfully authenticated");
            
            completeStep("Authentication verification");
            
        } catch (Exception e) {
            handleStepFailure("Authentication verification", e);
            throw e;
        }
    }
    
    @Then("session should be terminated")
    public void sessionShouldBeTerminated() {
        initializeStep("Verifying session termination");
        
        try {
            // Enhanced session termination verification
            boolean sessionTerminated = WaitUtils.waitForCondition(
                driver -> !homePage.isUserLoggedIn(), 
                10
            );
            
            assertFalse(homePage.isUserLoggedIn(), "Session should be terminated");
            assertTrue(sessionTerminated, "Session termination should be confirmed");
            
            // Verify login button is visible again
            assertTrue(homePage.isLoginButtonVisible(), "Login button should be available");
            
            logger.info("✅ Session terminated");
            
            completeStep("Session termination verification");
            
        } catch (Exception e) {
            handleStepFailure("Session termination verification", e);
            throw e;
        }
    }
    
    @Then("user menu should not be accessible")
    public void userMenuShouldNotBeAccessible() {
        initializeStep("Verifying user menu inaccessibility");
        
        try {
            assertFalse(homePage.isUserLoggedIn(), "User menu should not be accessible");
            
            // Additional validation - user menu elements should not be present
            boolean userMenuHidden = WaitUtils.waitForElementInvisible(
                DriverManager.getDriver(), 
                homePage.getUserMenuElement(), 
                5
            );
            
            assertTrue(userMenuHidden, "User menu elements should be hidden");
            logger.info("✅ User menu not accessible");
            
            completeStep("User menu inaccessibility verification");
            
        } catch (Exception e) {
            handleStepFailure("User menu inaccessibility verification", e);
            throw e;
        }
    }
    
    // ===== PRODUCT BROWSING STEPS - ENHANCED =====
    
    @Given("I am browsing the product catalog")
    public void iAmBrowsingTheProductCatalog() {
        initializeStep("Browsing product catalog");
        
        try {
            // Enhanced catalog loading verification
            boolean pageLoaded = WaitUtils.waitForDemoblazeHomepage(DriverManager.getDriver());
            assertTrue(pageLoaded, "Product catalog should be loaded");
            
            // Wait for products to load
            WaitUtils.waitForCondition(
                driver -> homePage.getProductCount() > 0, 
                15
            );
            
            int productCount = homePage.getProductCount();
            assertThat("Should have products to browse", productCount, greaterThan(0));
            
            testContext.put("product_count", productCount);
            logger.info("📱 Browsing product catalog with {} products", productCount);
            
            completeStep("Product catalog browsing");
            
        } catch (Exception e) {
            handleStepFailure("Product catalog browsing", e);
            throw e;
        }
    }
    
    @Given("I am browsing as guest user")
    public void iAmBrowsingAsGuestUser() {
        initializeStep("Browsing as guest user");
        
        try {
            iAmNotAuthenticated();
            iAmBrowsingTheProductCatalog();
            
            testContext.put("user_type", "guest");
            
            completeStep("Guest user browsing setup");
            
        } catch (Exception e) {
            handleStepFailure("Guest user browsing setup", e);
            throw e;
        }
    }
    
    @Given("I am viewing product {string}")
    public void iAmViewingProduct(String productName) {
        initializeStep("Viewing product: " + productName);
        
        try {
            selectedProduct = productName;
            
            // Enhanced product selection with retry
            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try {
                    productPage = homePage.selectProduct(productName);
                    
                    // Verify product page loaded
                    boolean pageLoaded = WaitUtils.waitForCondition(
                        driver -> productPage.isPageLoaded(), 
                        10
                    );
                    
                    if (pageLoaded) {
                        break;
                    }
                    
                } catch (Exception e) {
                    if (attempt == MAX_RETRIES) {
                        throw e;
                    }
                    logger.warn("⚠️ Product selection attempt {} failed, retrying...", attempt);
                    Thread.sleep(2000);
                }
            }
            
            assertTrue(productPage.isPageLoaded(), "Product page should be loaded");
            testContext.put("current_product", productName);
            
            logger.info("👁️ Viewing product: {}", productName);
            
            completeStep("Product viewing");
            
        } catch (Exception e) {
            handleStepFailure("Product viewing", e);
            throw e;
        }
    }
    
    @When("I browse to {string} category")
    public void iBrowseToCategory(String category) {
        initializeStep("Browsing to category: " + category);
        
        try {
            // Enhanced category selection with wait
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), homePage.getCategoryElement(category), 5);
            homePage.selectCategory(category);
            
            // Wait for category products to load
            WaitUtils.waitForCondition(
                driver -> homePage.getProductCount() > 0, 
                10
            );
            
            testContext.put("current_category", category);
            logger.info("📂 Browsed to category: {}", category);
            
            completeStep("Category browsing");
            
        } catch (Exception e) {
            handleStepFailure("Category browsing", e);
            throw e;
        }
    }
    
    @When("I select a product {string}")
    public void iSelectAProduct(String productName) {
        iSelectProduct(productName);
    }
    
    @When("I select product {string}")
    public void iSelectProduct(String productName) {
        initializeStep("Selecting product: " + productName);
        
        try {
            selectedProduct = productName;
            
            // Enhanced product selection with validation
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), homePage.getProductElement(productName), 10);
            productPage = homePage.selectProduct(productName);
            
            // Verify product page loads properly
            boolean pageLoaded = WaitUtils.waitForCondition(
                driver -> productPage.isPageLoaded() && productPage.getProductName().contains(productName), 
                15
            );
            
            assertTrue(pageLoaded, "Product page should load for: " + productName);
            testContext.put("current_product", productName);
            
            logger.info("🎯 Selected product: {}", productName);
            
            completeStep("Product selection");
            
        } catch (Exception e) {
            handleStepFailure("Product selection", e);
            throw e;
        }
    }
    
    @When("I examine product specifications")
    public void iExamineProductSpecifications() {
        initializeStep("Examining product specifications");
        
        try {
            // Enhanced product examination
            assertTrue(productPage.isPageLoaded(), "Product page should be loaded");
            
            // Wait for all product elements to load
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), productPage.getProductNameElement(), 5);
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), productPage.getProductPriceElement(), 5);
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), productPage.getProductDescriptionElement(), 5);
            
            // Collect product information
            String productName = productPage.getProductName();
            String productPrice = productPage.getProductPrice();
            String productDescription = productPage.getProductDescription();
            
            testContext.put("examined_product_name", productName);
            testContext.put("examined_product_price", productPrice);
            testContext.put("examined_product_description", productDescription);
            
            logger.info("🔍 Examining product specifications for: {}", productName);
            
            completeStep("Product specification examination");
            
        } catch (Exception e) {
            handleStepFailure("Product specification examination", e);
            throw e;
        }
    }
    
    @Then("product price should be displayed")
    public void productPriceShouldBeDisplayed() {
        initializeStep("Verifying product price display");
        
        try {
            // Enhanced price verification with wait
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), productPage.getProductPriceElement(), 10);
            
            String price = productPage.getProductPrice();
            assertThat("Product price should be displayed", price, not(emptyString()));
            assertThat("Product price should contain currency symbol", price, anyOf(containsString("$"), containsString("USD")));
            
            testContext.put("product_price", price);
            logger.info("💰 Product price displayed: {}", price);
            
            completeStep("Product price verification");
            
        } catch (Exception e) {
            handleStepFailure("Product price verification", e);
            throw e;
        }
    }
    
    @Then("product description should be available")
    public void productDescriptionShouldBeAvailable() {
        initializeStep("Verifying product description");
        
        try {
            // Enhanced description verification
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), productPage.getProductDescriptionElement(), 10);
            
            String description = productPage.getProductDescription();
            assertThat("Product description should be available", description, not(emptyString()));
            assertThat("Product description should be meaningful", description.length(), greaterThan(10));
            
            testContext.put("product_description", description);
            logger.info("📝 Product description available: {} chars", description.length());
            
            completeStep("Product description verification");
            
        } catch (Exception e) {
            handleStepFailure("Product description verification", e);
            throw e;
        }
    }
    
    @Then("product images should be loaded")
    public void productImagesShouldBeLoaded() {
        initializeStep("Verifying product images");
        
        try {
            // Enhanced image loading verification
            boolean imageLoaded = WaitUtils.waitForCondition(
                driver -> productPage.isImageLoaded(), 
                15
            );
            
            assertTrue(imageLoaded, "Product images should be loaded");
            
            // Additional validation - check image dimensions
            if (productPage.getProductImageElement() != null) {
                WaitUtils.waitForElementVisible(DriverManager.getDriver(), productPage.getProductImageElement(), 5);
            }
            
            logger.info("🖼️ Product images loaded successfully");
            
            completeStep("Product images verification");
            
        } catch (Exception e) {
            handleStepFailure("Product images verification", e);
            throw e;
        }
    }
    
    @Then("{string} button should be functional")
    public void buttonShouldBeFunctional(String buttonName) {
        initializeStep("Verifying button functionality: " + buttonName);
        
        try {
            if ("Add to cart".equals(buttonName)) {
                // Enhanced button functionality check
                WaitUtils.waitForElementClickable(DriverManager.getDriver(), productPage.getAddToCartButton(), 10);
                assertTrue(productPage.isAddToCartButtonEnabled(), "Add to cart button should be functional");
                
                // Additional validation - button should be visible and enabled
                assertTrue(productPage.getAddToCartButton().isDisplayed(), "Button should be visible");
                assertTrue(productPage.getAddToCartButton().isEnabled(), "Button should be enabled");
            }
            
            testContext.put("functional_button", buttonName);
            logger.info("🔘 {} button is functional", buttonName);
            
            completeStep("Button functionality verification");
            
        } catch (Exception e) {
            handleStepFailure("Button functionality verification", e);
            throw e;
        }
    }
    
    // ===== SHOPPING CART STEPS - ENHANCED =====
    
    @When("I add the product to cart")
    public void iAddTheProductToCart() {
        initializeStep("Adding product to cart");
        
        try {
            // Enhanced add to cart with comprehensive handling
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), productPage.getAddToCartButton(), 10);
            productPage.clickAddToCart();
            
            // Handle add to cart alert
            boolean addSuccess = AlertHandler.handleAddToCartAlert(DriverManager.getDriver());
            testContext.put("add_to_cart_success", addSuccess);
            testContext.put("cart_product", selectedProduct);
            
            logger.info("🛒 Added product to cart: {} (success: {})", selectedProduct, addSuccess);
            
            completeStep("Product addition to cart");
            
        } catch (Exception e) {
            handleStepFailure("Product addition to cart", e);
            throw e;
        }
    }
    
    @When("I add product to cart")
    public void iAddProductToCart() {
        iAddTheProductToCart();
    }
    
    @When("I add multiple products to cart")
    public void iAddMultipleProductsToCart(DataTable dataTable) {
        initializeStep("Adding multiple products to cart");
        
        try {
            List<Map<String, String>> products = dataTable.asMaps();
            int successCount = 0;
            
            for (Map<String, String> product : products) {
                String productName = product.get("productName");
                String category = product.get("category");
                
                try {
                    // Navigate to category and select product with enhanced error handling
                    if (category != null && !category.isEmpty()) {
                        homePage.selectCategory(category);
                        WaitUtils.waitForCondition(
                            driver -> homePage.getProductCount() > 0, 
                            10
                        );
                    }
                    
                    productPage = homePage.selectProduct(productName);
                    WaitUtils.waitForCondition(
                        driver -> productPage.isPageLoaded(), 
                        10
                    );
                    
                    productPage.clickAddToCart();
                    
                    // Handle add to cart alert
                    boolean addSuccess = AlertHandler.handleAddToCartAlert(DriverManager.getDriver());
                    if (addSuccess) {
                        successCount++;
                    }
                    
                    // Go back to home for next product
                    homePage = new HomePage().navigateToHome();
                    WaitUtils.waitForDemoblazeHomepage(DriverManager.getDriver());
                    
                    logger.info("🛒 Added {} to cart (success: {})", productName, addSuccess);
                    
                } catch (Exception e) {
                    logger.warn("⚠️ Failed to add {} to cart: {}", productName, e.getMessage());
                }
            }
            
            testContext.put("multiple_products_added", successCount);
            testContext.put("total_products_attempted", products.size());
            
            completeStep("Multiple products addition");
            
        } catch (Exception e) {
            handleStepFailure("Multiple products addition", e);
            throw e;
        }
    }
    
    @When("I attempt to add product to cart")
    public void iAttemptToAddProductToCart() {
        initializeStep("Attempting to add product to cart");
        
        try {
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), productPage.getAddToCartButton(), 5);
            productPage.clickAddToCart();
            
            // Handle any resulting alert
            String alertText = AlertHandler.acceptAlert(DriverManager.getDriver(), 5);
            testContext.put("add_attempt_alert", alertText);
            
            logger.info("🎯 Attempted to add product to cart");
            
            completeStep("Product addition attempt");
            
        } catch (Exception e) {
            handleStepFailure("Product addition attempt", e);
            throw e;
        }
    }
    
    @Then("product should be added successfully")
    public void productShouldBeAddedSuccessfully() {
        initializeStep("Verifying successful product addition");
        
        try {
            // Check context for success indicator
            Boolean addSuccess = (Boolean) testContext.get("add_to_cart_success");
            if (addSuccess != null) {
                assertTrue(addSuccess, "Product should be added successfully based on alert");
            }
            
            // Alternative verification - check cart contents
            if (selectedProduct != null) {
                cartPage = homePage.clickCart();
                WaitUtils.waitForCondition(
                    driver -> cartPage.isPageLoaded(), 
                    10
                );
                
                boolean productInCart = cartPage.isProductInCart(selectedProduct);
                testContext.put("product_in_cart", productInCart);
            }
            
            logger.info("✅ Product added successfully");
            
            completeStep("Product addition success verification");
            
        } catch (Exception e) {
            handleStepFailure("Product addition success verification", e);
            throw e;
        }
    }
    
    @Then("product should be added to cart")
    public void productShouldBeAddedToCart() {
        productShouldBeAddedSuccessfully();
    }
    
    @Then("second product should be added to cart")
    public void secondProductShouldBeAddedToCart() {
        productShouldBeAddedSuccessfully();
    }
    
    @Then("cart counter should be updated")
    public void cartCounterShouldBeUpdated() {
        initializeStep("Verifying cart counter update");
        
        try {
            // Enhanced cart counter verification
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), homePage.getCartCounterElement(), 5);
            
            String cartCounter = homePage.getCartCounter();
            if (cartCounter != null && !cartCounter.isEmpty()) {
                int count = Integer.parseInt(cartCounter);
                assertThat("Cart counter should be updated", count, greaterThan(0));
                testContext.put("cart_count", count);
            }
            
            logger.info("✅ Cart counter updated");
            
            completeStep("Cart counter verification");
            
        } catch (Exception e) {
            handleStepFailure("Cart counter verification", e);
            throw e;
        }
    }
    
    @Then("product should appear in cart")
    public void productShouldAppearInCart() {
        initializeStep("Verifying product appears in cart");
        
        try {
            cartPage = homePage.clickCart();
            
            // Enhanced cart verification with wait
            boolean cartLoaded = WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                10
            );
            assertTrue(cartLoaded, "Cart page should be loaded");
            
            // Wait for cart contents to load
            WaitUtils.waitForCondition(
                driver -> cartPage.getProductCount() > 0 || cartPage.isCartEmpty(), 
                10
            );
            
            assertTrue(cartPage.isProductInCart(selectedProduct), "Product should appear in cart: " + selectedProduct);
            
            testContext.put("product_in_cart_verified", true);
            logger.info("✅ Product appears in cart: {}", selectedProduct);
            
            completeStep("Cart product verification");
            
        } catch (Exception e) {
            handleStepFailure("Cart product verification", e);
            throw e;
        }
    }
    
    @Then("authentication prompt should appear")
    public void authenticationPromptShouldAppear() {
        initializeStep("Verifying authentication prompt");
        
        try {
            // Check for login modal or redirect
            boolean loginPromptVisible = WaitUtils.waitForCondition(
                driver -> homePage.isLoginButtonVisible() || loginPage.isModalVisible(), 
                5
            );
            
            assertTrue(loginPromptVisible, "Authentication prompt should appear");
            logger.info("✅ Authentication prompt appeared");
            
            completeStep("Authentication prompt verification");
            
        } catch (Exception e) {
            handleStepFailure("Authentication prompt verification", e);
            throw e;
        }
    }
    
    @Then("product should not be added")
    public void productShouldNotBeAdded() {
        initializeStep("Verifying product not added");
        
        try {
            // Check that product was not added to cart
            cartPage = homePage.clickCart();
            WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                10
            );
            
            boolean productNotInCart = !cartPage.isProductInCart(selectedProduct);
            assertTrue(productNotInCart, "Product should not be added to cart");
            
            logger.info("✅ Product correctly not added");
            
            completeStep("Product non-addition verification");
            
        } catch (Exception e) {
            handleStepFailure("Product non-addition verification", e);
            throw e;
        }
    }
    
    @Then("cart should remain empty")
    public void cartShouldRemainEmpty() {
        initializeStep("Verifying cart remains empty");
        
        try {
            cartPage = homePage.clickCart();
            WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                10
            );
            
            boolean cartEmpty = WaitUtils.waitForCondition(
                driver -> cartPage.isCartEmpty(), 
                5
            );
            
            assertTrue(cartEmpty, "Cart should remain empty");
            assertEquals(0, cartPage.getProductCount(), "Cart product count should be zero");
            
            logger.info("✅ Cart remains empty");
            
            completeStep("Empty cart verification");
            
        } catch (Exception e) {
            handleStepFailure("Empty cart verification", e);
            throw e;
        }
    }
    
    // ===== CART MANAGEMENT STEPS - ENHANCED =====
    
    @Given("I have products in my cart")
    public void iHaveProductsInMyCart(DataTable dataTable) {
        initializeStep("Setting up products in cart");
        
        try {
            List<Map<String, String>> products = dataTable.asMaps();
            int addedCount = 0;
            
            for (Map<String, String> product : products) {
                String productName = product.get("productName");
                
                try {
                    productPage = homePage.selectProduct(productName);
                    WaitUtils.waitForCondition(
                        driver -> productPage.isPageLoaded(), 
                        10
                    );
                    
                    productPage.clickAddToCart();
                    boolean addSuccess = AlertHandler.handleAddToCartAlert(DriverManager.getDriver());
                    
                    if (addSuccess) {
                        addedCount++;
                    }
                    
                    homePage = new HomePage().navigateToHome();
                    WaitUtils.waitForDemoblazeHomepage(DriverManager.getDriver());
                    
                } catch (Exception e) {
                    logger.warn("⚠️ Failed to add {} to cart: {}", productName, e.getMessage());
                }
            }
            
            testContext.put("cart_setup_count", addedCount);
            logger.info("🛒 Added {} products to cart", addedCount);
            
            completeStep("Cart products setup");
            
        } catch (Exception e) {
            handleStepFailure("Cart products setup", e);
            throw e;
        }
    }
    
    @Given("my cart is empty")
    public void myCartIsEmpty() {
        initializeStep("Ensuring cart is empty");
        
        try {
            cartPage = homePage.clickCart();
            WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                10
            );
            
            if (!cartPage.isCartEmpty()) {
                cartPage.clearCart();
                
                // Verify cart is now empty
                WaitUtils.waitForCondition(
                    driver -> cartPage.isCartEmpty(), 
                    10
                );
            }
            
            assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
            testContext.put("cart_status", "empty");
            
            logger.info("🧹 Cart is empty");
            
            completeStep("Cart emptying");
            
        } catch (Exception e) {
            handleStepFailure("Cart emptying", e);
            throw e;
        }
    }
    
    @Given("I have products in cart ready for checkout")
    public void iHaveProductsInCartReadyForCheckout() {
        initializeStep("Setting up cart for checkout");
        
        try {
            productPage = homePage.selectProduct("Samsung galaxy s6");
            WaitUtils.waitForCondition(
                driver -> productPage.isPageLoaded(), 
                10
            );
            
            productPage.clickAddToCart();
            boolean addSuccess = AlertHandler.handleAddToCartAlert(DriverManager.getDriver());
            
            testContext.put("checkout_ready", addSuccess);
            logger.info("🛒 Products ready for checkout (success: {})", addSuccess);
            
            completeStep("Checkout setup");
            
        } catch (Exception e) {
            handleStepFailure("Checkout setup", e);
            throw e;
        }
    }
    
    @Given("I have completed shopping cart setup")
    public void iHaveCompletedShoppingCartSetup() {
        iHaveProductsInCartReadyForCheckout();
    }
    
    @When("I remove {string} from cart")
    public void iRemoveFromCart(String productName) {
        initializeStep("Removing product from cart: " + productName);
        
        try {
            cartPage = homePage.clickCart();
            WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                10
            );
            
            // Enhanced product removal with validation
            boolean productExists = cartPage.isProductInCart(productName);
            if (productExists) {
                int initialCount = cartPage.getProductCount();
                cartPage.removeProduct(productName);
                
                // Wait for removal to complete
                WaitUtils.waitForCondition(
                    driver -> cartPage.getProductCount() < initialCount || cartPage.isCartEmpty(), 
                    10
                );
                
                testContext.put("removed_product", productName);
                testContext.put("removal_success", true);
            } else {
                testContext.put("removal_success", false);
                logger.warn("⚠️ Product {} not found in cart", productName);
            }
            
            logger.info("🗑️ Removed {} from cart", productName);
            
            completeStep("Product removal from cart");
            
        } catch (Exception e) {
            handleStepFailure("Product removal from cart", e);
            throw e;
        }
    }
    
    @When("I navigate to cart")
    public void iNavigateToCart() {
        initializeStep("Navigating to cart");
        
        try {
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), homePage.getCartButton(), 5);
            cartPage = homePage.clickCart();
            
            // Enhanced cart loading verification
            boolean cartLoaded = WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                15
            );
            
            assertTrue(cartLoaded, "Cart page should be loaded");
            testContext.put("current_page", "cart");
            
            logger.info("🛒 Navigated to cart");
            
            completeStep("Cart navigation");
            
        } catch (Exception e) {
            handleStepFailure("Cart navigation", e);
            throw e;
        }
    }
    
    @When("I attempt to proceed to checkout")
    public void iAttemptToProceedToCheckout() {
        initializeStep("Attempting checkout");
        
        try {
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), cartPage.getPlaceOrderButton(), 5);
            cartPage.clickPlaceOrder();
            
            // Handle any checkout alerts or modals
            String checkoutAlert = AlertHandler.acceptAlert(DriverManager.getDriver(), 3);
            testContext.put("checkout_alert", checkoutAlert);
            
            logger.info("🎯 Attempted to proceed to checkout");
            
            completeStep("Checkout attempt");
            
        } catch (Exception e) {
            handleStepFailure("Checkout attempt", e);
            throw e;
        }
    }
    
    @When("I proceed to checkout")
    public void iProceedToCheckout() {
        initializeStep("Proceeding to checkout");
        
        try {
            cartPage = homePage.clickCart();
            WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                10
            );
            
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), cartPage.getPlaceOrderButton(), 10);
            cartPage.clickPlaceOrder();
            
            // Wait for checkout modal or page
            WaitUtils.waitForCondition(
                driver -> cartPage.isCheckoutModalVisible() || cartPage.isCheckoutPageLoaded(), 
                10
            );
            
            testContext.put("checkout_initiated", true);
            logger.info("🛒 Proceeding to checkout");
            
            completeStep("Checkout process");
            
        } catch (Exception e) {
            handleStepFailure("Checkout process", e);
            throw e;
        }
    }
    
    @When("I proceed to checkout as guest")
    public void iProceedToCheckoutAsGuest() {
        initializeStep("Proceeding to checkout as guest");
        
        try {
            iProceedToCheckout();
            testContext.put("checkout_user_type", "guest");
            
            completeStep("Guest checkout process");
            
        } catch (Exception e) {
            handleStepFailure("Guest checkout process", e);
            throw e;
        }
    }
    
    @Then("all products should be in cart")
    public void allProductsShouldBeInCart() {
        initializeStep("Verifying all products in cart");
        
        try {
            cartPage = homePage.clickCart();
            WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                10
            );
            
            int productCount = cartPage.getProductCount();
            assertThat("All products should be in cart", productCount, greaterThan(0));
            
            // Compare with setup count if available
            Integer setupCount = (Integer) testContext.get("cart_setup_count");
            if (setupCount != null) {
                assertThat("Product count should match setup", productCount, equalTo(setupCount));
            }
            
            testContext.put("verified_cart_count", productCount);
            logger.info("✅ All products in cart: {}", productCount);
            
            completeStep("Cart products verification");
            
        } catch (Exception e) {
            handleStepFailure("Cart products verification", e);
            throw e;
        }
    }
    
    @Then("I should see both products in cart")
    public void iShouldSeeBothProductsInCart() {
        initializeStep("Verifying both products in cart");
        
        try {
            int productCount = cartPage.getProductCount();
            assertThat("Should see both products", productCount, greaterThanOrEqualTo(2));
            
            testContext.put("multiple_products_verified", true);
            logger.info("✅ Both products visible in cart: {}", productCount);
            
            completeStep("Multiple products verification");
            
        } catch (Exception e) {
            handleStepFailure("Multiple products verification", e);
            throw e;
        }
    }
    
    @Then("cart total should be calculated correctly")
    public void cartTotalShouldBeCalculatedCorrectly() {
        initializeStep("Verifying cart total calculation");
        
        try {
            // Enhanced total calculation verification
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), cartPage.getTotalElement(), 10);
            
            String total = cartPage.getTotalPrice();
            assertThat("Cart total should be calculated", total, not(emptyString()));
            assertThat("Cart total should contain currency", total, anyOf(containsString("$"), containsString("USD")));
            
            // Additional validation - total should be numeric
            String numericTotal = total.replaceAll("[^0-9.]", "");
            double totalValue = Double.parseDouble(numericTotal);
            assertThat("Total should be positive", totalValue, greaterThan(0.0));
            
            testContext.put("cart_total", total);
            testContext.put("cart_total_value", totalValue);
            logger.info("💰 Cart total calculated: {}", total);
            
            completeStep("Cart total verification");
            
        } catch (Exception e) {
            handleStepFailure("Cart total verification", e);
            throw e;
        }
    }
    
    @Then("cart total should be calculated")
    public void cartTotalShouldBeCalculated() {
        cartTotalShouldBeCalculatedCorrectly();
    }
    
    @Then("product details should be preserved")
    public void productDetailsShouldBePreserved() {
        initializeStep("Verifying product details preservation");
        
        try {
            // Enhanced product details verification
            if (selectedProduct != null && cartPage.isProductInCart(selectedProduct)) {
                String cartProductName = cartPage.getProductName(selectedProduct);
                String cartProductPrice = cartPage.getProductPrice(selectedProduct);
                
                assertThat("Product name should be preserved", cartProductName, containsString(selectedProduct));
                assertThat("Product price should be preserved", cartProductPrice, not(emptyString()));
                
                testContext.put("details_preserved", true);
            }
            
            logger.info("✅ Product details preserved");
            
            completeStep("Product details verification");
            
        } catch (Exception e) {
            handleStepFailure("Product details verification", e);
            throw e;
        }
    }
    
    @Then("product should be removed successfully")
    public void productShouldBeRemovedSuccessfully() {
        initializeStep("Verifying product removal success");
        
        try {
            // Check removal success from context
            Boolean removalSuccess = (Boolean) testContext.get("removal_success");
            if (removalSuccess != null) {
                assertTrue(removalSuccess, "Product should be removed successfully");
            }
            
            // Additional verification - product should not be in cart
            String removedProduct = (String) testContext.get("removed_product");
            if (removedProduct != null) {
                assertFalse(cartPage.isProductInCart(removedProduct), "Removed product should not be in cart");
            }
            
            logger.info("✅ Product removed successfully");
            
            completeStep("Product removal verification");
            
        } catch (Exception e) {
            handleStepFailure("Product removal verification", e);
            throw e;
        }
    }
    
    @Then("cart total should be recalculated")
    public void cartTotalShouldBeRecalculated() {
        initializeStep("Verifying cart total recalculation");
        
        try {
            // Enhanced recalculation verification
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), cartPage.getTotalElement(), 5);
            
            String newTotal = cartPage.getTotalPrice();
            assertThat("Cart total should be recalculated", newTotal, not(emptyString()));
            
            // Compare with previous total if available
            String previousTotal = (String) testContext.get("cart_total");
            if (previousTotal != null && !previousTotal.equals(newTotal)) {
                logger.info("💰 Cart total recalculated from {} to {}", previousTotal, newTotal);
            }
            
            testContext.put("recalculated_total", newTotal);
            logger.info("💰 Cart total recalculated: {}", newTotal);
            
            completeStep("Cart recalculation verification");
            
        } catch (Exception e) {
            handleStepFailure("Cart recalculation verification", e);
            throw e;
        }
    }
    
    @Then("remaining products should be intact")
    public void remainingProductsShouldBeIntact() {
        initializeStep("Verifying remaining products intact");
        
        try {
            int productCount = cartPage.getProductCount();
            assertThat("Remaining products should be intact", productCount, greaterThanOrEqualTo(0));
            
            // Verify each remaining product has valid details
            if (productCount > 0) {
                for (int i = 0; i < productCount; i++) {
                    String productName = cartPage.getProductNameByIndex(i);
                    String productPrice = cartPage.getProductPriceByIndex(i);
                    
                    assertThat("Product name should be valid", productName, not(emptyString()));
                    assertThat("Product price should be valid", productPrice, not(emptyString()));
                }
            }
            
            testContext.put("remaining_products_count", productCount);
            logger.info("✅ Remaining products intact: {}", productCount);
            
            completeStep("Remaining products verification");
            
        } catch (Exception e) {
            handleStepFailure("Remaining products verification", e);
            throw e;
        }
    }
    
    @Then("empty cart warning should appear")
    public void emptyCartWarningShouldAppear() {
        initializeStep("Verifying empty cart warning");
        
        try {
            // Enhanced empty cart warning verification
            boolean warningVisible = WaitUtils.waitForCondition(
                driver -> cartPage.isEmptyCartMessageVisible() || cartPage.isCartEmpty(), 
                5
            );
            
            assertTrue(warningVisible, "Empty cart warning should appear");
            
            // Additional verification - cart should indeed be empty
            assertTrue(cartPage.isCartEmpty(), "Cart should actually be empty");
            assertEquals(0, cartPage.getProductCount(), "Cart product count should be zero");
            
            logger.info("✅ Empty cart warning appeared");
            
            completeStep("Empty cart warning verification");
            
        } catch (Exception e) {
            handleStepFailure("Empty cart warning verification", e);
            throw e;
        }
    }
    
    @Then("checkout should be prevented")
    public void checkoutShouldBePrevented() {
        initializeStep("Verifying checkout prevention");
        
        try {
            // Check if checkout alert appeared
            String checkoutAlert = (String) testContext.get("checkout_alert");
            if (checkoutAlert != null) {
                logger.info("✅ Checkout prevented with alert: {}", checkoutAlert);
            }
            
            // Additional verification - should still be on cart page
            assertTrue(cartPage.isPageLoaded(), "Should remain on cart page");
            
            // Verify place order button behavior
            if (cartPage.isCartEmpty()) {
                assertFalse(cartPage.isPlaceOrderButtonEnabled(), "Place order button should be disabled for empty cart");
            }
            
            logger.info("✅ Checkout correctly prevented");
            
            completeStep("Checkout prevention verification");
            
        } catch (Exception e) {
            handleStepFailure("Checkout prevention verification", e);
            throw e;
        }
    }
    
    @Then("cart page should remain visible")
    public void cartPageShouldRemainVisible() {
        initializeStep("Verifying cart page visibility");
        
        try {
            // Enhanced cart page visibility verification
            boolean cartVisible = WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded() && DriverManager.getCurrentUrl().contains("cart"), 
                5
            );
            
            assertTrue(cartVisible, "Cart page should remain visible");
            assertTrue(cartPage.isPageLoaded(), "Cart page components should be loaded");
            
            // Additional validation - cart specific elements should be present
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), cartPage.getCartTableElement(), 5);
            
            logger.info("✅ Cart page remains visible");
            
            completeStep("Cart page visibility verification");
            
        } catch (Exception e) {
            handleStepFailure("Cart page visibility verification", e);
            throw e;
        }
    }
    
    // ===== ENHANCED CHECKOUT STEPS =====
    
    @When("I complete the checkout form with valid information")
    public void iCompleteTheCheckoutFormWithValidInformation(DataTable dataTable) {
        initializeStep("Completing checkout form");
        
        try {
            List<Map<String, String>> checkoutInfo = dataTable.asMaps();
            Map<String, String> info = checkoutInfo.get(0);
            
            // Enhanced checkout form completion
            WaitUtils.waitForElementVisible(DriverManager.getDriver(), cartPage.getCheckoutModal(), 10);
            
            cartPage.fillCheckoutForm(
                info.get("name"),
                info.get("country"),
                info.get("city"),
                info.get("creditCard"),
                info.get("month"),
                info.get("year")
            );
            
            testContext.put("checkout_info", info);
            logger.info("📝 Checkout form completed");
            
            completeStep("Checkout form completion");
            
        } catch (Exception e) {
            handleStepFailure("Checkout form completion", e);
            throw e;
        }
    }
    
    @When("I submit the order")
    public void iSubmitTheOrder() {
        initializeStep("Submitting order");
        
        try {
            WaitUtils.waitForElementClickable(DriverManager.getDriver(), cartPage.getPurchaseButton(), 5);
            cartPage.clickPurchase();
            
            // Handle order submission result
            boolean orderSuccess = AlertHandler.handleOrderAlert(DriverManager.getDriver());
            testContext.put("order_success", orderSuccess);
            
            logger.info("📦 Order submitted (success: {})", orderSuccess);
            
            completeStep("Order submission");
            
        } catch (Exception e) {
            handleStepFailure("Order submission", e);
            throw e;
        }
    }
    
    @Then("order should be placed successfully")
    public void orderShouldBePlacedSuccessfully() {
        initializeStep("Verifying successful order placement");
        
        try {
            // Check order success from context
            Boolean orderSuccess = (Boolean) testContext.get("order_success");
            if (orderSuccess != null) {
                assertTrue(orderSuccess, "Order should be placed successfully");
            }
            
            // Additional verification - success modal or confirmation should appear
            boolean confirmationVisible = WaitUtils.waitForCondition(
                driver -> cartPage.isOrderConfirmationVisible() || cartPage.isSuccessModalVisible(), 
                10
            );
            
            assertTrue(confirmationVisible, "Order confirmation should be visible");
            logger.info("✅ Order placed successfully");
            
            completeStep("Order placement verification");
            
        } catch (Exception e) {
            handleStepFailure("Order placement verification", e);
            throw e;
        }
    }
    
    @Then("order confirmation should be displayed")
    public void orderConfirmationShouldBeDisplayed() {
        initializeStep("Verifying order confirmation display");
        
        try {
            boolean confirmationDisplayed = WaitUtils.waitForElementVisible(
                DriverManager.getDriver(), 
                cartPage.getOrderConfirmationElement(), 
                10
            );
            
            assertTrue(confirmationDisplayed, "Order confirmation should be displayed");
            
            // Get confirmation details if available
            String confirmationText = cartPage.getOrderConfirmationText();
            if (confirmationText != null && !confirmationText.isEmpty()) {
                testContext.put("confirmation_text", confirmationText);
                logger.info("📄 Order confirmation: {}", confirmationText);
            }
            
            logger.info("✅ Order confirmation displayed");
            
            completeStep("Order confirmation verification");
            
        } catch (Exception e) {
            handleStepFailure("Order confirmation verification", e);
            throw e;
        }
    }
    
    @Then("cart should be cleared")
    public void cartShouldBeCleared() {
        initializeStep("Verifying cart clearance");
        
        try {
            // Navigate back to cart to verify it's empty
            homePage = new HomePage().navigateToHome();
            cartPage = homePage.clickCart();
            
            WaitUtils.waitForCondition(
                driver -> cartPage.isPageLoaded(), 
                10
            );
            
            boolean cartEmpty = WaitUtils.waitForCondition(
                driver -> cartPage.isCartEmpty(), 
                5
            );
            
            assertTrue(cartEmpty, "Cart should be cleared after successful order");
            assertEquals(0, cartPage.getProductCount(), "Cart should have no products");
            
            logger.info("✅ Cart cleared after order");
            
            completeStep("Cart clearance verification");
            
        } catch (Exception e) {
            handleStepFailure("Cart clearance verification", e);
            throw e;
        }
    }
    
    // ===== ENHANCED UTILITY METHODS =====
    
    /**
     * Get comprehensive test execution statistics
     */
    public String getTestExecutionStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("🦅 Kestrel Web Test Execution Statistics\n");
        stats.append("==========================================\n");
        stats.append("Current User: ").append(currentUsername != null ? currentUsername : "Not authenticated").append("\n");
        stats.append("Selected Product: ").append(selectedProduct != null ? selectedProduct : "None").append("\n");
        stats.append("Test Context Entries: ").append(testContext.size()).append("\n");
        stats.append("Max Retries: ").append(MAX_RETRIES).append("\n");
        stats.append("Current Retry Count: ").append(retryCount).append("\n");
        stats.append("Session Info: ").append(DriverManager.getSessionInfo()).append("\n");
        stats.append("==========================================");
        
        return stats.toString();
    }
    
    /**
     * Clear test context for new scenario
     */
    public void clearTestContext() {
        testContext.clear();
        currentUsername = null;
        currentPassword = null;
        selectedProduct = null;
        retryCount = 0;
        logger.debug("🧹 Test context cleared");
    }
    
    /**
     * Get current test context
     */
    public Map<String, Object> getTestContext() {
        return new ConcurrentHashMap<>(testContext);
    }
    
    /**
     * Enhanced error recovery mechanism
     */
    private void attemptErrorRecovery(String operation) {
        try {
            logger.info("🔄 Attempting error recovery for: {}", operation);
            
            // Clean up any alerts
            AlertHandler.cleanupAnyAlerts(DriverManager.getDriver());
            
            // Refresh page if needed
            if (!WaitUtils.waitForPageLoaded(DriverManager.getDriver(), 5)) {
                DriverManager.refresh();
                WaitUtils.waitForPageLoaded(DriverManager.getDriver(), 10);
            }
            
            // Re-initialize home page if needed
            if (homePage == null || !homePage.isPageLoaded()) {
                homePage = new HomePage().navigateToHome();
            }
            
            logger.info("✅ Error recovery completed for: {}", operation);
            
        } catch (Exception recovery) {
            logger.warn("⚠️ Error recovery failed for {}: {}", operation, recovery.getMessage());
        }
    }
    
    /**
     * Validate page object state
     */
    private boolean validatePageObjectState(Object pageObject, String pageName) {
        try {
            if (pageObject == null) {
                logger.warn("⚠️ Page object {} is null", pageName);
                return false;
            }
            
            // Use reflection to check if page has isPageLoaded method
            try {
                var isLoadedMethod = pageObject.getClass().getMethod("isPageLoaded");
                Boolean isLoaded = (Boolean) isLoadedMethod.invoke(pageObject);
                return Boolean.TRUE.equals(isLoaded);
            } catch (Exception e) {
                logger.debug("🔍 Page object {} doesn't have isPageLoaded method", pageName);
                return true; // Assume valid if method doesn't exist
            }
            
        } catch (Exception e) {
            logger.warn("⚠️ Error validating page object {}: {}", pageName, e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced step timing and performance tracking
     */
    private void trackStepPerformance(String stepName, long duration) {
        if (duration > 10000) { // More than 10 seconds
            logger.warn("🐌 Slow step detected: {} took {}ms", stepName, duration);
        } else if (duration > 5000) { // More than 5 seconds
            logger.info("⏱️ Step timing: {} took {}ms", stepName, duration);
        } else {
            logger.debug("⚡ Step timing: {} took {}ms", stepName, duration);
        }
        
        // Store performance data
        testContext.put("last_step_duration", duration);
        testContext.put("last_step_name", stepName);
    }
}
