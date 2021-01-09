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




