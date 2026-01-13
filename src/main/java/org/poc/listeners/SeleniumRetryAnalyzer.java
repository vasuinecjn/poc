package org.poc.listeners;

import org.openqa.selenium.devtools.v141.overlay.model.IsolatedElementHighlightConfig;
import org.openqa.selenium.devtools.v141.overlay.model.SourceOrderConfig;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class SeleniumRetryAnalyzer implements IRetryAnalyzer {

    private int attempt = 0;
    private final int maxRetryCount;

    public SeleniumRetryAnalyzer() {
        this(2);
    }
    public SeleniumRetryAnalyzer(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < maxRetryCount) {
            attempt++;
            System.out.println("[Retry] " + result.getName() + " attempt " + attempt + "/" + maxRetryCount);
            return true;
        }
        return false;
    }
}
