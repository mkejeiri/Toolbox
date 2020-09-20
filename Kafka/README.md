# Apache Kafka

**Kafka introduction**
----

- Created by **LinkedIn**, now **Open Source** Project maintained by **Confluent**
- **Distributed**, **resilent** architecture, **fault tolerant**
- **Horizontal scalability**:
	- Can scale to **100s** of **brokers**
	- Can scale to **1M messages per second**
- **High performance** (latency of less that 10 ms) - **real time**
- Used by **2000+firms **, 35% of the Fortune 500.
 
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
	- `acks=1` : `producer` will wait for a ** leader acknowledgement** (*limited data loss*)
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
