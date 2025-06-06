package com.kestrel.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Kestrel Engine Web Test Runner
 * Command center for Web UI hunting scenarios
 * 
 * Features:
 * - JUnit 5 Platform Suite
 * - Cucumber integration
 * - Web-specific configuration
 * - Screenshot capture on failure
 * 
 * Execution:
 * - Gradle: ./gradlew webTests
 * - IDE: Run this class directly
 * - Tag filter: @web scenarios only
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/web")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME, 
    value = "pretty," +
            "html:build/reports/cucumber/web.html," +
            "json:build/reports/cucumber/web.json," +
            "junit:build/reports/cucumber/web.xml," +
            "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.kestrel")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@web")
@ConfigurationParameter(key = PLUGIN_PUBLISH_ENABLED_PROPERTY_NAME, value = "false")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
public class WebTestRunner {
    
    /*
     * 🦅 KESTREL ENGINE WEB COMMAND CENTER
     * 
     * This runner executes all Web UI hunting scenarios marked with @web tag.
     * 
     * Target: Demoblaze e-commerce site
     * - Authentication flows
     * - Product browsing
     * - Shopping cart operations
     * - Checkout process (E2E)
     * - Negative scenarios
     * 
     * Browser Support:
     * - Chrome (default)
     * - Firefox (via -Dbrowser=firefox)
     * - Headless mode in CI/CD
     * 
     * Reports Generated:
     * - HTML: build/reports/cucumber/web.html
     * - JSON: build/reports/cucumber/web.json
     * - JUnit XML: build/reports/cucumber/web.xml
     * - Allure: build/allure-results/
     * - Screenshots: build/screenshots/ (on failure)
     * 
     * Configuration:
     * - Environment: Set via -Denv=staging (default: dev)
     * - Browser: Set via -Dbrowser=firefox (default: chrome)
     * - Headless: Set via -Dheadless=true (default: false in dev)
     * - Parallel: Limited to 2 threads for web stability
     * 
     * Usage Examples:
     * ./gradlew webTests                         # Run all web tests
     * ./gradlew webTests -Denv=staging           # Run in staging environment
     * ./gradlew webTests -Dbrowser=firefox       # Use Firefox browser
     * ./gradlew webTests -Dheadless=true         # Run in headless mode
     * ./gradlew webTests --tests="*Login*"       # Run tests with "Login" in name
     * 
     * Evidence Collection:
     * - Screenshots captured automatically on failure
     * - Page source saved for debugging
     * - Step-by-step evidence in dev environment
     * - All evidence attached to Allure reports
     * 
     * Happy hunting! 🎯
     */
}