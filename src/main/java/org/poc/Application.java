package org.poc;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.poc.pages.PayPalHomePage;

public class Application {

    private final WebDriver driver;
    private final WebOperations webOp;
    private final SeleniumTestCase.IdleDetector idleDetector;

    public Application(WebDriver driver, WebOperations webOp, SeleniumTestCase.IdleDetector idleDetector) {
        this.driver = driver;
        this.webOp = webOp;
        this.idleDetector = idleDetector;
    }

    public PayPalHomePage launchPayPalApplication() {
        driver.get("https://www.paypal.com/in/home");
        try {
            idleDetector.waitUntilIdle(15000, false);
        } catch (InterruptedException ignored) {
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("acceptAllButton"))));
            webOp.click(driver.findElement(By.id("acceptAllButton")));
        } catch (Exception e) {
        }
        return new PayPalHomePage(driver, webOp);
    }
}
