package org.poc.graphs;

import org.poc.Application;
import org.poc.SeleniumTestCase;

import java.util.Map;

public class MonthlyRevenueTests extends SeleniumTestCase {
    @Override
    public void exec(Map<String, Object> testCase) {
        Application application = new Application(getDriver(), getWebOperations(), getIdleDetector());
        application.launchEmpGwtApplication()
                .logRevenueAndCompare(testCase);
    }

    @Override
    public String getTestFileName() {
        return "monthlyRevenueTests.yaml";
    }
}
