package org.boozsoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebfluxConfiguration implements WebFluxConfigurer {

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    ReactiveSortHandlerMethodArgumentResolver methodResolver = new ReactiveSortHandlerMethodArgumentResolver();
    ReactivePageableHandlerMethodArgumentResolver pageResolver = new ReactivePageableHandlerMethodArgumentResolver();
      pageResolver.setOneIndexedParameters(true);
    configurer.addCustomResolver(
        methodResolver,
        pageResolver
    );
  }
}
