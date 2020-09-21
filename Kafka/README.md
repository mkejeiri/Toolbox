# Apache Kafka

**Kafka introduction**
----

- Created by **LinkedIn**, now **Open Source** Project maintained by **Confluent**
- **Distributed**, **resilent** architecture, **fault tolerant**
- **Horizontal scalability**:
	- Can scale to **100s** of **brokers**
	- Can scale to **1M messages per second**
- **High performance** (latency of less that 10 ms) - **real time**
- Used by **2000+firms**, 35% of the Fortune 500.
 
**What's for?**:

- **Messaging system**
- **Activity tracking**
- **Gather metrics** from many locations
- Application **Logs gathering**
- **Stream processing** (with Kafka `Streams Api` or `Spark` for example)
- **Decoupling** of data streams and system
- **Intregration** with `Spark`, `Flink`, `Storm`, `Hadoop`, and many other `Big Data` technologies

![pic](images/decouling-system.jpg)



# Topics, Partitions and offsets

**Topics**
----

- `Topics`: a particular **stream of data**
	- Similar to a `table` in a `database` (without all **constraints**) 
	- You can have as **many** as topics
	- A **topic** is identified by its **name**
- `Topics` are split into `partitions`
	- Each `partition` is **ordered**
	- Each **message** within a `partition` gets an incremental `id`, called `offset`
	- No **ordering** across `partitions`
	
![pic](images/partitions.jpg)

- `Offset` only have a meaning for a **specific** `partition`: e.g. `offset 3` in `partition 0` **doesn't represent** the same **data** as `offset 3` in `partition 1`
- **order** is **guaranteed** only within a `partition` and not **across partitions**
- **Data** is kept only for a **limited time** (one week by default)
- Once the **data** is **written** into a `partition`, it **CANNOT be changed** (immutability)
- **Data** is assigned **randomly** to a `partition` unless a key is provided

**Brokers**
----
- A **kafka** clusters composed of **multiple brokers** (**servers**)
- Each broker is **identified** with its `ID` (**integer**)
- After **connecting to any broker** (called a **boostrap broker**), you will be **connected** to the **entire cluster**
- a **good number** to get started is **3 brokers**, but some big clusters have over a **100 brokers**
- In these examples we choose to number brokers starting at 100 (arbitrary)
- Example of `topic-A` with 3 partitions
- Example of `topic-B` with 3 partitions

![pic](images/brokers.jpg)


**Topic replication factor**
----
- **Topics** should have a **replication factor**> 1 (usually > = 3)
- This way if a broker is **down**, another **broker** can **serve the data**
- Example: `Topic-A` with `2 partitions` and **replication** `factor of 2`

![pic](images/replication-factor.jpg)

> if we **lost** `broker 102`, either `broker 101` or `broker 103` can still **serve the data**.


