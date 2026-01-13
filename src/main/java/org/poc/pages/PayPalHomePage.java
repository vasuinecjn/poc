package org.poc.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.poc.WebOperations;

import java.util.ArrayList;

public class PayPalHomePage extends BasePage {

    public PayPalHomePage(WebDriver driver, WebOperations webOp) {
        super(driver, webOp);
    }

    public PayPalHomePage hoverOnMenuItems(ArrayList<String> menuItems) {
        for (String item : menuItems) {
            try {

                WebElement menu = webOp.getElement(getLocator("menuButton", item));
                Actions actions = new Actions(driver);
                actions.moveToElement(menu).perform();
                webOp.log().info("Hovered on '{}'", item);
                Thread.sleep(3000);
            } catch (Throwable t) {
                webOp.log().error("Failed to hover on: '{}'", t.getMessage());
            }
        }
        return this;
    }

    public ShopAndBuyPage navigateToShopAndBuyPage() {
        webOp.scrollToBottom();
        webOp.scrollToElement(webOp.getElement(getLocator("shopAndBuyLink")));
        webOp.jsClick(getLocator("shopAndBuyLink"));
        return new ShopAndBuyPage(driver, webOp);
    }
}
