package net.taptech.demo.mule.transformer

import groovy.json.JsonOutput
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.mule.api.MuleMessage
import org.mule.api.transformer.TransformerException
import org.mule.transformer.AbstractMessageTransformer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.nio.charset.Charset
import java.util.concurrent.Future
import java.util.stream.Collectors
import java.util.stream.Stream

@Component("consumerRecordListToJsonTransformer")
class BufferedInputStreamToStreamTransformer extends AbstractMessageTransformer{
    Logger logger = LoggerFactory.getLogger(BufferedInputStreamToStreamTransformer.class);

    @Override
    Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {

        Stream<String> lines = new BufferedReader(new InputStreamReader(muleMessage.getPayload(), Charset.defaultCharset())).lines();
        //Future<String> completableFuture
        return lines
    }

    def toMap(ConsumerRecord record){
        [topic: record.topic(),
         partition: record.partition(),
         offset: record.offset(),
         key: record.key(),
         value: record.value()]
    }

    BufferedInputStreamToStreamTransformer() {
        logger.info("Created ConsumerRecordListToJsonTransformer")
    }
}
