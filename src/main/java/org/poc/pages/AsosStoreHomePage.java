package org.poc.pages;

import org.openqa.selenium.WebDriver;
import org.poc.WebOperations;

import java.util.Map;

public class AsosStoreHomePage extends BasePage {

    private String mainWindowHandle;

    public AsosStoreHomePage(WebDriver driver, WebOperations webOp, String mainWindowHandle) {
        super(driver, webOp);
        this.mainWindowHandle = mainWindowHandle;
    }

    public AsosStoreHomePage searchAndAddProductToBag(Map<String, Object> data) {
        webOp.click(getLocator("searchField"));
        webOp.fill(getLocator("searchField"), (String) data.get("searchProduct"));
        webOp.click(getLocator("searchButton"));
        // this.webOp.waitUntilVisible(getBy("searchResults"), 10000);
        // this.webOp.click(getElement("searchResults"));
        webOp.waitUntilVisible(getLocator("selectProductByIndex", (String) data.get("selectProductByIndex")));
        webOp.click(getLocator("selectProductByIndex", (String) data.get("selectProductByIndex")));
        webOp.click(getLocator("addToBagButton"));
        return this;
    }
}
