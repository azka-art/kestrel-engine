package com.kestrel.web.pages;

import com.kestrel.utils.DriverManager;
import com.kestrel.utils.WaitUtils;
import com.kestrel.utils.AlertHandler;
import org.openqa.selenium.By;
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
import java.util.List;

/**
 * 🦅 Kestrel Engine Cart Page Object - Enhanced for WebStepDefinitions Integration
 * Handles shopping cart operations on Demoblaze with comprehensive functionality
 * 
 * Features:
 * - Enhanced wait strategies for cart page loading
 * - Comprehensive checkout modal handling
 * - Alert management integration for order operations
 * - Advanced cart item manipulation
 * - Enhanced validation and error handling
 * - Comprehensive getter methods for WebStepDefinitions
 * - Order placement and confirmation handling
 * 
 * @author Kestrel Engine
 * @version 2.0.0 (Enhanced for Compilation Fix)
 */
public class CartPage {
    private static final Logger logger = LoggerFactory.getLogger(CartPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Enhanced timeout constants
    private static final int DEFAULT_TIMEOUT = 15;
    private static final int PAGE_LOAD_TIMEOUT = 20;
    private static final int ELEMENT_TIMEOUT = 8;
    private static final int QUICK_TIMEOUT = 3;
    private static final int CHECKOUT_TIMEOUT = 10;
    
    // Cart Container and Structure Elements
    @FindBy(id = "tbodyid")
    private WebElement cartContainer;
    
    @FindBy(xpath = "//table[@class='table table-bordered table-hover table-sm']")
    private WebElement cartTable;
    
    @FindBy(xpath = "//thead")
    private WebElement cartTableHeader;
    
    @FindBy(xpath = "//tbody[@id='tbodyid']")
    private WebElement cartTableBody;
    
    // Cart Action Elements
    @FindBy(xpath = "//button[text()='Place Order']")
    private WebElement placeOrderButton;
    
    @FindBy(xpath = "//button[contains(@class,'btn-success')]")
    private WebElement placeOrderButtonAlt;
    
    @FindBy(id = "orderModal")
    private WebElement checkoutModal;
    
    @FindBy(xpath = "//div[@id='orderModal'][@style='display: block;']")
    private WebElement visibleCheckoutModal;
    
    // Total Price Elements
    @FindBy(id = "totalp")
    private WebElement totalPrice;
    
    @FindBy(xpath = "//h3[@id='totalp']")
    private WebElement totalPriceAlt;
    
    @FindBy(xpath = "//h3[contains(text(),'Total:')]")
    private WebElement totalPriceHeader;
    
    // Cart Items
    @FindBy(xpath = "//tr[@class='success']")
    private List<WebElement> cartItems;
    
    @FindBy(xpath = "//tbody[@id='tbodyid']/tr")
    private List<WebElement> cartItemsAlt;
    
    // Empty Cart Elements
    @FindBy(xpath = "//p[contains(text(),'Your cart is empty')]")
    private WebElement emptyCartMessage;
    
    @FindBy(xpath = "//div[contains(text(),'No items in cart')]")
    private WebElement noItemsMessage;
    
    // Checkout Modal Elements
    @FindBy(id = "name")
    private WebElement checkoutNameField;
    
    @FindBy(id = "country")
    private WebElement checkoutCountryField;
    
    @FindBy(id = "city")
    private WebElement checkoutCityField;
    
    @FindBy(id = "card")
    private WebElement checkoutCreditCardField;
    
    @FindBy(id = "month")
    private WebElement checkoutMonthField;
    
    @FindBy(id = "year")
    private WebElement checkoutYearField;
    
    @FindBy(xpath = "//button[text()='Purchase']")
    private WebElement purchaseButton;
    
    @FindBy(xpath = "//button[@onclick='purchaseOrder()']")
    private WebElement purchaseButtonAlt;
    
    @FindBy(xpath = "//div[@id='orderModal']//button[@class='close']")
    private WebElement checkoutCloseButton;
    
    // Order Confirmation Elements
    @FindBy(xpath = "//div[contains(@class,'sweet-alert')]")
    private WebElement orderConfirmationModal;
    
    @FindBy(xpath = "//h2[contains(text(),'Thank you for your purchase!')]")
    private WebElement orderConfirmationTitle;
    
    @FindBy(xpath = "//p[contains(text(),'Id:')]")
    private WebElement orderConfirmationDetails;
    
    @FindBy(xpath = "//button[text()='OK']")
    private WebElement orderConfirmationOkButton;
    
    // Navigation Elements
    @FindBy(xpath = "//nav[@class='navbar']")
    private WebElement navigationBar;
    
    @FindBy(linkText = "Home")
    private WebElement homeLink;
    
    /**
     * Constructor - Enhanced initialization with comprehensive validation
     */
    public CartPage() {
        initializePage();
    }
    
    /**
     * Enhanced page initialization with retry logic
     */
    private void initializePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
        
        logger.info("🦅 Initializing CartPage with enhanced validation");
        
        // Enhanced page loading with retry mechanism
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                waitForPageLoad();
                logger.info("✅ CartPage initialized successfully on attempt {}", attempt);
                return;
            } catch (Exception e) {
                logger.warn("⚠️ CartPage initialization attempt {} failed: {}", attempt, e.getMessage());
                if (attempt == 3) {
                    throw new RuntimeException("Failed to initialize CartPage after 3 attempts", e);
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
        logger.debug("🔍 Waiting for cart page to load...");
        
        try {
            // Primary validation - cart container
            wait.until(ExpectedConditions.visibilityOf(cartContainer));
            logger.debug("✅ Cart container is visible");
            
            // Secondary validation - wait for cart content to stabilize
            Thread.sleep(1000);
            
            // Check if cart is empty or has items
            if (isCartEmpty()) {
                logger.debug("🛒 Cart is empty");
                // Verify empty cart message or structure
                WaitUtils.waitForCondition(
                    driver -> isEmptyCartMessageVisible() || cartItems.isEmpty(),
                    QUICK_TIMEOUT
                );
            } else {
                logger.debug("🛒 Cart has items, waiting for items to load");
                // Wait for cart items to be fully loaded
                WaitUtils.waitForCondition(
                    driver -> !cartItems.isEmpty(),
                    ELEMENT_TIMEOUT
                );
            }
            
            // Validate page structure
            if (!isPageLoaded()) {
                throw new RuntimeException("Cart page validation failed after loading");
            }
            
            logger.debug("✅ Cart page fully loaded and validated");
            
        } catch (TimeoutException e) {
            logger.error("❌ Cart page failed to load within timeout");
            throw new RuntimeException("Cart page did not load properly", e);
        } catch (Exception e) {
            logger.error("❌ Error waiting for cart page: {}", e.getMessage());
            throw new RuntimeException("Cart page loading failed", e);
        }
    }
    
    /**
     * Enhanced product search in cart with multiple strategies
     * @param productName Product name to search for
     * @return true if product is found in cart
     */
    public boolean isProductInCart(String productName) {
        logger.debug("🔍 Searching for product in cart: {}", productName);
        
        try {
            // Wait for cart items to be loaded
            if (!WaitUtils.waitForCondition(
                driver -> !cartItems.isEmpty() || isCartEmpty(),
                ELEMENT_TIMEOUT
            )) {
                logger.debug("⏰ Timeout waiting for cart items to load");
                return false;
            }
            
            // Primary strategy - search in cart items
            for (WebElement item : cartItems) {
                try {
                    String itemText = item.getText().toLowerCase();
                    if (itemText.contains(productName.toLowerCase())) {
                        logger.debug("✅ Product found in cart: {}", productName);
                        return true;
                    }
                } catch (Exception e) {
                    logger.debug("⚠️ Error reading cart item: {}", e.getMessage());
                }
            }
            
            // Fallback strategy - search in alternative cart items
            for (WebElement item : cartItemsAlt) {
                try {
                    String itemText = item.getText().toLowerCase();
                    if (itemText.contains(productName.toLowerCase())) {
                        logger.debug("✅ Product found in cart (alt): {}", productName);
                        return true;
                    }
                } catch (Exception e) {
                    logger.debug("⚠️ Error reading alternative cart item: {}", e.getMessage());
                }
            }
            
            logger.debug("❌ Product not found in cart: {}", productName);
            return false;
            
        } catch (Exception e) {
            logger.warn("⚠️ Error searching for product in cart: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enhanced cart empty state verification
     * @return true if cart is empty
     */
    public boolean isCartEmpty() {
        try {
            // Primary check - cart items list
            boolean itemsEmpty = cartItems.isEmpty();
            
            // Secondary check - alternative items list
            boolean altItemsEmpty = cartItemsAlt.isEmpty();
            
            // Tertiary check - empty message visibility
            boolean emptyMessageVisible = isEmptyCartMessageVisible();
            
            boolean isEmpty = (itemsEmpty && altItemsEmpty) || emptyMessageVisible;
            
            logger.debug("🔍 Cart empty check: items={}, alt_items={}, message={}, result={}", 
                        itemsEmpty, altItemsEmpty, emptyMessageVisible, isEmpty);
            
            return isEmpty;
            
        } catch (Exception e) {
            logger.debug("⚠️ Error checking if cart is empty: {}", e.getMessage());
            return true; // Assume empty on error
        }
    }
    
    /**
     * Check if empty cart message is visible
     * @return true if empty cart message is displayed
     */
    public boolean isEmptyCartMessageVisible() {
        try {
            return WaitUtils.waitForElementVisible(driver, emptyCartMessage, 1) ||
                   WaitUtils.waitForElementVisible(driver, noItemsMessage, 1);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Enhanced product count with validation
     * @return Number of products in cart
     */
    public int getProductCount() {
        try {
            // Wait for cart to stabilize
            Thread.sleep(500);
            
            // Primary count
            int primaryCount = cartItems.size();
            
            // Fallback count
            int altCount = cartItemsAlt.size();
            
            // Use the higher count (more reliable)
            int count = Math.max(primaryCount, altCount);
            
            logger.debug("🔢 Cart product count: primary={}, alt={}, final={}", primaryCount, altCount, count);
            
            return count;
            
        } catch (Exception e) {
            logger.debug("⚠️ Error getting product count: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Enhanced total price retrieval with fallback strategies
     * @return Total price string
     */
    public String getTotalPrice() {
        try {
            // Primary strategy
            if (WaitUtils.waitForElementVisible(driver, totalPrice, QUICK_TIMEOUT)) {
                String price = totalPrice.getText().trim();
                if (!price.isEmpty()) {
                    logger.debug("💰 Total price: {}", price);
                    return price;
                }
            }
            
            // Fallback strategy
            if (WaitUtils.waitForElementVisible(driver, totalPriceAlt, QUICK_TIMEOUT)) {
                String price = totalPriceAlt.getText().trim();
                logger.debug("💰 Total price (alt): {}", price);
                return price;
            }
            
            // Header strategy
            if (WaitUtils.waitForElementVisible(driver, totalPriceHeader, QUICK_TIMEOUT)) {
                String headerText = totalPriceHeader.getText().trim();
                // Extract price from "Total: $XXX" format
                if (headerText.contains("Total:")) {
                    String price = headerText.substring(headerText.indexOf("Total:") + 6).trim();
                    logger.debug("💰 Total price (header): {}", price);
                    return price;
                }
            }
            
            logger.debug("⚠️ Could not retrieve total price");
            return "0";
            
        } catch (Exception e) {
            logger.warn("⚠️ Error getting total price: {}", e.getMessage());
            return "0";
        }
    }
    
    /**
     * Enhanced product removal with validation
     * @param productName Name of product to remove
     */
    public void removeProduct(String productName) {
        logger.info("🗑️ Removing product from cart: {}", productName);
        
        try {
            // Search and remove from primary cart items
            for (WebElement item : cartItems) {
                try {
                    if (item.getText().toLowerCase().contains(productName.toLowerCase())) {
                        WebElement deleteButton = item.findElement(By.linkText("Delete"));
                        WaitUtils.waitForElementClickable(driver, deleteButton, ELEMENT_TIMEOUT);
                        deleteButton.click();
                        
                        // Wait for removal to complete
                        Thread.sleep(1000);
                        
                        logger.info("✅ Product removed from cart: {}", productName);
                        return;
                    }
                } catch (Exception e) {
                    logger.debug("⚠️ Error processing cart item for removal: {}", e.getMessage());
                }
            }
            
            // Fallback - search in alternative cart items
            for (WebElement item : cartItemsAlt) {
                try {
                    if (item.getText().toLowerCase().contains(productName.toLowerCase())) {
                        WebElement deleteButton = item.findElement(By.linkText("Delete"));
                        WaitUtils.waitForElementClickable(driver, deleteButton, ELEMENT_TIMEOUT);
                        deleteButton.click();
                        
                        Thread.sleep(1000);
                        
                        logger.info("✅ Product removed from cart (alt): {}", productName);
                        return;
                    }
                } catch (Exception e) {
                    logger.debug("⚠️ Error processing alternative cart item for removal: {}", e.getMessage());
                }
            }
            
            logger.warn("⚠️ Product not found for removal: {}", productName);
            
        } catch (Exception e) {
            logger.error("❌ Error removing product from cart: {}", e.getMessage());
            throw new RuntimeException("Product removal failed", e);
        }
    }
    
    /**
     * Enhanced cart clearing with validation
     */
    public void clearCart() {
        logger.info("🧹 Clearing entire cart");
        
        try {
            int attempts = 0;
            int maxAttempts = 10; // Prevent infinite loop
            
            while (!isCartEmpty() && attempts < maxAttempts) {
                List<WebElement> currentItems = cartItems.isEmpty() ? cartItemsAlt : cartItems;
                
                if (!currentItems.isEmpty()) {
                    try {
                        WebElement firstItem = currentItems.get(0);
                        WebElement deleteButton = firstItem.findElement(By.linkText("Delete"));
                        WaitUtils.waitForElementClickable(driver, deleteButton, ELEMENT_TIMEOUT);
                        deleteButton.click();
                        
                        // Wait for item removal
                        Thread.sleep(1000);
                        attempts++;
                        
                        logger.debug("🗑️ Removed cart item {} of approximately {}", attempts, currentItems.size());
                        
                    } catch (Exception e) {
                        logger.warn("⚠️ Error removing cart item on attempt {}: {}", attempts, e.getMessage());
                        attempts++;
                    }
                } else {
                    break;
                }
            }
            
            if (isCartEmpty()) {
                logger.info("✅ Cart cleared successfully");
            } else {
                logger.warn("⚠️ Cart may not be completely cleared after {} attempts", attempts);
            }
            
        } catch (Exception e) {
            logger.error("❌ Error clearing cart: {}", e.getMessage());
            throw new RuntimeException("Cart clearing failed", e);
        }
    }
    
    /**
     * Enhanced place order operation with modal handling
     */
    public void clickPlaceOrder() {
        logger.info("📦 Placing order");
        
        try {
            // Enhanced button readiness validation
            WaitUtils.waitForElementClickable(driver, placeOrderButton, ELEMENT_TIMEOUT);
            
            // Click with retry mechanism
            boolean clickSuccessful = false;
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    placeOrderButton.click();
                    clickSuccessful = true;
                    logger.debug("✅ Place order button clicked successfully on attempt {}", attempt);
                    break;
                } catch (Exception e) {
                    logger.warn("⚠️ Place order click attempt {} failed: {}", attempt, e.getMessage());
                    if (attempt == 2) {
                        throw e;
                    }
                    Thread.sleep(500);
                }
            }
            
            if (!clickSuccessful) {
                throw new RuntimeException("Failed to click place order button after retries");
            }
            
            // Wait for checkout modal to appear
            WaitUtils.waitForElementVisible(driver, checkoutModal, CHECKOUT_TIMEOUT);
            logger.info("✅ Checkout modal opened");
            
        } catch (Exception e) {
            logger.error("❌ Place order operation failed: {}", e.getMessage());
            throw new RuntimeException("Place order operation failed", e);
        }
    }
    
    /**
     * Fill checkout form with provided information
     * @param name Customer name
     * @param country Customer country
     * @param city Customer city
     * @param creditCard Credit card number
     * @param month Expiration month
     * @param year Expiration year
     */
    public void fillCheckoutForm(String name, String country, String city, String creditCard, String month, String year) {
        logger.info("📝 Filling checkout form");
        
        try {
            // Wait for form fields to be available
            WaitUtils.waitForElementVisible(driver, checkoutNameField, ELEMENT_TIMEOUT);
            
            // Fill form fields with validation
            checkoutNameField.clear();
            checkoutNameField.sendKeys(name);
            
            checkoutCountryField.clear();
            checkoutCountryField.sendKeys(country);
            
            checkoutCityField.clear();
            checkoutCityField.sendKeys(city);
            
            checkoutCreditCardField.clear();
            checkoutCreditCardField.sendKeys(creditCard);
            
            checkoutMonthField.clear();
            checkoutMonthField.sendKeys(month);
            
            checkoutYearField.clear();
            checkoutYearField.sendKeys(year);
            
            logger.info("✅ Checkout form filled successfully");
            
        } catch (Exception e) {
            logger.error("❌ Error filling checkout form: {}", e.getMessage());
            throw new RuntimeException("Checkout form filling failed", e);
        }
    }
    
    /**
     * Submit the purchase order
     */
    public void clickPurchase() {
        logger.info("💳 Submitting purchase order");
        
        try {
            WaitUtils.waitForElementClickable(driver, purchaseButton, ELEMENT_TIMEOUT);
            purchaseButton.click();
            
            // Handle order confirmation
            boolean orderSuccess = AlertHandler.handleOrderAlert(driver);
            
            if (orderSuccess) {
                logger.info("✅ Order submitted successfully");
            } else {
                logger.warn("⚠️ Order submission may have failed or no confirmation received");
            }
            
        } catch (Exception e) {
            logger.error("❌ Purchase submission failed: {}", e.getMessage());
            throw new RuntimeException("Purchase submission failed", e);
        }
    }
    
    /**
     * Enhanced page loading validation
     * @return true if page is fully loaded and functional
     */
    public boolean isPageLoaded() {
        try {
            // Essential elements check
            boolean containerLoaded = cartContainer.isDisplayed();
            boolean tableLoaded = WaitUtils.waitForElementVisible(driver, cartTable, 1);
            boolean navigationLoaded = WaitUtils.waitForElementVisible(driver, navigationBar, 1);
            
            boolean fullyLoaded = containerLoaded && tableLoaded;
            
            logger.debug("🔍 Page load check: container={}, table={}, nav={}, overall={}", 
                        containerLoaded, tableLoaded, navigationLoaded, fullyLoaded);
            
            return fullyLoaded;
            
        } catch (Exception e) {
            logger.debug("⚠️ Page load check failed: {}", e.getMessage());
            return false;
        }
    }
    
    // ===== ADDITIONAL VALIDATION METHODS =====
    
    /**
     * Check if checkout modal is visible
     * @return true if checkout modal is displayed
     */
    public boolean isCheckoutModalVisible() {
        try {
            return WaitUtils.waitForElementVisible(driver, checkoutModal, 1) ||
                   WaitUtils.waitForElementVisible(driver, visibleCheckoutModal, 1);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if checkout page is loaded (alternative to modal)
     * @return true if on checkout page
     */
    public boolean isCheckoutPageLoaded() {
        try {
            return driver.getCurrentUrl().contains("checkout") ||
                   WaitUtils.waitForElementVisible(driver, checkoutNameField, 1);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if order confirmation is visible
     * @return true if order confirmation is displayed
     */
    public boolean isOrderConfirmationVisible() {
        try {
            return WaitUtils.waitForElementVisible(driver, orderConfirmationModal, 1) ||
                   WaitUtils.waitForElementVisible(driver, orderConfirmationTitle, 1);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if success modal is visible
     * @return true if success modal is displayed
     */
    public boolean isSuccessModalVisible() {
        return isOrderConfirmationVisible();
    }
    
    /**
     * Check if place order button is enabled
     * @return true if place order button is clickable
     */
    public boolean isPlaceOrderButtonEnabled() {
        try {
            return placeOrderButton.isEnabled() && 
                   placeOrderButton.isDisplayed() &&
                   WaitUtils.waitForElementClickable(driver, placeOrderButton, 1);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get product name by product name (for validation)
     * @param productName Product name to search for
     * @return Product name from cart item
     */
    public String getProductName(String productName) {
        try {
            for (WebElement item : cartItems) {
                String itemText = item.getText();
                if (itemText.toLowerCase().contains(productName.toLowerCase())) {
                    // Extract product name from cart item text
                    String[] parts = itemText.split("\n");
                    if (parts.length > 0) {
                        return parts[0].trim();
                    }
                }
            }
            return productName; // Return original if not found
        } catch (Exception e) {
            return productName;
        }
    }
    
    /**
     * Get product price by product name
     * @param productName Product name to search for
     * @return Product price from cart item
     */
    public String getProductPrice(String productName) {
        try {
            for (WebElement item : cartItems) {
                String itemText = item.getText();
                if (itemText.toLowerCase().contains(productName.toLowerCase())) {
                    // Extract price from cart item text
                    String[] parts = itemText.split("\n");
                    for (String part : parts) {
                        if (part.contains("$") || part.toLowerCase().contains("price")) {
                            return part.trim();
                        }
                    }
                }
            }
            return "Price not available";
        } catch (Exception e) {
            return "Price not available";
        }
    }
    
    /**
     * Get product name by index
     * @param index Product index in cart
     * @return Product name
     */
    public String getProductNameByIndex(int index) {
        try {
            if (index < cartItems.size()) {
                String itemText = cartItems.get(index).getText();
                String[] parts = itemText.split("\n");
                return parts.length > 0 ? parts[0].trim() : "Unknown Product";
            }
            return "Unknown Product";
        } catch (Exception e) {
            return "Unknown Product";
        }
    }
    
    /**
     * Get product price by index
     * @param index Product index in cart
     * @return Product price
     */
    public String getProductPriceByIndex(int index) {
        try {
            if (index < cartItems.size()) {
                String itemText = cartItems.get(index).getText();
                String[] parts = itemText.split("\n");
                for (String part : parts) {
                    if (part.contains("$")) {
                        return part.trim();
                    }
                }
            }
            return "Price not available";
        } catch (Exception e) {
            return "Price not available";
        }
    }
    
    /**
     * Get order confirmation text
     * @return Order confirmation text
     */
    public String getOrderConfirmationText() {
        try {
            if (WaitUtils.waitForElementVisible(driver, orderConfirmationDetails, QUICK_TIMEOUT)) {
                return orderConfirmationDetails.getText();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    
    // ===== REQUIRED GETTER METHODS FOR WEBSTEPDEFINITIONS INTEGRATION =====
    
    /**
     * Get the cart table element for external wait operations
     * Required by WebStepDefinitions for table validation
     * @return WebElement representing the cart table
     */
    public WebElement getCartTableElement() {
        return cartTable;
    }
    
    /**
     * Get the place order button element for external operations
     * Required by WebStepDefinitions for clickability checks
     * @return WebElement representing the place order button
     */
    public WebElement getPlaceOrderButton() {
        return placeOrderButton;
    }
    
    /**
     * Get the checkout modal element for external operations
     * Required by WebStepDefinitions for modal validation
     * @return WebElement representing the checkout modal
     */
    public WebElement getCheckoutModal() {
        return checkoutModal;
    }
    
    /**
     * Get the total price element for external operations
     * Required by WebStepDefinitions for price validation
     * @return WebElement representing the total price
     */
    public WebElement getTotalElement() {
        return totalPrice;
    }
    
    /**
     * Get the purchase button element for external operations
     * Required by WebStepDefinitions for purchase operations
     * @return WebElement representing the purchase button
     */
    public WebElement getPurchaseButton() {
        return purchaseButton;
    }
    
    /**
     * Get the order confirmation element for external operations
     * Required by WebStepDefinitions for confirmation validation
     * @return WebElement representing the order confirmation
     */
    public WebElement getOrderConfirmationElement() {
        return orderConfirmationModal;
    }
    
    /**
     * Get the cart container element for external operations
     * @return WebElement representing the cart container
     */
    public WebElement getCartContainer() {
        return cartContainer;
    }
    
    /**
     * Get the navigation bar element for external operations
     * @return WebElement representing the navigation bar
     */
    public WebElement getNavigationBar() {
        return navigationBar;
    }
    
    /**
     * Get the empty cart message element for validation
     * @return WebElement representing the empty cart message
     */
    public WebElement getEmptyCartMessage() {
        return emptyCartMessage;
    }
    
    /**
     * Get the checkout name field for form operations
     * @return WebElement representing the name field
     */
    public WebElement getCheckoutNameField() {
        return checkoutNameField;
    }
    
    /**
     * Get comprehensive cart state information
     * @return Cart state description
     */
    public String getCartState() {
        StringBuilder state = new StringBuilder();
        state.append("CartPage State: ");
        state.append("page_loaded=").append(isPageLoaded());
        state.append(", cart_empty=").append(isCartEmpty());
        state.append(", product_count=").append(getProductCount());
        state.append(", total_price='").append(getTotalPrice()).append("'");
        state.append(", place_order_enabled=").append(isPlaceOrderButtonEnabled());
        state.append(", checkout_modal_visible=").append(isCheckoutModalVisible());
        
        return state.toString();
    }
}