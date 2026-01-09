package org.poc.listeners;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.*;

public class ExtentTestNGListener implements ITestListener {
    private static ExtentReports extent = new ExtentReports();
    private static ExtentTest test;

    static {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
        extent.attachReporter(spark);
    }

    @Override
    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.fail(result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}