package stepdefinitions;

import base.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.LoginPage;
import utils.DataHelper;

/**
 * Cucumber step definitions for Login feature.
 * Uses lazy initialization for LoginPage to ensure DriverFactory is ready.
 */
public class LoginSteps {

    private LoginPage loginPage;

    private LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(DriverFactory.getDriver(), DriverFactory.getWait());
        }
        return loginPage;
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        getLoginPage().open();
    }

    @When("I login with valid email and password")
    public void iLoginWithValidEmailAndPassword() {
        getLoginPage().performLogin(
                DataHelper.get("login.validEmail"),
                DataHelper.get("login.validPassword"));
    }

    @When("I login with email {string} and password {string}")
    public void iLoginWithEmailAndPassword(String email, String password) {
        LoginPage page = getLoginPage();
        if (!email.isEmpty()) page.enterEmail(email);
        if (!password.isEmpty()) page.enterPassword(password);
        page.clickLogin();
    }

    @Then("I should be redirected to the dashboard")
    public void iShouldBeRedirectedToTheDashboard() {
        LoginPage page = getLoginPage();
        Assert.assertTrue(page.isDashboardDisplayed(),
                "Dashboard is not displayed");
        Assert.assertTrue(page.getCurrentUrl().contains(DataHelper.get("urls.dashboard")),
                "URL does not contain dashboard path");
    }

    @And("the dashboard title should contain {string}")
    public void theDashboardTitleShouldContain(String title) {
        Assert.assertTrue(getLoginPage().getPageTitle().contains(title),
                "Title does not contain: " + title);
    }

    @Then("I should see an error message or stay on the login page")
    public void iShouldSeeAnErrorMessageOrStayOnTheLoginPage() {
        LoginPage page = getLoginPage();
        boolean isErrorDisplayed = page.isErrorMessageDisplayed();
        boolean isStillOnLoginPage = page.getCurrentUrl().contains("authentication");

        Assert.assertTrue(isErrorDisplayed || isStillOnLoginPage,
                "Expected to see an error message or stay on the login page");
    }
}
