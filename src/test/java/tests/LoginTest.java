package tests;

import base.BaseTest;
import base.DriverFactory;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.DataHelper;

@Epic("CRM Application")
@Feature("Login")
public class LoginTest extends BaseTest {

    private LoginPage getLoginPage() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver(), DriverFactory.getWait());
        loginPage.open();
        return loginPage;
    }

    @Test(priority = 1, description = "TC01 - Đăng nhập thành công với thông tin hợp lệ")
    public void testLoginSuccess() {
        LoginPage loginPage = getLoginPage();
        loginPage.performLogin(DataHelper.get("login.validEmail"), DataHelper.get("login.validPassword"));

        Assert.assertTrue(loginPage.isDashboardDisplayed(),
                "Dashboard không hiển thị sau khi đăng nhập thành công");
        Assert.assertTrue(loginPage.getCurrentUrl().contains(DataHelper.get("urls.dashboard")),
                "URL không chuyển hướng đến Dashboard");
    }

    @Test(priority = 2, description = "TC02 - Kiểm tra title trang Dashboard sau login thành công")
    public void testDashboardTitleAfterLogin() {
        LoginPage loginPage = getLoginPage();
        loginPage.performLogin(DataHelper.get("login.validEmail"), DataHelper.get("login.validPassword"));

        Assert.assertTrue(loginPage.isDashboardDisplayed(), "Dashboard không hiển thị");
        Assert.assertTrue(loginPage.getPageTitle().contains("Dashboard"),
                "Title trang không chứa 'Dashboard'");
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return new Object[][] {
            // Email sai, Pass đúng
            {DataHelper.get("login.invalidEmail"), DataHelper.get("login.validPassword")},
            // Email đúng, Pass sai
            {DataHelper.get("login.validEmail"), DataHelper.get("login.invalidPassword")},
            // Email sai, Pass sai
            {DataHelper.get("login.invalidEmail"), DataHelper.get("login.invalidPassword")},
            // Trống Email
            {"", DataHelper.get("login.validPassword")},
            // Trống Pass
            {DataHelper.get("login.validEmail"), ""},
            // Trống cả 2
            {"", ""}
        };
    }

    @Test(priority = 3, description = "TC03-TC08 - Kiểm tra đăng nhập thất bại (DDT)", dataProvider = "invalidLoginData")
    public void testLoginFailure(String email, String password) {
        LoginPage loginPage = getLoginPage();
        
        if (!email.isEmpty()) loginPage.enterEmail(email);
        if (!password.isEmpty()) loginPage.enterPassword(password);
        
        loginPage.clickLogin();

        // Nếu có trường trống, thường URL không đổi hoặc có validation HTML5 (tùy app). 
        // Trong trường hợp này, ta check chung xem có thông báo lỗi hiển thị không hoặc URL có giữ nguyên không
        if (email.isEmpty() || password.isEmpty()) {
            Assert.assertTrue(loginPage.getCurrentUrl().contains("authentication"),
                    "Trang không được giữ nguyên khi để trống trường bắt buộc");
        } else {
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                    "Thông báo lỗi không hiển thị khi nhập sai thông tin");
        }
    }

    @Test(priority = 9, description = "TC09 - Kiểm tra trường Password ẩn ký tự")
    public void testPasswordFieldIsMasked() {
        LoginPage loginPage = getLoginPage();
        Assert.assertTrue(loginPage.isPasswordMasked(),
                "Trường Password không ẩn ký tự (type != password)");
    }

    @Test(priority = 10, description = "TC10 - Kiểm tra nút Login hiển thị trên trang")
    public void testLoginButtonIsDisplayed() {
        LoginPage loginPage = getLoginPage();
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), "Nút Login không hiển thị trên trang");
    }

    @Test(priority = 11, description = "TC11 - Kiểm tra title trang Login")
    public void testLoginPageTitle() {
        LoginPage loginPage = getLoginPage();
        String title = loginPage.getPageTitle();
        Assert.assertFalse(title.isEmpty(), "Title trang Login bị trống");
        Assert.assertTrue(title.contains("Login"),
                "Title trang không chứa từ 'Login'. Actual: " + title);
    }

    @Test(priority = 12, description = "TC12 - Kiểm tra SQL Injection cơ bản")
    public void testSqlInjection() {
        LoginPage loginPage = getLoginPage();
        loginPage.performLogin(DataHelper.get("security.sqlInjectionPayload"), DataHelper.get("login.validPassword"));

        Assert.assertFalse(loginPage.isDashboardDisplayed(),
                "Hệ thống bị tấn công SQL Injection - Dashboard hiển thị bất hợp pháp");
    }

    @Test(priority = 13, description = "TC13 - Kiểm tra XSS cơ bản")
    public void testXssAttack() {
        LoginPage loginPage = getLoginPage();
        loginPage.performLogin(DataHelper.get("security.xssPayload"), DataHelper.get("login.validPassword"));

        Assert.assertFalse(loginPage.isDashboardDisplayed(),
                "Hệ thống bị tấn công XSS - Dashboard hiển thị bất hợp pháp");
    }
}
