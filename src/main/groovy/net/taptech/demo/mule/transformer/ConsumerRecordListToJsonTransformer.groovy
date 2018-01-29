package net.taptech.demo.mule.transformer

import groovy.json.JsonOutput
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.mule.api.MuleMessage
import org.mule.api.transformer.TransformerException
import org.mule.transformer.AbstractMessageTransformer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@Component("consumerRecordListToJsonTransformer")
class ConsumerRecordListToJsonTransformer extends AbstractMessageTransformer{
    Logger logger = LoggerFactory.getLogger(ConsumerRecordListToJsonTransformer.class);

    @Override
    Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {
        logger.info("Got payload {}",muleMessage.getPayload().toString())
        def payload = muleMessage.getPayload()
        if (payload instanceof  List){
            List<ConsumerRecord> temp = (List<ConsumerRecord>)payload
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

    def toMap(ConsumerRecord record){
        [topic: record.topic(),
         partition: record.partition(),
         offset: record.offset(),
         key: record.key(),
         value: record.value()]
    }

    ConsumerRecordListToJsonTransformer() {
        logger.info("Created ConsumerRecordListToJsonTransformer")
    }
}
