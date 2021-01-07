# Eureka Server

### Eureka run as a cluster!
setting the default port for eureka :

```
server.port=8761
```



Eureka itself is designed to run as a cluster, so we'll have a cluster of Eureka servers going. Here, we are running locally, we don't want that to happen.
Eureka servers won't be registering with Eureka nor is it will fetch a registry


```
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```


Spring is recommending to log those levels off if we ever need to get into debugging.
```
logging.level.com.netflix.eureka=off
logging.level.com.netflix.discovery=off
```