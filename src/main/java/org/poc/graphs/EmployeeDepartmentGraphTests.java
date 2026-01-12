package org.poc.graphs;

import org.poc.Application;
import org.poc.SeleniumTestCase;

import java.util.Map;

public class EmployeeDepartmentGraphTests extends SeleniumTestCase {
    @Override
    public void exec(Map<String, Object> testCase) {
        Application application = new Application(getDriver(), getWebOperations(), getIdleDetector());
        application.launchEmpGwtApplication()
                .showAndHideDeptGraphs(testCase);
    }

    @Override
    public String getTestFileName() {
        return "employeeDepartmentGrowthTests.yaml";
    }
}
