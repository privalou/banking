package io.privalou.controller;

import com.google.gson.Gson;
import io.privalou.exception.BadRequestException;
import io.privalou.exception.TransferException;
import io.privalou.model.AccountModel;
import io.privalou.model.RegisterAccountRequest;
import io.privalou.service.AccountService;
import org.eclipse.jetty.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;

import static spark.Spark.*;

@Singleton
public class AccountsController {

    private final AccountService accountService;
    private final Gson gson;

    @Inject
    public AccountsController(AccountService accountService, Gson gson) {
        this.accountService = accountService;
        this.gson = gson;
    }

    public void configureRoutes() {
        path("/api/v1/accounts", () -> {
            post("", (request, response) -> {
                RegisterAccountRequest accountRequest = gson.fromJson(request.body(), RegisterAccountRequest.class);
                BigDecimal bigDecimal = new BigDecimal(accountRequest.getBalance());
                Long account = accountService.createAccount(bigDecimal);
                response.status(HttpStatus.OK_200);
                return gson.toJson(new AccountModel(account, bigDecimal));
            });
            get("/:id/balance", (request, response) -> {
                Long userId = Long.valueOf(request.params(":id"));
                BigDecimal balanceOfUser = accountService.getBalanceByAccountId(userId);
                AccountModel account = new AccountModel(userId, balanceOfUser);
                response.status(HttpStatus.OK_200);
                return gson.toJson(account);
            });
            exception(BadRequestException.class, (exception, request, response) -> {
                response.status(HttpStatus.BAD_REQUEST_400);
                response.body("Invalid request. Check request parameters and try again.");
            });
            exception(TransferException.class, (exception, request, response) -> {
                response.status(HttpStatus.BAD_REQUEST_400);
                response.body("Invalid request. Please, check sender id, receiver id and correctness of money amount.");
            });
            exception(Exception.class, (exception, request, response) -> {
                response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                response.body("Oops, something went wrong. Please, try once again.");
            });
        });
    }
}
