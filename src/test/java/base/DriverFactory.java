package base;

import base.browser.ChromeManager;
import base.browser.DriverManager;
import base.browser.EdgeManager;
import base.browser.FirefoxManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigManager;
import utils.LogHelper;

import java.time.Duration;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            String browser = ConfigManager.get("browser").toLowerCase();
            LogHelper.info("Initializing browser: " + browser);

            DriverManager driverManager;
            switch (browser) {
                case "firefox":
                    driverManager = new FirefoxManager();
                    break;
                case "edge":
                    driverManager = new EdgeManager();
                    break;
                case "chrome":
                default:
                    driverManager = new ChromeManager();
                    break;
            }

            WebDriver driver = driverManager.createDriver();

            int implicitWait = ConfigManager.getInt("implicit.wait");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

            if (!browser.equals("firefox")) {
                driver.manage().window().maximize();
            }

            driverThreadLocal.set(driver);

            int explicitWait = ConfigManager.getInt("explicit.wait");
            waitThreadLocal.set(new WebDriverWait(driver, Duration.ofSeconds(explicitWait)));
        }
        return driverThreadLocal.get();
    }

    public static WebDriverWait getWait() {
        if (waitThreadLocal.get() == null) {
            getDriver();
        }
        return waitThreadLocal.get();
    }

    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
            waitThreadLocal.remove();
            LogHelper.info("Driver quit successfully");
        }
    }
}
