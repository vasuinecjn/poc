package org.poc.pages;

import org.openqa.selenium.WebDriver;
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

    public String getLocator(String key, String... args) {
        String locatorValue = locators.get(key);
        if (locatorValue == null) {
            throw new RuntimeException("Locator not found for key: " + key);
        }
        String[] parts = locatorValue.split("\\|", 2);
        String type = parts[0].trim();
        String expr = parts[1].trim();

        // Replace placeholders {0}, {1}, {2}... with args
        for (int i = 0; i < args.length; i++) {
            expr = expr.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return key + "|" + type + "|" + expr;
    }
}