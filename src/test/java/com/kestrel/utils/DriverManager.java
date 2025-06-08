package com.kestrel.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.io.File;
import java.util.Set;

/**
 * 🦅 Kestrel Engine Driver Manager - Ultimate Enhanced Version
 * Manages WebDriver instances with surgical precision and enterprise-grade reliability
 * 
 * Features:
 * - Thread-safe driver management with intelligent cleanup
 * - Multi-browser support (Chrome, Firefox, Edge)
 * - Advanced headless mode optimization for CI/CD
 * - Auto-configuration from environment with fallbacks
 * - Enhanced stability for continuous web hunting
 * - Comprehensive error handling and retry logic with exponential backoff
 * - Chrome binary path auto-detection with multi-OS support
 * - Intelligent page load detection and validation
 * - Performance monitoring and optimization
 * - Alert handling integration
 * - Window management and focus handling
 * 
 * @author Kestrel Engine
 * @version 2.0.0 (Ultimate Enterprise Edition)
 */
public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<Long> sessionStartTime = new ThreadLocal<>();
    private static final ThreadLocal<String> sessionBrowser = new ThreadLocal<>();
    
    // Multi-OS Chrome binary path detection
    private static final String[] CHROME_BINARY_PATHS_WINDOWS = {
        "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
        "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
        System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe"
    };
    
    private static final String[] CHROME_BINARY_PATHS_LINUX = {
        "/usr/bin/google-chrome",
        "/usr/bin/google-chrome-stable",
        "/usr/bin/chromium-browser",
        "/snap/bin/chromium"
    };
    
    private static final String[] CHROME_BINARY_PATHS_MAC = {
        "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
    };
    
    // Enhanced configuration constants
    private static final int MAX_NAVIGATION_RETRIES = 3;
    private static final int BASE_RETRY_DELAY_MS = 2000;
    private static final int MAX_PAGE_LOAD_TIMEOUT = 60; // seconds
    private static final int ENHANCED_IMPLICIT_WAIT = 15; // seconds
    
    /**
     * Initialize WebDriver based on environment configuration with enhanced intelligence
     * Thread-safe implementation for parallel execution with session tracking
     */
    public static void initializeDriver() {
        if (driver.get() != null) {
            logger.warn("🦅 Driver already initialized for thread: {} (session: {}s)", 
                       Thread.currentThread().getName(), getSessionDuration());
            return;
        }
        
        // Record session start time
        sessionStartTime.set(System.currentTimeMillis());
        
        String browser = EnvironmentManager.getBrowser().toLowerCase();
        boolean headless = EnvironmentManager.isHeadless();
        String environment = EnvironmentManager.getCurrentEnvironment();
        
        // Store browser info for session tracking
        sessionBrowser.set(browser);
        
        logger.info("🦅 Kestrel initializing {} driver (headless: {}, env: {}, thread: {})", 
                    browser, headless, environment, Thread.currentThread().getName());
        
        WebDriver webDriver = switch (browser) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            case "edge" -> createEdgeDriver(headless);
            default -> throw new IllegalArgumentException("❌ Unsupported browser: " + browser + ". Supported: chrome, firefox, edge");
        };
        
        configureDriver(webDriver);
        driver.set(webDriver);
        
        // Verify driver is working
        if (verifyDriverHealth()) {
            logger.info("✅ Kestrel {} driver ready for hunt on thread: {} (health check passed)", 
                       browser, Thread.currentThread().getName());
        } else {
            logger.error("❌ Driver health check failed");
            quitDriver();
            throw new RuntimeException("Driver initialization failed health check");
        }
    }
    
    /**
     * Detect Chrome binary path automatically across different operating systems
     * @return Chrome binary path or null if not found
     */
    private static String detectChromeBinaryPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] pathsToCheck;
        
        if (os.contains("win")) {
            pathsToCheck = CHROME_BINARY_PATHS_WINDOWS;
        } else if (os.contains("linux")) {
            pathsToCheck = CHROME_BINARY_PATHS_LINUX;
        } else if (os.contains("mac")) {
            pathsToCheck = CHROME_BINARY_PATHS_MAC;
        } else {
            logger.warn("⚠️ Unknown OS: {}, using Windows Chrome paths as fallback", os);
            pathsToCheck = CHROME_BINARY_PATHS_WINDOWS;
        }
        
        for (String path : pathsToCheck) {
            File chromeFile = new File(path);
            if (chromeFile.exists() && chromeFile.isFile()) {
                logger.debug("🔍 Chrome binary detected at: {} (OS: {})", path, os);
                return path;
            }
        }
        
        logger.warn("⚠️ Chrome binary not found in standard locations for OS: {}", os);
        return null;
    }
    
    /**
     * Create Chrome WebDriver with ultimate Kestrel optimizations and reliability
     * @param headless Whether to run in headless mode
     * @return Configured Chrome WebDriver with enterprise-grade settings
     */
    private static WebDriver createChromeDriver(boolean headless) {
        try {
            // Enhanced WebDriverManager setup with caching
            WebDriverManager chromeManager = WebDriverManager.chromedriver();
            chromeManager.clearDriverCache(); // Ensure latest driver
            chromeManager.setup();
            
            ChromeOptions options = new ChromeOptions();
            
            // 🎯 CHROME BINARY PATH FIX - Multi-OS auto-detection
            String chromeBinaryPath = detectChromeBinaryPath();
            if (chromeBinaryPath != null) {
                options.setBinary(chromeBinaryPath);
                logger.info("🎯 Chrome binary path set to: {}", chromeBinaryPath);
            } else {
                logger.warn("⚠️ Chrome binary path not detected, using system default (may cause issues)");
            }
            
            // Essential headless configuration with enhanced stability
            if (headless) {
                options.addArguments("--headless=new"); // Latest headless mode
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--remote-debugging-port=0"); // Use random available port
                options.addArguments("--virtual-time-budget=10000"); // Enhanced for CI/CD
                logger.debug("🦅 Chrome configured for headless hunting with CI/CD optimizations");
            } else {
                options.addArguments("--start-maximized");
                options.addArguments("--disable-infobars");
                options.addArguments("--disable-notifications");
                logger.debug("🦅 Chrome configured for visual hunting");
            }
            
            // Core stability options (JAVASCRIPT ENABLED for web app testing)
            options.addArguments("--disable-web-security");
            options.addArguments("--disable-features=VizDisplayCompositor,TranslateUI");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-plugins");
            options.addArguments("--no-first-run");
            options.addArguments("--disable-default-apps");
            
            // Enhanced performance and stability for continuous testing
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--disable-renderer-backgrounding");
            options.addArguments("--disable-ipc-flooding-protection");
            options.addArguments("--disable-hang-monitor");
            options.addArguments("--disable-prompt-on-repost");
            options.addArguments("--disable-domain-reliability");
            options.addArguments("--disable-component-extensions-with-background-pages");
            
            // Memory and resource optimization for long-running sessions
            options.addArguments("--max_old_space_size=4096");
            options.addArguments("--disable-background-networking");
            options.addArguments("--disable-sync");
            options.addArguments("--disable-translate");
            options.addArguments("--disable-logging");
            options.addArguments("--disable-login-animations");
            options.addArguments("--disable-motion-blur");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--no-pings");
            
            // Security and privacy enhancements
            options.addArguments("--disable-client-side-phishing-detection");
            options.addArguments("--disable-component-update");
            options.addArguments("--disable-datasaver-prompt");
            options.addArguments("--disable-desktop-notifications");
            
            // Performance preferences
            options.setExperimentalOption("useAutomationExtension", false);
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.addArguments("--disable-blink-features=AutomationControlled");
            
            // User agent and behavior optimization
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 KestrelEngine/2.0");
            
            // Create driver with enhanced error handling
            WebDriver chromeDriver = new ChromeDriver(options);
            logger.info("✅ Chrome driver created successfully");
            return chromeDriver;
            
        } catch (Exception e) {
            logger.error("❌ Failed to create Chrome driver: {}", e.getMessage(), e);
            throw new RuntimeException("Chrome driver initialization failed: " + e.getMessage(), e);
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
            options.addPreference("media.volume_scale", "0.0");
            options.addPreference("dom.webnotifications.enabled", false);
            options.addPreference("dom.push.enabled", false);
            options.addPreference("dom.popup_maximum", 0);
            options.addPreference("browser.cache.disk.enable", false);
            options.addPreference("browser.cache.memory.enable", true);
            options.addPreference("network.http.use-cache", false);
            options.addPreference("browser.sessionstore.privacy_level", 2);
            options.addPreference("network.cookie.cookieBehavior", 1);
            
            logger.debug("🦅 Firefox options configured with enhanced stability");
            return new FirefoxDriver(options);
            
        } catch (Exception e) {
            logger.error("❌ Failed to create Firefox driver: {}", e.getMessage());
            throw new RuntimeException("Firefox driver initialization failed", e);
        }
    }
    
    /**
     * Create Edge WebDriver (Windows users)
     * @param headless Whether to run in headless mode
     * @return Configured Edge WebDriver
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        try {
            WebDriverManager.edgedriver().setup();
            org.openqa.selenium.edge.EdgeOptions options = new org.openqa.selenium.edge.EdgeOptions();
            
            if (headless) {
                options.addArguments("--headless");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
            }
            
            options.addArguments("--disable-web-security");
            options.addArguments("--disable-features=VizDisplayCompositor");
            
            logger.debug("🦅 Edge options configured for hunting");
            return new org.openqa.selenium.edge.EdgeDriver(options);
            
        } catch (Exception e) {
            logger.error("❌ Failed to create Edge driver: {}", e.getMessage());
            throw new RuntimeException("Edge driver initialization failed", e);
        }
    }
    
    /**
     * Configure driver timeouts and settings with intelligent defaults
     * @param webDriver WebDriver instance to configure
     */
    private static void configureDriver(WebDriver webDriver) {
        try {
            // Enhanced timeout configuration
            int implicitWait = Math.max(EnvironmentManager.getImplicitWait(), ENHANCED_IMPLICIT_WAIT);
            int pageLoadTimeout = Math.max(EnvironmentManager.getPageLoadTimeout(), MAX_PAGE_LOAD_TIMEOUT);
            
            webDriver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(implicitWait))
                .pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout))
                .scriptTimeout(Duration.ofSeconds(45)); // Enhanced script timeout
            
            // Window management
            if (!EnvironmentManager.isHeadless()) {
                webDriver.manage().window().maximize();
                logger.debug("🦅 Browser window maximized for optimal hunting");
            }
            
            // Cookie management
            webDriver.manage().deleteAllCookies();
            
            logger.info("🦅 Driver configured - Implicit: {}s, Page Load: {}s, Script: 45s", 
                       implicitWait, pageLoadTimeout);
                        
        } catch (Exception e) {
            logger.warn("⚠️ Warning during driver configuration: {}", e.getMessage());
            // Continue execution even if some configurations fail
        }
    }
    
    /**
     * Verify driver health with comprehensive checks
     * @return true if driver is healthy and responsive
     */
    private static boolean verifyDriverHealth() {
        try {
            WebDriver currentDriver = driver.get();
            if (currentDriver == null) {
                return false;
            }
            
            // Test basic operations
            String title = currentDriver.getTitle();
            String url = currentDriver.getCurrentUrl();
            
            // Test JavaScript execution
            if (currentDriver instanceof JavascriptExecutor) {
                Object result = ((JavascriptExecutor) currentDriver).executeScript("return 'kestrel-test';");
                if (!"kestrel-test".equals(result)) {
                    logger.warn("⚠️ JavaScript execution test failed");
                    return false;
                }
            }
            
            logger.debug("✅ Driver health check passed (title: {}, url: {})", 
                        title != null ? title.substring(0, Math.min(title.length(), 20)) : "null", 
                        url != null ? url.substring(0, Math.min(url.length(), 30)) : "null");
            return true;
            
        } catch (Exception e) {
            logger.warn("❌ Driver health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get session duration in seconds
     * @return Session duration in seconds
     */
    private static long getSessionDuration() {
        Long startTime = sessionStartTime.get();
        if (startTime != null) {
            return (System.currentTimeMillis() - startTime) / 1000;
        }
        return 0;
    }
    
    /**
     * Get current WebDriver instance for this thread with validation
     * @return WebDriver instance
     * @throws IllegalStateException if driver not initialized
     */
    public static WebDriver getDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver == null) {
            throw new IllegalStateException("❌ Driver not initialized. Call initializeDriver() first.");
        }
        
        // Verify driver is still responsive
        try {
            currentDriver.getCurrentUrl(); // Simple health check
        } catch (Exception e) {
            logger.error("❌ Driver appears to be dead, attempting recovery: {}", e.getMessage());
            quitDriver();
            throw new IllegalStateException("Driver became unresponsive and was terminated", e);
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
     * Navigate to URL with intelligent retry logic and comprehensive error handling
     * @param url Target URL
     */
    public static void navigateTo(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("❌ URL cannot be null or empty");
        }
        
        // Validate URL format
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            logger.warn("⚠️ URL missing protocol, adding https://: {}", url);
            url = "https://" + url;
        }
        
        int retryCount = 0;
        Exception lastException = null;
        
        while (retryCount < MAX_NAVIGATION_RETRIES) {
            try {
                logger.info("🎯 Kestrel navigating to: {} (attempt: {}/{})", url, retryCount + 1, MAX_NAVIGATION_RETRIES);
                
                // Enhanced timeout for this navigation
                WebDriver currentDriver = getDriver();
                currentDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(MAX_PAGE_LOAD_TIMEOUT));
                
                // Clear any existing alerts before navigation
                AlertHandler.cleanupAnyAlerts(currentDriver);
                
                // Perform navigation
                long startTime = System.currentTimeMillis();
                currentDriver.get(url);
                long navigationTime = System.currentTimeMillis() - startTime;
                
                // Wait for basic page readiness
                if (waitForBasicPageReadiness(15)) {
                    logger.info("✅ Navigation successful to: {} ({}ms)", url, navigationTime);
                    return;
                } else {
                    throw new RuntimeException("Page did not reach ready state within timeout");
                }
                
            } catch (org.openqa.selenium.TimeoutException e) {
                lastException = e;
                retryCount++;
                long delay = calculateRetryDelay(retryCount);
                logger.warn("⏰ Navigation timeout on attempt {}/{} for: {} - waiting {}ms before retry", 
                           retryCount, MAX_NAVIGATION_RETRIES, url, delay);
                
                if (retryCount >= MAX_NAVIGATION_RETRIES) {
                    break;
                }
                
                // Enhanced recovery strategy
                try {
                    Thread.sleep(delay);
                    
                    // Try to refresh or recover
                    if (retryCount == 2) {
                        logger.info("🔄 Attempting page refresh for recovery");
                        getDriver().navigate().refresh();
                        Thread.sleep(2000);
                    }
                    
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                logger.warn("⚠️ Navigation attempt {}/{} failed: {}", retryCount, MAX_NAVIGATION_RETRIES, e.getMessage());
                
                if (retryCount >= MAX_NAVIGATION_RETRIES) {
                    break;
                }
                
                try {
                    Thread.sleep(calculateRetryDelay(retryCount));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        // All retries failed
        logger.error("❌ Navigation failed after {} attempts to: {}", MAX_NAVIGATION_RETRIES, url);
        throw new RuntimeException("Navigation failed after " + MAX_NAVIGATION_RETRIES + " attempts to: " + url, lastException);
    }
    
    /**
     * Calculate retry delay with exponential backoff
     * @param retryCount Current retry attempt
     * @return Delay in milliseconds
     */
    private static long calculateRetryDelay(int retryCount) {
        return BASE_RETRY_DELAY_MS * (long) Math.pow(2, retryCount - 1);
    }
    
    /**
     * Wait for basic page readiness with multiple indicators
     * @param timeoutSeconds Maximum time to wait
     * @return true if page appears ready
     */
    private static boolean waitForBasicPageReadiness(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
            
            // Wait for document ready state
            wait.until(webDriver -> {
                try {
                    return ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete");
                } catch (Exception e) {
                    return false;
                }
            });
            
            // Additional wait for dynamic content
            Thread.sleep(1000);
            
            logger.debug("✅ Page readiness confirmed");
            return true;
            
        } catch (Exception e) {
            logger.warn("⚠️ Page readiness check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current page title with enhanced error handling
     * @return Page title or fallback value
     */
    public static String getCurrentTitle() {
        try {
            String title = getDriver().getTitle();
            if (title == null || title.trim().isEmpty()) {
                title = "Empty Title";
            }
            logger.debug("📄 Current page title: {}", title);
            return title;
        } catch (Exception e) {
            logger.warn("⚠️ Could not get page title: {}", e.getMessage());
            return "Unknown Title";
        }
    }
    
    /**
     * Get current page URL with enhanced error handling
     * @return Current URL or fallback value
     */
    public static String getCurrentUrl() {
        try {
            String url = getDriver().getCurrentUrl();
            if (url == null || url.trim().isEmpty()) {
                url = "about:blank";
            }
            logger.debug("🔗 Current URL: {}", url);
            return url;
        } catch (Exception e) {
            logger.warn("⚠️ Could not get current URL: {}", e.getMessage());
            return "unknown";
        }
    }
    
    /**
     * Enhanced page loading check with multiple validation points
     * @return true if page appears fully loaded
     */
    public static boolean isPageLoaded() {
        try {
            WebDriver currentDriver = getDriver();
            
            // Check basic properties
            String title = getCurrentTitle();
            String url = getCurrentUrl();
            
            if (title.equals("Unknown Title") || url.equals("unknown") || url.equals("data:,")) {
                return false;
            }
            
            // Check document ready state
            if (currentDriver instanceof JavascriptExecutor) {
                Object readyState = ((JavascriptExecutor) currentDriver)
                    .executeScript("return document.readyState");
                if (!"complete".equals(readyState)) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            logger.debug("Page load check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Refresh current page with enhanced error handling
     */
    public static void refresh() {
        try {
            logger.debug("🔄 Refreshing current page");
            getDriver().navigate().refresh();
            
            // Wait for refresh to complete
            waitForBasicPageReadiness(10);
            
        } catch (Exception e) {
            logger.warn("⚠️ Could not refresh page: {}", e.getMessage());
        }
    }
    
    /**
     * Enhanced window management
     */
    public static void switchToWindow(String windowHandle) {
        try {
            getDriver().switchTo().window(windowHandle);
            logger.debug("🪟 Switched to window: {}", windowHandle);
        } catch (Exception e) {
            logger.warn("⚠️ Could not switch to window {}: {}", windowHandle, e.getMessage());
        }
    }
    
    /**
     * Get all window handles
     */
    public static Set<String> getAllWindowHandles() {
        try {
            return getDriver().getWindowHandles();
        } catch (Exception e) {
            logger.warn("⚠️ Could not get window handles: {}", e.getMessage());
            return Set.of();
        }
    }
    
    /**
     * Enhanced driver cleanup with comprehensive resource management
     */
    public static void quitDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            try {
                String threadName = Thread.currentThread().getName();
                String browserType = sessionBrowser.get();
                long sessionDuration = getSessionDuration();
                
                logger.info("🦅 Kestrel {} driver mission complete on thread: {} (session: {}s), shutting down", 
                           browserType, threadName, sessionDuration);
                
                // Clean up any alerts first
                try {
                    AlertHandler.cleanupAnyAlerts(currentDriver);
                } catch (Exception e) {
                    logger.debug("Alert cleanup during quit: {}", e.getMessage());
                }
                
                // Close all windows
                try {
                    Set<String> windowHandles = currentDriver.getWindowHandles();
                    for (String handle : windowHandles) {
                        try {
                            currentDriver.switchTo().window(handle);
                            currentDriver.close();
                        } catch (Exception e) {
                            logger.debug("Error closing window {}: {}", handle, e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Window cleanup failed (normal): {}", e.getMessage());
                }
                
                // Final quit
                currentDriver.quit();
                logger.debug("✅ Driver quit successfully after {}s session", sessionDuration);
                
            } catch (Exception e) {
                logger.warn("⚠️ Error during driver quit: {}", e.getMessage());
            } finally {
                // Always clean up thread locals
                driver.remove();
                sessionStartTime.remove();
                sessionBrowser.remove();
                logger.debug("🧹 All thread locals cleaned up");
            }
        } else {
            logger.debug("🔍 No driver to quit on thread: {}", Thread.currentThread().getName());
        }
    }
    
    /**
     * Emergency cleanup for all resources
     */
    public static void forceQuitAll() {
        logger.warn("🚨 Emergency driver cleanup initiated");
        try {
            WebDriver currentDriver = driver.get();
            if (currentDriver != null) {
                currentDriver.quit();
            }
        } catch (Exception e) {
            logger.error("❌ Error during force quit: {}", e.getMessage());
        } finally {
            // Force cleanup all thread locals
            driver.remove();
            sessionStartTime.remove();
            sessionBrowser.remove();
        }
        logger.info("🧹 Emergency cleanup completed");
    }
    
    /**
     * Wait for page to be ready with enhanced intelligence
     * @param timeoutSeconds Maximum time to wait
     * @return true if page is ready within timeout
     */
    public static boolean waitForPageReady(int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (isPageLoaded()) {
                // Additional validation for dynamic content
                try {
                    Thread.sleep(500); // Brief pause for dynamic elements
                    if (isPageLoaded()) { // Double-check
                        return true;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        logger.warn("⏰ Page not ready within {} seconds", timeoutSeconds);
        return false;
    }
    
    /**
     * Enhanced page load waiting with multiple validation strategies
     * @param timeoutSeconds Maximum time to wait
     * @return true if page is fully loaded and interactive
     */
    public static boolean waitForPageFullyLoaded(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
            
            // Wait for document ready
            wait.until(webDriver -> {
                try {
                    return ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete");
                } catch (Exception e) {
                    return false;
                }
            });
            
            // Wait for jQuery if present
            wait.until(webDriver -> {
                try {
                    Object result = ((JavascriptExecutor) webDriver)
                        .executeScript("return typeof jQuery !== 'undefined' ? jQuery.active === 0 : true");
                    return Boolean.TRUE.equals(result);
                } catch (Exception e) {
                    return true; // jQuery not present, that's fine
                }
            });
            
            // Wait for Angular if present
            wait.until(webDriver -> {
                try {
                    Object result = ((JavascriptExecutor) webDriver)
                        .executeScript("return typeof angular !== 'undefined' ? angular.element(document).injector().get('$http').pendingRequests.length === 0 : true");
                    return Boolean.TRUE.equals(result);
                } catch (Exception e) {
                    return true; // Angular not present, that's fine
                }
            });
            
            logger.debug("✅ Page fully loaded with all frameworks ready");
            return true;
            
        } catch (Exception e) {
            logger.warn("⚠️ Enhanced page load check failed: {}", e.getMessage());
            return waitForPageReady(5); // Fallback to basic check
        }
    }
    
    /**
     * Smart navigation with pre-flight checks
     * @param url Target URL
     * @param waitForLoad Whether to wait for full page load
     */
    public static void navigateToWithOptions(String url, boolean waitForLoad) {
        navigateTo(url);
        
        if (waitForLoad) {
            if (!waitForPageFullyLoaded(20)) {
                logger.warn("⚠️ Page may not be fully loaded after navigation to: {}", url);
            }
        }
    }
    
    /**
     * Execute JavaScript with error handling
     * @param script JavaScript code to execute
     * @return Result of script execution or null if failed
     */
    public static Object executeJavaScript(String script) {
        try {
            WebDriver currentDriver = getDriver();
            if (currentDriver instanceof JavascriptExecutor) {
                Object result = ((JavascriptExecutor) currentDriver).executeScript(script);
                logger.debug("✅ JavaScript executed successfully");
                return result;
            } else {
                logger.warn("⚠️ Driver does not support JavaScript execution");
                return null;
            }
        } catch (Exception e) {
            logger.warn("⚠️ JavaScript execution failed: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Take screenshot for evidence collection
     * @return Screenshot as byte array or null if failed
     */
    public static byte[] takeScreenshot() {
        try {
            WebDriver currentDriver = getDriver();
            if (currentDriver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) currentDriver).getScreenshotAs(OutputType.BYTES);
                logger.debug("📸 Screenshot captured ({} bytes)", screenshot.length);
                return screenshot;
            } else {
                logger.warn("⚠️ Driver does not support screenshots");
                return null;
            }
        } catch (Exception e) {
            logger.warn("⚠️ Screenshot capture failed: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Get page source with error handling
     * @return HTML source of current page
     */
    public static String getPageSource() {
        try {
            String source = getDriver().getPageSource();
            logger.debug("📄 Page source retrieved ({} characters)", source.length());
            return source;
        } catch (Exception e) {
            logger.warn("⚠️ Could not get page source: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Navigate back in browser history
     */
    public static void navigateBack() {
        try {
            logger.debug("⬅️ Navigating back");
            getDriver().navigate().back();
            waitForBasicPageReadiness(10);
        } catch (Exception e) {
            logger.warn("⚠️ Could not navigate back: {}", e.getMessage());
        }
    }
    
    /**
     * Navigate forward in browser history
     */
    public static void navigateForward() {
        try {
            logger.debug("➡️ Navigating forward");
            getDriver().navigate().forward();
            waitForBasicPageReadiness(10);
        } catch (Exception e) {
            logger.warn("⚠️ Could not navigate forward: {}", e.getMessage());
        }
    }
    
    /**
     * Clear browser data (cookies, cache, etc.)
     */
    public static void clearBrowserData() {
        try {
            WebDriver currentDriver = getDriver();
            
            // Clear cookies
            currentDriver.manage().deleteAllCookies();
            
            // Clear local storage and session storage
            if (currentDriver instanceof JavascriptExecutor) {
                ((JavascriptExecutor) currentDriver).executeScript("localStorage.clear();");
                ((JavascriptExecutor) currentDriver).executeScript("sessionStorage.clear();");
            }
            
            logger.debug("🧹 Browser data cleared");
            
        } catch (Exception e) {
            logger.warn("⚠️ Could not clear browser data: {}", e.getMessage());
        }
    }
    
    /**
     * Set browser window size
     * @param width Window width
     * @param height Window height
     */
    public static void setWindowSize(int width, int height) {
        try {
            getDriver().manage().window().setSize(new Dimension(width, height));
            logger.debug("🪟 Window size set to {}x{}", width, height);
        } catch (Exception e) {
            logger.warn("⚠️ Could not set window size: {}", e.getMessage());
        }
    }
    
    /**
     * Maximize browser window
     */
    public static void maximizeWindow() {
        try {
            getDriver().manage().window().maximize();
            logger.debug("🪟 Window maximized");
        } catch (Exception e) {
            logger.warn("⚠️ Could not maximize window: {}", e.getMessage());
        }
    }
    
    /**
     * Get current window size
     * @return Window dimension or null if failed
     */
    public static Dimension getWindowSize() {
        try {
            Dimension size = getDriver().manage().window().getSize();
            logger.debug("🪟 Current window size: {}x{}", size.width, size.height);
            return size;
        } catch (Exception e) {
            logger.warn("⚠️ Could not get window size: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if current browser is mobile/responsive
     * @return true if window width suggests mobile device
     */
    public static boolean isMobileView() {
        try {
            Dimension size = getWindowSize();
            if (size != null) {
                return size.width <= 768; // Common mobile breakpoint
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Wait for specific condition with custom timeout
     * @param condition WebDriver condition to wait for
     * @param timeoutSeconds Maximum time to wait
     * @return true if condition is met, false if timeout
     */
    public static boolean waitForCondition(org.openqa.selenium.support.ui.ExpectedCondition<?> condition, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
            wait.until(condition);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            logger.debug("⏰ Condition not met within {} seconds", timeoutSeconds);
            return false;
        } catch (Exception e) {
            logger.warn("⚠️ Error waiting for condition: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced browser focus management
     */
    public static void focusBrowser() {
        try {
            WebDriver currentDriver = getDriver();
            
            // Try to focus the current window
            currentDriver.switchTo().window(currentDriver.getWindowHandle());
            
            // Execute JavaScript to focus
            if (currentDriver instanceof JavascriptExecutor) {
                ((JavascriptExecutor) currentDriver).executeScript("window.focus();");
            }
            
            logger.debug("🎯 Browser focus restored");
            
        } catch (Exception e) {
            logger.warn("⚠️ Could not focus browser: {}", e.getMessage());
        }
    }
    
    /**
     * Get browser capabilities and information
     * @return Browser info string
     */
    public static String getBrowserInfo() {
        try {
            WebDriver currentDriver = getDriver();
            
            if (currentDriver instanceof org.openqa.selenium.HasCapabilities) {
                org.openqa.selenium.Capabilities caps = ((org.openqa.selenium.HasCapabilities) currentDriver).getCapabilities();
                String browserName = caps.getBrowserName();
                String browserVersion = caps.getBrowserVersion();
                String platform = caps.getPlatformName().toString();
                
                return String.format("Browser: %s %s on %s", browserName, browserVersion, platform);
            }
            
            return "Browser info not available";
            
        } catch (Exception e) {
            logger.warn("⚠️ Could not get browser info: {}", e.getMessage());
            return "Unknown browser";
        }
    }
    
    /**
     * Performance monitoring - get page load time
     * @return Page load time in milliseconds or -1 if failed
     */
    public static long getPageLoadTime() {
        try {
            Object result = executeJavaScript("return performance.timing.loadEventEnd - performance.timing.navigationStart;");
            if (result instanceof Number) {
                long loadTime = ((Number) result).longValue();
                logger.debug("⏱️ Page load time: {}ms", loadTime);
                return loadTime;
            }
            return -1;
        } catch (Exception e) {
            logger.warn("⚠️ Could not get page load time: {}", e.getMessage());
            return -1;
        }
    }
    
    /**
     * Memory usage monitoring
     * @return Memory usage info string
     */
    public static String getMemoryUsage() {
        try {
            Object result = executeJavaScript(
                "return JSON.stringify({" +
                "  usedJSHeapSize: performance.memory ? performance.memory.usedJSHeapSize : 0," +
                "  totalJSHeapSize: performance.memory ? performance.memory.totalJSHeapSize : 0," +
                "  jsHeapSizeLimit: performance.memory ? performance.memory.jsHeapSizeLimit : 0" +
                "});"
            );
            
            if (result != null) {
                return "Memory: " + result.toString();
            }
            
            return "Memory info not available";
            
        } catch (Exception e) {
            logger.warn("⚠️ Could not get memory usage: {}", e.getMessage());
            return "Memory monitoring failed";
        }
    }
    
    /**
     * Get comprehensive driver session information
     * @return Session info string
     */
    public static String getSessionInfo() {
        if (!isDriverInitialized()) {
            return "No active session";
        }
        
        String browser = sessionBrowser.get();
        long duration = getSessionDuration();
        String url = getCurrentUrl();
        String browserInfo = getBrowserInfo();
        Dimension windowSize = getWindowSize();
        
        return String.format("Session: %s driver, %ds active, %s, window: %dx%d, current: %s", 
                           browser, duration, browserInfo, 
                           windowSize != null ? windowSize.width : 0,
                           windowSize != null ? windowSize.height : 0,
                           url);
    }
    
    /**
     * Get detailed session statistics
     * @return Detailed session statistics
     */
    public static String getDetailedSessionStats() {
        if (!isDriverInitialized()) {
            return "No active session";
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("🦅 Kestrel Engine Session Statistics\n");
        stats.append("=====================================\n");
        stats.append(getSessionInfo()).append("\n");
        stats.append("Page Load Time: ").append(getPageLoadTime()).append("ms\n");
        stats.append("Memory Usage: ").append(getMemoryUsage()).append("\n");
        stats.append("Page Title: ").append(getCurrentTitle()).append("\n");
        stats.append("Page Source Size: ").append(getPageSource().length()).append(" chars\n");
        stats.append("Mobile View: ").append(isMobileView()).append("\n");
        stats.append("=====================================");
        
        return stats.toString();
    }
}