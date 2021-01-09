## Considerations

Before running the microservices, we need to start up an activeMQ artemis from docker as follows:

- `docker run -d -e ARTEMIS_USERNAME=username -e ARTEMIS_PASSWORD=password -p 8161:8161 -p 61616:61616 -p 36118:36118 vromero/activemq-artemis`
- check that the ActiveMq broker is up and running :
    - `http://localhost:8161/console/`
    - credentials : `username/password`

[More info: https://github.com/vromero/activemq-artemis-docker](https://github.com/vromero/activemq-artemis-docker)

# MSSC Beer Service

## mapstruct

1- @DecoratedWith(BeerMapperDecorator.class): the @DecoratedWith will generate the code below through the preprocessor,
note that the implementation is as @Primary:

```Java
@Component
@Primary
public class BeerMapperImpl extends BeerMapperDecorator implements BeerMapper {
}
```

2- MapStruct (@Mapper annotation) creates automatically through the preprocessor an implementation BeerMapperImpl by
matching the property names, along with @DecoratedWith will create a "BeerMapperImpl_" with @Qualifier("delegate") as
follow:

```Java
import Beer;
import Beer.BeerBuilder;
import BeerDto;
import BeerDto.BeerDtoBuilder;
import BeerStyleEnum;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-12-28T13:59:01+0100",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 11.0.8 (Red Hat, Inc.)"
)
@Component
@Qualifier("delegate")
public class BeerMapperImpl_ implements BeerMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public BeerDto beerToBeerDto(Beer beer) {
        if ( beer == null ) {
            return null;
        }

        BeerDtoBuilder beerDto = BeerDto.builder();

        beerDto.id( beer.getId() );
        if ( beer.getVersion() != null ) {
            beerDto.version( beer.getVersion().intValue() );
        }
        beerDto.createdDate( dateMapper.asOffsetDateTime( beer.getCreatedDate() ) );
        beerDto.lastModifiedDate( dateMapper.asOffsetDateTime( beer.getLastModifiedDate() ) );
        beerDto.beerName( beer.getBeerName() );
        if ( beer.getBeerStyle() != null ) {
            beerDto.beerStyle( Enum.valueOf( BeerStyleEnum.class, beer.getBeerStyle() ) );
        }
        beerDto.upc( beer.getUpc() );
        beerDto.price( beer.getPrice() );

        return beerDto.build();
    }

    @Override
    public Beer beerDtoToBeer(BeerDto dto) {
        if ( dto == null ) {
            return null;
        }

        BeerBuilder beer = Beer.builder();

        beer.id( dto.getId() );
        if ( dto.getVersion() != null ) {
            beer.version( dto.getVersion().longValue() );
        }
        beer.createdDate( dateMapper.asTimestamp( dto.getCreatedDate() ) );
        beer.lastModifiedDate( dateMapper.asTimestamp( dto.getLastModifiedDate() ) );
        beer.beerName( dto.getBeerName() );
        if ( dto.getBeerStyle() != null ) {
            beer.beerStyle( dto.getBeerStyle().name() );
        }
        beer.upc( dto.getUpc() );
        beer.price( dto.getPrice() );

        return beer.build();
    }

    @Override
    public BeerDto beerToBeerDtoWithInventory(Beer beer) {
        if ( beer == null ) {
            return null;
        }

        BeerDtoBuilder beerDto = BeerDto.builder();

        beerDto.id( beer.getId() );
        if ( beer.getVersion() != null ) {
            beerDto.version( beer.getVersion().intValue() );
        }
        beerDto.createdDate( dateMapper.asOffsetDateTime( beer.getCreatedDate() ) );
        beerDto.lastModifiedDate( dateMapper.asOffsetDateTime( beer.getLastModifiedDate() ) );
        beerDto.beerName( beer.getBeerName() );
        if ( beer.getBeerStyle() != null ) {
            beerDto.beerStyle( Enum.valueOf( BeerStyleEnum.class, beer.getBeerStyle() ) );
        }
        beerDto.upc( beer.getUpc() );
        beerDto.price( beer.getPrice() );

        return beerDto.build();
    }
}
```

## ehcache

1- Add ehcache curated dependencies :

```xml
        <dependency>
            <groupId>javax.cache</groupId>
            <artifactId>cache-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.ehcache</groupId>
            <artifactId>ehcache</artifactId>
        </dependency>
```

2- create ehcache.xml in resources, with following content :

```xml
<config
        xmlns:jsr107='http://www.ehcache.org/v3/jsr107'
        xmlns='http://www.ehcache.org/v3'>
    <service>
        <jsr107:defaults enable-management="true" enable-statistics="true"/>
    </service>

    <cache alias="beerCache" uses-template="config-cache"/>
    <cache alias="beerUpcCache" uses-template="config-cache"/>
    <cache alias="beerListCache" uses-template="config-cache"/>

    <cache-template name="config-cache">
        <expiry>
            <ttl unit="minutes">5</ttl>
        </expiry>
        <resources>
            <heap>1</heap>
            <offheap unit="MB">1</offheap>
        </resources>
    </cache-template>
</config>
```

3- instruct springboot to use cache in `application.properties` as follow:

```
spring.cache.jcache.config=classpath:ehcache.xml
```

4- Added annotation to two methods in BeerServiceImpl as follow:

```Java
    @Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false ")
    @Override
    public BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, boolean showInventoryOnHand) {
        System.out.println("listBeers has been called");
        BeerPagedList beerPagedList;
		....
```

```Java
  @Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false ")
    @Override
    public BeerDto getById(UUID beerId, boolean showInventoryOnHand) {
        System.out.println("getById has been called");
        if (showInventoryOnHand) {
            return beerMapper.beerToBeerDtoWithInventory(
                    beerRepository.findById(beerId).orElseThrow(NotFoundException::new)
            );
        }
        return beerMapper.beerToBeerDto(
                beerRepository.findById(beerId).orElseThrow(NotFoundException::new)
        );
    }
```

5- Enable Caching either create a dedicated `CacheConfig` `class` :

```Java
  
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {
}
```

**OR** add `@EnableCaching` to `MsscBeerServiceApplication` `class`

```Java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCaching
public class MsscBeerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsscBeerServiceApplication.class, args);
    }

}

```

# Eureka Server

### Eureka run as a cluster!

setting the default port for eureka :

```
server.port=8761
```

Eureka itself is designed to run as a cluster, so we'll have a cluster of Eureka servers going. Here, we are running
locally, we don't want that to happen. Eureka servers won't be registering with Eureka nor is it will fetch a registry

```
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

Spring is recommending to log those levels off if we ever need to get into debugging.

```
logging.level.com.netflix.eureka=off
logging.level.com.netflix.discovery=off
```

Eureka Client Configuration
-----

add maven dependency

```
<dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>           
</dependency>
```

add application name to be used to register with Eureka:

```
spring.application.name=beer-service
```

add a new config:

```java
@Profile("local-discovery")
@EnableEurekaClient
@Configuration
public class LocalDiscoveryConfig {
}
```

Service discovery using OpenFeign
-------

Add dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```

We need to enable **EnableFeignClients**:

```java
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"elearning.sfg.beer"})
public class MsscBeerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscBeerServiceApplication.class, args);
    }
}
```

**Configure** the service; and instead of restTempate making a call to inventory service, it should use FeignClient, the
feign client works like Spring Data JPA, We are going to provide an interface and decorate the interface with some
annotations and then at runtime Spring is going to provide an implementation for us.

```java
@FeignClient(name="beer-inventory-service")
public interface InventoryServiceFeignClient {
    @RequestMapping(method = RequestMethod.GET,value = BeerInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<BeerInventoryDto> getOnhandQuantity(UUID beerId);
```

Create and implements **BeerInventoryServiceFeign** :

```java
@Slf4j
@RequiredArgsConstructor
@Profile("local-discovery")
public class BeerInventoryServiceFeign implements BeerInventoryService{
    private final InventoryServiceFeignClient inventoryServiceFeignClient;
    @Override
    public Integer getOnhandInventory(UUID beerId) {
        log.debug("Calling inventory Service - BeerId: "+ beerId);
        ResponseEntity<List<BeerInventoryDto>> responseEntity = inventoryServiceFeignClient.getOnhandQuantity(beerId);

        Integer onHand = Objects.requireNonNull(responseEntity.getBody())
                .stream()
                .mapToInt(BeerInventoryDto::getQuantityOnHand)
                .sum();
        return onHand;
    }
}
```

since **BeerInventoryServiceFeign** and **BeerInventoryServiceRestTemplateImpl** both implements **
BeerInventoryService** interface, we need to make a distinct otherwise spring boot doesn't know which one to
wire/inject. The trick here is consider the **profile** of
BeerInventoryServiceRestTemplateImpl (`@Profile("!local-discovery")`) the **reverse** of BeerInventoryServiceFeign;

```java
@Slf4j
@Profile("!local-discovery")
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = true)
@Configuration
public class BeerInventoryServiceRestTemplateImpl implements BeerInventoryService {
  public static final String INVENTORY_PATH = "/api/v1/beer/{beerId}/inventory";
  private final RestTemplate restTemplate;
...
}

```

Configure Gateway for Service Discovery
-----
We configure the gateway to use Eureka; to do service lookups to find the services and the gateway:

We need to add client dependency to the gateway project (see gateway project).

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	<version>${eureka-client-version}</version>
</dependency>
``` 

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

The Circuit Breaker Pattern
-------

- The Circuit Breaker Pattern is a simple concept which allows you to recover from errors.
- If a service is unavailable or has unrecoverable errors, via the Circuit Breaker Pattern you can specify an
  alternative action.
- For example, we wish to always have inventory for potential orders.
- If the inventory service is down, we can provide a fallback service to respond with inventory.

Spring Cloud Circuit Breaker
-----

- Spring Cloud Circuit Breaker is a project which provides abstractions across several circuit breaker implementations.
- Thus your source code is not tied to a specific implementation (like SLF4J).
- Supported:
    - Netflix Hystrix
    - Resilience4J
    - Sentinel
    - Spring Retry

Spring Cloud Gateway Circuit Breakers
-------

Spring Cloud Gateway support Circuit Breakers as well. These are integrated into filters. So Gateway filters are used on
top of the Spring Cloud Circuit Breaker APIs. it's important to note Netflix has placed Hystrix into maintenance mode,
So Spring is suggesting to use Resilience4J.

- Spring Cloud Gateway supports Netflix Hystrix and Resilience4J
- Gateway Filters are used on top the the Spring Cloud Circuit Breaker APIs
- Netflix has placed Hystrix into maintenance mode, Spring suggests using Resilience4J

**Actions** :

- Create Inventory Failover service
- Configure Spring Cloud Gateway to use circuit breaker for failover
- Configure Feign to use Circuit Breaker

