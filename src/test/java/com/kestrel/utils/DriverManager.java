package com.kestrel.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Kestrel Engine Driver Manager - Enhanced Version
 * Manages WebDriver instances with hunting precision
 * 
 * Features:
 * - Thread-safe driver management
 * - Multi-browser support (Chrome, Firefox)
 * - Headless mode for CI/CD
 * - Auto-configuration from environment
 * - Enhanced stability for web hunting
 * - Improved error handling and retry logic
 * 
 * @author Kestrel Engine
 * @version 1.1.0 (Enhanced)
 */
public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    /**
     * Initialize WebDriver based on environment configuration
     * Thread-safe implementation for parallel execution
     */
    public static void initializeDriver() {
        if (driver.get() != null) {
            logger.warn("🦅 Driver already initialized for thread: {}", Thread.currentThread().getName());
            return;
        }
        
        String browser = EnvironmentManager.getBrowser().toLowerCase();
        boolean headless = EnvironmentManager.isHeadless();
        String environment = EnvironmentManager.getCurrentEnvironment();
        
        logger.info("🦅 Kestrel initializing {} driver (headless: {}, env: {})", 
                    browser, headless, environment);
        
        WebDriver webDriver = switch (browser) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            default -> throw new IllegalArgumentException("❌ Unsupported browser: " + browser);
        };
        
        configureDriver(webDriver);
        driver.set(webDriver);
        
        logger.info("✅ Kestrel driver ready for hunt on thread: {}", Thread.currentThread().getName());
    }
    
    /**
     * Create Chrome WebDriver with enhanced Kestrel optimizations
     * @param headless Whether to run in headless mode
     * @return Configured Chrome WebDriver
     */
    private static WebDriver createChromeDriver(boolean headless) {
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            
            // Essential headless configuration
            if (headless) {
                options.addArguments("--headless=new"); // New headless mode
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--remote-debugging-port=9222");
                logger.debug("🦅 Chrome configured for headless hunting");
            } else {
                options.addArguments("--start-maximized");
                logger.debug("🦅 Chrome configured for visual hunting");
            }
            
            // Core stability options (KEEP JAVASCRIPT ENABLED!)
            options.addArguments("--disable-web-security");
            options.addArguments("--disable-features=VizDisplayCompositor");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-plugins");
            // REMOVED: --disable-javascript (this breaks web apps!)
            // REMOVED: --disable-images (needed for visual verification)
            
            // Enhanced stability for CI/CD environments
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--disable-renderer-backgrounding");
            options.addArguments("--disable-ipc-flooding-protection");
            options.addArguments("--disable-hang-monitor");
            options.addArguments("--disable-prompt-on-repost");
            options.addArguments("--disable-domain-reliability");
            
            // Memory and performance optimizations
            options.addArguments("--max_old_space_size=4096");
            options.addArguments("--disable-background-networking");
            options.addArguments("--no-first-run");
            options.addArguments("--disable-default-apps");
            options.addArguments("--disable-popup-blocking");
            
            // Additional stability improvements
            options.addArguments("--disable-translate");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-logging");
            options.addArguments("--disable-login-animations");
            options.addArguments("--disable-motion-blur");
            options.addArguments("--disable-new-tab-first-run");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--no-pings");
            options.addArguments("--disable-sync");
            
            // Prevent common issues with file downloads and permissions
            options.addArguments("--disable-component-update");
            options.addArguments("--disable-client-side-phishing-detection");
            
            logger.debug("🦅 Chrome options configured for enhanced stability with multiple optimizations");
            return new ChromeDriver(options);
            
        } catch (Exception e) {
            logger.error("❌ Failed to create Chrome driver: {}", e.getMessage());
            throw new RuntimeException("Chrome driver initialization failed", e);
        }
    }
    
    /**
     * Create Firefox WebDriver with enhanced Kestrel optimizations
     * @param headless Whether to run in headless mode
     * @return Configured Firefox WebDriver
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        try {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            
            if (headless) {
                options.addArguments("--headless");
                logger.debug("🦅 Firefox configured for headless hunting");
            }
            
            // Enhanced performance optimizations
            options.addPreference("media.volume_scale", "0.0"); // Mute audio
            options.addPreference("dom.webnotifications.enabled", false); // Disable notifications
            options.addPreference("dom.push.enabled", false); // Disable push notifications
            options.addPreference("dom.popup_maximum", 0); // Disable popups
            options.addPreference("browser.cache.disk.enable", false); // Disable disk cache
            options.addPreference("browser.cache.memory.enable", true); // Enable memory cache
            options.addPreference("network.http.use-cache", false); // Disable HTTP cache
            
            logger.debug("🦅 Firefox options configured with enhanced stability");
            return new FirefoxDriver(options);
            
        } catch (Exception e) {
            logger.error("❌ Failed to create Firefox driver: {}", e.getMessage());
            throw new RuntimeException("Firefox driver initialization failed", e);
        }
    }
    
    /**
     * Configure driver timeouts and settings with enhanced error handling
     * @param webDriver WebDriver instance to configure
     */
    private static void configureDriver(WebDriver webDriver) {
        try {
            // Configure timeouts from environment with enhanced values
            webDriver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(EnvironmentManager.getImplicitWait()))
                .pageLoadTimeout(Duration.ofSeconds(EnvironmentManager.getPageLoadTimeout()))
                .scriptTimeout(Duration.ofSeconds(30)); // Add script timeout
            
            // Maximize window if not headless
            if (!EnvironmentManager.isHeadless()) {
                webDriver.manage().window().maximize();
                logger.debug("🦅 Browser window maximized for visual hunting");
            }
            
            logger.debug("🦅 Driver timeouts configured - Implicit: {}s, Page Load: {}s, Script: 30s", 
                        EnvironmentManager.getImplicitWait(), 
                        EnvironmentManager.getPageLoadTimeout());
                        
        } catch (Exception e) {
            logger.warn("⚠️ Warning during driver configuration: {}", e.getMessage());
            // Continue execution even if some configurations fail
        }
    }
    
    /**
     * Get current WebDriver instance for this thread
     * @return WebDriver instance
     * @throws IllegalStateException if driver not initialized
     */
    public static WebDriver getDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver == null) {
            throw new IllegalStateException("❌ Driver not initialized. Call initializeDriver() first.");
        }
        return currentDriver;
    }
    
    /**
     * Check if driver is initialized for current thread
     * @return true if driver is initialized
     */
    public static boolean isDriverInitialized() {
        return driver.get() != null;
    }
    
    /**
     * Navigate to URL with enhanced error handling and retry logic
     * @param url Target URL
     */
    public static void navigateTo(String url) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                logger.info("🎯 Kestrel navigating to: {} (attempt: {})", url, retryCount + 1);
                getDriver().get(url);
                
                // Wait a moment for page to start loading
                Thread.sleep(1000);
                
                logger.debug("✅ Navigation successful");
                return;
                
            } catch (Exception e) {
                retryCount++;
                logger.warn("⚠️ Navigation attempt {} failed: {}", retryCount, e.getMessage());
                
                if (retryCount >= maxRetries) {
                    logger.error("❌ Navigation failed after {} attempts to: {}", maxRetries, url);
                    throw new RuntimeException("Navigation failed after retries", e);
                }
                
                try {
                    Thread.sleep(2000); // Wait before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    /**
     * Get current page title with error handling
     * @return Page title
     */
    public static String getCurrentTitle() {
        try {
            String title = getDriver().getTitle();
            logger.debug("📄 Current page title: {}", title);
            return title;
        } catch (Exception e) {
            logger.warn("⚠️ Could not get page title: {}", e.getMessage());
            return "Unknown Title";
        }
    }
    
    /**
     * Get current page URL with error handling
     * @return Current URL
     */
    public static String getCurrentUrl() {
        try {
            String url = getDriver().getCurrentUrl();
            logger.debug("🔗 Current URL: {}", url);
            return url;
        } catch (Exception e) {
            logger.warn("⚠️ Could not get current URL: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Quit driver and clean up resources with enhanced cleanup
     * Thread-safe cleanup
     */
    public static void quitDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            try {
                String threadName = Thread.currentThread().getName();
                logger.info("🦅 Kestrel driver mission complete on thread: {}, shutting down", threadName);
                
                // Try to close all windows first
                try {
                    currentDriver.close();
                } catch (Exception e) {
                    logger.debug("Close windows failed (normal): {}", e.getMessage());
                }
                
                // Then quit the driver
                currentDriver.quit();
                logger.debug("✅ Driver quit successfully");
                
            } catch (Exception e) {
                logger.warn("⚠️ Error during driver quit: {}", e.getMessage());
            } finally {
                driver.remove();
                logger.debug("🧹 Driver thread local cleaned up");
            }
        } else {
            logger.debug("🔍 No driver to quit on thread: {}", Thread.currentThread().getName());
        }
    }
    
    /**
     * Force quit all drivers (emergency cleanup)
     * Use only in exceptional circumstances
     */
    public static void forceQuitAll() {
        logger.warn("🚨 Emergency driver cleanup initiated");
        try {
            if (driver.get() != null) {
                driver.get().quit();
            }
        } catch (Exception e) {
            logger.error("❌ Error during force quit: {}", e.getMessage());
        } finally {
            driver.remove();
        }
        logger.info("🧹 Emergency cleanup completed");
    }
    
    /**
     * Check if current page is loaded (enhanced check)
     * @return true if page appears to be loaded
     */
    public static boolean isPageLoaded() {
        try {
            String title = getCurrentTitle();
            String url = getCurrentUrl();
            return !title.isEmpty() && !url.isEmpty() && !url.equals("data:,");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Refresh current page with error handling
     */
    public static void refresh() {
        try {
            logger.debug("🔄 Refreshing current page");
            getDriver().navigate().refresh();
            Thread.sleep(1000); // Wait for refresh to start
        } catch (Exception e) {
            logger.warn("⚠️ Could not refresh page: {}", e.getMessage());
        }
    }
    
    /**
     * Wait for page to be ready (basic implementation)
     * @param timeoutSeconds Maximum time to wait
     * @return true if page is ready within timeout
     */
    public static boolean waitForPageReady(int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (isPageLoaded()) {
                return true;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}