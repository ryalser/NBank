package generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

import static generators.RandomModelGenerator.random;

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

    // Имя пользователя
    public static String getName() {
        return RandomStringUtils.randomAlphabetic(4) + " " +
                RandomStringUtils.randomAlphabetic(4);
    }

    // Сумма пополнения
    public static double getDepositAmount() {
        return roundToTwoDecimals(Math.random() * 4999.99 + 0.01);
    }

    // Сумма перевода
    public static double getTransferAmount(double maxValue) {
        return roundToTwoDecimals(Math.random() * maxValue + 0.01);
    }

    // Рандомный id и не равный существующему
    public static int getRandomId(int existingId) {
        return(int) (Math.random() * 900) + existingId;
    }

    // Рандомный id
    public static int getRandomId() {
        return(int) (Math.random() * 900) + 100;
    }

    // Невалидная/отрицательная суммы
    public static double getInvalidNegativeAmount() {
        return roundToTwoDecimals(-(random.nextDouble() * 100 + 0.01));
    }

    // Сумма невалидного пополнения свыше лимита
    public static double getInvalidExceedingAmount() {
        return roundToTwoDecimals(10000 + random.nextDouble() * 5000 + 0.01);
    }

    // Сумма  больше заданной (для проверки недостаткасредств)
    public static double getAmountExceeding(double maxValue) {
        return roundToTwoDecimals(maxValue + random.nextDouble() * 1000 + 0.01);
    }

    // Имя профиля без пробелов
    public static String getNameWithoutSpace() {
        return  RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphabetic(3).toLowerCase() +
                RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    // Имя профиля с цифрами
    public static String getNameWithNumber() {
        return RandomStringUtils.randomNumeric(3) + " " +
                RandomStringUtils.randomAlphabetic(3);
    }

    // Имя профиля только с пробелами
    public static String getNameWithSpacesOnly() {
        return "  ";
    }

    // Приведение к 2 знакам после запятой
    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}