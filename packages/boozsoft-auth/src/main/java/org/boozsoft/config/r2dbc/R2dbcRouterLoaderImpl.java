package org.boozsoft.config.r2dbc;

import lombok.RequiredArgsConstructor;
import org.springbooz.datasource.r2dbc.BoozR2dbcRoutingConnectionFactory;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import reactor.core.CorePublisher;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/1/5 11:53 上午
 */
@Service
@DependsOn("connectionFactory")
@RequiredArgsConstructor
public class R2dbcRouterLoaderImpl implements R2dbcRouterLoader {
    private final BoozR2dbcRoutingConnectionFactory r2dbcRoutingConnectionFactory;

    private Map session;

    @Override
    public String[] getOwnerSchema() {

        Object userInfo = session.get("userInfo");

        return new String[]{"prod-nc","hello1"};
    }

    @Override
    public <T extends CorePublisher>T bind(Supplier<T> supplier, R2dbcRouterBuilder builder) {
        return r2dbcRoutingConnectionFactory.loadDataSource(supplier.get(),builder);
    }

}
