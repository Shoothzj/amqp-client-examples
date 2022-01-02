package com.github.shoothzj.amqp.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RabbitmqProducerInitRetry {

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("qpid-producer-init").build();

    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(1, threadFactory);

    private final RabbitmqConfig rabbitmqConfig;

    private volatile Channel channel;

    public RabbitmqProducerInitRetry(RabbitmqConfig rabbitmqConfig) {
        this.rabbitmqConfig = rabbitmqConfig;
    }

    public void init() {
        executorService.scheduleWithFixedDelay(this::initWithRetry, 0, 10, TimeUnit.SECONDS);
    }

    private void initWithRetry() {
        ConnectionFactory factory = rabbitmqConfig.acquireConnectionFactory();
        Connection connection = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(rabbitmqConfig.getQueueName(), false, false, false, null);
        } catch (Exception e) {
            StreamUtil.closeQuite(channel);
            StreamUtil.closeQuite(connection);
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
