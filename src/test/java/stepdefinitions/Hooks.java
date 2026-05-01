package stepdefinitions;

import base.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.LogHelper;

/**
 * Cucumber lifecycle hooks for WebDriver management.
 * Handles driver initialization and cleanup, plus automatic
 * screenshot capture on scenario failure.
 */
public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        LogHelper.info("▶ Cucumber Scenario: " + scenario.getName());
        DriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            LogHelper.error("❌ Scenario Failed: " + scenario.getName());
            final byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot on failure");
        }
        DriverFactory.quitDriver();
    }
}
