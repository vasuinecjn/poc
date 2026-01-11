package org.poc.pages;

import org.openqa.selenium.By;
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
                WebElement menu = getElement("menuButton", item);
                Actions actions = new Actions(this.driver);
                actions.moveToElement(menu).perform();
                System.out.println("Hovered on: " + item);
                Thread.sleep(3000);
            } catch (Throwable t) {
                System.out.println("Failed to hover on: " + item + " -> " + t.getMessage());
            }
        }
        return this;
    }

    public ShopAndBuyPage navigateToShopAndBuyPage() {
        this.webOp.scrollToBottom();
        // this.webOp.scrollUntilElementVisible(getElement("shopAndBuyLink"));
        this.webOp.scrollToElement(getElement("shopAndBuyLink"));
        this.webOp.jsClick(getElement("shopAndBuyLink"));
        return new ShopAndBuyPage(driver, webOp);
    }
}
