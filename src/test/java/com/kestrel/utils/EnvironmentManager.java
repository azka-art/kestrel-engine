package com.kestrel.utils;

import java.io.FileInputStream;
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
     */
    private static void loadProperties() {
        currentEnvironment = System.getProperty("env", DEFAULT_ENV);
        String configFile = "config/" + currentEnvironment + ".properties";
        
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            System.out.println("🦅 Kestrel Engine loaded: " + currentEnvironment + " environment");
            System.out.println("📄 Config file: " + configFile);
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to load config: " + configFile, e);
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
        return getProperty("browser");
    }
    
    /**
     * Check if headless mode is enabled
     * @return true if headless mode
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }
    
    /**
     * Get implicit wait timeout
     * @return Implicit wait in seconds
     */
    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait"));
    }
    
    /**
     * Get explicit wait timeout
     * @return Explicit wait in seconds
     */
    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait"));
    }
    
    /**
     * Get page load timeout
     * @return Page load timeout in seconds
     */
    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout"));
    }
    
    // ===== API CONFIGURATION =====
    
    /**
     * Get API base URL
     * @return API base URL (e.g., https://dummyapi.io/data/v1)
     */
    public static String getApiUrl() {
        return getProperty("api.url");
    }
    
    /**
     * Get API app ID for authentication
     * @return App ID for DummyAPI
     */
    public static String getAppId() {
        return getProperty("app.id");
    }
    
    // ===== REPORTING CONFIGURATION =====
    
    /**
     * Check if screenshot capture on failure is enabled
     * @return true if screenshots should be captured
     */
    public static boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure"));
    }
    
    /**
     * Get Allure results directory
     * @return Allure results directory path
     */
    public static String getAllureResultsDirectory() {
        return getProperty("allure.results.directory");
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
        System.out.println("Browser: " + getBrowser());
        System.out.println("Headless: " + isHeadless());
        System.out.println("Implicit Wait: " + getImplicitWait() + "s");
        System.out.println("Screenshot on Failure: " + isScreenshotOnFailure());
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
            getAppId();
            getBrowser();
            
            // Validate numeric properties
            if (getImplicitWait() <= 0) {
                throw new IllegalArgumentException("Implicit wait must be positive");
            }
            if (getExplicitWait() <= 0) {
                throw new IllegalArgumentException("Explicit wait must be positive");
            }
            
            System.out.println("✅ Kestrel Engine configuration validated successfully");
            
        } catch (Exception e) {
            throw new RuntimeException("❌ Configuration validation failed: " + e.getMessage(), e);
        }
    }
}