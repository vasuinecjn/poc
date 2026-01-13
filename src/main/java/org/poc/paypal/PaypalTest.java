package org.poc.paypal;

import org.poc.Application;
import org.poc.SeleniumTestCase;

import java.util.ArrayList;
import java.util.Map;

public class PaypalTest extends SeleniumTestCase {

    @Override
    public void exec(Map<String, Object> testCase) {
        Application application = new Application(getDriver(), getWebOperations(), getIdleDetector());
        application
                .launchPayPalApplication()
                .hoverOnMenuItems((ArrayList<String>) testCase.get("hover"));
        getWebOperations().log().info("Title: " + getDriver().getTitle());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTestFileName() {
        return "hoverTests.yaml";
    }
}
