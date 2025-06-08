package com.kestrel.web.pages;

import com.kestrel.utils.DriverManager;
import com.kestrel.utils.WaitUtils;
import com.kestrel.utils.AlertHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 🦅 Kestrel Engine Product Page Object - Enhanced for WebStepDefinitions Integration
 * Handles product details and cart operations on Demoblaze with comprehensive functionality
 * 
 * Features:
 * - Enhanced wait strategies for product page loading
 * - Alert management integration for add to cart operations
 * - Comprehensive getter methods for WebStepDefinitions
 * - Advanced error handling and validation
 * - Enhanced logging and debugging support
 * - Image loading verification
 * - Product information extraction
 * 
 * @author Kestrel Engine
 * @version 2.0.0 (Enhanced for Compilation Fix)
 */
public class ProductPage {
    private static final Logger logger = LoggerFactory.getLogger(ProductPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Enhanced timeout constants
    private static final int DEFAULT_TIMEOUT = 15;
    private static final int PAGE_LOAD_TIMEOUT = 20;
    private static final int ELEMENT_TIMEOUT = 8;
    private static final int QUICK_TIMEOUT = 3;
    
    // Product Information Elements - Enhanced with multiple locator strategies
    @FindBy(xpath = "//h2[@class='name']")
    private WebElement productTitle;
    
    @FindBy(xpath = "//h2[contains(@class,'name')]")
    private WebElement productTitleAlt;
    
    @FindBy(xpath = "//h3[@class='price-container']")
    private WebElement productPrice;
    
    @FindBy(xpath = "//h3[contains(@class,'price')]")
    private WebElement productPriceAlt;
    
    @FindBy(id = "more-information")
    private WebElement productDescription;
    
    @FindBy(xpath = "//div[@id='more-information']//p")
    private WebElement productDescriptionText;
    
    @FindBy(xpath = "//div[contains(@class,'item active')]//p")
    private WebElement productDescriptionAlt;
    
    // Action Elements
    @FindBy(xpath = "//a[text()='Add to cart']")
    private WebElement addToCartButton;
    
    @FindBy(xpath = "//a[contains(@class,'btn') and contains(text(),'Add to cart')]")
    private WebElement addToCartButtonAlt;
    
    @FindBy(linkText = "Add to cart")
    private WebElement addToCartButtonLink;
    
    // Image Elements
    @FindBy(xpath = "//img[contains(@class,'img-responsive')]")
    private WebElement productImage;
    
    @FindBy(xpath = "//div[@class='item active']//img")
    private WebElement productImageActive;
    
    @FindBy(xpath = "//img[@class='d-block w-100']")
    private WebElement productImageCarousel;
    
    // Navigation and Page Elements
    @FindBy(xpath = "//nav[@class='navbar navbar-expand-lg navbar-dark bg-dark fixed-top']")
    private WebElement navigationBar;
    
    @FindBy(xpath = "//div[@class='container']")
    private WebElement pageContainer;
    
    @FindBy(xpath = "//div[@class='row']")
    private WebElement productRow;
    
    // Breadcrumb and navigation
    @FindBy(linkText = "Home")
    private WebElement homeLink;
    
    @FindBy(id = "narvbarx")
    private WebElement navbar;
    
    /**
     * Constructor - Enhanced initialization with comprehensive validation
     */
    public ProductPage() {
        initializePage();
    }
    
    /**
     * Enhanced page initialization with retry logic
     */
    private void initializePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
        
        logger.info("🦅 Initializing ProductPage with enhanced validation");
        
        // Enhanced page loading with retry mechanism
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                waitForPageLoad();
                logger.info("✅ ProductPage initialized successfully on attempt {}", attempt);
                return;
            } catch (Exception e) {
                logger.warn("⚠️ ProductPage initialization attempt {} failed: {}", attempt, e.getMessage());
                if (attempt == 3) {
                    throw new RuntimeException("Failed to initialize ProductPage after 3 attempts", e);
                }
                
                try {
                    Thread.sleep(2000 * attempt); // Progressive delay
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    /**
     * Enhanced page loading validation with multiple checkpoints
     */
    private void waitForPageLoad() {
        logger.debug("🔍 Waiting for product page to load...");
        
        try {
            // Primary validation - essential elements
            wait.until(ExpectedConditions.visibilityOf(productTitle));
            logger.debug("✅ Product title is visible");
            
            wait.until(ExpectedConditions.visibilityOf(addToCartButton));
            logger.debug("✅ Add to cart button is visible");
            
            // Secondary validation - page structure
            wait.until(ExpectedConditions.visibilityOf(pageContainer));
            logger.debug("✅ Page container is loaded");
            
            // Wait for price to load (essential for product page)
            WaitUtils.waitForElementVisible(driver, productPrice, ELEMENT_TIMEOUT);
            logger.debug("✅ Product price is loaded");
            
            // Brief stabilization wait for dynamic content
            Thread.sleep(1000);
            
            // Validate page readiness
            if (!isPageLoaded()) {
                throw new RuntimeException("Page validation failed after loading");
            }
            
            logger.debug("✅ Product page fully loaded and validated");
            
        } catch (TimeoutException e) {
            logger.error("❌ Product page failed to load within timeout");
            throw new RuntimeException("Product page did not load properly", e);
        } catch (Exception e) {
            logger.error("❌ Error waiting for product page: {}", e.getMessage());
            throw new RuntimeException("Product page loading failed", e);
        }
    }
    
    /**
     * Enhanced add to cart operation with comprehensive alert handling
     */
    public void clickAddToCart() {
        logger.info("🛒 Attempting to add product to cart");
        
        try {
            // Enhanced button readiness validation
            WaitUtils.waitForElementClickable(driver, addToCartButton, ELEMENT_TIMEOUT);
            
            // Store product info before adding (for logging)
            String productName = getProductName();
            String productPrice = getProductPrice();
            
            // Click with retry mechanism
            boolean clickSuccessful = false;
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    addToCartButton.click();
                    clickSuccessful = true;
                    logger.debug("✅ Add to cart button clicked successfully on attempt {}", attempt);
                    break;
                } catch (Exception e) {
                    logger.warn("⚠️ Add to cart click attempt {} failed: {}", attempt, e.getMessage());
                    if (attempt == 2) {
                        throw e;
                    }
                    Thread.sleep(500);
                }
            }
            
            if (!clickSuccessful) {
                throw new RuntimeException("Failed to click add to cart button after retries");
            }
            
            // Enhanced alert handling
            boolean addSuccess = AlertHandler.handleAddToCartAlert(driver);
            
            if (addSuccess) {
                logger.info("✅ Product added to cart successfully: {} - {}", productName, productPrice);
            } else {
                logger.warn("⚠️ Add to cart may have failed or no confirmation received");
            }
            
        } catch (Exception e) {
            logger.error("❌ Add to cart operation failed: {}", e.getMessage());
            throw new RuntimeException("Add to cart operation failed", e);
        }
    }
    
