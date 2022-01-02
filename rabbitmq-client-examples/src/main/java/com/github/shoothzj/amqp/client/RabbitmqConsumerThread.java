package com.github.shoothzj.amqp.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RabbitmqConsumerThread extends Thread {

    private final RabbitmqConfig rabbitmqConfig;

    private final DeliverCallback deliverCallback;

    public RabbitmqConsumerThread(RabbitmqConfig rabbitmqConfig, DeliverCallback deliverCallback) {
        this.rabbitmqConfig = rabbitmqConfig;
        this.deliverCallback = deliverCallback;
        this.setName("rabbitmq-consumer-" + rabbitmqConfig.getQueueName());
    }

    @Override
    public void run() {
        super.run();
        while (!initRabbitmqConsumer()) {
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean initRabbitmqConsumer() {
        ConnectionFactory factory = rabbitmqConfig.acquireConnectionFactory();
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(rabbitmqConfig.getQueueName(), false, false, false, null);
            channel.basicConsume(rabbitmqConfig.getQueueName(), true, deliverCallback, consumerTag -> {
            });
        } catch (Exception e) {
            log.error("init rabbitmq consumer error ", e);
            StreamUtil.closeQuite(channel);
            StreamUtil.closeQuite(connection);
            return false;
        }
        return true;
    }

}
