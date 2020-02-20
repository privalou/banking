package io.privalou.controller;

import com.google.gson.Gson;
import com.google.inject.Guice;
import io.privalou.config.ConfigModule;
import io.privalou.model.AccountModel;
import io.privalou.model.RegisterAccountRequest;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.math.BigDecimal;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.with;

public class AccountsControllerTest {

    private static final Gson gson = new Gson();

    private static final String BALANCE = "10.00";

    @BeforeAll
    public static void before() {
        Spark.awaitStop();
        Guice.createInjector(new ConfigModule())
                .getInstance(AccountsController.class)
                .configureRoutes();
        Spark.awaitInitialization();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Spark.port();
    }

    @AfterAll
    public static void after() {
        Spark.stop();
    }

    @Test
    public void testSuccessfulAccountCreationAndObtainingBalance() {
        RegisterAccountRequest registerAccountRequest = new RegisterAccountRequest("10.00");
        Response registerResponse = with().body(gson.toJson(registerAccountRequest))
                .post("/api/v1/accounts");
        registerResponse.then().statusCode(HttpStatus.OK_200);

        AccountModel createdAccount = registerResponse.as(AccountModel.class, ObjectMapperType.GSON);

        Assertions.assertEquals(new BigDecimal(BALANCE), createdAccount.getBalance());

        Response getBalanceResponse = get("/api/v1/accounts/" + createdAccount.getId() + "/balance");
        getBalanceResponse.then().statusCode(HttpStatus.OK_200);

        AccountModel getBalanceResponseBody = getBalanceResponse.as(AccountModel.class, ObjectMapperType.GSON);
        Assertions.assertEquals(new BigDecimal(BALANCE), getBalanceResponseBody.getBalance());
    }

}
