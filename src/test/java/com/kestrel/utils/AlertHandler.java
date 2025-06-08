package com.kestrel.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 🦅 Kestrel Engine - Alert Handler Utility
 * Specialized for handling Demoblaze JavaScript alerts with precision
 * 
 * Demoblaze uses JavaScript alerts for:
 * - Sign up successful/failed
 * - Login successful/failed  
 * - Product added to cart
 * - Order placed successfully
 * - Various error messages
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class AlertHandler {
    private static final Logger logger = LoggerFactory.getLogger(AlertHandler.class);
    private static final int DEFAULT_ALERT_TIMEOUT = 10; // seconds
    
    /**
     * Wait for alert and handle it (accept/dismiss)
     * @param driver WebDriver instance
     * @param accept true to accept (OK), false to dismiss (Cancel)
     * @param timeoutSeconds Max time to wait for alert
     * @return Alert text if found, null if no alert
     */
    public static String handleAlert(WebDriver driver, boolean accept, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            
            String alertText = alert.getText();
            logger.info("🚨 Alert detected: '{}'", alertText);
            
            if (accept) {
                alert.accept();
                logger.debug("✅ Alert accepted (OK clicked)");
            } else {
                alert.dismiss();
                logger.debug("❌ Alert dismissed (Cancel clicked)");
            }
            
            return alertText;
            
        } catch (TimeoutException e) {
            logger.debug("⏰ No alert appeared within {} seconds", timeoutSeconds);
            return null;
        } catch (Exception e) {
            logger.warn("⚠️ Error handling alert: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Wait for alert and accept it (click OK) - most common case
     * @param driver WebDriver instance
     * @return Alert text if found, null if no alert
     */
    public static String acceptAlert(WebDriver driver) {
        return handleAlert(driver, true, DEFAULT_ALERT_TIMEOUT);
    }
    
    /**
     * Wait for alert and accept it with custom timeout
     * @param driver WebDriver instance
     * @param timeoutSeconds Max time to wait for alert
     * @return Alert text if found, null if no alert
     */
    public static String acceptAlert(WebDriver driver, int timeoutSeconds) {
        return handleAlert(driver, true, timeoutSeconds);
    }
    
    /**
     * Wait for alert and dismiss it (click Cancel)
     * @param driver WebDriver instance
     * @return Alert text if found, null if no alert
     */
    public static String dismissAlert(WebDriver driver) {
        return handleAlert(driver, false, DEFAULT_ALERT_TIMEOUT);
    }
    
    /**
     * Check if alert is present without waiting
     * @param driver WebDriver instance
     * @return true if alert is present
     */
    public static boolean isAlertPresent(WebDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Wait for specific alert text and handle it
     * @param driver WebDriver instance
     * @param expectedText Expected alert text (partial match)
     * @param accept true to accept, false to dismiss
     * @param timeoutSeconds Max time to wait
     * @return true if expected alert was found and handled
     */
    public static boolean handleExpectedAlert(WebDriver driver, String expectedText, boolean accept, int timeoutSeconds) {
        String alertText = handleAlert(driver, accept, timeoutSeconds);
        if (alertText != null && alertText.toLowerCase().contains(expectedText.toLowerCase())) {
            logger.info("✅ Expected alert found and handled: '{}'", alertText);
            return true;
        } else if (alertText != null) {
            logger.warn("⚠️ Unexpected alert text. Expected: '{}', Got: '{}'", expectedText, alertText);
            return false;
        } else {
            logger.warn("⚠️ No alert found for expected text: '{}'", expectedText);
            return false;
        }
    }
    
    /**
     * Handle Demoblaze signup alert
     * @param driver WebDriver instance
     * @return true if signup was successful
     */
    public static boolean handleSignupAlert(WebDriver driver) {
        String alertText = acceptAlert(driver, 5);
        if (alertText != null) {
            if (alertText.toLowerCase().contains("successful")) {
                logger.info("✅ Signup successful: {}", alertText);
                return true;
            } else {
                logger.warn("❌ Signup failed: {}", alertText);
                return false;
            }
        }
        logger.warn("⚠️ No signup alert detected");
        return false;
    }
    
    /**
     * Handle Demoblaze login alert
     * @param driver WebDriver instance
     * @return true if login was successful (no alert = success for login)
     */
    public static boolean handleLoginAlert(WebDriver driver) {
        String alertText = acceptAlert(driver, 3);
        if (alertText == null) {
            // No alert usually means successful login in Demoblaze
            logger.info("✅ Login successful (no alert)");
            return true;
        } else {
            // Alert usually means login failed
            logger.warn("❌ Login failed: {}", alertText);
            return false;
        }
    }
    
    /**
     * Handle product added to cart alert
     * @param driver WebDriver instance
     * @return true if product was added successfully
     */
    public static boolean handleAddToCartAlert(WebDriver driver) {
        String alertText = acceptAlert(driver, 5);
        if (alertText != null && alertText.toLowerCase().contains("added")) {
            logger.info("✅ Product added to cart: {}", alertText);
            return true;
        } else if (alertText != null) {
            logger.warn("❌ Add to cart failed: {}", alertText);
            return false;
        }
        logger.warn("⚠️ No add to cart alert detected");
        return false;
    }
    
    /**
     * Handle order placement alert
     * @param driver WebDriver instance
     * @return true if order was placed successfully
     */
    public static boolean handleOrderAlert(WebDriver driver) {
        String alertText = acceptAlert(driver, 5);
        if (alertText != null && (alertText.toLowerCase().contains("thank") || alertText.toLowerCase().contains("successful"))) {
            logger.info("✅ Order placed successfully: {}", alertText);
            return true;
        } else if (alertText != null) {
            logger.warn("❌ Order placement failed: {}", alertText);
            return false;
        }
        logger.warn("⚠️ No order alert detected");
        return false;
    }
    
    /**
     * Cleanup any unexpected alerts
     * @param driver WebDriver instance
     */
    public static void cleanupAnyAlerts(WebDriver driver) {
        try {
            while (isAlertPresent(driver)) {
                String alertText = acceptAlert(driver, 1);
                logger.debug("🧹 Cleaned up unexpected alert: '{}'", alertText);
            }
        } catch (Exception e) {
            logger.debug("🧹 Alert cleanup completed");
        }
    }
}