**Partition Leader**
----
- Only once **broker** can be **leader** for a `partition` at any given time
- Only the that **leader** can **receive and serve** the `data` for a `partition`
- The **other brokers** will **synchronize** the `data`
- As a result, each `partition` has one and only one **leader at any given moment** and **multiple ISR** (In-Sync Replica's) 
![pic](images/partition-leader.jpg)


**Producers**
----
- **Producers** `write` data into `topics` (which made of `partitions`)
- **Producers** automatically **know** to which **broker** and `partition` to **write to**
- In case of **broker failures**, **producers** will automatically **recover**
- **Producers** can choose **acknowledgemen** of **data writes in 3 ways**: 
	- `acks=0` : `producer` won't wait for **acknowledgement**
	- `acks=1` : `producer` will wait for a **leader acknowledgement** (*limited data loss*)
	- `acks=all` :`producer` will wait for a **leader & replicas acknowledgement**  (*no data loss*)
	
![pic](images/producers.jpg)


- **Producers** can choose to send a **key** with the `message`(`string`, `number`, ect ...)
- If `key=null`, data is sent in `round robin` manner (`broker 101`, `broker 102` , ...)
- If a **key** is sent, then all **messages** for that `key` will always go to the **same** `partition`
- A `key` is basically **sent** if you **need message ordering** for a **specific fields**
		- e.g. *truck_id*
![pic](images/producers-key.jpg)		


**Consumers**
----	
- **Consumers**	**read data** from a `topic` (identified by `name`)
- **Consumers**	know which `broker` to **read from**
- In case of **broker failures**, **consumers** know how to **recover**
	
![pic](images/Consumers.jpg)	
	
	
	
**Consumer Group**
----	

- **Consumers**	**read data** in consumer **groups**
- Each **consumer** within a **group** reads from **exclusive partitions**
- If you have more consumer than **partition**, some **consumer** will be **inactive**
![pic](images/consumer-groups.jpg)	


- If we have **more consumers** than `partitions`, some **consumers** will be `inactive`
 ![pic](images/inactive-consumer.jpg)	
 
 
 
 	
**Consumer Offsets**
----	

- `Kafka` stores the `offsets` at which a **consumer group** has been reading
- The `offsets` committed live in a Kafka `topic` named `___consumer_offsets`
- When a `consumer` in a `group` has **processed data** received from `kafka`, **_it should commit the offsets!_**
- If a `consumer` **dies**, it will be able to **read back** from where it **left off** thanks to the **committed consumer** `offsets`!
 ![pic](images/consumer-offsets.jpg)	
 
 	
**Delivery semantics for consumers**
----	

**Consumers** choose when to **commit** `offsets` , there is three delivery semantics :

- **At most once**
	- **offsets** are committed as soon as the message is received.
	- If the processing goes wrong, the message will be **lost** (it won't be read again)
- **At least once** :
	- `offsets` are **committed** after the **message** is **processed**.
	- If the **processing** goes wrong, the `message` will be **read again**.
	- This can result in **duplicate processing** of messages if the processing is not `idempotent`.
- **Exactly once**:
	- Can be **achieved** for **Kafka** => **Kafka workflow** using `Kafka Streams Api`
	- For **Kafka** => **External System workflows**, use an `idempotent consumer`.
	
	> `idempotent` : means processing again the messages won't have any impact on the systems.
	
	
**Kafka Broker Discovery**
----	
- Every Kafka broker is also called a `bootstrap server`.
- Since each `broker` already **knows** about all **others brokers**, `topics` and `partitions` (`metadata`), this means that we need to **connect** only to **one broker** and then we will be **connected** to the entire **cluster**.
	
 ![pic](images/broker-discovery.jpg)
 
 
**Zookeeper**
----	
- **Manages all brokers** (keep a list of them)
- performs **leader elections** for `partitions`
- **send notifications** to `Kafka` in case of **changes** (e.g. new topic, broker dies, broker comes up, delete topic, etc,...)
- `Kafka` **CANNOT work without Zookeeper**
- It **operates** by design with an **odd number** (3,5,7)
- It has a **leader** that handles `writes`, and **followers** (i.e. rest of the servers) that handles `reads` only.
- Zookeeper **does NOT store** `consumer offset` anymore since `Kafka V0.10+`
  ![pic](images/zookeeper.jpg)
 
**Kafka Guarantees**
----	
- **Messages** are appended to a `topic-partition` in the order they are sent.
- **Consumers** read messages in the **order stored** in a **topic-partition**.
- With a **replication factor of N**, `producers` and `consumers` can tolerate up to `N-1 brokers` being **down**.
- This is why a **replication of 3 seems wise**:
	- Allows for **one broker** to be **taken down** for **maintenance**.
	- Allows for **another broker** to be **taken down unexpectedly**.	
- As long as the **number of partitions** remains **constant** for a `topic` (**no new partitions**), the same `key` will always go to the **same** `partition`.

![pic](images/summary.jpg)


# Ubuntu 18.4 Insallation 

1 - ` wget https://downloads.apache.org/kafka/2.6.0/kafka_2.13-2.6.0.tgz`.

2 - `tar xzf kafka_2.13-2.6.0.tgz  -C /opt/`.

3 - ` dev@slave-node:/opt/kafka_2.13-2.6.0$ mkdir data`.

4 - ` dev@slave-node:/opt/kafka_2.13-2.6.0$ mkdir data/zookeeper`.

5 - ` vim config/zookeeper.properties `  & **change** `dataDir=/opt/kafka_2.13-2.6.0/data/zookeeper` and **save**.

6 - start zookeeper ` dev@slave-node:/opt/kafka_2.13-2.6.0$ zookeeper-server-start.sh  config/zookeeper.properties`. or `dev@slave-node:~$ zookeeper-server-start.sh  /opt/kafka_2.13-2.6.0/config/zookeeper.properties`.

7 - check if version-2 is created .
```sh
dev@slave-node:/opt/kafka_2.13-2.6.0$ ls data/zookeeper/
version-2
```
8- Create **kafka folder** which holds **metadata** : `mkdir /opt/kafka_2.13-2.6.0/data/kafka`

9- change `log.dirs` in `server.properties` file : `vim /opt/kafka_2.13-2.6.0/config/server.properties` and then `log.dirs=/opt/kafka_2.13-2.6.0/data/kafka` , and **save**

10- start kafka servers : `kafka-server-start.sh /opt/kafka_2.13-2.6.0/config/server.properties`

11- [install kafka tool](https://www.kafkatool.com/download.html) and connect to `localhost:2181`


# Windows 10 Insallation 

1- [GET KAFKA](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.6.0/kafka_2.13-2.6.0.tgz),    [see quick start](https://kafka.apache.org/quickstart)

2- use the winrar  and put the folder [kafka_x.xx-x.x.x] in a `c:\` drive

3- Add the  `c:\[kafka_x.xx-x.x.x]\bin\windows` to the Path environment variable

4- Java 8 is required : Install it if not done yet. 

5- inside  `c:\[kafka_x.xx-x.x.x]\` Create folder `data\logs` `data\zooKeeper`.

6- Change the `dataDir=C:\[kafka_x.xx-x.x.x]\data\zookeeper` inside `C:\[kafka_x.xx-x.x.x]\config\zookeeper.properties` 

7- Change the `log.dirs=C:\kafka_2.13-2.6.0\data\logs` inside `C:\[kafka_x.xx-x.x.x]\config\server.properties` 

8- Start the ZooKeeper service : `zookeeper-server-start.bat C:\[kafka_x.xx-x.x.x]\config\zookeeper.properties`
8- Start the Kafka broker service : `kafka-server-start.bat C:\[kafka_x.xx-x.x.x]\config\server.properties`

> Soon, ZooKeeper will no longer be required by Apache Kafka.


**Kafka Console Producer CLI**
----

- `kafka-console-producer.bat --broker-list 127.0.0.1:9092 --topic first_topic`
```
>hello there
```
- `kafka-console-producer.bat --broker-list 127.0.0.1:9092 --topic first_topic --producer-property acks=all`
```
>hi again how are
>I thought you were sleeping
```
> `acks=all` the producer has to wait for **acknowledgment** from both the **leader** and **In-Sync/replicas partition** (zero loss data)

- `kafka-console-producer.bat --broker-list 127.0.0.1:9092 --topic new_topic` : new_topic doesn't exist yet!
```
>hi there new_topic
[2020-09-21 18:12:15,897] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
>another message
>
```

> If we go to broker console `[2020-09-21 18:12:15,894] INFO [KafkaApi-0] Auto creation of topic new_topic with 1 partitions and replication factor 1 is successful (kafka.server.KafkaApis)` : `new_topic` was created but the leader election for the partition is not happened yet! that why we get the exception/WARN `{new_topic=LEADER_NOT_AVAILABLE}`. The producer is able to recover from errors, i.e. the producer was able to produce the message afterward (e.g. we still get `hi there new_topic` to the partition). 


**all kafka-console-producer.bat arguments**

```ssh
Missing required option(s) [bootstrap-server]
Option                                   Description
------                                   -----------
--batch-size <Integer: size>             Number of messages to send in a single
                                           batch if they are not being sent
                                           synchronously. (default: 200)
--bootstrap-server <String: server to    REQUIRED unless --broker-list
  connect to>                              (deprecated) is specified. The server
                                           (s) to connect to. The broker list
                                           string in the form HOST1:PORT1,HOST2:
                                           PORT2.
--broker-list <String: broker-list>      DEPRECATED, use --bootstrap-server
                                           instead; ignored if --bootstrap-
                                           server is specified.  The broker
                                           list string in the form HOST1:PORT1,
                                           HOST2:PORT2.
--compression-codec [String:             The compression codec: either 'none',
  compression-codec]                       'gzip', 'snappy', 'lz4', or 'zstd'.
                                           If specified without value, then it
                                           defaults to 'gzip'
--help                                   Print usage information.
--line-reader <String: reader_class>     The class name of the class to use for
                                           reading lines from standard in. By
                                           default each line is read as a
                                           separate message. (default: kafka.
                                           tools.
                                           ConsoleProducer$LineMessageReader)
--max-block-ms <Long: max block on       The max time that the producer will
  send>                                    block for during a send request
                                           (default: 60000)
--max-memory-bytes <Long: total memory   The total memory used by the producer
  in bytes>                                to buffer records waiting to be sent
                                           to the server. (default: 33554432)
--max-partition-memory-bytes <Long:      The buffer size allocated for a
  memory in bytes per partition>           partition. When records are received
                                           which are smaller than this size the
                                           producer will attempt to
                                           optimistically group them together
                                           until this size is reached.
                                           (default: 16384)
--message-send-max-retries <Integer>     Brokers can fail receiving the message
                                           for multiple reasons, and being
                                           unavailable transiently is just one
                                           of them. This property specifies the
                                           number of retires before the
                                           producer give up and drop this
                                           message. (default: 3)
--metadata-expiry-ms <Long: metadata     The period of time in milliseconds
  expiration interval>                     after which we force a refresh of
                                           metadata even if we haven't seen any
                                           leadership changes. (default: 300000)
--producer-property <String:             A mechanism to pass user-defined
  producer_prop>                           properties in the form key=value to
                                           the producer.
--producer.config <String: config file>  Producer config properties file. Note
                                           that [producer-property] takes
                                           precedence over this config.
--property <String: prop>                A mechanism to pass user-defined
                                           properties in the form key=value to
                                           the message reader. This allows
                                           custom configuration for a user-
                                           defined message reader. Default
                                           properties include:
        parse.
                                           key=true|false
        key.separator=<key.
                                           separator>
        ignore.error=true|false
--request-required-acks <String:         The required acks of the producer
  request required acks>                   requests (default: 1)
--request-timeout-ms <Integer: request   The ack timeout of the producer
  timeout ms>                              requests. Value must be non-negative
                                           and non-zero (default: 1500)
--retry-backoff-ms <Integer>             Before each retry, the producer
                                           refreshes the metadata of relevant
                                           topics. Since leader election takes
                                           a bit of time, this property
                                           specifies the amount of time that
                                           the producer waits before refreshing
                                           the metadata. (default: 100)
--socket-buffer-size <Integer: size>     The size of the tcp RECV size.
                                           (default: 102400)
--sync                                   If set message send requests to the
                                           brokers are synchronously, one at a
                                           time as they arrive.
--timeout <Integer: timeout_ms>          If set and the producer is running in
                                           asynchronous mode, this gives the
                                           maximum amount of time a message
                                           will queue awaiting sufficient batch
                                           size. The value is given in ms.
                                           (default: 1000)
--topic <String: topic>                  REQUIRED: The topic id to produce
                                           messages to.
--version                                Display Kafka version.
```

**get the list of topics through zookeeper**


```ssh
kafka-topics --zookeeper 127.0.0.1:2181 --list
#output
first_topic
new_topic
second_topic
```

```ssh
kafka-topics --zookeeper 127.0.0.1:2181 --topic new_topic --describe
#output : new_topic has one partition (partition 0) and one replication factor
Topic: new_topic        PartitionCount: 1       ReplicationFactor: 1    Configs:
        Topic: new_topic        Partition: 0    Leader: 0       Replicas: 0     Isr: 0
```
**Changing the config new topic to have 3 partitions by default**
- Go to `C:\[kafka_x.xx-x.x.x]\config\server.properties`  and update `num.partitions=3`
- Stop the broker and start it: `kafka-server-start.sh /opt/kafka_2.13-2.6.0/config/server.properties`

```ssh
kafka-console-producer.bat --broker-list 127.0.0.1:9092 --topic third_topic --producer-property acks=all
>hi there (WARN)
[2020-09-21 21:17:10,647] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {third_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
>Terminate batch job (Y/N)? y


kafka-topics --zookeeper 127.0.0.1:2181 --list
#output
first_topic
new_topic
second_topic
third_topic


kafka-topics --zookeeper 127.0.0.1:2181 --topic third_topic --describe
Topic: third_topic      PartitionCount: 3       ReplicationFactor: 1    Configs:
        Topic: third_topic      Partition: 0    Leader: 0       Replicas: 0     Isr: 0
        Topic: third_topic      Partition: 1    Leader: 0       Replicas: 0     Isr: 0
        Topic: third_topic      Partition: 2    Leader: 0       Replicas: 0     Isr: 0
```

> **Best practices**: create topics before using them. 
