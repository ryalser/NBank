package generators;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class RandomData {
    private RandomData(){} // не создавать экземпляров

    // Генерация username определенной длины
    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    // Генерация пароля
    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomAlphabetic(5).toLowerCase() +
                RandomStringUtils.randomNumeric(3) +
                "$";
    }

    // Рандомное имя пользователя
    public static String getName() {
        return RandomStringUtils.randomAlphabetic(4) + " " +
                RandomStringUtils.randomAlphabetic(4);
    }
}
