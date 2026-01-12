package org.poc.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.poc.WebOperations;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphHomePage extends BasePage {

    public GraphHomePage(WebDriver driver, WebOperations webOp) {
        super(driver, webOp);
    }

    public void validateEmpGrowthPerYear() {
        List<WebElement> elements = driver.findElements(getBy("employeeGrowthYearNodes"));
        Actions actions = new Actions(driver);
        for(WebElement element: elements) {
            actions.moveToElement(element).perform();
            WebElement tooltip = getElement("employeeGrowthYearNodesTooltip");
            try {
                Thread.sleep(100);
            } catch (Exception e) {}
            System.out.println(tooltip.getText());
        }
    }

    public void showAndHideDeptGraphs(Map<String, Object> data) {
        for (Object object: (List<Map<String, Object>>) data.get("departments")) {
            WebElement department = getElement("hideEmpGrowthGraph", new String[]{String.valueOf(object)});
            department.click();
            try {
                Thread.sleep(2000);
            } catch (Exception e) {}
        }
    }

    public void logRevenueAndCompare(Map<String, Object> data) {
        List<WebElement> bars = driver.findElements(getBy("monthBar"));
        List<WebElement> tooltips = driver.findElements(getBy("monthTooltip"));
        Actions actions = new Actions(driver);
        Map<String, String> monthlyRevenue = new HashMap<>();
        for(int i=0; i < bars.size(); i++) {
            WebElement bar = bars.get(i);
            WebElement tooltip = tooltips.get(i);
            actions.moveToElement(bar).perform();
            tooltip = new WebDriverWait(driver, Duration.ofMillis(500)).until(ExpectedConditions.visibilityOf(tooltip));
            String uiText = tooltip.getText();
            String[] parts = uiText.split(": ");
            monthlyRevenue.put(parts[0], parts[1].replaceAll("[$,]", ""));
            System.out.println(uiText);
        }
        String[] between = String.valueOf(data.get("between")).split(",");
        String r1 = String.valueOf(monthlyRevenue.get(between[0]));
        String r2 = String.valueOf(monthlyRevenue.get(between[1]));
        if (Integer.parseInt(r1) > Integer.parseInt(r2)) {
            System.out.println(between[0] + " has higher revenue: " + r1 + " than " + between[1] + " revenue: " + r2);
        } else if (Integer.parseInt(r2) > Integer.parseInt(r1)) {
            System.out.println(between[1] + " has higher revenue: " + r2 + " than " + between[0] + " revenue: " + r1);
        } else {
            System.out.println(between[0] + " and " + between[1] + " have equal revenue: " + r1);
        }
    }
}
