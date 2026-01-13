package org.poc.pages;

import org.openqa.selenium.WebDriver;
import org.poc.WebOperations;

import java.util.Map;

public class ShopAndBuyPage extends BasePage {

    public ShopAndBuyPage(WebDriver driver, WebOperations webOp) {
        super(driver, webOp);
    }

    public AsosStoreHomePage selectStoreByCategory(Map<String, Object> data) {
        String mainWindowHandle = driver.getWindowHandle();
        webOp.select(getLocator("categoryDropdown"), (String) data.get("category"));
        webOp.click(getLocator("categoryGoButton"));
        // WebElement section = getElement("offerSection");
        // webOp.click(section.findElement(By.cssSelector("a[href='https://www.asos.com/women/']")));
        webOp.scrollToElement(getLocator("asosStoreLink"));
        webOp.navigateTo(webOp.getElement(getLocator("asosStoreLink")).getAttribute("href"));

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        return new AsosStoreHomePage(driver, webOp, mainWindowHandle);
    }
}
