@web @e2e
Feature: Complete Checkout Mission - End-to-End Shopping Flow
  As a Kestrel Engine operator
  I want to execute complete shopping mission
  So that I can validate entire e-commerce workflow

  Background:
    Given Kestrel agent is positioned at Demoblaze homepage

  @critical @e2e
  Scenario: Complete shopping mission - Full checkout flow
    Given I am on the homepage
    When I navigate to sign up
    And I register with credentials "kestrel_shopper" and "mission123"
    Then registration should be successful
    
    When I login with credentials "kestrel_shopper" and "mission123"
    Then I should be successfully authenticated
    
    When I browse to "Phones" category
    And I select product "Samsung galaxy s6"
    And I add product to cart
    Then product should be added to cart
    
    When I browse to "Laptops" category
    And I select product "Sony vaio i5"
    And I add product to cart
    Then second product should be added to cart
    
    When I navigate to cart
    Then I should see both products in cart
    And cart total should be calculated
    
    When I proceed to checkout
    And I fill checkout form with mission details
      | name           | country   | city    | card             | month | year |
      | Kestrel Agent  | Indonesia | Jakarta | 4111111111111111 | 12    | 2025 |
    And I complete the purchase
    Then order should be confirmed
    And confirmation message should appear
    And order ID should be generated

  @e2e @positive
  Scenario: Guest checkout with product selection
    Given I am browsing as guest user
    When I select product "Nokia lumia 1520"
    And I add product to cart
    And I proceed to checkout as guest
    And I fill guest checkout form
      | name         | country   | city      | card             |
      | Guest Buyer  | Indonesia | Surabaya  | 5555555555554444 |
    And I complete guest purchase
    Then guest order should be confirmed
    And guest confirmation should appear

  @e2e @negative
  Scenario: Incomplete checkout form validation
    Given I have products in cart ready for checkout
    When I proceed to checkout
    And I attempt purchase with incomplete form
      | name    | country | city | card |
      | Kestrel |         |      |      |
    Then form validation should trigger
    And required field errors should appear
    And purchase should be prevented
    And form should remain on checkout page

  @e2e @boundary
  Scenario: Checkout with invalid payment details
    Given I have completed shopping cart setup
    When I proceed to checkout
    And I fill form with invalid payment details
      | name           | country   | city    | card        |
      | Kestrel Agent  | Indonesia | Jakarta | 1234567890  |
    And I attempt purchase
    Then payment validation should fail
    And error message should indicate invalid card
    And checkout should remain on payment page