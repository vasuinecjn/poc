package org.poc.paypal;

import org.poc.Application;
import org.poc.SeleniumTestCase;

import java.util.Map;

public class ShopAndBuyTests extends SeleniumTestCase {
    @Override
    public void exec(Map<String, Object> testCase) {
        Application application = new Application(getDriver(), getWebOperations(), getIdleDetector());
        application.launchPayPalApplication()
                .navigateToShopAndBuyPage()
                .selectStoreByCategory(testCase)
                .searchAndAddProductToBag(testCase);
    }

    @Override
    public String getTestFileName() {
        return "shopAndBuyTests.yaml";
    }
}
