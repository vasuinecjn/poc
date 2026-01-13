package org.poc;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.poc.listeners.ExtentTestNGListener;

import java.util.List;
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

    public WebElement getElement(String type, String value) {
        switch (type.toLowerCase()) {
            case "css":
                return this.waitForElement(By.cssSelector(value));
            case "xpath":
                return this.waitForElement(By.xpath(value));
            case "id":
                return this.waitForElement(By.id(value));
            case "name":
                return this.waitForElement(By.name(value));
            default:
                log().error("Unsupported locator type: {}", type);
                throw new RuntimeException("Unsupported locator type: " + type);
        }
    }

    public WebElement getElement(String locator) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        return getElement(type, value);
    }

    public List<WebElement> getElements(String type, String value) {
        switch (type.toLowerCase()) {
            case "css":
                return this.waitForElements(By.cssSelector(value));
            case "xpath":
                return this.waitForElements(By.xpath(value));
            case "id":
                return this.waitForElements(By.id(value));
            case "name":
                return this.waitForElements(By.name(value));
            default:
                log().error("Unsupported locator type: {}", type);
                throw new RuntimeException("Unsupported locator type: " + type);
        }
    }

    public List<WebElement> getElements(String locator) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        return getElements(type, value);
    }

    public void click(String locator) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        getElement(type, value).click();
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
        log().info("Clicked on element '{}' using {}={}", name, type, value);
    }

    public void jsClick(String locator) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", getElement(type, value));
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
        log().info("Clicked on element '{}' using JS using {}={}", name, type, value);
    }

    public void fill(String locator, String text) {
        fill(locator, text, false);
    }

    public void fill(String locator, String text, boolean waitForIdle) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        getElement(type, value).sendKeys(text);
        if (waitForIdle) {
            try {
                idleDetector.waitUntilIdle(15000, false);
            } catch (InterruptedException ignored) {
            }
        }
        log().info("Typed in element '{}' using {}={}", name, type, value);
    }

    public void select(String locator, String selectByValue) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        WebElement selectElement = getElement(locator);
        Select select = new Select(selectElement);
        select.selectByValue(selectByValue);
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
        log().info("Selected '{}' in element '{}' using {}={}", selectByValue, name, type, value);
    }

    public Logger log() {
        return ExtentTestNGListener.getLogger();
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
    }

    public void scrollToElement(String locator) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", getElement(type, value));
        log().info("Scroll to element '{}' with {}={}", name, type, value);
    }

    public void scrollToBottom() {
        log().info("Scrolling to the bottom of the page using javascript");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public void scrollUntilElementVisible(String locator) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        WebElement element = getElement(type, value);
        int maxScrolls = 10;
        for (int i = 0; i < maxScrolls; i++) {
            try {
                if (element.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
                    log().info("Scrolled to the element '{}' using {}={}", name, type, value);
                    return;
                }
            } catch (NoSuchElementException e) {
                // keep scrolling
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500)");
            }
        }
        log().error("Unable to scroll to the element '{}' using {}={}", name, type, value);
        throw new RuntimeException("Element not found after scrolling");

    }

    public void scrollUntilElementVisible(WebElement element) {
        int maxScrolls = 10;
        for (int i = 0; i < maxScrolls; i++) {
            try {
                if (element.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
                    log().info("Scrolled until the element is visible");
                    return;
                }
            } catch (NoSuchElementException e) {
                // keep scrolling
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500)");
            }
        }
        log().error("Element not found after scrolling");
        throw new RuntimeException("Element not found after scrolling");
    }

    public void navigateTo(String url) {
        log().info("Navigating to '{}'", url);
        driver.get(url);
    }

    public WebElement waitForElement(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public List<WebElement> waitForElements(By by) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    public void waitUntilVisible(String locator) {
        String[] parts = locator.split("\\|");
        String name = parts[0].trim();
        String type = parts[1].trim();
        String value = parts[2].trim();
        wait.until(ExpectedConditions.visibilityOf(getElement(type, value)));
        log().info("Element '{}' with {} is visible", name, type);
    }
}
