package org.boozsoft.api;

import feign.Headers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "testaaaa")
@Headers("Authorization: mock token")
public interface GreetingReactive {
    String API_PREFIX = "/nc";
    String TOP = API_PREFIX + "/top";
    String TOP2 = API_PREFIX + "/top2";

    @GetMapping(TOP)
    Mono<String> greeting();

    @GetMapping(TOP2)
    @Headers("Authorization: mock token")
    Mono<String> greetingWithParam(@RequestParam(value = "id") Long id);
}
