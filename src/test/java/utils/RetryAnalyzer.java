package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    
    private int count = 0;
    private static final int maxTry = 1; // Số lần retry tối đa (mặc định 1)

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) { 
            if (count < maxTry) {
                count++;
                iTestResult.setStatus(ITestResult.FAILURE);
                LogHelper.warn("Retrying test " + iTestResult.getName() + " with status "
                        + getResultStatusName(iTestResult.getStatus()) + " for the " + count + " time(s).");
                return true; 
            }
        }
        return false;
    }

    private String getResultStatusName(int status) {
        String resultName = null;
        if (status == 1) resultName = "SUCCESS";
        if (status == 2) resultName = "FAILURE";
        if (status == 3) resultName = "SKIP";
        return resultName;
    }
}
