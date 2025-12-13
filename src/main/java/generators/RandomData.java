package generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {
    private RandomData(){} // Приватный конструктор запретит создание экземпляров класса

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(8); // Генерация строки определенной длины
    }

    public static String getPassword() {
        // Генерация пароля
        return RandomStringUtils.randomAlphabetic(3).toUpperCase() +  // 3 заглавные буквы
                RandomStringUtils.randomAlphabetic(5).toLowerCase() + // 5 строчных букв
                RandomStringUtils.randomNumeric(3) + // 3 цифры
                "$"; // доп. символ
    }
}
