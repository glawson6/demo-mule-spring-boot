# demo-mule-spring-boot with Kafka connector from net.taptech

I wanted to experiemnt with Kafka and Mule with Maven utilizing the public repositories.
```

<dependency>
  <groupId>org.mule.modules</groupId>
  <artifactId>mule-module-kafka</artifactId>
  <version>2.1.0</version>
</dependency>
```

I was disappointed when the build came back with 

```

[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.851 s
[INFO] Finished at: 2018-01-28T01:20:13-05:00
[INFO] Final Memory: 25M/269M
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal on project demo-mule-spring-boot: Could not resolve dependencies for project net.taptech:demo-mule-spring-boot:jar:0.0.1-SNAPSHOT: Could not find artifact org.mule.modules:mule-module-kafka:jar:2.1.0 in Central (http://repo1.maven.org/maven2/) -> [Help 1]

```

After checking the repositories, I found that the connector was a select connector.

So I decided to build a an open source version of the connector using Apache Kafka clients and streams version 1.

Lets take a look at what I was able to build:

		<dependency>
			<groupId>net.taptech</groupId>
			<artifactId>mule-kafka-connector</artifactId>
			<version>1.0.0</version>
		</dependency>
		
My use cases were to use the connector as producers and consumers of kafka topics.

One, the consumer endoint can be used as an inbound endpoint:

```
<kafka:consumer config-ref="kafka-config" topic="streams-plaintext-input"/>
```

Place this in your Mule flows as you would any inbound endpoint.

Example:
```
<flow name="streams-plaintext-input">
        <kafka:consumer config-ref="kafka-config" topic="streams-plaintext-input"/>
        <foreach doc:name="Foreach" collection="#[payload.iterator()]">
            <logger message="consumer received from topic [#[payload.topic()]] key => [#[payload.key()]] value => [#[payload.value()]] "
                    category="net.taptech.kafka.mule.word-count-flow" level="INFO" doc:name="Consumed message logger"/>
        </foreach>
    </flow>
```

The payload returned is a List<ConsumerRecord<K, V>>. A Consumer record is defined [here.](https://kafka.apache.org/10/javadoc/org/apache/kafka/clients/consumer/ConsumerRecord.html) 

There is a similar producer as well:

```
 <kafka:producer config-ref="kafka-config" topic="streams-plaintext-input" key="#[flowVars['recordKey']]"
                        message="#[payload]"/>
```

One of the current limitations is the key and message should be of type String. 

of course there is a config and it looks similar to this:
```
<kafka:config name="kafka-config" consumerPropertiesFile="kafka-consumer.properties"
                  producerPropertiesFile="kafka-producer.properties"/>
```

One of the big features of Kafka is the ability the go back and get messages. I also provide a way to get messages from
an offset.  

```
<kafka:seek config-ref="kafka-config" partition="#[flowVars.partition]" topic="#[flowVars.topic]" offset="#[flowVars.offset]"/>
```

This also returns a List<ConsumerRecord<K, V>> to the payload.