    /**
     * Enhanced product name retrieval with fallback strategies
     * @return Product name
     */
    public String getProductName() {
        try {
            // Primary strategy
            if (productTitle.isDisplayed()) {
                String name = productTitle.getText().trim();
                if (!name.isEmpty()) {
                    logger.debug("📝 Product name: {}", name);
                    return name;
                }
            }
            
            // Fallback strategy
            if (productTitleAlt.isDisplayed()) {
                String name = productTitleAlt.getText().trim();
                logger.debug("📝 Product name (alt): {}", name);
                return name;
            }
            
            logger.warn("⚠️ Could not retrieve product name");
            return "Unknown Product";
            
        } catch (Exception e) {
            logger.warn("⚠️ Error getting product name: {}", e.getMessage());
            return "Unknown Product";
        }
    }
    
    /**
     * Enhanced product price retrieval with validation
     * @return Product price
     */
    public String getProductPrice() {
        try {
            // Primary strategy
            if (WaitUtils.waitForElementVisible(driver, productPrice, QUICK_TIMEOUT)) {
                String price = productPrice.getText().trim();
                if (!price.isEmpty()) {
                    logger.debug("💰 Product price: {}", price);
                    return price;
                }
            }
            
            // Fallback strategy
            if (WaitUtils.waitForElementVisible(driver, productPriceAlt, QUICK_TIMEOUT)) {
                String price = productPriceAlt.getText().trim();
                logger.debug("💰 Product price (alt): {}", price);
                return price;
            }
            
            logger.warn("⚠️ Could not retrieve product price");
            return "Price not available";
            
        } catch (Exception e) {
            logger.warn("⚠️ Error getting product price: {}", e.getMessage());
            return "Price not available";
        }
    }
    
