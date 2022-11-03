package org.boozsoft.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuquanwen
 * @version 1.0
 * @date 2022/4/13 14:19
 * @since jdk11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class WebSocketWrap {
    public static final Map<String, WebSocketWrap> SENDER = new ConcurrentHashMap<>();

    private String id;
    private WebSocketSession session;
    private FluxSink<WebSocketMessage> sink;

    /**
     * 发送广播消息
     *
     * @param obj 消息对象，会被转为JSON
     * @return void
     * @date 2022/4/13
     * @since jdk11
     */
    public static void broadcastText(Object obj) {
        SENDER.values().forEach(wrap -> wrap.sendText(obj));
    }

    public void sendText(Object obj) {
        sink.next(session.textMessage(""));
    }


    static {
        purge();
    }

    /**
     * 清理不可用的SESSION
     * @since jdk11
     * @date 2022/4/13
     * @return void
     */
    @SuppressWarnings("AlibabaThreadPoolCreation")
    public static void purge() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            new ArrayList<>(SENDER.values()).forEach(wrap -> {
                if (!wrap.getSession().isOpen()) {
                    log.warn(String.format("用户ID: [%s] 的session: [%s] 已经关闭，将被清理", wrap.getId(), wrap.getSession().getId()));
                    SENDER.remove(wrap.getId());
                    wrap.getSession().close();
                }
            });
        }, 30, 30, TimeUnit.SECONDS);
    }

}


