package tests;

import base.BaseTest;
import base.DriverFactory;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.DataHelper;

/**
 * TestNG test class for CRM Login functionality.
 * Uses Data-Driven Testing (DDT) via @DataProvider for negative cases.
 */
@Epic("CRM Application")
@Feature("Login")
public class LoginTest extends BaseTest {

    private LoginPage getLoginPage() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver(), DriverFactory.getWait());
        loginPage.open();
        return loginPage;
    }

    // ═══════════════════════ POSITIVE TESTS ═══════════════════════

    @Test(priority = 1, description = "TC01 — Login successfully with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void testLoginSuccess() {
        LoginPage loginPage = getLoginPage();
        loginPage.performLogin(
                DataHelper.get("login.validEmail"),
                DataHelper.get("login.validPassword"));

        Assert.assertTrue(loginPage.isDashboardDisplayed(),
                "Dashboard should be displayed after successful login");
        Assert.assertTrue(loginPage.getCurrentUrl().contains(DataHelper.get("urls.dashboard")),
                "URL should redirect to Dashboard");
    }

    @Test(priority = 2, description = "TC02 — Verify Dashboard title after login")
    @Severity(SeverityLevel.NORMAL)
    public void testDashboardTitleAfterLogin() {
        LoginPage loginPage = getLoginPage();
        loginPage.performLogin(
                DataHelper.get("login.validEmail"),
                DataHelper.get("login.validPassword"));

        Assert.assertTrue(loginPage.isDashboardDisplayed(),
                "Dashboard should be displayed");
        Assert.assertTrue(loginPage.getPageTitle().contains("Dashboard"),
                "Page title should contain 'Dashboard'");
    }

    // ═══════════════════════ NEGATIVE TESTS (DDT) ═══════════════════════

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return new Object[][]{
                {DataHelper.get("login.invalidEmail"), DataHelper.get("login.validPassword")},
                {DataHelper.get("login.validEmail"), DataHelper.get("login.invalidPassword")},
                {DataHelper.get("login.invalidEmail"), DataHelper.get("login.invalidPassword")},
                {"", DataHelper.get("login.validPassword")},
                {DataHelper.get("login.validEmail"), ""},
                {"", ""}
        };
    }

    @Test(priority = 3, description = "TC03-TC08 — Login failure with invalid credentials (DDT)",
            dataProvider = "invalidLoginData")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginFailure(String email, String password) {
        LoginPage loginPage = getLoginPage();

        if (!email.isEmpty()) loginPage.enterEmail(email);
        if (!password.isEmpty()) loginPage.enterPassword(password);
        loginPage.clickLogin();

        if (email.isEmpty() || password.isEmpty()) {
            Assert.assertTrue(loginPage.getCurrentUrl().contains("authentication"),
                    "Should stay on login page when required fields are empty");
        } else {
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                    "Error message should be displayed for invalid credentials");
        }
    }

    // ═══════════════════════ UI TESTS ═══════════════════════

    @Test(priority = 9, description = "TC09 — Password field should be masked")
    @Severity(SeverityLevel.MINOR)
    public void testPasswordFieldIsMasked() {
        LoginPage loginPage = getLoginPage();
        Assert.assertTrue(loginPage.isPasswordMasked(),
                "Password field type should be 'password'");
    }

    @Test(priority = 10, description = "TC10 — Login button should be visible")
    @Severity(SeverityLevel.MINOR)
    public void testLoginButtonIsDisplayed() {
        LoginPage loginPage = getLoginPage();
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button should be displayed on the page");
    }

    @Test(priority = 11, description = "TC11 — Login page title should not be empty")
    @Severity(SeverityLevel.MINOR)
    public void testLoginPageTitle() {
        LoginPage loginPage = getLoginPage();
        String title = loginPage.getPageTitle();

        Assert.assertFalse(title.isEmpty(), "Login page title should not be empty");
        Assert.assertTrue(title.contains("Login"),
                "Page title should contain 'Login'. Actual: " + title);
    }

    // ═══════════════════════ SECURITY TESTS ═══════════════════════

    @Test(priority = 12, description = "TC12 — SQL Injection should not bypass login")
    @Severity(SeverityLevel.BLOCKER)
    public void testSqlInjection() {
        LoginPage loginPage = getLoginPage();
        loginPage.performLogin(
                DataHelper.get("security.sqlInjectionPayload"),
                DataHelper.get("login.validPassword"));

        Assert.assertFalse(loginPage.isDashboardDisplayed(),
                "SQL Injection should not grant access to Dashboard");
    }

    @Test(priority = 13, description = "TC13 — XSS payload should not bypass login")
    @Severity(SeverityLevel.BLOCKER)
    public void testXssAttack() {
        LoginPage loginPage = getLoginPage();
        loginPage.performLogin(
                DataHelper.get("security.xssPayload"),
                DataHelper.get("login.validPassword"));

        Assert.assertFalse(loginPage.isDashboardDisplayed(),
                "XSS payload should not grant access to Dashboard");
    }
}
