package models.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ModelComparisonConfigLoader {

    private final Map<String, ComparisonRule> rules = new HashMap<>();

    public ModelComparisonConfigLoader(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                System.out.println("Config file not found: " + configFile + ", using empty rules");
                return;
            }

            Properties props = new Properties();
            props.load(input);

            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                rules.put(key.trim(), parseRule(value));
            }

        } catch (IOException e) {
            System.out.println("Failed to load config: " + configFile + ", error: " + e.getMessage());
        }
    }

    private ComparisonRule parseRule(String ruleString) {
        String[] parts = ruleString.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid rule format: " + ruleString);
        }

        String responseClass = parts[0].trim();
        String[] fieldPairs = parts[1].split(",");

        Map<String, String> fieldMappings = new HashMap<>();
        for (String pair : fieldPairs) {
            String[] mapping = pair.split("=");
            if (mapping.length == 2) {
                fieldMappings.put(mapping[0].trim(), mapping[1].trim());
            } else {
                // Если нет явного маппинга, используем то же имя поля
                fieldMappings.put(pair.trim(), pair.trim());
            }
        }

        return new ComparisonRule(responseClass, fieldMappings);
    }

    public ComparisonRule getRuleFor(Class<?> requestClass) {
        return rules.get(requestClass.getSimpleName());
    }

    public static class ComparisonRule {
        private final String responseClassSimpleName;
        private final Map<String, String> fieldMappings;

        public ComparisonRule(String responseClassSimpleName, Map<String, String> fieldMappings) {
            this.responseClassSimpleName = responseClassSimpleName;
            this.fieldMappings = fieldMappings;
        }

        public String getResponseClassSimpleName() {
            return responseClassSimpleName;
        }

        public Map<String, String> getFieldMappings() {
            return fieldMappings;
        }
    }
}