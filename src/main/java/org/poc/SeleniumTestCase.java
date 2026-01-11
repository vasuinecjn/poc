package org.poc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v141.network.Network;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.Set;
import org.openqa.selenium.devtools.v141.network.model.ResourceType;

abstract public class SeleniumTestCase {

    private final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private final ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();
    private final ThreadLocal<WebOperations> webOperations = new ThreadLocal<>();
    private final ThreadLocal<IdleDetector> idleDetector = new ThreadLocal<>();

    @BeforeMethod
    public void setUp() {
        System.out.println(">>> BeforeMethod: Setup resources");
        WebDriver cd = new ChromeDriver();
        WebDriverWait wt = new WebDriverWait(cd, Duration.ofSeconds(60));
        driver.set(cd);
        wait.set(wt);
        IdleDetector detector = new IdleDetector((ChromeDriver) cd);
        idleDetector.set(detector);
        webOperations.set(new WebOperations(cd, wt, detector));
        getDriver().manage().window().maximize();
    }

    @Test(dataProvider = "testData")
    public void runTest(Map<String, Object> testCase) {
        exec(testCase);
    }

    @AfterMethod
    public void tearDown() {
        System.out.println(">>> AfterMethod: Cleanup resources");
        if (getDriver() != null) {
            getDriver().quit();
        }
    }

    @DataProvider(name = "testData")
    public Object[][] provideData() {
        Yaml yaml = new Yaml();
        String fileName = getTestFileName();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("tests/" + fileName)) {
            Map<String, Object> data = yaml.load(inputStream);
            List<Map<String, Object>> tests = (List<Map<String, Object>>) data.get("testCases");
            List<Map<String, Object>> filteredTests = tests.stream()
                    .filter(test -> {
                        Object isSkip = test.get("isSkip");
                        return !(isSkip instanceof Boolean && (Boolean) isSkip);
                    })
                    .collect(Collectors.toList());
            System.out.println("Total tests ---> " + filteredTests.size());
            Object[][] result = new Object[filteredTests.size()][1];
            for (int i = 0; i < filteredTests.size(); i++) {
                result[i][0] = filteredTests.get(i);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read YAML file", e);
        }
    }

    public abstract void exec(Map<String, Object> testCase);

    public abstract String getTestFileName();

    public WebDriver getDriver() {
        return driver.get();
    }

    public WebDriverWait getWebDriverWait() {
        return wait.get();
    }

    public WebOperations getWebOperations() {
        return webOperations.get();
    }

    public IdleDetector getIdleDetector() {
        return idleDetector.get();
    }

    public static class IdleDetector {
        private final AtomicInteger activeRequests = new AtomicInteger(0);
        private final Set<String> ignoredRequestIds = ConcurrentHashMap.newKeySet();
        private final ConcurrentHashMap<String, String> activeRequestUrls = new ConcurrentHashMap<>();

        public IdleDetector(ChromeDriver driver) {
            DevTools devTools = driver.getDevTools();
            devTools.createSession();

            // Enable network tracking
            devTools.send(Network.enable(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));

            // Increment when a request is sent
            devTools.addListener(Network.requestWillBeSent(), event -> {
                ResourceType type = event.getType().orElse(ResourceType.OTHER);
                String url = event.getRequest().getUrl();
                if (isIgnored(type) || isIgnoredUrl(url)) {
                    ignoredRequestIds.add(event.getRequestId().toString());
                } else {
                    activeRequests.incrementAndGet();
                    activeRequestUrls.put(event.getRequestId().toString(), event.getRequest().getUrl());
                }
            });

            // Decrement when a request finishes or fails
            devTools.addListener(Network.loadingFinished(), event -> {
                String id = event.getRequestId().toString();
                if (ignoredRequestIds.contains(id)) {
                    ignoredRequestIds.remove(id);
                } else {
                    activeRequests.decrementAndGet();
                    activeRequestUrls.remove(id);
                }
            });
            devTools.addListener(Network.loadingFailed(), event -> {
                String id = event.getRequestId().toString();
                if (ignoredRequestIds.contains(id)) {
                    ignoredRequestIds.remove(id);
                } else {
                    activeRequests.decrementAndGet();
                    activeRequestUrls.remove(id);
                }
            });
        }

        private boolean isIgnored(ResourceType type) {
            return type == ResourceType.IMAGE ||
                    type == ResourceType.STYLESHEET ||
                    type == ResourceType.FONT ||
                    type == ResourceType.MEDIA ||
                    type == ResourceType.WEBSOCKET ||
                    type == ResourceType.MANIFEST;
        }

        private boolean isIgnoredUrl(String url) {
            return false;
            // return url.contains("t.paypal.com") ||
            // url.contains("google-analytics") ||
            // url.contains("doubleclick");
        }

        public boolean isIdle() {
            return activeRequests.get() == 0;
        }

        public void reset() {
            activeRequests.set(0);
        }

        public void waitUntilIdle(long timeoutMillis, boolean strict) throws InterruptedException {
            long start = System.currentTimeMillis();
            while (activeRequests.get() > 0) {
                if (System.currentTimeMillis() - start > timeoutMillis) {
                    int pending = activeRequests.get();
                    reset();
                    if (strict) {
                        throw new RuntimeException(
                                "Timeout waiting for browser to become idle. Pending: " + activeRequestUrls.values());
                    } else {
                        System.out.println("Browser was not idle even after " + timeoutMillis + " with active calls "
                                + pending + ". Pending: " + activeRequestUrls.values());
                        return; // soft exit
                    }
                }
                Thread.sleep(100);
            }
        }

        public void waitUntilIdle(long timeoutMillis) throws InterruptedException {
            long start = System.currentTimeMillis();
            while (activeRequests.get() > 0) {
                if (System.currentTimeMillis() - start > timeoutMillis) {
                    throw new RuntimeException("Timeout waiting for browser to become idle");
                }
                Thread.sleep(100);
            }
        }
    }
}
