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
        this.webOp.select(getElement("categoryDropdown"), (String) data.get("category"));
        this.webOp.click(getElement("categoryGoButton"));
        // WebElement section = getElement("offerSection");
        // webOp.click(section.findElement(By.cssSelector("a[href='https://www.asos.com/women/']")));
        this.webOp.scrollToElement(getElement("asosStoreLink"));
        this.webOp.navigateTo(getElement("asosStoreLink").getAttribute("href"));

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        return new AsosStoreHomePage(driver, webOp, mainWindowHandle);
    }
}
