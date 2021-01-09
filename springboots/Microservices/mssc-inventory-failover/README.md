# Failer over Webflux implementation


Dependencies 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.1</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>elearning.sfg.beer</groupId>
    <artifactId>mssc-inventory-failover</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>mssc-inventory-failover</name>
    <description>Inventory Service failover</description>

    <properties>
        <java.version>11</java.version>
        <eureka-client-version>3.0.0</eureka-client-version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>${eureka-client-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```



Create a LocalDiscoveryConfig to be discovered by eureka server

```java
@Profile("local-discovery")
@EnableDiscoveryClient
@Configuration
public class LocalDiscoveryConfig {
}
```


create **InventoryHandler** (i.e versus controller in Spring MVC)


```java
@Component
public class InventoryHandler {
    //setting up server response
    public Mono<ServerResponse>  listInventory (ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Arrays.asList(BeerInventoryDto.builder()
                        .id(UUID.randomUUID())
                        .upc("00000000")
                        .beerId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                        .quantityOnHand(999)
                        .createdDate(OffsetDateTime.now())
                        .lastModifiedDate(OffsetDateTime.now())
                        .build())),BeerInventoryDto.class);    }
}

```

Add a **RouterConfig**


```java
@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction inventoryRoute(InventoryHandler inventoryHandler) {
        return route(GET("/inventory-failover").and(accept(MediaType.APPLICATION_JSON)),
                inventoryHandler::listInventory);
    }

}
```

api gateway adjustement
-----------

add  `spring-cloud-starter-circuitbreaker-reactor-resilience4j` to the api gateway, **we need to use the reactive version since the failover service use reactive spring (webflux)**

```xml 
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
```

adjust the route in api gateway service:

```java
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

                .route(r ->    r.path("inventory-failover/*","/inventory-failover/*") //everything
                                .uri("lb://inventory-failover")
                        /*.id("inventory-failover")*/)

                .build();
    }
```


