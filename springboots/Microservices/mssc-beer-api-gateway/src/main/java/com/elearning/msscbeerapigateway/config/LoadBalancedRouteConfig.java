package com.elearning.msscbeerapigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/*
When Spring runs and wires up beans by default if we don't tell it any different, it's going
to use a class name. So avoid having the class name and method name with same the same.
* */

@Profile("local-discovery")
@Configuration
public class LoadBalancedRouteConfig {

    @Bean
    public RouteLocator loadBalancedHostRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
//https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
                .route(r -> r.path("/api/v1/beer*", "/api/v1/beer/*", "/api/v1/beerUpc/*")
                                .uri("lb://beer-service")
                        /*.id("beer-service")*/)
                .route(r -> r.path("/api/v1/customers/**")
                                .uri("lb://beer-order-service")
                        /*.id("order-service")*/)
                .route(r -> r.path("/api/v1/beer/*/inventory")
                                .uri("lb://beer-inventory-service")
                        /*.id("inventory-service")*/)
                .build();
    }
}