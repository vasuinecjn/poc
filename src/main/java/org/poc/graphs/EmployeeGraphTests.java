package org.poc.graphs;

import org.poc.Application;
import org.poc.SeleniumTestCase;

import java.util.Map;

public class EmployeeGraphTests extends SeleniumTestCase {
    @Override
    public void exec(Map<String, Object> testCase) {
        Application application = new Application(getDriver(), getWebOperations(), getIdleDetector());
        application.launchEmpGwtApplication()
                .validateEmpGrowthPerYear();
    }

    @Override
    public String getTestFileName() {
        return "employeeGrowthTests.yaml";
    }
}
