## Considerations

Before running the microservices, we need to start up an activeMQ artemis from docker as follows:

- `docker run -d  -e ARTEMIS_USERNAME=username -e ARTEMIS_PASSWORD=password  -p 8161:8161 -p 61616:61616 -p 36118:36118 vromero/activemq-artemis`
- check that the ActiveMq broker is up and running : 
	- `http://localhost:8161/console/`
	- credentials : `username/password`
	

[More info: https://github.com/vromero/activemq-artemis-docker](https://github.com/vromero/activemq-artemis-docker)