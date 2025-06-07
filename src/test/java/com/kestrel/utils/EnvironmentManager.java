package com.kestrel.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Kestrel Engine Environment Manager
 * Handles configuration across different environments (dev, staging, prod)
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class EnvironmentManager {
    private static final Properties properties = new Properties();
    private static final String DEFAULT_ENV = "dev";
    private static String currentEnvironment;
    
    static {
        loadProperties();
    }
    
    /**
     * Load properties based on environment
     * Priority: System property > Default environment
     * Tries multiple loading strategies for robust configuration
     */
    private static void loadProperties() {
        currentEnvironment = System.getProperty("env", DEFAULT_ENV);
        String configFileName = currentEnvironment + ".properties";
        
        boolean loaded = false;
        
        // Strategy 1: Try loading from classpath (src/test/resources/config/)
        try {
            String classpathFile = "/config/" + configFileName;
            InputStream inputStream = EnvironmentManager.class.getResourceAsStream(classpathFile);
            if (inputStream != null) {
                properties.load(inputStream);
                inputStream.close();
                System.out.println("🦅 Kestrel Engine loaded: " + currentEnvironment + " environment");
                System.out.println("📄 Config source: CLASSPATH (" + classpathFile + ")");
                loaded = true;
            }
        } catch (IOException e) {
            System.out.println("⚠️ Could not load from classpath: " + e.getMessage());
        }
        
        // Strategy 2: Try loading from file system (config/ folder)
        if (!loaded) {
            try {
                String configFile = "config/" + configFileName;
                FileInputStream fis = new FileInputStream(configFile);
                properties.load(fis);
                fis.close();
                System.out.println("🦅 Kestrel Engine loaded: " + currentEnvironment + " environment");
                System.out.println("📄 Config source: FILE SYSTEM (" + configFile + ")");
                loaded = true;
            } catch (IOException e) {
                System.out.println("⚠️ Could not load from file system: " + e.getMessage());
            }
        }
        
        // Strategy 3: Use default configuration if nothing works
        if (!loaded) {
            System.out.println("⚠️ No config file found, using default JSONPlaceholder configuration");
            setDefaultProperties();
        }
        
        // Validate loaded configuration
        validateBasicConfiguration();
    }
    
    /**
     * Set default properties for JSONPlaceholder testing
     * This ensures the framework works even without config files
     */
    private static void setDefaultProperties() {
        properties.setProperty("api.url", "https://jsonplaceholder.typicode.com");
        properties.setProperty("base.url", "https://www.demoblaze.com");
        properties.setProperty("browser", "chrome");
        properties.setProperty("headless", "true");
        properties.setProperty("implicit.wait", "10");
        properties.setProperty("explicit.wait", "15");
        properties.setProperty("page.load.timeout", "30");
        properties.setProperty("app.id", "NOT_NEEDED");
        properties.setProperty("screenshot.on.failure", "true");
        properties.setProperty("screenshot.path", "build/screenshots");
        properties.setProperty("report.path", "build/reports/cucumber");
        properties.setProperty("allure.results.directory", "build/allure-results");
        properties.setProperty("parallel.threads", "2");
        properties.setProperty("retry.count", "1");
        properties.setProperty("debug.mode", "false");
        
        System.out.println("✅ Default JSONPlaceholder configuration applied");
    }
    
    /**
     * Validate basic configuration to ensure essential properties exist
     */
    private static void validateBasicConfiguration() {
        try {
            String apiUrl = properties.getProperty("api.url");
            String baseUrl = properties.getProperty("base.url");
            
            if (apiUrl == null || apiUrl.trim().isEmpty()) {
                throw new RuntimeException("API URL is required but not configured");
            }
            
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new RuntimeException("Base URL is required but not configured");
            }
            
            System.out.println("✅ Basic configuration validation passed");
            System.out.println("   API URL: " + apiUrl);
            System.out.println("   Base URL: " + baseUrl);
            
        } catch (Exception e) {
            throw new RuntimeException("❌ Configuration validation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get property value with system property override
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null) {
            throw new RuntimeException("❌ Property not found: " + key);
        }
        return value;
    }
    
    /**
     * Get property value with system property override and default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key, properties.getProperty(key, defaultValue));
        return value;
    }
    
    /**
     * Get current environment name
     * @return Environment name (dev/staging/prod)
     */
    public static String getCurrentEnvironment() {
        return currentEnvironment;
    }
    
    // ===== WEB CONFIGURATION =====
    
    /**
     * Get base URL for web testing
     * @return Base URL (e.g., https://www.demoblaze.com)
     */
    public static String getBaseUrl() {
        return getProperty("base.url");
    }
    
    /**
     * Get browser type
     * @return Browser name (chrome/firefox)
     */
    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    /**
     * Check if headless mode is enabled
     * @return true if headless mode
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "true"));
    }
    
    /**
     * Get implicit wait timeout
     * @return Implicit wait in seconds
     */
    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }
    
    /**
     * Get explicit wait timeout
     * @return Explicit wait in seconds
     */
    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "15"));
    }
    
    /**
     * Get page load timeout
     * @return Page load timeout in seconds
     */
    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout", "30"));
    }
    
    // ===== API CONFIGURATION =====
    
    /**
     * Get API base URL
     * @return API base URL (e.g., https://jsonplaceholder.typicode.com)
     */
    public static String getApiUrl() {
        return getProperty("api.url");
    }
    
    /**
     * Get API app ID for authentication (optional for JSONPlaceholder)
     * @return App ID or empty string if not needed
     */
    public static String getAppId() {
        return getProperty("app.id", "NOT_NEEDED");
    }
    
    /**
     * Check if API requires authentication
     * @return true if app.id is configured and not empty
     */
    public static boolean isApiAuthRequired() {
        String appId = getAppId();
        return appId != null && !appId.trim().isEmpty() && !"NOT_NEEDED".equals(appId);
    }
    
    // ===== REPORTING CONFIGURATION =====
    
    /**
     * Check if screenshot capture on failure is enabled
     * @return true if screenshots should be captured
     */
    public static boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }
    
    /**
     * Get Allure results directory
     * @return Allure results directory path
     */
    public static String getAllureResultsDirectory() {
        return getProperty("allure.results.directory", "build/allure-results");
    }
    
    /**
     * Get screenshot path
     * @return Screenshot directory path
     */
    public static String getScreenshotPath() {
        return getProperty("screenshot.path", "build/screenshots");
    }
    
    /**
     * Get report path
     * @return Report directory path
     */
    public static String getReportPath() {
        return getProperty("report.path", "build/reports/cucumber");
    }
    
    // ===== UTILITY METHODS =====
    
    /**
     * Print current configuration for debugging
     */
    public static void printConfiguration() {
        System.out.println("\n🦅 ================================");
        System.out.println("   KESTREL ENGINE CONFIGURATION");
        System.out.println("================================");
        System.out.println("Environment: " + getCurrentEnvironment());
        System.out.println("Base URL: " + getBaseUrl());
        System.out.println("API URL: " + getApiUrl());
        System.out.println("API Auth Required: " + isApiAuthRequired());
        if (isApiAuthRequired()) {
            String appId = getAppId();
            System.out.println("App ID: " + appId.substring(0, Math.min(appId.length(), 8)) + "***");
        } else {
            System.out.println("App ID: Not required (JSONPlaceholder)");
        }
        System.out.println("Browser: " + getBrowser());
        System.out.println("Headless: " + isHeadless());
        System.out.println("Implicit Wait: " + getImplicitWait() + "s");
        System.out.println("Explicit Wait: " + getExplicitWait() + "s");
        System.out.println("Page Load Timeout: " + getPageLoadTimeout() + "s");
        System.out.println("Screenshot on Failure: " + isScreenshotOnFailure());
        System.out.println("Screenshot Path: " + getScreenshotPath());
        System.out.println("Report Path: " + getReportPath());
        System.out.println("Allure Results: " + getAllureResultsDirectory());
        System.out.println("================================\n");
    }
    
    /**
     * Validate configuration
     * @throws RuntimeException if configuration is invalid
     */
    public static void validateConfiguration() {
        try {
            // Validate required properties
            getBaseUrl();
            getApiUrl();
            getBrowser();
            
            // Validate numeric properties
            if (getImplicitWait() <= 0) {
                throw new IllegalArgumentException("Implicit wait must be positive");
            }
            if (getExplicitWait() <= 0) {
                throw new IllegalArgumentException("Explicit wait must be positive");
            }
            if (getPageLoadTimeout() <= 0) {
                throw new IllegalArgumentException("Page load timeout must be positive");
            }
            
            // Validate API configuration
            String apiUrl = getApiUrl();
            if (!apiUrl.startsWith("http")) {
                throw new IllegalArgumentException("API URL must start with http/https");
            }
            
            // Log API authentication status
            if (isApiAuthRequired()) {
                System.out.println("✅ API authentication configured");
            } else {
                System.out.println("✅ API authentication not required (JSONPlaceholder mode)");
            }
            
            System.out.println("✅ Kestrel Engine configuration validated successfully");
            
        } catch (Exception e) {
            throw new RuntimeException("❌ Configuration validation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if current environment is development
     * @return true if environment is dev
     */
    public static boolean isDevelopment() {
        return "dev".equals(currentEnvironment);
    }
    
    /**
     * Check if current environment is production
     * @return true if environment is prod
     */
    public static boolean isProduction() {
        return "prod".equals(currentEnvironment);
    }
    
    /**
     * Check if current environment is staging
     * @return true if environment is staging
     */
    public static boolean isStaging() {
        return "staging".equals(currentEnvironment);
    }
    
    /**
     * Get parallel thread count
     * @return Number of parallel threads
     */
    public static int getParallelThreads() {
        return Integer.parseInt(getProperty("parallel.threads", "2"));
    }
    
    /**
     * Get retry count for failed tests
     * @return Number of retries
     */
    public static int getRetryCount() {
        return Integer.parseInt(getProperty("retry.count", "1"));
    }
    
    /**
     * Check if debug mode is enabled
     * @return true if debug mode is on
     */
    public static boolean isDebugMode() {
        return Boolean.parseBoolean(getProperty("debug.mode", "false"));
    }
    
    /**
     * Test the configuration loading - useful for debugging
     * @return true if configuration can be loaded successfully
     */
    public static boolean testConfiguration() {
        try {
            System.out.println("🔍 Testing Kestrel Engine configuration...");
            printConfiguration();
            validateConfiguration();
            System.out.println("✅ Configuration test passed!");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Configuration test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}