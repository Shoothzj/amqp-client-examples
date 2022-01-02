package com.github.shoothzj.amqp.client;

import com.rabbitmq.client.Delivery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RabbitmqProduceConsumeTest extends RabbitmqSuite {

    @Test
    @Timeout(60)
    void testProduceConsume() throws Exception {
        RabbitmqConfig.RabbitmqConfigBuilder rabbitmqConfigBuilder = new RabbitmqConfig.RabbitmqConfigBuilder();
        RabbitmqConfig rabbitmqConfig = rabbitmqConfigBuilder.amqpHost(RabbitmqConst.DEFAULT_HOST).amqpPort(amqpPort)
                .username("root").password("root123").queueName("test-queue").build();
        AtomicReference<Delivery> atomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new RabbitmqConsumerThread(rabbitmqConfig, (consumerTag, message) -> {
            atomicReference.set(message);
            countDownLatch.countDown();
        }).start();
        RabbitmqProducerService producerService = new RabbitmqProducerService(rabbitmqConfig);
        TimeUnit.SECONDS.sleep(3);
        producerService.sendMsg("test-message");
        countDownLatch.await();
        Delivery message = atomicReference.get();
        Assertions.assertNotNull(message);
    }

}
