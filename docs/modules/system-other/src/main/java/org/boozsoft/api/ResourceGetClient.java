package org.boozsoft.api;

import feign.Headers;
import org.boozsoft.domain.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 *
 */
@ReactiveFeignClient(name = "boozsoft-nc-resource-service-dev")
@Headers("Authorization: Bearer mock token")
public interface ResourceGetClient {
    // a
    /**
     * sadsa
     */
    String API_PREFIX = "/resources";

  /**
   *
   * @param key asd
   * @return asdsa
   */
  @GetMapping(API_PREFIX+"/state/data")
    @Headers("Authorization: Bearer mock token")
    public Mono<Object> stateData(@RequestParam("key") String key);
}
