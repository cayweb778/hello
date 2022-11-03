package org.boozsoft.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author zhuquanwen
 * @version 1.0
 * @date 2022/4/13 13:37
 * @since jdk11
 */
@Component
@Slf4j
public class AdminWebSocketHandler implements WebSocketHandler {
    private static final String CONNECT = "connect:";

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 校验权限
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        Map<String, String> queryMap = getQueryMap(handshakeInfo.getUri().getQuery());
        String id = queryMap.get("id");
        // 暂时只校验了是否携带了ID,以后可以改为校验TOKEN
        if (StringUtils.isNotBlank(id)) {
            // 输入输出封装
            Mono<Void> input = session.receive().doOnNext(message -> this.messageHandle(session, message))
                    .log()
                    .doOnError(throwable -> log.error("webSocket发生异常：" + throwable))
                    .doOnComplete(() -> log.info("webSocket结束")).then();
            Mono<Void> output = session.send(Flux.create(sink -> WebSocketWrap.SENDER.put(id, new WebSocketWrap(id, session, sink))));
            return Mono.zip(input, output).then();
        } else {
            return session.close(new CloseStatus(1016, "连接未通过校验,即将关闭连接"));
        }
    }


    @SuppressWarnings(value = "unused")
    private void messageHandle(WebSocketSession session, WebSocketMessage message) {
        // 接收客户端请求的处理回调
        switch (message.getType()) {
            case TEXT:
            case BINARY:
            case PONG:
            case PING:
                break;
            default:
        }
    }

    private Map<String, String> getQueryMap(String queryStr) {
        Map<String, String> queryMap = new HashMap<>(4);
        if (!StringUtils.isEmpty(queryStr)) {
            String[] queryParam = queryStr.split("&");
            Arrays.stream(queryParam).forEach(s -> {
                String[] kv = s.split("=", 2);
                String value = kv.length == 2 ? kv[1] : "";
                queryMap.put(kv[0], value);
            });
        }
        return queryMap;
    }

}


