package org.poc.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.poc.WebOperations;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BasePage {
    public WebDriver driver;
    public WebOperations webOp;
    private static final Map<String, Map<String, String>> locatorsCache = new HashMap<>();

    protected Map<String, String> locators;

    public BasePage(WebDriver driver, WebOperations webOp) {
        this.driver = driver;
        this.webOp = webOp;
        loadPropertiesForClass();
    }

    private void loadPropertiesForClass() {
        String className = this.getClass().getSimpleName();

        if (locatorsCache.containsKey(className)) {
            locators = locatorsCache.get(className);
            return;
        }

        try {
            String resourcePath = this.getClass().getName().replace('.', '/') + ".properties";

            InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                throw new RuntimeException("Properties file not found: " + resourcePath);
            }

            Properties props = new Properties();
            props.load(is);

            Map<String, String> pageLocators = new HashMap<>();
            for (String key : props.stringPropertyNames()) {
                pageLocators.put(key, props.getProperty(key));
            }

            locatorsCache.put(className, pageLocators);
            locators = pageLocators;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file for " + this.getClass().getName(), e);
        }
    }

    public String getLocator(String key) {
        return locators.get(key);
    }

    public WebElement getElement(String key, String... args) {
        String locatorValue = getLocator(key); // e.g. "css|button[data-text='{0}']"
        if (locatorValue == null) {
            throw new RuntimeException("Locator not found for key: " + key);
        }

        // Split into type and expression
        String[] parts = locatorValue.split("\\|", 2);
        String type = parts[0].trim();
        String expr = parts[1].trim();

        // Replace placeholders {0}, {1}, {2}... with args
        for (int i = 0; i < args.length; i++) {
            expr = expr.replace("{" + i + "}", String.valueOf(args[i]));
        }

        // Build By based on type
        switch (type.toLowerCase()) {
            case "css":
                return this.driver.findElement(By.cssSelector(expr));
            case "xpath":
                return this.driver.findElement(By.xpath(expr));
            case "id":
                return this.driver.findElement(By.id(expr));
            case "name":
                return this.driver.findElement(By.name(expr));
            default:
                throw new RuntimeException("Unsupported locator type: " + type);
        }
    }
}