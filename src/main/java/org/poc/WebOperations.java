package org.poc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebOperations {
    WebDriver driver;
    WebDriverWait wait;
    SeleniumTestCase.IdleDetector idleDetector;

    public WebOperations(WebDriver driver, WebDriverWait wait, SeleniumTestCase.IdleDetector idleDetector) {
        this.driver = driver;
        this.wait = wait;
        this.idleDetector = idleDetector;
    }

    public void click(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.click();
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
    }

    public void fill(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
    }


}
