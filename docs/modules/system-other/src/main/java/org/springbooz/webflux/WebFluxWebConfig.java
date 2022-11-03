package org.springbooz.webflux;


import org.springbooz.webflux.holder.context.security.resolver.BoozContextMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

/**
 * @ClassName : WebFluxWebConfig
 * @Description :
 * @Author : miao
 * @Date: 2020-10-23 15:41
 */
@Configuration
public class WebFluxWebConfig implements WebFluxConfigurer {
    /**
     * @Author : 木子桉易洋
     * @param configurer
     */
    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new BoozContextMethodArgumentResolver());
    }
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(6 * 1024 * 1024);
    }
}
