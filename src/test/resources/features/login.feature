Feature: Login to CRM Application
  As a user of the CRM application
  I want to be able to log in with valid credentials
  So that I can access my dashboard

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I login with valid email and password
    Then I should be redirected to the dashboard
    And the dashboard title should contain "Dashboard"

  Scenario Outline: Failed login with invalid credentials
    Given I am on the login page
    When I login with email "<email>" and password "<password>"
    Then I should see an error message or stay on the login page

    Examples:
      | email                | password |
      | invalid@example.com  | 123456   |
      | admin@example.com    | wrong    |
      | invalid@example.com  | wrong    |
      |                      | 123456   |
      | admin@example.com    |          |
      |                      |          |
