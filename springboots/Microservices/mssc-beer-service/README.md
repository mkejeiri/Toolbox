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

3- tells springboot to use cache in `application.properties` as follow:

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


Failover Settings 
--------

Beer service call sync (rest call) to the inventory service. We need to use the failover mechanism for resiliency.

Add file `application-local-discovery.properties`
```
# hystrix under OpenFeign is no longuer supported!
feign.hystrix.enabled=true 
#feign.circuitbreaker.enabled=true
```

create an  **InventoryFailoverFeignClient**  interface that will call `inventory-failover` service through the `FeignClient`.

```java
/*
the feign client works like Spring Data JPA, We are going to provide an interface and decorate the interface with
some annotations and then at runtime Spring is going to provide an implementation for us.
*/

//name of inventory service used by eureka (inventory service application name)
@FeignClient(name = "inventory-failover")
public interface InventoryFailoverFeignClient  {
    @RequestMapping(method = RequestMethod.GET, value = "/inventory-failover")
    ResponseEntity<List<BeerInventoryDto>> getOnhandInventory();
}
```

Adjust the openfeign client  (i.e. `InventoryServiceFeignClient` **interface**) to include the fallback. 

```java
/*
the feign client works like Spring Data JPA, We are going to provide an interface and decorate the interface with
some annotations and then at runtime Spring is going to provide an implementation for us.
*/

//name of inventory service used by eureka (inventory service application name)
//when it fails, it fallbacks on its BeerInventoryServiceFeignClientFailoverImpl implementation
@FeignClient(name = "beer-inventory-service", fallback = BeerInventoryServiceFeignClientFailoverImpl.class)
public interface InventoryServiceFeignClient {
    @RequestMapping(method = RequestMethod.GET, value = BeerInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<List<BeerInventoryDto>> getOnhandInventory(@PathVariable UUID beerId);
}
```

make sure that FeignClients is Enabled on the startup.

```java
@EnableFeignClients
//@SpringBootApplication(scanBasePackages = {"elearning.sfg.beer"})
@SpringBootApplication()
public class MsscBeerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscBeerServiceApplication.class, args);
    }
}

```

Setting up cloud config client
-----
The `beer-service`  will fetch the config from spring cloud config server `mssc-config-server` service which in turn fetch the config from github `https://github.com/mkejeiri/mssc-config-repo.git`
 
**add the cloud config client dependency**
 
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
	<version>2.2.3.RELEASE</version>
</dependency>
```


**Two phases configuration step**: 
- It will start up, i.e. it will load bootstrap.properties file to find the environment. 
- and then it's will continue its normal course.

**we can bootstrap application and also profiles**. This is very important, because it allows us to bootstrap locally any profile (such as eureka or no).


**Create** `bootstrap-local-discovery.properties` **file** add the following:  

```properties
#we didn't config any Eureka properties, Spring default to localhost.
#enable cloud discovery
spring.cloud.discovery.enabled=true

#discover the config server mssc-config-server
spring.cloud.config.discovery.service-id=mssc-config-server

```

**update active profiles** (i.e. edit configuration): e.g. `local,local-discovery`.

**Imporant** : we need to disable discovery if we want to work with local settings (e.g. `application-mysql.properties`), so we need to add `spring.cloud.discovery.enabled=false` to `application-mysql.properties`


Adding Zipkin and logstash
-----------
A few words on zipkin :
- Zipkin is an open source project used to report distributed tracing metrics
	- Information can be reported to Zipkin via webservices via HTTP
	- Optionally metrics can be provided via Kafka or Rabbit
- Zipkin is a Spring MVC project
	- Recommended to use binary distribution or Docker image
	- Building your own is not supported
- Uses in memory database for development
- Cassandra or Elasticsearch should be used for production

`docker run -d -p 9411:9411 openzipkin/zipkin`

**Spring Cloud Sleuth**
- org.springframework.cloud:spring-cloud-starter-sleuth: Starter for logging only.
- org.springframework.cloud:spring-cloud-starter-zipkin : Starter for Sleuth with Zipkin - includes Sleuth dependencies
- Property spring.zipkin.baseUrl is used to configure Zipkin server

**Example** : `DEBUG [beer-service,39853b63c1c3f919,419b9ac9a073bbba,true]`


**Dependency**:

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-zipkin</artifactId>
	<version>${zipkin-version}</version>
</dependency>
```

