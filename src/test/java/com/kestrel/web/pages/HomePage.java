package com.kestrel.web.pages;

import com.kestrel.utils.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Kestrel Engine Home Page Object
 * Demoblaze homepage interactions and navigation
 * 
 * @author Kestrel Engine
 * @version 1.0.0
 */
public class HomePage {
    private static final Logger logger = LoggerFactory.getLogger(HomePage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Header Navigation Elements
    @FindBy(id = "narvbarx")
    private WebElement navigationBar;
    
    @FindBy(linkText = "Home")
    private WebElement homeLink;
    
    @FindBy(linkText = "Contact")
    private WebElement contactLink;
    
    @FindBy(linkText = "About us")
    private WebElement aboutUsLink;
    
    @FindBy(linkText = "Cart")
    private WebElement cartLink;
    
    @FindBy(id = "login2")
    private WebElement loginButton;
    
    @FindBy(id = "signin2")
    private WebElement signUpButton;
    
    @FindBy(id = "nameofuser")
    private WebElement welcomeMessage;
    
    @FindBy(id = "logout2")
    private WebElement logoutButton;
    
    // Category Navigation
    @FindBy(linkText = "Phones")
    private WebElement phonesCategory;
    
    @FindBy(linkText = "Laptops") 
    private WebElement laptopsCategory;
    
    @FindBy(linkText = "Monitors")
    private WebElement monitorsCategory;
    
    // Product List Elements
    @FindBy(id = "tbodyid")
    private WebElement productContainer;
    
    @FindBy(className = "card-title")
    private List<WebElement> productTitles;
    
    @FindBy(className = "card-block")
    private List<WebElement> productCards;
    
    // Carousel Elements
    @FindBy(id = "carouselExampleIndicators")
    private WebElement carousel;
    
    @FindBy(className = "carousel-item")
    private List<WebElement> carouselItems;
    
    // Footer Elements
    @FindBy(className = "list-unstyled")
    private WebElement footerLinks;
    
    /**
     * Constructor - Initialize page elements
     */
    public HomePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        logger.debug("🦅 HomePage initialized");
    }
    
    /**
     * Navigate to homepage
     * @return Current HomePage instance
     */
    public HomePage navigateToHome() {
        driver.get("https://www.demoblaze.com");
        waitForPageLoad();
        logger.info("🏠 Navigated to Demoblaze homepage");
        return this;
    }
    
    /**
     * Wait for page to load completely
     */
    private void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(navigationBar));
        wait.until(ExpectedConditions.visibilityOf(productContainer));
        logger.debug("✅ Homepage loaded successfully");
    }
    
    /**
     * Click login button to open login modal
     * @return LoginPage instance
     */
    public LoginPage clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
        logger.info("🔐 Login button clicked");
        return new LoginPage();
    }
    
    /**
     * Click sign up button to open registration modal  
     * @return SignUpPage instance
     */
    public SignUpPage clickSignUp() {
        wait.until(ExpectedConditions.elementToBeClickable(signUpButton));
        signUpButton.click();
        logger.info("📝 Sign up button clicked");
        return new SignUpPage();
    }
    
    /**
     * Click cart link to navigate to cart
     * @return CartPage instance
     */
    public CartPage clickCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink));
        cartLink.click();
        logger.info("🛒 Cart link clicked");
        return new CartPage();
    }
    
    /**
     * Click logout button
     * @return Current HomePage instance
     */
    public HomePage clickLogout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        logoutButton.click();
        logger.info("🚪 Logout button clicked");
        return this;
    }
    
    /**
     * Select product category
     * @param category Category name (Phones, Laptops, Monitors)
     * @return Current HomePage instance
     */
    public HomePage selectCategory(String category) {
        WebElement categoryElement = switch (category.toLowerCase()) {
            case "phones" -> phonesCategory;
            case "laptops" -> laptopsCategory;
            case "monitors" -> monitorsCategory;
            default -> throw new IllegalArgumentException("Invalid category: " + category);
        };
        
        wait.until(ExpectedConditions.elementToBeClickable(categoryElement));
        categoryElement.click();
        
        // Wait for products to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("card-title")));
        
        logger.info("📱 Selected category: {}", category);
        return this;
    }
    
    /**
     * Click on specific product by name
     * @param productName Name of the product
     * @return ProductPage instance
     */
    public ProductPage selectProduct(String productName) {
        // Wait for products to load
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card-title")));
        
        // Find and click the product
        for (WebElement productTitle : productTitles) {
            if (productTitle.getText().trim().equalsIgnoreCase(productName.trim())) {
                wait.until(ExpectedConditions.elementToBeClickable(productTitle));
                productTitle.click();
                logger.info("📦 Selected product: {}", productName);
                return new ProductPage();
            }
        }
        
        throw new RuntimeException("Product not found: " + productName);
    }
    
    /**
     * Get welcome message text
     * @return Welcome message text or empty string if not logged in
     */
    public String getWelcomeMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(welcomeMessage));
            String message = welcomeMessage.getText();
            logger.debug("👋 Welcome message: {}", message);
            return message;
        } catch (Exception e) {
            logger.debug("No welcome message visible (user not logged in)");
            return "";
        }
    }
    
    /**
     * Check if user is logged in
     * @return true if user is logged in
     */
    public boolean isUserLoggedIn() {
        try {
            return welcomeMessage.isDisplayed() && logoutButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if login button is visible
     * @return true if login button is visible
     */
    public boolean isLoginButtonVisible() {
        try {
            return loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get list of visible product names
     * @return List of product names
     */
    public List<String> getVisibleProductNames() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card-title")));
        List<String> productNames = productTitles.stream()
            .map(WebElement::getText)
            .toList();
        logger.debug("📋 Found {} products", productNames.size());
        return productNames;
    }
    
    /**
     * Get number of visible products
     * @return Number of products displayed
     */
    public int getProductCount() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card-title")));
        int count = productTitles.size();
        logger.debug("🔢 Product count: {}", count);
        return count;
    }
    
    /**
     * Check if carousel is displayed
     * @return true if carousel is visible
     */
    public boolean isCarouselDisplayed() {
        try {
            return carousel.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verify homepage is loaded
     * @return true if homepage is properly loaded
     */
    public boolean isPageLoaded() {
        try {
            return navigationBar.isDisplayed() && 
                   productContainer.isDisplayed() &&
                   !productTitles.isEmpty();
        } catch (Exception e) {
            logger.warn("⚠️ Homepage not properly loaded: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get page title
     * @return Page title
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("📄 Page title: {}", title);
        return title;
    }
    
    /**
     * Get current URL
     * @return Current page URL
     */
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("🔗 Current URL: {}", url);
        return url;
    }
}