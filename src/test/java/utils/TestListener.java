package utils;

import base.DriverFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        LogHelper.info("Test Started: " + result.getMethod().getMethodName());
        if (ConfigManager.getBoolean("video.on.fail")) {
            ScreenRecorderHelper.startRecording(result.getMethod().getMethodName());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogHelper.info("Test Passed: " + result.getMethod().getMethodName());
        if (ConfigManager.getBoolean("video.on.fail")) {
            ScreenRecorderHelper.stopRecording(result.getMethod().getMethodName(), false);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogHelper.error("Test Failed: " + result.getMethod().getMethodName(), result.getThrowable());
        String testName = result.getMethod().getMethodName();

        if (ConfigManager.getBoolean("video.on.fail")) {
            ScreenRecorderHelper.stopRecording(testName, true);
        }

        if (ConfigManager.getBoolean("screenshot.on.fail")) {
            if (DriverFactory.getDriver() != null) {
                ScreenRecorderHelper.captureScreenshot(DriverFactory.getDriver(), testName);
                ScreenRecorderHelper.saveScreenshotToFile(DriverFactory.getDriver(), testName);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogHelper.warn("Test Skipped: " + result.getMethod().getMethodName());
        if (ConfigManager.getBoolean("video.on.fail")) {
            ScreenRecorderHelper.stopRecording(result.getMethod().getMethodName(), false);
        }
    }

    @Override
    public void onStart(ITestContext context) {
        LogHelper.info("Test Suite Started: " + context.getName());
        
        // Tạo environment.properties cho Allure Report
        try {
            java.io.File allureResultsDir = new java.io.File("target/allure-results");
            if (!allureResultsDir.exists()) {
                allureResultsDir.mkdirs();
            }
            java.util.Properties props = new java.util.Properties();
            props.setProperty("Browser", ConfigManager.get("browser").toUpperCase());
            props.setProperty("Headless", ConfigManager.get("headless"));
            props.setProperty("OS", System.getProperty("os.name"));
            props.setProperty("Java Version", System.getProperty("java.version"));
            
            java.io.FileOutputStream out = new java.io.FileOutputStream("target/allure-results/environment.properties");
            props.store(out, "Allure Environment Setup");
            out.close();
        } catch (Exception e) {
            LogHelper.error("Failed to create Allure environment properties", e);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        LogHelper.info("Test Suite Finished: " + context.getName());
    }
}
