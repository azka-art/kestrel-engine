@api
Feature: User Hunt
  Background:
    Given Kestrel API client is armed with credentials

  @smoke
  Scenario: Get all users
    When I hunt for all users via GET /users
    Then the hunt should return status 200

  @smoke  
  Scenario: Get specific user
    Given a valid user ID exists in the system
    When I hunt for user details via GET /users/{id}
    Then the hunt should return status 200