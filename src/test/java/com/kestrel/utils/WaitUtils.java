package com.kestrel.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Function;

/**
 * 🦅 Kestrel Engine - Production-Grade Wait Utilities
 * Professional waiting strategies with proper exception handling and fluent interface
 * 
 * Design Principles:
 * - Fail Fast: Methods throw exceptions on timeout instead of returning false
 * - No Thread.sleep(): All waits are dynamic and condition-based
 * - Fluent Interface: Instantiated class for cleaner method calls
 * - Comprehensive Logging: Detailed debug information for troubleshooting
 * 
 * @author Kestrel Engine
 * @version 3.0.0 (Production-Grade Edition)
 */
public class WaitUtils {
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);
    
    // Instance variables for fluent interface
    private final WebDriverWait wait;
    private final WebDriver driver;
    
    // Timeout constants
    public static final int DEFAULT_TIMEOUT = 15; // seconds
    public static final int QUICK_TIMEOUT = 5; // seconds
    public static final int EXTENDED_TIMEOUT = 30; // seconds
    
    /**
     * Constructor for fluent interface
     * @param driver WebDriver instance
     * @param defaultTimeoutSeconds Default timeout for all operations
     */
    public WaitUtils(WebDriver driver, int defaultTimeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeoutSeconds));
        logger.debug("🦅 WaitUtils initialized with {}s default timeout", defaultTimeoutSeconds);
    }
    
    /**
     * Constructor with default timeout
     * @param driver WebDriver instance
     */
    public WaitUtils(WebDriver driver) {
        this(driver, DEFAULT_TIMEOUT);
    }
    
    // ===== FLUENT INTERFACE METHODS (INSTANCE) =====
    
    /**
     * Wait for element to be visible (fluent interface)
     * @param element WebElement to wait for
     * @throws TimeoutException if element is not visible within timeout
     */
    public void forElementVisible(WebElement element) {
        forElementVisible(element, null);
    }
    
    /**
     * Wait for element to be visible with custom timeout (fluent interface)
     * @param element WebElement to wait for
     * @param timeoutSeconds Custom timeout (null for default)
     * @throws TimeoutException if element is not visible within timeout
     */
    public void forElementVisible(WebElement element, Integer timeoutSeconds) {
        try {
            WebDriverWait customWait = timeoutSeconds != null ? 
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)) : wait;
            
            customWait.until(ExpectedConditions.visibilityOf(element));
            logger.debug("✅ Element visible: {}", getElementDescription(element));
            
        } catch (TimeoutException e) {
            String timeout = timeoutSeconds != null ? timeoutSeconds.toString() : "default";
            String message = String.format("Element not visible within %s seconds: %s", 
                                          timeout, getElementDescription(element));
            logger.error("❌ {}", message);
            throw new TimeoutException(message, e);
        }
    }
    
    /**
     * Wait for element to be clickable (fluent interface)
     * @param element WebElement to wait for
     * @throws TimeoutException if element is not clickable within timeout
     */
    public void forElementClickable(WebElement element) {
        forElementClickable(element, null);
    }
    
    /**
     * Wait for element to be clickable with custom timeout (fluent interface)
     * @param element WebElement to wait for
     * @param timeoutSeconds Custom timeout (null for default)
     * @throws TimeoutException if element is not clickable within timeout
     */
    public void forElementClickable(WebElement element, Integer timeoutSeconds) {
        try {
            WebDriverWait customWait = timeoutSeconds != null ? 
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)) : wait;
            
            customWait.until(ExpectedConditions.elementToBeClickable(element));
            logger.debug("✅ Element clickable: {}", getElementDescription(element));
            
        } catch (TimeoutException e) {
            String timeout = timeoutSeconds != null ? timeoutSeconds.toString() : "default";
            String message = String.format("Element not clickable within %s seconds: %s", 
                                          timeout, getElementDescription(element));
            logger.error("❌ {}", message);
            throw new TimeoutException(message, e);
        }
    }
    
    /**
     * Wait for element to be invisible (fluent interface)
     * @param element WebElement to wait for
     * @throws TimeoutException if element is still visible after timeout
     */
    public void forElementInvisible(WebElement element) {
        forElementInvisible(element, null);
    }
    
    /**
     * Wait for element to be invisible with custom timeout (fluent interface)
     * @param element WebElement to wait for
     * @param timeoutSeconds Custom timeout (null for default)
     * @throws TimeoutException if element is still visible after timeout
     */
    public void forElementInvisible(WebElement element, Integer timeoutSeconds) {
        try {
            WebDriverWait customWait = timeoutSeconds != null ? 
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)) : wait;
            
            customWait.until(ExpectedConditions.invisibilityOf(element));
            logger.debug("✅ Element invisible: {}", getElementDescription(element));
            
        } catch (TimeoutException e) {
            String timeout = timeoutSeconds != null ? timeoutSeconds.toString() : "default";
            String message = String.format("Element still visible after %s seconds: %s", 
                                          timeout, getElementDescription(element));
            logger.error("❌ {}", message);
            throw new TimeoutException(message, e);
        }
    }
    
    /**
     * Wait for custom condition (fluent interface)
     * @param condition Function that returns Boolean
     * @throws TimeoutException if condition is not met within timeout
     */
    public void forCondition(Function<WebDriver, Boolean> condition) {
        forCondition(condition, null, "custom condition");
    }
    
    /**
     * Wait for custom condition with description (fluent interface)
     * @param condition Function that returns Boolean
     * @param description Description for logging
     * @throws TimeoutException if condition is not met within timeout
     */
    public void forCondition(Function<WebDriver, Boolean> condition, String description) {
        forCondition(condition, null, description);
    }
    
    /**
     * Wait for custom condition with timeout and description (fluent interface)
     * @param condition Function that returns Boolean
     * @param timeoutSeconds Custom timeout (null for default)
     * @param description Description for logging
     * @throws TimeoutException if condition is not met within timeout
     */
    public void forCondition(Function<WebDriver, Boolean> condition, Integer timeoutSeconds, String description) {
        try {
            WebDriverWait customWait = timeoutSeconds != null ? 
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)) : wait;
            
            Boolean result = customWait.until(condition::apply);
            if (Boolean.TRUE.equals(result)) {
                logger.debug("✅ Condition met: {}", description);
            } else {
                throw new TimeoutException("Condition returned false: " + description);
            }
            
        } catch (TimeoutException e) {
            String timeout = timeoutSeconds != null ? timeoutSeconds.toString() : "default";
            String message = String.format("Condition not met within %s seconds: %s", timeout, description);
            logger.error("❌ {}", message);
            throw new TimeoutException(message, e);
        }
    }
    
    /**
     * Wait for ExpectedCondition (fluent interface)
     * @param condition ExpectedCondition to wait for
     * @throws TimeoutException if condition is not met within timeout
     */
    public void forCondition(ExpectedCondition<?> condition) {
        forCondition(condition, null);
    }
    
    /**
     * Wait for ExpectedCondition with custom timeout (fluent interface)
     * @param condition ExpectedCondition to wait for
     * @param timeoutSeconds Custom timeout (null for default)
     * @throws TimeoutException if condition is not met within timeout
     */
    public void forCondition(ExpectedCondition<?> condition, Integer timeoutSeconds) {
        try {
            WebDriverWait customWait = timeoutSeconds != null ? 
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)) : wait;
            
            Object result = customWait.until(condition);
            logger.debug("✅ Expected condition met");
            
        } catch (TimeoutException e) {
            String timeout = timeoutSeconds != null ? timeoutSeconds.toString() : "default";
            String message = String.format("Expected condition not met within %s seconds", timeout);
            logger.error("❌ {}", message);
            throw new TimeoutException(message, e);
        }
    }
    
    /**
     * Wait for page to be fully loaded (fluent interface)
     * @throws TimeoutException if page is not loaded within timeout
     */
    public void forPageLoaded() {
        forPageLoaded(null);
    }
    
    /**
     * Wait for page to be fully loaded with custom timeout (fluent interface)
     * @param timeoutSeconds Custom timeout (null for default)
     * @throws TimeoutException if page is not loaded within timeout
     */
    public void forPageLoaded(Integer timeoutSeconds) {
        try {
            WebDriverWait customWait = timeoutSeconds != null ? 
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)) : wait;
            
            customWait.until(webDriver -> {
                try {
                    Object readyState = ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState");
                    return "complete".equals(readyState);
                } catch (Exception e) {
                    return false;
                }
            });
            
            logger.debug("✅ Page fully loaded");
            
        } catch (TimeoutException e) {
            String timeout = timeoutSeconds != null ? timeoutSeconds.toString() : "default";
            String message = String.format("Page not loaded within %s seconds", timeout);
            logger.error("❌ {}", message);
            throw new TimeoutException(message, e);
        }
    }
    
    /**
     * Wait for title to contain text (fluent interface)
     * @param expectedTitle Expected title text (partial match)
     * @throws TimeoutException if title doesn't contain text within timeout
     */
    public void forTitleContaining(String expectedTitle) {
        forTitleContaining(expectedTitle, null);
    }
    
    /**
     * Wait for title to contain text with custom timeout (fluent interface)
     * @param expectedTitle Expected title text (partial match)
     * @param timeoutSeconds Custom timeout (null for default)
     * @throws TimeoutException if title doesn't contain text within timeout
     */
    public void forTitleContaining(String expectedTitle, Integer timeoutSeconds) {
        try {
            WebDriverWait customWait = timeoutSeconds != null ? 
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)) : wait;
            
            customWait.until(ExpectedConditions.titleContains(expectedTitle));
            logger.debug("✅ Title contains: '{}'", expectedTitle);
            
        } catch (TimeoutException e) {
            String timeout = timeoutSeconds != null ? timeoutSeconds.toString() : "default";
            String currentTitle = driver.getTitle();
            String message = String.format("Title '%s' not found within %s seconds. Current title: '%s'", 
                                          expectedTitle, timeout, currentTitle);
            logger.error("❌ {}", message);
            throw new TimeoutException(message, e);
        }
    }
    
    // ===== DEMOBLAZE-SPECIFIC ENHANCED METHODS =====
    
    /**
     * Wait for Demoblaze homepage to be ready (all conditions must pass)
     * @throws TimeoutException if homepage is not ready within timeout
     */
    public void forDemoblazeHomepage() {
        logger.debug("🔍 Waiting for Demoblaze homepage readiness...");
        
        // Sequential validation - each step must pass
        forPageLoaded(10);
        forTitleContaining("STORE", 5);
        
        // Dynamic wait for products to load instead of Thread.sleep
        forCondition(d -> {
            try {
                return ((JavascriptExecutor) d)
                    .executeScript("return document.querySelectorAll('.card-title').length > 0");
            } catch (Exception e) {
                return false;
            }
        }, 10, "products to load on homepage");
        
        logger.info("✅ Demoblaze homepage is ready");
    }
    
    /**
     * Wait for Demoblaze modal to be ready (visible and interactive)
     * @param modalElement Modal element to wait for
     * @throws TimeoutException if modal is not ready within timeout
     */
    public void forDemoblazeModal(WebElement modalElement) {
        logger.debug("🔍 Waiting for Demoblaze modal readiness...");
        
        // Wait for modal to be visible
        forElementVisible(modalElement);
        
        // Wait for modal to have proper display style (no Thread.sleep)
        forCondition(ExpectedConditions.attributeContains(modalElement, "style", "block"), 3);
        
        // Dynamic wait for modal to be fully interactive
        forCondition(d -> {
            try {
                return modalElement.isDisplayed() && modalElement.isEnabled();
            } catch (Exception e) {
                return false;
            }
        }, 5, "modal to be fully interactive");
        
        logger.debug("✅ Demoblaze modal is ready");
    }
    
    /**
     * Wait for Demoblaze product page to be ready
     * @param productNameElement Product name element
     * @param addToCartButton Add to cart button element
     * @throws TimeoutException if product page is not ready within timeout
     */
    public void forDemoblazeProductPage(WebElement productNameElement, WebElement addToCartButton) {
        logger.debug("🔍 Waiting for Demoblaze product page readiness...");
        
        // Sequential validation
        forElementVisible(productNameElement);
        forElementClickable(addToCartButton);
        forPageLoaded(QUICK_TIMEOUT);
        
        logger.debug("✅ Demoblaze product page is ready");
    }
    
    /**
     * Wait for Demoblaze cart page to be ready
     * @param cartContainer Cart container element
     * @throws TimeoutException if cart page is not ready within timeout
     */
    public void forDemoblazeCartPage(WebElement cartContainer) {
        logger.debug("🔍 Waiting for Demoblaze cart page readiness...");
        
        // Wait for cart container
        forElementVisible(cartContainer);
        
        // Dynamic wait for cart content to stabilize (instead of Thread.sleep)
        forCondition(d -> {
            try {
                // Wait for either cart items to load or empty cart message to appear
                return !cartContainer.findElements(By.tagName("tr")).isEmpty() ||
                       cartContainer.getText().toLowerCase().contains("empty");
            } catch (Exception e) {
                return false;
            }
        }, 10, "cart content to stabilize");
        
        logger.debug("✅ Demoblaze cart page is ready");
    }
    
    // ===== STATIC BACKWARD COMPATIBILITY METHODS =====
    
    /**
     * Static method for backward compatibility - Wait for element to be visible
     * @param driver WebDriver instance
     * @param element WebElement to wait for
     * @param timeoutSeconds Max time to wait
     * @return true if element is visible, false if timeout (for compatibility)
     * @deprecated Use fluent interface instead: new WaitUtils(driver).forElementVisible(element)
     */
    @Deprecated
    public static boolean waitForElementVisible(WebDriver driver, WebElement element, int timeoutSeconds) {
        try {
            new WaitUtils(driver, timeoutSeconds).forElementVisible(element);
            return true;
        } catch (TimeoutException e) {
            logger.warn("⚠️ Static method - Element not visible: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Static method for backward compatibility - Wait for element to be clickable
     * @param driver WebDriver instance
     * @param element WebElement to wait for
     * @param timeoutSeconds Max time to wait
     * @return true if element is clickable, false if timeout (for compatibility)
     * @deprecated Use fluent interface instead: new WaitUtils(driver).forElementClickable(element)
     */
    @Deprecated
    public static boolean waitForElementClickable(WebDriver driver, WebElement element, int timeoutSeconds) {
        try {
            new WaitUtils(driver, timeoutSeconds).forElementClickable(element);
            return true;
        } catch (TimeoutException e) {
            logger.warn("⚠️ Static method - Element not clickable: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Static method for backward compatibility - Wait for custom condition
     * @param driver WebDriver instance
     * @param condition Function condition to wait for
     * @param timeoutSeconds Max time to wait
     * @return true if condition is met, false if timeout (for compatibility)
     * @deprecated Use fluent interface instead: new WaitUtils(driver).forCondition(condition)
     */
    @Deprecated
    public static boolean waitForCondition(WebDriver driver, Function<WebDriver, Boolean> condition, int timeoutSeconds) {
        try {
            new WaitUtils(driver, timeoutSeconds).forCondition(condition, "backward compatibility condition");
            return true;
        } catch (TimeoutException e) {
            logger.warn("⚠️ Static method - Condition not met: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Static method for backward compatibility - Wait for Demoblaze homepage
     * @param driver WebDriver instance
     * @return true if homepage is ready, false if timeout (for compatibility)
     * @deprecated Use fluent interface instead: new WaitUtils(driver).forDemoblazeHomepage()
     */
    @Deprecated
    public static boolean waitForDemoblazeHomepage(WebDriver driver) {
        try {
            new WaitUtils(driver).forDemoblazeHomepage();
            return true;
        } catch (TimeoutException e) {
            logger.warn("⚠️ Static method - Demoblaze homepage not ready: {}", e.getMessage());
            return false;
        }
    }
    
    // ===== UTILITY METHODS =====
    
    /**
     * Get element description for logging
     * @param element WebElement
     * @return Element description string
     */
    private static String getElementDescription(WebElement element) {
        try {
            String tagName = element.getTagName();
            String id = element.getAttribute("id");
            String className = element.getAttribute("class");
            
            StringBuilder desc = new StringBuilder(tagName);
            if (id != null && !id.isEmpty()) {
                desc.append("#").append(id);
            }
            if (className != null && !className.isEmpty()) {
                desc.append(".").append(className.split(" ")[0]);
            }
            return desc.toString();
        } catch (Exception e) {
            return "unknown-element";
        }
    }
    
    /**
     * Check if element is ready (visible and clickable) without waiting
     * @param element WebElement to check
     * @return true if element is ready immediately
     */
    public boolean isElementReady(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get the underlying WebDriver instance
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }
    
    /**
     * Get the underlying WebDriverWait instance
     * @return WebDriverWait instance
     */
    public WebDriverWait getWait() {
        return wait;
    }
}