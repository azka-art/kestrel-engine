package com.kestrel.web.pages;

import com.kestrel.utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Kestrel Engine Sign Up Page Object
 * Handles user registration operations on Demoblaze
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class SignUpPage {
    private static final Logger logger = LoggerFactory.getLogger(SignUpPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(id = "signInModal")
    private WebElement signUpModal;
    
    @FindBy(id = "sign-username")
    private WebElement usernameField;
    
    @FindBy(id = "sign-password")
    private WebElement passwordField;
    
    @FindBy(xpath = "//button[text()='Sign up']")
    private WebElement signUpButton;
    
    public SignUpPage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        waitForModalToAppear();
    }
    
    private void waitForModalToAppear() {
        wait.until(ExpectedConditions.visibilityOf(signUpModal));
    }
    
    public HomePage signUp(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        signUpButton.click();
        wait.until(ExpectedConditions.invisibilityOf(signUpModal));
        return new HomePage();
    }
    
    public boolean attemptSignUp(String username, String password) {
        try {
            signUp(username, password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isModalVisible() {
        try {
            return signUpModal.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
