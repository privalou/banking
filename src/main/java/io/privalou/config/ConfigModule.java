package io.privalou.config;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;

import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import io.privalou.Main;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ConfigModule extends AbstractModule {

    private static final String CONFIG_PATH = "src/main/resources/config.yml";

    @Override
    protected void configure() {
        bind(Main.class).in(Singleton.class);
        configureDatabase();
    }

    private void configureDatabase() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(CONFIG_PATH)) {
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            log.error("Property has not been found", e);
        } catch (IOException e) {
            log.error("Can not read properties", e);
        }
        Names.bindProperties(binder(), properties);
    }

    @Provides
    @Singleton
    private DSLContext provideDslContext(@Named("url") String url,
                                         @Named("user") String username,
                                         @Named("password") String password,
                                         @Named("autoCommit") boolean autoCommit,
                                         @Named("sqlDialect") String sqlDialect) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDefaultAutoCommit(autoCommit);
        return DSL.using(dataSource, SQLDialect.valueOf(sqlDialect),
                new Settings().withExecuteWithOptimisticLocking(true));
    }

    @Provides
    @Singleton
    private Gson provideObjectMapper() {
        return new Gson();
    }
}
