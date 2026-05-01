package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Automatically retries failed tests up to a configurable number of times.
 * Helps handle flaky tests caused by network latency or server lag.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY = 1;

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess() && retryCount < MAX_RETRY) {
            retryCount++;
            LogHelper.warn("Retrying test '" + result.getName()
                    + "' [" + getStatusName(result.getStatus()) + "] — attempt "
                    + retryCount + "/" + MAX_RETRY);
            return true;
        }
        return false;
    }

    private String getStatusName(int status) {
        return switch (status) {
            case ITestResult.SUCCESS -> "SUCCESS";
            case ITestResult.FAILURE -> "FAILURE";
            case ITestResult.SKIP -> "SKIP";
            default -> "UNKNOWN";
        };
    }
}
