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
 * Kestrel Engine Driver Manager
 * Manages WebDriver instances with hunting precision
 * 
 * Features:
 * - Thread-safe driver management
 * - Multi-browser support (Chrome, Firefox)
 * - Headless mode for CI/CD
 * - Auto-configuration from environment
 * 
 * @author Kestrel Engine
 * @version 1.0.0
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
     * Create Chrome WebDriver with Kestrel optimizations
     * @param headless Whether to run in headless mode
     * @return Configured Chrome WebDriver
     */
    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        // Headless configuration
        if (headless) {
            options.addArguments("--headless=new"); // New headless mode
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        }
        
        // Performance optimizations
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images"); // Faster loading
        options.addArguments("--disable-javascript"); // Only if testing doesn't require JS
        
        // Window size for consistent screenshots
        if (headless) {
            options.addArguments("--window-size=1920,1080");
        }
        
        // Additional stability options
        options.addArguments("--no-first-run");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-popup-blocking");
        
        logger.debug("🦅 Chrome options configured for Kestrel Engine");
        return new ChromeDriver(options);
    }
    
    /**
     * Create Firefox WebDriver with Kestrel optimizations
     * @param headless Whether to run in headless mode
     * @return Configured Firefox WebDriver
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        // Performance optimizations
        options.addPreference("media.volume_scale", "0.0"); // Mute audio
        options.addPreference("dom.webnotifications.enabled", false); // Disable notifications
        
        logger.debug("🦅 Firefox options configured");
        return new FirefoxDriver(options);
    }
    
    /**
     * Configure driver timeouts and settings
     * @param webDriver WebDriver instance to configure
     */
    private static void configureDriver(WebDriver webDriver) {
        // Configure timeouts from environment
        webDriver.manage().timeouts()
            .implicitlyWait(Duration.ofSeconds(EnvironmentManager.getImplicitWait()))
            .pageLoadTimeout(Duration.ofSeconds(EnvironmentManager.getPageLoadTimeout()));
        
        // Maximize window if not headless
        if (!EnvironmentManager.isHeadless()) {
            webDriver.manage().window().maximize();
            logger.debug("🦅 Browser window maximized");
        }
        
        logger.debug("🦅 Driver timeouts configured - Implicit: {}s, Page Load: {}s", 
                    EnvironmentManager.getImplicitWait(), 
                    EnvironmentManager.getPageLoadTimeout());
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
     * Navigate to URL with error handling
     * @param url Target URL
     */
    public static void navigateTo(String url) {
        try {
            logger.info("🎯 Kestrel navigating to: {}", url);
            getDriver().get(url);
            logger.debug("✅ Navigation successful");
        } catch (Exception e) {
            logger.error("❌ Navigation failed to: {}", url, e);
            throw new RuntimeException("Navigation failed", e);
        }
    }
    
    /**
     * Get current page title
     * @return Page title
     */
    public static String getCurrentTitle() {
        String title = getDriver().getTitle();
        logger.debug("📄 Current page title: {}", title);
        return title;
    }
    
    /**
     * Get current page URL
     * @return Current URL
     */
    public static String getCurrentUrl() {
        String url = getDriver().getCurrentUrl();
        logger.debug("🔗 Current URL: {}", url);
        return url;
    }
    
    /**
     * Quit driver and clean up resources
     * Thread-safe cleanup
     */
    public static void quitDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            try {
                String threadName = Thread.currentThread().getName();
                logger.info("🦅 Kestrel driver mission complete on thread: {}, shutting down", threadName);
                currentDriver.quit();
                logger.debug("✅ Driver quit successfully");
            } catch (Exception e) {
                logger.warn("⚠️ Error during driver quit: {}", e.getMessage());
            } finally {
                driver.remove();
                logger.debug("🧹 Driver thread local cleaned up");
            }
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
}