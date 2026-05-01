package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for all TestNG test classes.
 * Handles WebDriver lifecycle (setup/teardown) per test method.
 * Reporting concerns (screenshots, videos) are handled by TestListener.
 */
public class BaseTest {

    @BeforeMethod
    public void setUp() {
        DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }
}