    /**
     * Enhanced product description retrieval with multiple strategies
     * @return Product description
     */
    public String getProductDescription() {
        try {
            // Primary strategy - more-information element
            if (WaitUtils.waitForElementVisible(driver, productDescription, QUICK_TIMEOUT)) {
                String description = productDescription.getText().trim();
                if (!description.isEmpty()) {
                    logger.debug("📝 Product description: {} chars", description.length());
                    return description;
                }
            }
            
            // Secondary strategy - description text element
            if (WaitUtils.waitForElementVisible(driver, productDescriptionText, QUICK_TIMEOUT)) {
                String description = productDescriptionText.getText().trim();
                if (!description.isEmpty()) {
                    logger.debug("📝 Product description (text): {} chars", description.length());
                    return description;
                }
            }
            
            // Fallback strategy - alternative description
            if (WaitUtils.waitForElementVisible(driver, productDescriptionAlt, QUICK_TIMEOUT)) {
                String description = productDescriptionAlt.getText().trim();
                logger.debug("📝 Product description (alt): {} chars", description.length());
                return description;
            }
            
            logger.debug("ℹ️ No product description found");
            return "Description not available";
            
        } catch (Exception e) {
            logger.warn("⚠️ Error getting product description: {}", e.getMessage());
            return "Description not available";
        }
    }
    
