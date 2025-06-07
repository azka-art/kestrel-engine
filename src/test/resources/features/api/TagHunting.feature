@api
Feature: Posts Hunt
  Background:
    Given Kestrel API client is armed with credentials

  @smoke
  Scenario: Get all posts
    When I hunt for all available tags via GET /tag
    Then the hunt should return status 200

  @smoke
  Scenario: Get specific post  
    Given a valid tag exists in the system
    When I hunt for posts by tag via GET /post/tag/{tag}
    Then the hunt should return status 200