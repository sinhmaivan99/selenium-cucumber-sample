package base.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import utils.ConfigManager;

public class EdgeManager implements DriverManager {

    @Override
    public WebDriver createDriver() {
        EdgeOptions edgeOptions = new EdgeOptions();
        boolean isHeadless = ConfigManager.getBoolean("headless");
        if (isHeadless) {
            edgeOptions.addArguments("--headless=new");
        }
        edgeOptions.addArguments("--start-maximized");
        edgeOptions.addArguments("--disable-notifications");
        edgeOptions.addArguments("--remote-allow-origins=*");
        return new EdgeDriver(edgeOptions);
    }
}
