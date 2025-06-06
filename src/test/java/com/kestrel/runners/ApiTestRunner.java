package com.kestrel.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Kestrel Engine API Test Runner
 * Command center for API hunting scenarios
 * 
 * Features:
 * - JUnit 5 Platform Suite
 * - Cucumber integration
 * - API-specific configuration
 * - Parallel execution support
 * 
 * Execution:
 * - Gradle: ./gradlew apiTests
 * - IDE: Run this class directly
 * - Tag filter: @api scenarios only
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/api")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME, 
    value = "pretty," +
            "html:build/reports/cucumber/api.html," +
            "json:build/reports/cucumber/api.json," +
            "junit:build/reports/cucumber/api.xml," +
            "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.kestrel")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@api")
@ConfigurationParameter(key = PLUGIN_PUBLISH_ENABLED_PROPERTY_NAME, value = "false")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
public class ApiTestRunner {
    
    /*
     * 🦅 KESTREL ENGINE API COMMAND CENTER
     * 
     * This runner executes all API hunting scenarios marked with @api tag.
     * 
     * Target: DummyAPI.io endpoints
     * - User CRUD operations
     * - Tag management  
     * - Authentication testing
     * - Negative scenarios
     * 
     * Reports Generated:
     * - HTML: build/reports/cucumber/api.html
     * - JSON: build/reports/cucumber/api.json
     * - JUnit XML: build/reports/cucumber/api.xml
     * - Allure: build/allure-results/
     * 
     * Configuration:
     * - Environment: Set via -Denv=staging (default: dev)
     * - Parallel: Controlled by junit-platform.properties
     * - Tags: Only @api scenarios executed
     * 
     * Usage Examples:
     * ./gradlew apiTests                    # Run all API tests
     * ./gradlew apiTests -Denv=staging      # Run in staging environment
     * ./gradlew apiTests --tests="*User*"   # Run tests with "User" in name
     * 
     * Happy hunting! 🎯
     */
}