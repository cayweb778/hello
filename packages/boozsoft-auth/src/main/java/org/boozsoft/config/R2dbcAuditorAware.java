package org.boozsoft.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class R2dbcAuditorAware implements AuditorAware<Long> {

  @Override
  public Optional<Long> getCurrentAuditor() {
    return Optional.of(555L);
  }
  @Bean
  ReactiveAuditorAware<String> auditorAware() {
    Snowflake snowflake = IdUtil.createSnowflake(1, 1);
    return () -> {
      String id = snowflake.nextIdStr();
      return Mono.just(id);
    };
  }
}
