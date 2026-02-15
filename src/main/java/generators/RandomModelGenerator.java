package generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomModelGenerator {
    static final Random random = new Random();

    public static <T> T generate(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : getAllFields(clazz)) {
                field.setAccessible(true);
                Object value = generateValueForField(field);
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate model for class: " + clazz.getSimpleName(), e);
        }
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static Object generateValueForField(Field field) {
        // Сначала проверяем аннотацию @GeneratingRule
        GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
        if (rule != null) {
            Object value = generateFromRegex(rule.regex(), field.getType());
            // Если generateFromRegex вернул "default", пробуем сгенерировать по имени поля
            if ("default".equals(value)) {
                value = generateByFieldName(field);
            }
            return value;
        }

        // Если аннотации нет, генерируем по имени поля
        return generateByFieldName(field);
    }

    // Новый метод для генерации по имени поля
    private static Object generateByFieldName(Field field) {
        Class<?> type = field.getType();
        String fieldName = field.getName().toLowerCase();

        // Специальные случаи для ваших полей
        if (fieldName.contains("username")) {
            return RandomStringUtils.randomAlphanumeric(8); // Простой username
        } else if (fieldName.contains("password")) {
            // Ваш формат пароля
            return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                    RandomStringUtils.randomAlphabetic(5).toLowerCase() +
                    RandomStringUtils.randomNumeric(4) + "$";
        } else if (fieldName.contains("name") && type.equals(String.class)) {
            return RandomData.getName();
        } else if (fieldName.contains("role") && type.equals(String.class)) {
            return "USER";
        }

        // Общие типы
        if (type.equals(String.class)) {
            return RandomStringUtils.randomAlphabetic(8);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return random.nextInt(1000) + 1;
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return roundToTwoDecimals(random.nextDouble() * 1000);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return random.nextBoolean();
        } else if (type.equals(List.class)) {
            return new ArrayList<>();
        } else {
            // Для других моделей рекурсивно генерируем
            return generate(type);
        }
    }

    private static Object generateFromRegex(String regex, Class<?> type) {
        try {
            // 1. Username: ^[A-Za-z0-9]{3,15}$
            if (regex.equals("^[A-Za-z0-9]{3,15}$") && type.equals(String.class)) {
                int length = random.nextInt(13) + 3; // 3-15
                return RandomStringUtils.randomAlphanumeric(length);
            }

            // 2. Пароль: ^[A-Z]{3}[a-z]{5}[0-9]{4}\$$
            if (regex.equals("^[A-Z]{3}[a-z]{5}[0-9]{4}\\$$") && type.equals(String.class)) {
                return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                        RandomStringUtils.randomAlphabetic(5).toLowerCase() +
                        RandomStringUtils.randomNumeric(4) + "$";
            }

            // 3. Роль: ^(USER|ADMIN)$
            if (regex.equals("^(USER|ADMIN)$") && type.equals(String.class)) {
                return random.nextBoolean() ? "USER" : "ADMIN";
            }

            // 4. Имя: ^[A-Z][a-z]{3,10} [A-Z][a-z]{3,10}$
            if (regex.equals("^[A-Z][a-z]{3,10} [A-Z][a-z]{3,10}$") && type.equals(String.class)) {
                return RandomData.getName();
            }

            // 5. ID: ^[1-9][0-9]{0,3}$
            if (regex.equals("^[1-9][0-9]{0,3}$") && (type.equals(Integer.class) || type.equals(int.class))) {
                return random.nextInt(9999) + 1;
            }

            // 6. Amount: ^[0-9]{1,4}\.[0-9]{2}$
            if (regex.equals("^[0-9]{1,4}\\.[0-9]{2}$") && (type.equals(Double.class) || type.equals(double.class))) {
                return roundToTwoDecimals(random.nextDouble() * 9999.99);
            }

            // Если regex не распознан, возвращаем "default" чтобы triggerнуть fallback
            return "default";

        } catch (Exception e) {
            System.err.println("ERROR in generateFromRegex: " + e.getMessage());
            return "default";
        }
    }

    private static String generateSimpleStringFromRegex(String regex) {
        // Простая реализация для common случаев
        if (regex.contains("{3,15}")) {
            int length = random.nextInt(13) + 3; // от 3 до 15
            return RandomStringUtils.randomAlphanumeric(length);
        } else if (regex.contains("{8}")) {
            return RandomStringUtils.randomAlphanumeric(8);
        }
        return RandomStringUtils.randomAlphabetic(10);
    }

    private static int generateNumberFromRegex(String regex) {
        if (regex.contains("{0,3}")) {
            return random.nextInt(1000); // 0-999
        } else if (regex.contains("{1,4}")) {
            return random.nextInt(9000) + 1000; // 1000-9999
        }
        return random.nextInt(100) + 1;
    }

    private static int extractMaxNumber(String regexPart) {
        try {
            if (regexPart.contains("{1,4}")) {
                return 9999;
            } else if (regexPart.contains("{1,3}")) {
                return 999;
            }
        } catch (Exception e) {
            // ignore
        }
        return 1000;
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type.equals(String.class)) return "default";
        if (type.equals(Integer.class) || type.equals(int.class)) return 1;
        if (type.equals(Double.class) || type.equals(double.class)) return 1.0;
        if (type.equals(Boolean.class) || type.equals(boolean.class)) return false;
        return null;
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}