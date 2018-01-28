package net.taptech.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:kafka-consumer.properties")
@PropertySource("classpath:kafka-producer.properties")
public class KafkaConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Autowired
    private Environment env;

    /*
    @Bean
    KStream<String, String> wordCountSource(StreamsBuilder builder){
        KStream<String, String> source = builder.stream("streams-plaintext-input");
        source.flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
                .groupBy((key, value) -> value)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"))
                .toStream()
                .to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));
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

*/
}
