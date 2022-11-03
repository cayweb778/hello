package org.boozsoft.api;

import feign.Headers;
import org.boozsoft.domain.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;
import java.security.Principal;

/**
 *
 */
@ReactiveFeignClient(name = "boozsoft-nc-resource-service-dev")
@Headers("Authorization: Bearer mock token")
public interface SsoUserInfoClient {
    // a
    /**
     * sadsa
     */
    String API_PREFIX = "/resources";

    /**
     *
     * @return sadsa
     */
    @GetMapping(API_PREFIX+"/getSsoUserInfoClient")
    @Headers("Authorization: Bearer mock token")
    public Mono<User> getSsoUserInfoClient();
}
