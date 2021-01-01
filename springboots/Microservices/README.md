### Considerations

run  ActiveMq artemis on docker as follow :

`docker run -d  -e ARTEMIS_USERNAME=username -e ARTEMIS_PASSWORD=password  -p 8161:8161 -p 61616:61616 -p 36118:36118 vromero/activemq-artemis`
