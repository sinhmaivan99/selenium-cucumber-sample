package base.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.ConfigManager;

public class ChromeManager implements DriverManager {

    @Override
    public WebDriver createDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        boolean isHeadless = ConfigManager.getBoolean("headless");
        if (isHeadless) {
            chromeOptions.addArguments("--headless=new");
        }
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(chromeOptions);
    }
}
