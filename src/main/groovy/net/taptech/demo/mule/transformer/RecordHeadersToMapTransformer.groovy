package net.taptech.demo.mule.transformer

import groovy.json.JsonOutput
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.mule.api.MuleMessage
import org.mule.api.transformer.TransformerException
import org.mule.api.transport.PropertyScope
import org.mule.transformer.AbstractMessageTransformer
import org.mule.util.SerializationUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@Component("recordHeadersToMapTransformer")
class RecordHeadersToMapTransformer extends AbstractMessageTransformer{
    Logger logger = LoggerFactory.getLogger(RecordHeadersToMapTransformer.class);

    @Override
    Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {
        def payload = muleMessage.getPayload()
        payload = toMap payload
        def json = JsonOutput.toJson(payload)
        muleMessage.addProperties(payload,PropertyScope.OUTBOUND)
        return json.toString()
    }

    def toMap(ConsumerRecord record){

        Map<String, Object> aMap =  Arrays.asList(record.headers().toArray()).stream()
        .collect(Collectors.toConcurrentMap({header -> header.key()},
                {header -> SerializationUtils.deserialize(header.value())}));
       aMap
    }

    RecordHeadersToMapTransformer() {
        logger.info("Created RecordHeadersToMapTransformer")
    }
}
