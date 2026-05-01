package utils;

import base.DriverFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * TestNG listener for automated logging, screenshot capture, and video management.
 * Separates reporting concerns from test lifecycle (BaseTest).
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        LogHelper.info("▶ Test Started: " + testName);

        if (ConfigManager.getBoolean("video.on.fail")) {
            ScreenRecorderHelper.startRecording(testName);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        LogHelper.info("✅ Test Passed: " + testName);

        if (ConfigManager.getBoolean("video.on.fail")) {
            ScreenRecorderHelper.stopRecording(testName, false);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        LogHelper.error("❌ Test Failed: " + testName, result.getThrowable());

        if (ConfigManager.getBoolean("video.on.fail")) {
            ScreenRecorderHelper.stopRecording(testName, true);
        }

        if (ConfigManager.getBoolean("screenshot.on.fail")) {
            try {
                ScreenRecorderHelper.captureScreenshot(DriverFactory.getDriver(), testName);
                ScreenRecorderHelper.saveScreenshotToFile(DriverFactory.getDriver(), testName);
            } catch (Exception e) {
                LogHelper.warn("Could not capture screenshot for: " + testName);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        LogHelper.warn("⏭ Test Skipped: " + testName);

        if (ConfigManager.getBoolean("video.on.fail")) {
            ScreenRecorderHelper.stopRecording(testName, false);
        }
    }

    @Override
    public void onStart(ITestContext context) {
        LogHelper.info("═══ Suite Started: " + context.getName() + " ═══");
        writeAllureEnvironment();
    }

    @Override
    public void onFinish(ITestContext context) {
        LogHelper.info("═══ Suite Finished: " + context.getName() + " ═══");
    }

    // ─── Private helpers ────────────────────────────────────────────────

    /**
     * Writes environment.properties for Allure Report display.
     */
    private void writeAllureEnvironment() {
        File allureDir = new File("target/allure-results");
        if (!allureDir.exists()) {
            allureDir.mkdirs();
        }

        Properties props = new Properties();
        props.setProperty("Browser", ConfigManager.getOrDefault("browser", "chrome").toUpperCase());
        props.setProperty("Headless", ConfigManager.getOrDefault("headless", "false"));
        props.setProperty("OS", System.getProperty("os.name"));
        props.setProperty("Java Version", System.getProperty("java.version"));

        try (FileOutputStream out = new FileOutputStream(
                new File(allureDir, "environment.properties"))) {
            props.store(out, "Allure Environment");
        } catch (IOException e) {
            LogHelper.error("Failed to write Allure environment properties", e);
        }
    }
}
