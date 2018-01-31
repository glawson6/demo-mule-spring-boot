package net.taptech.demo.mule.transformer

import groovy.json.JsonOutput
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.mule.api.MuleMessage
import org.mule.api.transformer.TransformerException
import org.mule.transformer.AbstractMessageTransformer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@Component("recordMetadataListToJsonTransformer")
class RecordMetadataListToJsonTransformer extends AbstractMessageTransformer{
    Logger logger = LoggerFactory.getLogger(RecordMetadataListToJsonTransformer.class);

    @Override
    Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {
        logger.info("Got payload {}",muleMessage.getPayload().toString())
        def payload = muleMessage.getPayload()
        if (payload instanceof  List){
            List<RecordMetadata> temp = (List<RecordMetadata>)payload
            List<Map<String, String>> newMap = temp.stream()
                .map({record -> toMap record})
                .collect(Collectors.toList())
            payload = newMap
        } else {
            payload = toMap payload
        }
        logger.info("Got payload {}",payload.toString())
        def json = JsonOutput.toJson(payload)
        return json.toString()
    }

    def toMap(RecordMetadata record){
        [topic: record.topic(),
         partition: record.partition(),
         offset: record.offset()]
    }

    RecordMetadataListToJsonTransformer() {
        logger.info("Created RecordMetadataListToJsonTransformer")
    }
}
