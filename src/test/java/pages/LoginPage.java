package pages;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends BasePage {

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(id = "remember")
    private WebElement rememberMeCheckbox;

    @FindBy(css = "div.alert-danger")
    private WebElement errorMessage;

    private final By dashboardHeading = By.xpath("//span[normalize-space()='Dashboard']");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    @Step("Mở trang Login")
    public LoginPage open() {
        navigateTo(utils.DataHelper.get("urls.login"));
        return this;
    }

    @Step("Nhập email: {email}")
    public LoginPage enterEmail(String email) {
        setText(emailInput, email);
        return this;
    }

    @Step("Nhập password")
    public LoginPage enterPassword(String password) {
        setText(passwordInput, password);
        return this;
    }

    @Step("Click nút Login")
    public LoginPage clickLogin() {
        click(loginButton);
        return this;
    }

    @Step("Tick checkbox Remember Me")
    public LoginPage tickRememberMe() {
        setCheckbox(rememberMeCheckbox, true);
        return this;
    }

    @Step("Đăng nhập với email: {email}")
    public LoginPage performLogin(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
        return this;
    }

    @Step("Lấy thông báo lỗi")
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    @Step("Kiểm tra thông báo lỗi hiển thị")
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    @Step("Kiểm tra Dashboard hiển thị")
    public boolean isDashboardDisplayed() {
        return isDisplayed(dashboardHeading);
    }

    @Step("Kiểm tra trường Password có ẩn ký tự")
    public boolean isPasswordMasked() {
        return "password".equals(getAttribute(passwordInput, "type"));
    }

    @Step("Kiểm tra nút Login hiển thị")
    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton);
    }
}
