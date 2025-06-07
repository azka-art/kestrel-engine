package com.kestrel.runners;

import org.junit.platform.suite.api.*;

/**
 * Kestrel Engine API Test Runner
 * Executes API hunting scenarios with comprehensive reporting
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/api")
@ConfigurationParameter(
    key = "cucumber.plugin",
    value = "pretty, " +
            "html:build/reports/cucumber/api-tests.html, " +
            "json:build/reports/cucumber/api-tests.json, " +
            "junit:build/reports/cucumber/api-tests.xml, " +
            "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
@ConfigurationParameter(key = "cucumber.filter.tags", value = "@api")
@ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "true")
@ConfigurationParameter(key = "cucumber.execution.parallel.config.strategy", value = "dynamic")
@ConfigurationParameter(key = "cucumber.publish.quiet", value = "true")
public class ApiTestRunner {
    // JUnit 5 Suite - no additional code needed
}