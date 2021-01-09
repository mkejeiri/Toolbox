# Spring API Gateway Show case

### More on AntPathMatcher:

https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html



Configure Gateway for Service Discovery
-----
We configure the gateway to use Eureka; to do service lookups to find the services and the gateway:

We need to add client dependency to the gateway project.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	<version>${eureka-client-version}</version>
</dependency>
``` 

add  `application-local-discovery.properties` with the following config :

```properties
#disable registration with Eureka
eureka.client.register-with-eureka=false
```

Mofify configuration to `LocalHostRouteConfig` to ignore `local-discovery` profile.

```java
@Profile({"mylocal", "!local-discovery"})
@Configuration
public class LocalHostRouteConfig {

    @Bean
    public RouteLocator localHostRoutes(RouteLocatorBuilder builder){
        return builder.routes()
//https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html
                .route(r -> r.path("/api/v1/beer*", "/api/v1/beer/*", "/api/v1/beerUpc/*")
                        .uri("http://localhost:8080")
                        /*.id("beer-service")*/)
                .route(r -> r.path("/api/v1/customers/**")
                        .uri("http://localhost:8081")
                        /*.id("order-service")*/)
                .route(r -> r.path("/api/v1/beer/*/inventory")
                        .uri("http://localhost:8082")
                        /*.id("inventory-service")*/)
                .build();
    }
}

```

**Important** : When Spring runs and wires up beans by default if we don't tell it any different, it's going to use
method name. So avoid having the class name and method name with the same name (we CANNOT
do `puplic class LocalHostRouteConfig` and method name `public RouteLocator LocalHostRouteConfig`).

Add new config for api-gateway to use loadbalancer (**Ribbon**); the config will allow to use the api-gateway to lookup
from Eureka the `beer-service` , `beer-order-service` and `beer-inventory-service`. Note that the route is using `lb` (
i.e. load balancer) instead of `http` direct access to the services.

```java
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"mylocal", "local-discovery"})
@Configuration
public class LoadBalancedRouteConfig {

    @Bean
    public RouteLocator loadBalancedHostRoutes(RouteLocatorBuilder builder){
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

```

