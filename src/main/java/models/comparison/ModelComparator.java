package models.comparison;

import java.lang.reflect.Field;
import java.util.*;

public class ModelComparator {
    public static ComparisonResult compare(Object expected, Object actual, Map<String, String> fieldMappings) {
        List<Mismatch> mismatches = new ArrayList<>();

        for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
            String expectedField = entry.getKey();
            String actualField = entry.getValue();

            Object expectedValue = getFieldValue(expected, expectedField);
            Object actualValue = getFieldValue(actual, actualField);

            if (!Objects.equals(String.valueOf(expectedValue), String.valueOf(actualValue))) {
                mismatches.add(new Mismatch(expectedField + " -> " + actualField,
                        expectedValue, actualValue));
            }
        }

        return new ComparisonResult(mismatches);
    }

    public static ComparisonResult compare(Object expected, Object actual) {
        // Сравниваем все поля с одинаковыми именами
        List<Mismatch> mismatches = new ArrayList<>();
        List<Field> expectedFields = getAllFields(expected.getClass());

        for (Field expectedField : expectedFields) {
            expectedField.setAccessible(true);
            try {
                String fieldName = expectedField.getName();
                Object expectedValue = expectedField.get(expected);

                // Пытаемся найти поле с таким же именем в actual
                try {
                    Field actualField = getField(actual.getClass(), fieldName);
                    if (actualField != null) {
                        actualField.setAccessible(true);
                        Object actualValue = actualField.get(actual);

                        if (!Objects.equals(String.valueOf(expectedValue), String.valueOf(actualValue))) {
                            mismatches.add(new Mismatch(fieldName, expectedValue, actualValue));
                        }
                    }
                } catch (NoSuchFieldException e) {
                    // Поле не найдено в actual - это нормально
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access field", e);
            }
        }

        return new ComparisonResult(mismatches);
    }

    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = getField(obj.getClass(), fieldName);
            if (field == null) {
                throw new RuntimeException("Field not found: " + fieldName);
            }
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access field: " + fieldName, e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field not found: " + fieldName);
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static class ComparisonResult {
        private final List<Mismatch> mismatches;

        public ComparisonResult(List<Mismatch> mismatches) {
            this.mismatches = mismatches;
        }

        public boolean isSuccess() {
            return mismatches.isEmpty();
        }

        public List<Mismatch> getMismatches() {
            return mismatches;
        }

        @Override
        public String toString() {
            if (isSuccess()) {
                return "All fields match.";
            }
            StringBuilder sb = new StringBuilder("Mismatched fields:\n");
            for (Mismatch m : mismatches) {
                sb.append("  - ").append(m.fieldName)
                        .append(": expected='").append(m.expected)
                        .append("', actual='").append(m.actual).append("'\n");
            }
            return sb.toString();
        }
    }

    public static class Mismatch {
        public final String fieldName;
        public final Object expected;
        public final Object actual;

        public Mismatch(String fieldName, Object expected, Object actual) {
            this.fieldName = fieldName;
            this.expected = expected;
            this.actual = actual;
        }
    }
}