Add `spring.zipkin.base-url=http://localhost:9411/` to the global `application.properties` file to be aligned with zipkin docker local installation (e.g. `docker run -d -p 9411:9411 openzipkin/zipkin`).


Consolidated logging using "logstash/logback"
--------

Microservices typically will use consolidated logging:
- Number of different approaches for this - highly dependent on deployment environment
- To support consolidated logging, log data should be available in JSON
- Spring Boot by default uses logback, which is easy to configure for JSON output



**Dependency for logstash/logback**: to allow log consolidation in json.

```xml
dependency>
	<groupId>net.logstash.logback</groupId>
	<artifactId>logstash-logback-encoder</artifactId>
	<version>6.3</version>
</dependency>
```

We need to set a **custom config** in an xml file `logback-spring.xml` for the logstash, in resources folder.

**Important** : We want JSON object for every log message being written out to the console and later in the to be used for consolidated logging.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    ​
    <springProperty scope="context" name="springAppName" source="spring.application.name"/>

    <!-- You can override this to have a custom pattern -->
    <property name="CONSOLE_LOG_PATTERN"
              value="%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}"/>

    <!-- Appender to log to console in a JSON format -->
    <appender name="jsonConsole" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp>
                    <timeZone>UTC</timeZone>
                </timestamp>
                <version/>
                <logLevel/>
                <message/>
                <loggerName/>
                <threadName/>
                <context/>
                <pattern>
                    <omitEmptyFields>true</omitEmptyFields>
                    <pattern>
                        {
                        "severity": "%level",
                        "service": "${springAppName:-}",
                        "trace": "%X{X-B3-TraceId:-}",
                        "span": "%X{X-B3-SpanId:-}",
                        "parent": "%X{X-B3-ParentSpanId:-}",
                        "exportable": "%X{X-Span-Export:-}",
                        "baggage": "%X{key:-}",
                        "pid": "${PID:-}",
                        "thread": "%thread",
                        "class": "%logger{40}",
                        "rest": "%message"
                        }
                    </pattern>
                </pattern>
            </providers>
        </encoder>
    </appender>
    ​
    <root level="INFO">
        <appender-ref ref="jsonConsole"/>
    </root>
</configuration>
```



Adding basic auth to the microservice (e.g inventory)
--------
1- add spring security dependency

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2- add a config 

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityFilterConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }
}

```

3- add credentials in property file (not recommended, this a show case)

```properties
#credentials to log in to microservice RestApi
spring.security.user.name=legitimate
spring.security.user.password=ramsis
```


Add basic auth to the restTemplate
--------
1- Add the credentials to the properties file (not recommended and it will do for dev)

```properties
#credentials for restTemplate to authenticate in Inventory Service
sfg.brewery.inventory-user=legitimate
sfg.brewery.inventory-password=ramsis
```

2- adjust the restTemplate to add authentication when building it

```java
public BeerInventoryServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder,
        @Value("${sfg.brewery.inventory-user}") String inventoryServiceUser,
        @Value("${sfg.brewery.inventory-password}") String inventoryServicePassword) {
    this.restTemplate = restTemplateBuilder
                .basicAuthentication(inventoryServiceUser, inventoryServicePassword)
                .build();
    }
```


Adding basic auth for FeignClient
--------

1 - Create a config class that will allows to inject an interceptor bean

```java
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientBasicAuthInterceptorConfig {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(@Value("${sfg.brewery.inventory-user}") String inventoryUser,
                                                                   @Value("${sfg.brewery.inventory-password}")String inventoryPassword){
        return new BasicAuthRequestInterceptor(inventoryUser, inventoryPassword);
    }
}

```

2- make the annotated `InventoryServiceFeignClient` interface aware to use the `configuration` of type `FeignClientBasicAuthInterceptorConfig.class`.

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

/*
the feign client works like Spring Data JPA, We are going to provide an interface and decorate the interface with
some annotations and then at runtime Spring is going to provide an implementation for us.
*/

//name of inventory service used by eureka (inventory service application name)
//when it fails, it fallbacks on its BeerInventoryServiceFeignClientFailoverImpl implementation
@FeignClient(name = "beer-inventory-service", fallback = BeerInventoryServiceFeignClientFailoverImpl.class,
        configuration = FeignClientBasicAuthInterceptorConfig.class)
public interface InventoryServiceFeignClient {
    @RequestMapping(method = RequestMethod.GET, value = BeerInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<List<BeerInventoryDto>> getOnhandInventory(@PathVariable UUID beerId);
}
```