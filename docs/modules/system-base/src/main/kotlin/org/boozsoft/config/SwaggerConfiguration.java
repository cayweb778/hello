package org.boozsoft.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;


@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    //上线后改成false
    @Value(value = "${swagger.enabled}")
    Boolean swaggerEnabled;

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("财税达软件")
                        .description("财税达软件接口文档")
                        // 作者信息
                        .contact(new Contact("财税达", "https://www.chinabooz.com", "mzayy_top@163.com"))
                        .version("1.0.0-SNAPSHOT")
                        .build())
                //分组名称
                .groupName("财税达软件")
                .enable(swaggerEnabled).select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("org.boozsoft"))
                .paths(PathSelectors.any())
                .build();
                //下面这个是配置全局token的，不需要的请不要加
//                .securityContexts(Lists.newArrayList(securityContext())).securitySchemes(Lists.<SecurityScheme>newArrayList(apiKey()));;
        return docket;
    }

//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .forPaths(PathSelectors.regex("/.*"))
//                .build();
//    }

//    List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Lists.newArrayList(new SecurityReference("token", authorizationScopes));
//    }

    private ApiKey apiKey() {
        return new ApiKey("token", "token", "header");
    }
}

