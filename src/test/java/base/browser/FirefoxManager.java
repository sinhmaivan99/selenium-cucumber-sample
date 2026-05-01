package base.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import utils.ConfigManager;

public class FirefoxManager implements DriverManager {

    @Override
    public WebDriver createDriver() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        boolean isHeadless = ConfigManager.getBoolean("headless");
        if (isHeadless) {
            firefoxOptions.addArguments("-headless");
        }
        return new FirefoxDriver(firefoxOptions);
    }
}
