package com.elearning.msscbeerapigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.codec.ServerCodecConfigurer;

/*
When Spring runs and wires up beans by default if we don't tell it any different, it's going
to use a class name. So avoid having the class name and method name with same the same.
* */

@Profile("local-discovery")
@Configuration
public class LoadBalancedRouteConfig {

    @Bean
    public RouteLocator loadBalancedHostRoutes(RouteLocatorBuilder builder) {
//https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
        return builder.routes()
                .route(r ->    r.path("/api/v1/beer*", "/api/v1/beer/*", "/api/v1/beerUpc/*")
                                .uri("lb://beer-service")
                        /*.id("beer-service")*/)

                .route(r ->    r.path("/api/v1/customers/**")
                                .uri("lb://beer-order-service")
                        /*.id("order-service")*/)

                .route(r ->    r.path("/api/v1/beer/*/inventory")
                                .filters(f -> f.circuitBreaker(c ->
                                               c.setName("inventoryCB")
                                                .setFallbackUri("forward:/inventory-failover")
                                                .setRouteId("inv-failover")))
                                .uri("lb://beer-inventory-service")
                )

                //added a lb path so that we could discover the path from Eureka
                .route(r ->    r.path("/inventory-failover/**") //everything
                                .uri("lb://inventory-failover")
                        /*.id("inventory-failover")*/)

                .build();
    }

 /*   @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }*/
}