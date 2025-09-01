package com.ecommerceapi.ecommerceapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducerService {

    @Value("${spring.kafka.topic}")
    private String kafkaTopic;
    ObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * case 1: This method is only to send the Text Message to Kafka Topic
     **/
    public void sendTextMessage(String message) {
        kafkaTemplate.send(kafkaTopic, message);
    }

    public CompletableFuture<SendResult<String, String>> sendResultAsync(String user) throws JsonProcessingException, ExecutionException, InterruptedException {
        String key  = user;
        String value = objectMapper.writeValueAsString(user);

        Header headers = new RecordHeader("delivery-topic", "delivery-topic".getBytes());
        List<Header> headerList = List.of(headers);
        var record =  new ProducerRecord<>(kafkaTopic, null, key, value, headerList);

        CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(record);
        return completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                handleFailure(throwable);
            } else {
                handleSuccess(key, value, result);
            }
        });
    }

    private void handleSuccess(String key, String value, SendResult<String, String> sendresult) {
        System.out.println("Message sent successfully for the key : " + key + " and the value is : " + value + " and the partition is : " + sendresult.getRecordMetadata().partition());
    }

    private void handleFailure(Throwable ex) {
        System.out.println("Error while sending kafka message to topic " + ex.getMessage());
    }

}
