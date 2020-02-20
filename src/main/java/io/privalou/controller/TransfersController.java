package io.privalou.controller;

import com.google.gson.Gson;
import io.privalou.exception.BadRequestException;
import io.privalou.model.TransferModel;
import io.privalou.service.TransferService;
import org.eclipse.jetty.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static spark.Spark.*;

@Singleton
public class TransfersController {
    private static final String SUCCESSFULL_TRANSFER_MESSAGE = "Transfer has been been executed.";
    private static final String BAD_REQUEST_MESSAGE_400 = "Invalid request. Check request parameters and try again.";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE_500 = "Oops, something went wrong. Please, try once " +
            "again.";

    private final TransferService transferService;
    private final Gson gson;

    @Inject
    public TransfersController(TransferService transferService, Gson gson) {
        this.transferService = transferService;
        this.gson = gson;
    }

    public void configureRoutes() {
        path("/api/v1/transfers", () -> {
            get("/:accountId", (request, response) -> {
                List<TransferModel> transfersByAccountId =
                        transferService.getTransfersByAccountId(Long.valueOf(request.params(":accountId")));
                response.status(HttpStatus.OK_200);
                return gson.toJson(transfersByAccountId);
            });
            post("", (request, response) -> {
                TransferModel transferModel = gson.fromJson(request.body(), TransferModel.class);
                transferService.executeMoneyTransfer(transferModel);
                response.status(HttpStatus.OK_200);
                return gson.toJson(SUCCESSFULL_TRANSFER_MESSAGE);
            });
            exception(BadRequestException.class, (exception, request, response) -> {
                response.status(HttpStatus.BAD_REQUEST_400);
                response.body(BAD_REQUEST_MESSAGE_400);
            });
            exception(Exception.class, (exception, request, response) -> {
                response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                response.body(INTERNAL_SERVER_ERROR_MESSAGE_500);
            });
        });
    }
}
