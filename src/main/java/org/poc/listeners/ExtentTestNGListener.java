package org.poc.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.Map;

public class ExtentTestNGListener implements ITestListener {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static ThreadLocal<Logger> logger = new ThreadLocal<>();


    // Initialize ExtentReports once
    static {
        String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport.html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("Automation Report");
        spark.config().setReportName("Selenium + TestNG Results");
        spark.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        // If YAML provided a name, override it
        Object[] params = result.getParameters();
        if (params.length > 0 && params[0] instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) params[0];
            testName = (String) data.getOrDefault("name", testName);
        }

        test.set(extent.createTest(testName));

        // Create per-test log file
        String logFile = "logs/" + testName.replaceAll("\\s+", "_") + ".log";
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("[%d{HH:mm:ss}] %-5level %logger{36} - %msg%n")
                .build();
        FileAppender appender = FileAppender.newBuilder()
                .setName(testName + "Appender")
                .withFileName(logFile)
                .setLayout(layout)
                .setConfiguration(config)
                .build();
        appender.start();
        config.addAppender(appender);
        config.getRootLogger().addAppender(appender, null, null);
        ctx.updateLoggers();

        logger.set(LogManager.getLogger(testName));

    }

    @Override
    public void onTestSuccess(ITestResult result) {
        attachLogToReport(result);
        test.get().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachLogToReport(result);
        test.get().fail(result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    private void attachLogToReport(ITestResult result) {
        String testName = test.get().getModel().getName();
        String logFile = "logs/" + testName.replaceAll("\\s+", "_") + ".log";
        test.get().addScreenCaptureFromPath(logFile); // attaches log file as artifact
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static Logger getLogger() {
        return logger.get();
    }


}