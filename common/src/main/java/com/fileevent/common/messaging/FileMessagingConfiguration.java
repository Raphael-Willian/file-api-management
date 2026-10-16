package com.fileevent.common.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class FileMessagingConfiguration {

    @Bean
    public TopicExchange fileEventsExchange() {
        return new TopicExchange(FileMessagingContract.EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange fileDeadLetterExchange() {
        return new DirectExchange(FileMessagingContract.EVENT_DLX, true, false);
    }

    @Bean
    public Queue deliveryQueue() {
        return QueueBuilder
                .durable(FileMessagingContract.DELIVERY_QUEUE)
                .deadLetterExchange(FileMessagingContract.EVENT_DLX)
                .deadLetterRoutingKey(FileMessagingContract.DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue deliveryDeadLetterQueue() {
        return QueueBuilder
                .durable(FileMessagingContract.DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean
    public Binding deliveryBinding(
            @Qualifier("deliveryQueue") Queue queue,
            @Qualifier("fileEventsExchange") TopicExchange exchange
    ) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(FileMessagingContract.RK_DELIVERY);
    }

    @Bean
    public Binding deadLetterBinding(
            @Qualifier("deliveryDeadLetterQueue") Queue queue,
            @Qualifier("fileDeadLetterExchange") DirectExchange exchange
    ) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(FileMessagingContract.DEAD_LETTER_ROUTING_KEY);
    }
}
