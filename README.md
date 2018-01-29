# Using Mule with Kafka connector from net.taptech

I wanted to experiment with Kafka and Mule with Maven utilizing the public repositories. Their conector is:
```
<dependency>
  <groupId>org.mule.modules</groupId>
  <artifactId>mule-module-kafka</artifactId>
  <version>2.1.0</version>
</dependency>
```

I was disappointed when the build came back with: 

```

[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.851 s
[INFO] Finished at: 2018-01-28T01:20:13-05:00
[INFO] Final Memory: 25M/269M
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal on project demo-mule-spring-boot: Could not resolve dependencies for project net.taptech:demo-mule-spring-boot:jar:0.0.1-SNAPSHOT: Could not find artifact org.mule.modules:mule-module-kafka:jar:2.1.0 in Central (http://repo1.maven.org/maven2/) -> [Help 1]

```

After checking the repositories, I found that the connector was a select connector. Information on Mule connectors 
can be found [here.](https://docs.mulesoft.com/mule-user-guide/v/3.9/anypoint-connectors)

So I decided to build a an open source version of the connector using Apache Kafka clients and streams version 1.

Lets take a look at what I was able to build:

		<dependency>
			<groupId>net.taptech</groupId>
			<artifactId>mule-kafka-connector</artifactId>
			<version>1.0.0</version>
		</dependency>
		
My use cases were to use the connector as producers and consumers of kafka topics.

The consumer endoint can be used as an inbound endpoint:

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
kafka-consumer.propertie and kafka-producer.properties should be available on the classpath.

One of the big features of Kafka is the ability to go back and get messages. I also provide a way to get messages from
an offset.  

```
<kafka:seek config-ref="kafka-config" partition="#[flowVars.partition]" topic="#[flowVars.topic]" offset="#[flowVars.offset]"/>
```

This also returns a List<ConsumerRecord<K, V>> to the payload.

After starting kafka locally, run this command:

```
./mvnw clean package -Dmaven.test.skip=true && java -jar target/demo-mule-spring-boot-0.0.1-SNAPSHOT.jar
```
Here is the sample mule-config.xml
```
<?xml version="1.0" encoding="UTF-8"?>

<mule xmlns:http="http://www.mulesoft.org/schema/mule/http" xmlns="http://www.mulesoft.org/schema/mule/core"
      xmlns:doc="http://www.mulesoft.org/schema/mule/documentation"
      xmlns:kafka="http://www.mulesoft.org/schema/mule/kafka"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd
      http://www.mulesoft.org/schema/mule/kafka http://www.mulesoft.org/schema/mule/kafka/current/mule-kafka.xsd
http://www.mulesoft.org/schema/mule/http http://www.mulesoft.org/schema/mule/http/current/mule-http.xsd">
    <http:listener-config name="HTTP_Listener_Configuration" host="0.0.0.0" port="8081"
                          doc:name="HTTP Listener Configuration"/>

    <kafka:config name="kafka-config" consumerPropertiesFile="kafka-consumer.properties"
                  producerPropertiesFile="kafka-producer.properties"/>

    <flow name="mule-demo-filesFlow1">
        <http:listener config-ref="HTTP_Listener_Configuration" path="/payload" allowedMethods="POST" doc:name="HTTP"/>

        <logger message="New message #[message] arrived: #[payload]" category="org.mule.module.kafka" level="INFO"
                doc:name="Consumed message logger"/>
        <object-to-string-transformer/>
        <set-variable variableName="recordKey" value="#[java.util.UUID.randomUUID().toString()]"/>
        <kafka:producer config-ref="kafka-config" topic="streams-plaintext-input" key="#[flowVars['recordKey']]"
                        message="#[payload]"/>
        <object-to-string-transformer/>
    </flow>

    <flow name="mule-demo-filesFlow2">
        <http:listener config-ref="HTTP_Listener_Configuration" path="/payload" allowedMethods="GET" doc:name="HTTP"/>
        <set-variable variableName="offset" value="#[message.inboundProperties.'http.query.params'.offset]"/>
        <set-variable variableName="partition" value="#[message.inboundProperties.'http.query.params'.partition]"/>
        <set-variable variableName="topic" value="#[message.inboundProperties.'http.query.params'.topic]"/>

        <logger message="New message #[message] arrived: #[payload]" category="org.mule.module.kafka" level="INFO"
                doc:name="Consumed message logger"/>
        <kafka:seek config-ref="kafka-config" partition="#[flowVars.partition]" topic="#[flowVars.topic]" offset="#[flowVars.offset]"/>
        <object-to-string-transformer/>
    </flow>


    <flow name="streams-plaintext-input">
        <kafka:consumer config-ref="kafka-config" topic="streams-plaintext-input"/>
        <foreach doc:name="Foreach" collection="#[payload.iterator()]">
            <logger message="consumer received from topic [#[payload.topic()]] key => [#[payload.key()]] value => [#[payload.value()]] "
                    category="net.taptech.kafka.mule.word-count-flow" level="INFO" doc:name="Consumed message logger"/>
        </foreach>
    </flow>

</mule>

```


