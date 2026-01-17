package generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomData {
    private RandomData(){} // не создавать экземпляров
    public static Random random = new Random();

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

    // Рандомная сумма пополнения
    public static double getDepositAmount() {
        return Math.random() * 4999.99 + 0.01;
    }

    // Рандомная сумма перевода
    public static double getTransferAmount(double maxValue) {
        return Math.random() * maxValue + 0.01;
    }

    // Рандомный id и не равный существующему
    public static int getRandomId(int existingId) {
        return(int) (Math.random() * 900) + existingId;
    }

    // Рандомный id
    public static int getRandomId() {
        return(int) (Math.random() * 900) + 100;
    }
}
