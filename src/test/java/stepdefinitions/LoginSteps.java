package stepdefinitions;

import base.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.LoginPage;
import utils.DataHelper;

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
        getLoginPage().performLogin(DataHelper.get("login.validEmail"), DataHelper.get("login.validPassword"));
    }

    @When("I login with email {string} and password {string}")
    public void iLoginWithEmailAndPassword(String email, String password) {
        if (!email.isEmpty()) getLoginPage().enterEmail(email);
        if (!password.isEmpty()) getLoginPage().enterPassword(password);
        getLoginPage().clickLogin();
    }

    @Then("I should be redirected to the dashboard")
    public void iShouldBeRedirectedToTheDashboard() {
        Assert.assertTrue(getLoginPage().isDashboardDisplayed(), "Dashboard is not displayed");
        Assert.assertTrue(getLoginPage().getCurrentUrl().contains(DataHelper.get("urls.dashboard")), "URL does not contain dashboard");
    }

    @And("the dashboard title should contain {string}")
    public void theDashboardTitleShouldContain(String title) {
        Assert.assertTrue(getLoginPage().getPageTitle().contains(title), "Title does not contain: " + title);
    }

    @Then("I should see an error message or stay on the login page")
    public void iShouldSeeAnErrorMessageOrStayOnTheLoginPage() {
        // If the URL is still the login page, or an error message is displayed
        boolean isErrorDisplayed = getLoginPage().isErrorMessageDisplayed();
        boolean isStillOnLoginPage = getLoginPage().getCurrentUrl().contains("authentication");
        
        Assert.assertTrue(isErrorDisplayed || isStillOnLoginPage, "Expected to see an error message or stay on the login page");
    }
}
