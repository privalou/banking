package io.privalou;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.privalou.config.ConfigModule;
import io.privalou.controller.AccountsController;
import io.privalou.controller.TransfersController;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ConfigModule());
        injector.getInstance(AccountsController.class).configureRoutes();
        injector.getInstance(TransfersController.class).configureRoutes();
    }
}
