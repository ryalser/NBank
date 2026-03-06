package api.tests_level_senior.iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import requests.steps.AdminSteps;
import specs.RequestsSpecs;
import utils.cleanup.UserCleanup;

import java.util.List;

public class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    public void setupTest(){
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest(){
        softly.assertAll();
        RequestsSpecs.clearAuthCache();
    }

    @AfterAll
    public static void cleanTestData() {
        UserCleanup.cleanUsers();
        AdminSteps.clearPasswordsCache();
    }
}