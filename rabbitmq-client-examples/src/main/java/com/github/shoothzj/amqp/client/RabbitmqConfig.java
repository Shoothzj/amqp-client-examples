package com.github.shoothzj.amqp.client;

import com.rabbitmq.client.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class RabbitmqConfig {

    private String amqpHost;

    private int amqpPort;

    private String username;

    private String password;

    private String queueName;

    public RabbitmqConfig() {
    }

    public ConnectionFactory acquireConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(amqpHost);
        factory.setPort(amqpPort);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

}
