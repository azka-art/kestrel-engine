package com.kestrel.utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kestrel Engine Screenshot Capture
 * Evidence collection system for hunting failures
 * 
 * Features:
 * - Thread-safe screenshot capture
 * - Allure report integration
 * - Automatic failure evidence
 * - Multiple output formats
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class ScreenshotCapture {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotCapture.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String SCREENSHOT_DIR = "build/screenshots";
    
    /**
     * Capture screenshot from current WebDriver instance
     * @return Screenshot as byte array
     */
    public static byte[] captureScreenshot() {
        try {
            if (!DriverManager.isDriverInitialized()) {
                logger.warn("⚠️ No driver initialized, cannot capture screenshot");
                return new byte[0];
            }
            
            TakesScreenshot screenshot = (TakesScreenshot) DriverManager.getDriver();
            byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);
            
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            logger.info("📸 Kestrel captured evidence at: {}", timestamp);
            
            return screenshotBytes;
            
        } catch (Exception e) {
            logger.error("❌ Kestrel failed to capture evidence: {}", e.getMessage());
            return new byte[0];
        }
    }
    
    /**
     * Capture screenshot and save to file
     * @param filename Custom filename (without extension)
     * @return File path of saved screenshot
     */
    public static String captureScreenshotToFile(String filename) {
        byte[] screenshotBytes = captureScreenshot();
        if (screenshotBytes.length == 0) {
            return null;
        }
        
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String fileName = String.format("%s_%s.png", filename, timestamp);
            File screenshotFile = new File(screenshotDir, fileName);
            
            // Write screenshot to file
            try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
                fos.write(screenshotBytes);
            }
            
            logger.info("💾 Screenshot saved: {}", screenshotFile.getAbsolutePath());
            return screenshotFile.getAbsolutePath();
            
        } catch (IOException e) {
            logger.error("❌ Failed to save screenshot: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Attach screenshot to Allure report
     * @param description Description for the attachment
     */
    public static void attachToAllure(String description) {
        byte[] screenshot = captureScreenshot();
        if (screenshot.length > 0) {
            Allure.addAttachment(description, "image/png", new ByteArrayInputStream(screenshot), "png");
            logger.debug("📎 Screenshot attached to Allure: {}", description);
        }
    }
    
    /**
     * Capture and attach failure evidence to Allure
     * @param scenarioName Name of the failed scenario
     */
    public static void attachFailureEvidence(String scenarioName) {
        String description = "🔍 Failure Evidence - " + scenarioName;
        attachToAllure(description);
        
        // Also save to file for manual inspection
        String sanitizedName = scenarioName.replaceAll("[^a-zA-Z0-9\\-_\\.]", "_");
        captureScreenshotToFile("FAILURE_" + sanitizedName);
    }
    
    /**
     * Capture screenshot with custom Allure step
     * @param stepName Name of the step
     */
    public static void captureStep(String stepName) {
        String description = "📋 Step Evidence - " + stepName;
        attachToAllure(description);
        logger.debug("📸 Step screenshot captured: {}", stepName);
    }
    
    /**
     * Capture screenshot for successful scenario
     * @param scenarioName Name of the successful scenario
     */
    public static void attachSuccessEvidence(String scenarioName) {
        if (EnvironmentManager.getCurrentEnvironment().equals("dev")) {
            // Only capture success screenshots in dev environment
            String description = "✅ Success Evidence - " + scenarioName;
            attachToAllure(description);
        }
    }
    
    /**
     * Capture page source along with screenshot
     * @param description Description for both attachments
     */
    public static void capturePageSourceAndScreenshot(String description) {
        try {
            // Capture screenshot
            attachToAllure(description + " - Screenshot");
            
            // Capture page source
            if (DriverManager.isDriverInitialized()) {
                String pageSource = DriverManager.getDriver().getPageSource();
                Allure.addAttachment(description + " - Page Source", "text/html", pageSource, "html");
                logger.debug("📄 Page source captured: {}", description);
            }
            
        } catch (Exception e) {
            logger.error("❌ Failed to capture page source and screenshot: {}", e.getMessage());
        }
    }
    
    /**
     * Clean up old screenshot files (older than specified days)
     * @param daysToKeep Number of days to keep screenshots
     */
    public static void cleanupOldScreenshots(int daysToKeep) {
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            return;
        }
        
        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L);
        int deletedCount = 0;
        
        File[] files = screenshotDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
        }
        
        if (deletedCount > 0) {
            logger.info("🧹 Cleaned up {} old screenshot(s)", deletedCount);
        }
    }
    
    /**
     * Get current page title for screenshot naming
     * @return Sanitized page title
     */
    private static String getCurrentPageTitle() {
        try {
            if (DriverManager.isDriverInitialized()) {
                String title = DriverManager.getDriver().getTitle();
                return title.replaceAll("[^a-zA-Z0-9\\-_\\.]", "_").substring(0, Math.min(title.length(), 50));
            }
        } catch (Exception e) {
            logger.debug("Could not get page title: {}", e.getMessage());
        }
        return "unknown_page";
    }
    
    /**
     * Capture screenshot with automatic naming based on current page
     * @return File path of saved screenshot
     */
    public static String captureScreenshotAuto() {
        String pageTitle = getCurrentPageTitle();
        return captureScreenshotToFile(pageTitle);
    }
}