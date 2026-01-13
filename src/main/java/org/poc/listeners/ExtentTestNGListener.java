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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.LoggerConfig;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentTestNGListener implements ITestListener {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static ThreadLocal<Logger> logger = new ThreadLocal<>();

    // Initialize ExtentReports once
    static {
        String reportPath = System.getProperty("user.dir") + "\\test-output\\ExtentReport.html";
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

        // Sanitize test name for logger and file
        String safeTestName = testName.replaceAll("\\s+", "_");
        String logFile = "logs\\" + safeTestName + ".log";

        System.out.println("Initializing logger for: " + safeTestName + " -> " + logFile);

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("[%d{HH:mm:ss}] %-5level %logger{36} - %msg%n")
                .build();
        FileAppender appender = FileAppender.newBuilder()
                .setName(safeTestName + "Appender")
                .withFileName(logFile)
                .setLayout(layout)
                .setConfiguration(config)
                .build();
        appender.start();
        config.addAppender(appender);

        // Create a new LoggerConfig for this specific test logger
        LoggerConfig loggerConfig = new LoggerConfig(safeTestName, Level.DEBUG, false);
        loggerConfig.addAppender(appender, null, null);
        config.removeLogger(safeTestName); // Remove existing if any
        config.addLogger(safeTestName, loggerConfig);

        ctx.updateLoggers();

        logger.set(LogManager.getLogger(safeTestName));
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
        String safeTestName = testName.replaceAll("\\s+", "_");
        String logFile = "logs\\" + safeTestName + ".log";
        String absolutePath = System.getProperty("user.dir") + "\\" + logFile;

        try {
            String logContent = new String(Files.readAllBytes(Paths.get(absolutePath)));
            test.get().info(MarkupHelper.createCodeBlock(logContent));
        } catch (IOException e) {
            test.get().warning("Could not attach log file: " + absolutePath + " - " + e.getMessage());
        }
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static Logger getLogger() {
        return logger.get();
    }

}