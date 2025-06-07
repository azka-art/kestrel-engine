@api
Feature: Posts Hunt - JSONPlaceholder Content Management
  As a Kestrel Engine operator
  I want to test posts and content operations
  So that I can validate content management API functionality

  Background:
    Given Kestrel API client is armed with credentials

  @smoke @positive
  Scenario: Content reconnaissance - Get all posts
    When I hunt for all available tags via GET /tag
    Then the hunt should return status 200
    And the intelligence should contain tag collection
    And each tag should be properly formatted
    And tag list should not be empty

  @positive
  Scenario: Targeted content hunt - Get specific post
    Given a valid tag exists in the system
    When I hunt for posts by tag via GET /post/tag/{tag}
    Then the hunt should return status 200
    And response should contain relevant posts
    And all posts should be tagged correctly
    And post metadata should be complete

  @positive
  Scenario: Multi-tag intelligence gathering
    Given multiple posts exist with different tags
    When I hunt for posts using popular tags
      | tag      | expectedMinPosts |
      | general  | 1                |
      | tech     | 1                |
      | business | 1                |
    Then each hunt should return status 200
    And filtered results should meet criteria
    And content relevance should be maintained

  @positive
  Scenario: User-specific content hunt
    Given a valid user ID exists in the system
    When I hunt for posts by user via GET posts endpoint
    Then the hunt should return status 200
    And response should contain user-specific posts
    And all posts should belong to correct user
    And user association should be maintained

  @performance
  Scenario: Rapid content retrieval
    When I execute rapid successive posts retrieval
    Then all requests should complete successfully
    And response times should be consistent
    And data integrity should be maintained

  @negative
  Scenario: Hunt for non-existent content
    When I hunt for posts with invalid tag "non_existent_tag_99999"
    Then the hunt should return appropriate status
    And response should handle gracefully
    And error message should be informative

  @boundary
  Scenario: Edge case content testing
    When I hunt for posts with edge case IDs
      | postId | expectedBehavior |
      | 1      | success          |
      | 100    | success          |
      | 999    | not_found        |
      | 0      | not_found        |
    Then boundary conditions should be handled correctly
    And appropriate status codes should be returned
    And error handling should be consistent

  @security @negative
  Scenario: Unauthorized content access attempt
    Given Kestrel API client lacks proper credentials
    When I attempt tag reconnaissance via GET /tag
    Then response should indicate forbidden access
    And no tag information should be exposed
    And security measures should be enforced