    /**
     * Enhanced image loading verification with multiple strategies
     * @return true if product image is loaded and displayed
     */
    public boolean isImageLoaded() {
        try {
            // Primary image check
            if (WaitUtils.waitForElementVisible(driver, productImage, QUICK_TIMEOUT)) {
                boolean imageDisplayed = productImage.isDisplayed();
                
                // Additional validation - check if image source is loaded
                String imageSrc = productImage.getAttribute("src");
                boolean hasValidSrc = imageSrc != null && !imageSrc.isEmpty() && !imageSrc.contains("data:image");
                
                if (imageDisplayed && hasValidSrc) {
                    logger.debug("🖼️ Product image loaded successfully");
                    return true;
                }
            }
            
            // Fallback - check active image
            if (WaitUtils.waitForElementVisible(driver, productImageActive, QUICK_TIMEOUT)) {
                boolean imageDisplayed = productImageActive.isDisplayed();
                logger.debug("🖼️ Product image (active) loaded: {}", imageDisplayed);
                return imageDisplayed;
            }
            
            // Final fallback - carousel image
            if (WaitUtils.waitForElementVisible(driver, productImageCarousel, QUICK_TIMEOUT)) {
                boolean imageDisplayed = productImageCarousel.isDisplayed();
                logger.debug("🖼️ Product image (carousel) loaded: {}", imageDisplayed);
                return imageDisplayed;
            }
            
            logger.debug("📷 No product images found or loaded");
            return false;
            
        } catch (Exception e) {
            logger.debug("⚠️ Error checking image loading: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced add to cart button state verification
     * @return true if add to cart button is enabled and clickable
     */
    public boolean isAddToCartButtonEnabled() {
        try {
            // Primary button check
            boolean primaryEnabled = addToCartButton.isEnabled() && 
                                   addToCartButton.isDisplayed() &&
                                   WaitUtils.waitForElementClickable(driver, addToCartButton, 1);
            
            if (primaryEnabled) {
                logger.debug("✅ Add to cart button is enabled and clickable");
                return true;
            }
            
            // Fallback button check
            if (WaitUtils.waitForElementClickable(driver, addToCartButtonAlt, 1)) {
                logger.debug("✅ Add to cart button (alt) is enabled and clickable");
                return true;
            }
            
            // Link button check
            if (WaitUtils.waitForElementClickable(driver, addToCartButtonLink, 1)) {
                logger.debug("✅ Add to cart button (link) is enabled and clickable");
                return true;
            }
            
            logger.debug("❌ Add to cart button is not enabled or clickable");
            return false;
            
        } catch (Exception e) {
            logger.debug("⚠️ Error checking add to cart button state: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced page loading validation with comprehensive checks
     * @return true if page is fully loaded and functional
     */
    public boolean isPageLoaded() {
        try {
            // Essential elements check
            boolean titleLoaded = productTitle.isDisplayed();
            boolean buttonLoaded = addToCartButton.isDisplayed();
            boolean containerLoaded = pageContainer.isDisplayed();
            
            // Additional validation
            boolean priceLoaded = WaitUtils.waitForElementVisible(driver, productPrice, 1);
            boolean navbarLoaded = WaitUtils.waitForElementVisible(driver, navbar, 1);
            
            boolean fullyLoaded = titleLoaded && buttonLoaded && containerLoaded && priceLoaded;
            
            logger.debug("🔍 Page load check: title={}, button={}, container={}, price={}, navbar={}, overall={}", 
                        titleLoaded, buttonLoaded, containerLoaded, priceLoaded, navbarLoaded, fullyLoaded);
            
            return fullyLoaded;
            
        } catch (Exception e) {
            logger.debug("⚠️ Page load check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get product title (alias for getProductName for compatibility)
     * @return Product title/name
     */
    public String getProductTitle() {
        return getProductName();
    }
    
    /**
     * Navigate back to home page
     * @return HomePage instance
     */
    public HomePage navigateToHome() {
        try {
            WaitUtils.waitForElementClickable(driver, homeLink, ELEMENT_TIMEOUT);
            homeLink.click();
            logger.info("🏠 Navigated back to home page");
            return new HomePage();
        } catch (Exception e) {
            logger.error("❌ Failed to navigate to home: {}", e.getMessage());
            throw new RuntimeException("Navigation to home failed", e);
        }
    }
    
    // ===== REQUIRED GETTER METHODS FOR WEBSTEPDEFINITIONS INTEGRATION =====
    
    /**
     * Get the product name element for external wait operations
     * Required by WebStepDefinitions for product validation
     * @return WebElement representing the product name
     */
    public WebElement getProductNameElement() {
        return productTitle;
    }
    
    /**
     * Get the product price element for external wait operations
     * Required by WebStepDefinitions for price validation
     * @return WebElement representing the product price
     */
    public WebElement getProductPriceElement() {
        return productPrice;
    }
    
    /**
     * Get the product description element for external wait operations
     * Required by WebStepDefinitions for description validation
     * @return WebElement representing the product description
     */
    public WebElement getProductDescriptionElement() {
        return productDescription;
    }
    
    /**
     * Get the add to cart button element for external operations
     * Required by WebStepDefinitions for clickability checks
     * @return WebElement representing the add to cart button
     */
    public WebElement getAddToCartButton() {
        return addToCartButton;
    }
    
    /**
     * Get the product image element for external operations
     * Required by WebStepDefinitions for image validation
     * @return WebElement representing the product image
     */
    public WebElement getProductImageElement() {
        return productImage;
    }
    
    /**
     * Get the navigation bar element for external operations
     * @return WebElement representing the navigation bar
     */
    public WebElement getNavigationBar() {
        return navigationBar;
    }
    
    /**
     * Get the page container element for external operations
     * @return WebElement representing the page container
     */
    public WebElement getPageContainer() {
        return pageContainer;
    }
    
    /**
     * Get the product row element for external operations
     * @return WebElement representing the product row
     */
    public WebElement getProductRow() {
        return productRow;
    }
    
    /**
     * Get the home link element for navigation
     * @return WebElement representing the home link
     */
    public WebElement getHomeLink() {
        return homeLink;
    }
    
    /**
     * Get the navbar element for validation
     * @return WebElement representing the navbar
     */
    public WebElement getNavbar() {
        return navbar;
    }
    
    /**
     * Get alternative add to cart button for fallback operations
     * @return WebElement representing the alternative add to cart button
     */
    public WebElement getAddToCartButtonAlt() {
        return addToCartButtonAlt;
    }
    
    /**
     * Get add to cart link button for link-based operations
     * @return WebElement representing the add to cart link
     */
    public WebElement getAddToCartButtonLink() {
        return addToCartButtonLink;
    }
    
    /**
     * Get product image active element for carousel validation
     * @return WebElement representing the active product image
     */
    public WebElement getProductImageActive() {
        return productImageActive;
    }
    
    /**
     * Enhanced product information retrieval
     * @return Comprehensive product information
     */
    public String getProductInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Product Info: ");
        info.append("name='").append(getProductName()).append("', ");
        info.append("price='").append(getProductPrice()).append("', ");
        info.append("description_length=").append(getProductDescription().length()).append(", ");
        info.append("image_loaded=").append(isImageLoaded()).append(", ");
        info.append("add_to_cart_enabled=").append(isAddToCartButtonEnabled());
        
        return info.toString();
    }
    
    /**
     * Get comprehensive page state information
     * @return Page state description
     */
    public String getPageState() {
        StringBuilder state = new StringBuilder();
        state.append("ProductPage State: ");
        state.append("page_loaded=").append(isPageLoaded());
        state.append(", product_name='").append(getProductName()).append("'");
        state.append(", price_available=").append(!getProductPrice().equals("Price not available"));
        state.append(", image_loaded=").append(isImageLoaded());
        state.append(", button_enabled=").append(isAddToCartButtonEnabled());
        
        return state.toString();
    }
}