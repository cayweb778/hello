package org.springbooz.datasource.r2dbc;

import org.springbooz.datasource.r2dbc.mode.BaseModeConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2020/12/30 1:34 下午
 */
public class BoozR2dbcBaseConnectionFactory extends AbstractRoutingConnectionFactory {

    private final static String DB_KEY = "HOTKIT-R2DBC-DB";
    public Map<String, ConnectionFactory> connectionFactoryMap;
    public BoozR2dbcBaseConnectionFactory(BaseModeConnectionFactory modeConnectionFactory) {
        connectionFactoryMap = modeConnectionFactory.build();

        setTargetConnectionFactories(connectionFactoryMap);
        ConnectionFactory defaultTarget = connectionFactoryMap.get(modeConnectionFactory.getDefaultDataBase());
        setDefaultTargetConnectionFactory(defaultTarget);
    }

    public static <T> Mono<T> putDataSource(Mono<T> mono, String dataSource) {
        return mono.subscriberContext(context -> context.put(DB_KEY, dataSource));
    }

    public static <T> Flux<T> putDataSource(Flux<T> flux, String dataSource) {
        return flux.subscriberContext(f -> f.put(DB_KEY, dataSource));
    }

    @Override
    protected Mono<Object> determineCurrentLookupKey() {
        return Mono.subscriberContext().handle((context, sink) -> {
            if (context.hasKey(DB_KEY)) {
                sink.next(context.get(DB_KEY));
            }
        });
    }

}
