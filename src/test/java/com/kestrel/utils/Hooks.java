package com.kestrel.utils;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kestrel Engine Hooks
 * Cucumber lifecycle management for hunting scenarios
 * 
 * Features:
 * - Scenario lifecycle management
 * - Automatic evidence collection
 * - Environment-aware behavior
 * - Parallel execution support
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class Hooks {
    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);
    private static boolean globalSetupComplete = false;
    
    /**
     * Global setup - runs once before all scenarios
     */
    @BeforeAll
    public static void globalSetup() {
        if (!globalSetupComplete) {
            logger.info("🦅 ================================");
            logger.info("   KESTREL ENGINE HUNT BEGINS");
            logger.info("================================");
            
            // Print environment configuration
            EnvironmentManager.printConfiguration();
            
            // Validate configuration
            EnvironmentManager.validateConfiguration();
            
            // Clean up old screenshots
            ScreenshotCapture.cleanupOldScreenshots(7); // Keep last 7 days
            
            globalSetupComplete = true;
            logger.info("✅ Kestrel Engine global setup complete");
        }
    }
    
    /**
     * Global teardown - runs once after all scenarios
     */
    @AfterAll
    public static void globalTeardown() {
        logger.info("🦅 ================================");
        logger.info("   KESTREL ENGINE HUNT COMPLETE");
        logger.info("================================");
        
        // Force cleanup any remaining drivers
        try {
            DriverManager.forceQuitAll();
        } catch (Exception e) {
            logger.warn("⚠️ Warning during global cleanup: {}", e.getMessage());
        }
        
        logger.info("🎯 Hunt mission accomplished!");
    }
    
    /**
     * Setup for Web UI scenarios
     * @param scenario Current scenario
     */
    @Before("@web")
    public void beforeWebScenario(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        logger.info("🦅 Kestrel Web Hunt Starting: {} [Thread: {}]", scenario.getName(), threadName);
        
        try {
            // Initialize WebDriver
            DriverManager.initializeDriver();
            
            // Navigate to base URL
            String baseUrl = EnvironmentManager.getBaseUrl();
            DriverManager.navigateTo(baseUrl);
            
            logger.info("🌐 Web hunt initiated at: {}", baseUrl);
            
        } catch (Exception e) {
            logger.error("❌ Failed to initialize web hunt: {}", e.getMessage());
            scenario.attach(e.getMessage().getBytes(), "text/plain", "Web Setup Error");
            throw new RuntimeException("Web scenario setup failed", e);
        }
    }
    
    /**
     * Setup for API scenarios
     * @param scenario Current scenario
     */
    @Before("@api")
    public void beforeApiScenario(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        logger.info("🦅 Kestrel API Hunt Starting: {} [Thread: {}]", scenario.getName(), threadName);
        
        try {
            // Log API configuration
            String apiUrl = EnvironmentManager.getApiUrl();
            logger.info("🔗 API hunt target: {}", apiUrl);
            
            // Validate API configuration
            if (EnvironmentManager.getAppId().equals("YOUR_DUMMY_API_APP_ID")) {
                logger.warn("⚠️ Using default API key - update config/dev.properties");
            }
            
        } catch (Exception e) {
            logger.error("❌ Failed to initialize API hunt: {}", e.getMessage());
            scenario.attach(e.getMessage().getBytes(), "text/plain", "API Setup Error");
            throw new RuntimeException("API scenario setup failed", e);
        }
    }
    
    /**
     * Setup for End-to-End scenarios
     * @param scenario Current scenario
     */
    @Before("@e2e")
    public void beforeE2EScenario(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        logger.info("🦅 Kestrel E2E Hunt Starting: {} [Thread: {}]", scenario.getName(), threadName);
        
        // E2E scenarios might need both web and API setup
        if (scenario.getSourceTagNames().contains("@web")) {
            beforeWebScenario(scenario);
        }
        
        logger.info("🎯 End-to-End hunt preparation complete");
    }
    
    /**
     * Capture evidence after each step for Web scenarios
     * @param scenario Current scenario
     */
    @AfterStep("@web")
    public void afterWebStep(Scenario scenario) {
        if (scenario.isFailed()) {
            logger.warn("🎯 Target missed! Capturing failure evidence...");
            
            try {
                // Capture screenshot for failure
                byte[] screenshot = ScreenshotCapture.captureScreenshot();
                scenario.attach(screenshot, "image/png", "Failure Evidence");
                
                // Attach to Allure report
                ScreenshotCapture.attachFailureEvidence(scenario.getName());
                
                // Capture page source if available
                if (DriverManager.isDriverInitialized()) {
                    try {
                        String pageSource = DriverManager.getDriver().getPageSource();
                        scenario.attach(pageSource.getBytes(), "text/html", "Page Source");
                    } catch (Exception e) {
                        logger.debug("Could not capture page source: {}", e.getMessage());
                    }
                }
                
            } catch (Exception e) {
                logger.error("❌ Failed to capture failure evidence: {}", e.getMessage());
            }
        } else if (EnvironmentManager.getCurrentEnvironment().equals("dev")) {
            // Capture success screenshots only in dev environment
            try {
                ScreenshotCapture.captureStep(scenario.getName() + " - Step Success");
            } catch (Exception e) {
                logger.debug("Could not capture step evidence: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Log API step details for debugging
     * @param scenario Current scenario
     */
    @AfterStep("@api")
    public void afterApiStep(Scenario scenario) {
        if (scenario.isFailed()) {
            logger.warn("🎯 API hunt failed! Check request/response details...");
            
            // API failures will be handled by step definitions
            // This hook is for additional logging/reporting
            try {
                String errorInfo = String.format("API Hunt Failed: %s [Thread: %s]", 
                    scenario.getName(), Thread.currentThread().getName());
                scenario.attach(errorInfo.getBytes(), "text/plain", "API Failure Info");
                
            } catch (Exception e) {
                logger.error("❌ Failed to capture API failure info: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Cleanup after Web scenarios
     * @param scenario Current scenario
     */
    @After("@web")
    public void afterWebScenario(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        
        try {
            // Capture final evidence if needed
            if (scenario.isFailed()) {
                logger.error("❌ Web hunt failed: {} [Thread: {}]", scenario.getName(), threadName);
                
                // Final screenshot before cleanup
                ScreenshotCapture.attachFailureEvidence("FINAL_" + scenario.getName());
                
            } else {
                logger.info("✅ Web hunt successful: {} [Thread: {}]", scenario.getName(), threadName);
                
                // Success evidence in dev environment
                if (EnvironmentManager.getCurrentEnvironment().equals("dev")) {
                    ScreenshotCapture.attachSuccessEvidence(scenario.getName());
                }
            }
            
        } catch (Exception e) {
            logger.warn("⚠️ Warning during evidence capture: {}", e.getMessage());
        } finally {
            // Always quit driver to free resources
            try {
                DriverManager.quitDriver();
                logger.debug("🧹 Driver cleanup completed for: {}", scenario.getName());
            } catch (Exception e) {
                logger.warn("⚠️ Warning during driver cleanup: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Cleanup after API scenarios
     * @param scenario Current scenario
     */
    @After("@api")
    public void afterApiScenario(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        
        if (scenario.isFailed()) {
            logger.error("❌ API hunt failed: {} [Thread: {}]", scenario.getName(), threadName);
        } else {
            logger.info("✅ API hunt successful: {} [Thread: {}]", scenario.getName(), threadName);
        }
        
        // API scenarios don't need driver cleanup
        logger.debug("🧹 API scenario cleanup completed for: {}", scenario.getName());
    }
    
    /**
     * Cleanup after E2E scenarios
     * @param scenario Current scenario
     */
    @After("@e2e")
    public void afterE2EScenario(Scenario scenario) {
        String threadName = Thread.currentThread().getName();
        
        if (scenario.isFailed()) {
            logger.error("❌ E2E hunt failed: {} [Thread: {}]", scenario.getName(), threadName);
            
            // Comprehensive failure evidence for E2E
            try {
                ScreenshotCapture.capturePageSourceAndScreenshot("E2E_FAILURE_" + scenario.getName());
            } catch (Exception e) {
                logger.warn("⚠️ Could not capture E2E failure evidence: {}", e.getMessage());
            }
            
        } else {
            logger.info("✅ E2E hunt successful: {} [Thread: {}]", scenario.getName(), threadName);
        }
        
        logger.info("🎯 End-to-End hunt complete for: {}", scenario.getName());
    }
}
