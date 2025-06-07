@web
Feature: Demoblaze Shopping Cart Operations
  As a Kestrel Engine operator
  I want to test shopping cart functionality
  So that I can validate e-commerce operations

  Background:
    Given Kestrel agent is positioned at Demoblaze homepage
    And I am authenticated as "testuser"

  @smoke @positive
  Scenario: Product reconnaissance and acquisition
    Given I am browsing the product catalog
    When I select a product "Samsung galaxy s6"
    And I add the product to cart
    Then product should be added successfully
    And cart counter should be updated
    And product should appear in cart

  @positive
  Scenario: Multi-product cart assembly
    Given I am browsing the product catalog
    When I add multiple products to cart
      | productName       | category |
      | Samsung galaxy s6 | Phones   |
      | Sony vaio i5      | Laptops  |
    Then all products should be in cart
    And cart total should be calculated correctly
    And product details should be preserved

  @positive
  Scenario: Cart modification operations
    Given I have products in my cart
      | productName       | quantity |
      | Samsung galaxy s6 | 1        |
      | Sony vaio i5      | 1        |
    When I remove "Sony vaio i5" from cart
    Then product should be removed successfully
    And cart total should be recalculated
    And remaining products should be intact

  @positive
  Scenario: Product details verification
    Given I am viewing product "Samsung galaxy s6"
    When I examine product specifications
    Then product price should be displayed
    And product description should be available
    And product images should be loaded
    And "Add to cart" button should be functional

  @negative
  Scenario: Cart operations without authentication
    Given I am not authenticated
    When I attempt to add product to cart
    Then authentication prompt should appear
    And product should not be added
    And cart should remain empty

  @negative
  Scenario: Empty cart checkout attempt
    Given my cart is empty
    When I attempt to proceed to checkout
    Then empty cart warning should appear
    And checkout should be prevented
    And cart page should remain visible