package org.springbooz.datasource.r2dbc.service;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import reactor.core.CorePublisher;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface R2dbcRouterLoader {

    String[] getOwnerSchema();

    abstract <T extends CorePublisher>T bind(Supplier<T> supplier, R2dbcRouterBuilder builder);
}

