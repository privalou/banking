package io.privalou.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import io.privalou.config.ConfigModule;
import io.privalou.model.*;
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
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.with;

public class TransfersControllerTest {

    private static final Gson gson = new Gson();

    private static final String MESSAGE = "Transfer has been been executed.";

    private static final Long FIRST_ACCOUNT_ID = 3L;
    private static final Long SECOND_ACCOUNT_ID = 4L;
    private static final String AMOUNT_OF_TRANSFER = "10.00";

    @BeforeAll
    public static void before() {
        Spark.awaitStop();
        Guice.createInjector(new ConfigModule())
                .getInstance(TransfersController.class)
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
    public void testSuccessfulMoneyTransfer() {
        TransferModel transfer = new TransferModel(FIRST_ACCOUNT_ID,
                SECOND_ACCOUNT_ID,
                new BigDecimal(AMOUNT_OF_TRANSFER));
        Response transferResponse = with()
                .body(gson.toJson(transfer))
                .post("/api/v1/transfers");
        transferResponse.then().statusCode(HttpStatus.OK_200);
        String message = transferResponse.as(String.class, ObjectMapperType.GSON);
        Assertions.assertEquals(MESSAGE, message);
    }

    @Test
    public void testSuccessfulMoneyTransferAndObtainTransferHistory() {
        TransferModel transfer = new TransferModel(FIRST_ACCOUNT_ID,
                SECOND_ACCOUNT_ID,
                new BigDecimal(AMOUNT_OF_TRANSFER));
        Response transferResponse = with()
                .body(gson.toJson(transfer))
                .post("/api/v1/transfers");
        transferResponse.then().statusCode(HttpStatus.OK_200);
        String message = transferResponse.as(String.class, ObjectMapperType.GSON);
        Assertions.assertEquals(MESSAGE, message);

        Response getHistoryResponse = get("/api/v1/transfers/" + FIRST_ACCOUNT_ID);
        getHistoryResponse.then().statusCode(HttpStatus.OK_200);
        List<TransferModel> transfersOfUser = Arrays.asList(getHistoryResponse.as(TransferModel[].class,
                ObjectMapperType.GSON));
        TransferModel recentTransfer = transfersOfUser.get(0);
        Assertions.assertEquals(FIRST_ACCOUNT_ID, recentTransfer.getSenderId());
        Assertions.assertEquals(SECOND_ACCOUNT_ID, recentTransfer.getReceiverId());
        Assertions.assertEquals(new BigDecimal(AMOUNT_OF_TRANSFER), recentTransfer.getAmount());
    }
}
