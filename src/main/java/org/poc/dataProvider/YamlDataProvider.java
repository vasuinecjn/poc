package org.poc.dataProvider;

// import org.poc.SeleniumTestCase;
// import org.testng.annotations.DataProvider;
// import org.yaml.snakeyaml.Yaml;

// import java.io.InputStream;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

public class YamlDataProvider {

    // @DataProvider(name = "testData")
    // public Object[][] provideData() {
    // Yaml yaml = new Yaml();
    //// SeleniumTestCase testCase = (SeleniumTestCase) testInstance;
    //// String fileName = testCase.getTestFileName();
    //
    // String file = "tests/hoverTests.yaml";
    // try (InputStream inputStream =
    // getClass().getClassLoader().getResourceAsStream(file)) {
    // Map<String, Object> data = yaml.load(inputStream);
    //
    // // "tests" is a List of Maps
    // List<Map<String, Object>> tests = (List<Map<String, Object>>)
    // data.get("testCases");
    // List<Map<String, Object>> filteredTests = tests.stream()
    // .filter(test -> {
    // Object isSkip = test.get("isSkip");
    // return !(isSkip instanceof Boolean && (Boolean) isSkip);
    // })
    // .collect(Collectors.toList());
    // Object[][] result = new Object[tests.size()][1];
    // for (int i = 0; i < filteredTests.size(); i++) {
    // result[i][0] = filteredTests.get(i);
    // }
    // return result;
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to read YAML file", e);
    // }
    // }
}
