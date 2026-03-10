package api;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import api.requests.steps.AdminSteps;
import api.specs.RequestsSpecs;
import api.utils.cleanup.UserCleanup;

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