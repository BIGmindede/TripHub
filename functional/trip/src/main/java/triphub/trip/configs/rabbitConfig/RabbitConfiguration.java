package triphub.trip.configs.rabbitConfig;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.exchange-notifications}")
    private String exchangeNotifications;

    @Value("${spring.rabbitmq.routing-key-notifications}")
    private String routingKeyNotifications;

    @Value("${spring.rabbitmq.exchange-reports}")
    private String exchangeReports;

    @Value("${spring.rabbitmq.routing-key-reports}")
    private String routingKeyReports;

    @Value("${spring.rabbitmq.exchange-kanban}")
    private String exchangeKanban;

    @Value("${spring.rabbitmq.routing-key-kanban}")
    private String routingKeyKanban;

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(exchangeNotifications);
    }

    @Bean
    public TopicExchange reportExchange() {
        return new TopicExchange(exchangeReports);
    }

    @Bean
    public TopicExchange kanbanExchange() {
        return new TopicExchange(exchangeKanban);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

