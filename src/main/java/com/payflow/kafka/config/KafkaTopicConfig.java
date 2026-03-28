package com.payflow.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String PAYMENT_CREATED_TOPIC  = "payment.created";
    public static final String PAYMENT_UPDATED_TOPIC  = "payment.status.updated";

    @Bean
    public NewTopic paymentCreatedTopic() {
        return TopicBuilder.name(PAYMENT_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)   // set to 3 in production with a multi-broker cluster
                .build();
    }

    @Bean
    public NewTopic paymentStatusUpdatedTopic() {
        return TopicBuilder.name(PAYMENT_UPDATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}