package net.taptech.demo.config;


import net.taptech.demo.DemoMuleSpringBootApplication;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;

import java.util.*;

//@Configuration
//@PropertySource("classpath:kafka-consumer.properties")
//@PropertySource("classpath:kafka-producer.properties")
public class KafkaConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Autowired
    private Environment env;


    @Bean
    KStream<String, String> wordCountSource(StreamsBuilder builder){
        KStream<String, String> source = builder.stream("streams-numbers-count-input1");
        source.flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
                .groupBy((key, value) -> value)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"))
                .toStream()
                .to("streams-numbers-count-output1", Produced.with(Serdes.String(), Serdes.Long()));
        return source;
    }

    @Bean
    public StreamsBuilderFactoryBean myKStreamBuilder(StreamsConfig streamsConfig) {
        return new StreamsBuilderFactoryBean(streamsConfig);
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "mc1");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("bootstrap.servers"));
        return new StreamsConfig(props);
    }


    @Bean
    KafkaStreams createStreams(StreamsBuilder builder, KStream<String, String> source){
        final Topology topology = builder.build();
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "mc1");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("bootstrap.servers"));
        final KafkaStreams streams = new KafkaStreams(topology, props);
        return streams;
    }


}
