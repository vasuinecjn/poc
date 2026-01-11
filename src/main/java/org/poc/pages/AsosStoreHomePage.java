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
        this.webOp.click(getElement("searchField"));
        this.webOp.fill(getElement("searchField"), (String) data.get("searchProduct"));
        this.webOp.click(getElement("searchButton"));
        // this.webOp.waitUntilVisible(getBy("searchResults"), 10000);
        // this.webOp.click(getElement("searchResults"));
        this.webOp.waitUntilVisible(getBy("selectProductByIndex", (String) data.get("selectProductByIndex")), 10000);
        this.webOp.click(getElement("selectProductByIndex", (String) data.get("selectProductByIndex")));
        this.webOp.click(getElement("addToBagButton"));
        return this;
    }
}
