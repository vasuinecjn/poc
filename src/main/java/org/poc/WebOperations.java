package org.poc;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.NoSuchElementException;

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
        element = wait.until(ExpectedConditions.visibilityOf(element));
        element.click();
//        ExtentTestNGListener.getLogger().info("Clicked on element: {}", locator.toString());
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
    }

    public void jsClick(WebElement element) {
        element = wait.until(ExpectedConditions.visibilityOf(element));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
    }

    public void fill(WebElement element, String text) {
        element = wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
    }

    public void select(WebElement element, String selectValue) {
        element = wait.until(ExpectedConditions.visibilityOf(element));
        Select select = new Select(element);
        select.selectByValue(selectValue);
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public void scrollUntilElementVisible(WebElement element) {
        int maxScrolls = 10;
        for (int i = 0; i < maxScrolls; i++) {
            try {
                if (element.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
                    return;
                }
            } catch (NoSuchElementException e) {
                // keep scrolling
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500)");
            }
        }
        throw new RuntimeException("Element not found after scrolling");
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public WebElement waitForElement(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement waitUntilVisible(By locator, int timeoutInMillis) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(timeoutInMillis));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
