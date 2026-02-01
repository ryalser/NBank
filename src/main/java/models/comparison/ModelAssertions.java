package models.comparison;

import org.assertj.core.api.AbstractAssert;

import java.util.HashMap;
import java.util.Map;

public class ModelAssertions extends AbstractAssert<ModelAssertions, Object> {

    private final Object expected;
    private final Object actual;

    private ModelAssertions(Object expected, Object actual) {
        super(expected, ModelAssertions.class);
        this.expected = expected;
        this.actual = actual;
    }

    public static ModelAssertions assertThatModels(Object expected, Object actual) {
        return new ModelAssertions(expected, actual);
    }

    public ModelAssertions match() {
        ModelComparator.ComparisonResult result = ModelComparator.compare(expected, actual);

        if (!result.isSuccess()) {
            failWithMessage("Models do not match:\n%s", result);
        }

        return this;
    }

    public ModelAssertions matchWithMapping(Map<String, String> fieldMappings) {
        ModelComparator.ComparisonResult result = ModelComparator.compare(expected, actual, fieldMappings);

        if (!result.isSuccess()) {
            failWithMessage("Models do not match with given field mappings:\n%s", result);
        }

        return this;
    }

    public ModelAssertions matchPartially(String... fieldsToCheck) {
        Map<String, String> fieldMappings = new HashMap<>();
        for (String field : fieldsToCheck) {
            fieldMappings.put(field, field);
        }

        ModelComparator.ComparisonResult result = ModelComparator.compare(expected, actual, fieldMappings);

        if (!result.isSuccess()) {
            failWithMessage("Models do not match on specified fields:\n%s", result);
        }

        return this;
    }
}