package com.tunderhill;

import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.protobuf.ByteString;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.io.kafka.KafkaRecord;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DataflowPipeline {

    private static final Logger logger = LogManager.getLogger(DataflowPipeline.class);

    public static void main(String[] args) throws IOException {

        PipelineOptionsFactory.register(CustomPipelineOptions.class);
        CustomPipelineOptions options = PipelineOptionsFactory.fromArgs(args)
                .withValidation()
                .as(CustomPipelineOptions.class);

        String username = decryptKey(options.getProject(), options.getRegion(), options.getkeyRing(), options.getkmsUsernameKeyId(), Base64.getDecoder().decode(options.getConfluentCloudEncryptedUsername().getBytes("UTF-8")));
        String password = decryptKey(options.getProject(), options.getRegion(), options.getkeyRing(), options.getkmsPasswordKeyId(), Base64.getDecoder().decode(options.getConfluentCloudEncryptedPassword().getBytes("UTF-8")));

        Map<String, Object> props = new HashMap<>();
        props.put("auto.offset.reset", "earliest");
        props.put("ssl.endpoint.identification.algorithm", "https");
        props.put("sasl.mechanism", "PLAIN");
        props.put("request.timeout.ms", 20000);
        props.put("retry.backoff.ms", 500);
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.jaas.config",String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",username, password));

        LogKafkaMsg logKafkaMsg = new LogKafkaMsg();

        Pipeline pipeline = Pipeline.create(options);

        pipeline.apply(
                "ConsumeFromConfluentCloud",
                KafkaIO.<String, String>read()
                    .withBootstrapServers("<bootstrap server>")
                    .withTopic("orders")
                    .withConsumerConfigUpdates(props)
                    .withKeyDeserializer(StringDeserializer.class)
                    .withValueDeserializer(StringDeserializer.class)
        ).apply(ParDo.of(logKafkaMsg));

        pipeline.run().waitUntilFinish();
    }

    public static class LogKafkaMsg extends DoFn<KafkaRecord<String, String>, KV<String, String>> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            logger.info("key: " + c.element().getKV().getKey() + " value: " + c.element().getKV().getValue());
        }
    }

    public static String decryptKey(String kmsKey, byte[] ciphertext) throws IOException {
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {

            CryptoKeyName keyName = CryptoKeyName.of(gcpProject, location, keyRing, kmsKey);
            DecryptResponse response = client.decrypt(keyName, ByteString.copyFrom(ciphertext));

            return response.getPlaintext().toStringUtf8();
        }
    }
}