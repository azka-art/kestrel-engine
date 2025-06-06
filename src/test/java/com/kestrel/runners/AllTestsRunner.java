package com.kestrel.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Kestrel Engine All Tests Runner
 * Master command center for complete hunting mission
 * 
 * Features:
 * - JUnit 5 Platform Suite
 * - Combined API + Web execution
 * - Comprehensive reporting
 * - Full parallel execution
 * 
 * Execution:
 * - Gradle: ./gradlew allTests
 * - IDE: Run this class directly
 * - Tag filter: @api OR @web scenarios
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME, 
    value = "pretty," +
            "html:build/reports/cucumber/all.html," +
            "json:build/reports/cucumber/all.json," +
            "junit:build/reports/cucumber/all.xml," +
            "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.kestrel")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@api or @web")
@ConfigurationParameter(key = PLUGIN_PUBLISH_ENABLED_PROPERTY_NAME, value = "false")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
public class AllTestsRunner {
    
    /*
     * 🦅 KESTREL ENGINE MASTER COMMAND CENTER
     * 
     * This runner executes the complete Kestrel hunting mission:
     * - All API scenarios (@api)
     * - All Web UI scenarios (@web)  
     * - End-to-end scenarios (@e2e)
     * 
     * Complete Coverage:
     * ==================
     * 
     * API HUNTING TARGETS:
     * - DummyAPI.io User Management
     *   ✓ GET /user (pagination, filtering)
     *   ✓ GET /user/{id} (valid/invalid IDs)
     *   ✓ POST /user/create (CRUD operations)
     *   ✓ PUT /user/{id} (update scenarios)
     *   ✓ DELETE /user/{id} (cleanup operations)
     * 
     * - Authentication & Security
     *   ✓ Valid app-id requests
     *   ✓ Invalid app-id (negative)
     *   ✓ Missing authentication (negative)
     * 
     * - Data Validation
     *   ✓ JSON schema validation
     *   ✓ Required field validation
     *   ✓ Data type validation
     * 
     * WEB HUNTING TARGETS:
     * - Demoblaze E-commerce Platform
     *   ✓ User Authentication (login/logout)
     *   ✓ Product Catalog Browsing
     *   ✓ Shopping Cart Management
     *   ✓ Checkout Process (E2E)
     *   ✓ Form Validation
     *   ✓ Navigation Testing
     * 
     * - Negative Scenarios
     *   ✓ Invalid login credentials
     *   ✓ Empty form submissions
     *   ✓ Broken link detection
     * 
     * EXECUTION STRATEGY:
     * ===================
     * - API tests: 4 parallel threads (fast execution)
     * - Web tests: 2 parallel threads (browser stability)
     * - E2E tests: Sequential execution (data consistency)
     * - Evidence collection: Screenshots + logs + page source
     * 
     * REPORTING OUTPUTS:
     * ==================
     * - Cucumber HTML: build/reports/cucumber/all.html
     * - Cucumber JSON: build/reports/cucumber/all.json  
     * - JUnit XML: build/reports/cucumber/all.xml
     * - Allure Report: build/allure-results/
     * - Screenshots: build/screenshots/
     * - Console Logs: Detailed execution logs
     * 
     * CONFIGURATION OPTIONS:
     * ======================
     * Environment Control:
     * - dev (default): Full evidence collection, local browser
     * - staging: Reduced evidence, headless browser  
     * - prod: Minimal evidence, optimized execution
     * 
     * Browser Control:
     * - chrome (default): Primary browser
     * - firefox: Alternative browser
     * - headless: CI/CD mode
     * 
     * Parallel Execution:
     * - Full parallel: Maximum speed
     * - Thread-safe: Isolated test data
     * - Resource management: Automatic cleanup
     * 
     * USAGE EXAMPLES:
     * ===============
     * # Complete hunting mission
     * ./gradlew allTests
     * 
     * # Staging environment mission  
     * ./gradlew allTests -Denv=staging
     * 
     * # Production readiness check
     * ./gradlew allTests -Denv=prod -Dheadless=true
     * 
     * # Firefox compatibility hunt
     * ./gradlew allTests -Dbrowser=firefox
     * 
     * # Specific scenario hunt
     * ./gradlew allTests --tests="*Checkout*"
     * 
     * # Generate Allure report
     * ./gradlew allTests allureReport
     * 
     * SUCCESS CRITERIA:
     * =================
     * ✅ All API endpoints responding correctly
     * ✅ All web functionalities working
     * ✅ No critical bugs detected
     * ✅ Performance within acceptable limits
     * ✅ Security validations passed
     * ✅ Cross-browser compatibility confirmed
     * 
     * Mission Status: Ready for Hunt! 🎯
     * 
     * "In the world of automation testing, precision is not just about 
     *  finding bugs—it's about hunting them with surgical accuracy, 
     *  speed, and unwavering focus. That's the Kestrel way."
     * 
     * Happy hunting, fellow Kestrel! 🦅
     */
}