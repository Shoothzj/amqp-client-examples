package com.github.shoothzj.qpid.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javax.jms.DeliveryMode;
import javax.jms.Message;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
class QpidProduceConsumeTest extends QpidServerSuite {

    @Test
    @Timeout(60)
    void testProduceConsume() throws Exception {
        QpidConfig.QpidConfigBuilder qpidConfigBuilder = new QpidConfig.QpidConfigBuilder();
        QpidConfig qpidConfig = qpidConfigBuilder.qpidHost(QpidConst.DEFAULT_HOST).qpidPort(QpidConst.DEFAULT_PORT)
                .vhost("default").username("guest").password("guest").queueName("test-queue").build();
        AtomicReference<Message> atomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new QpidConsumerThread(qpidConfig, newValue -> {
            atomicReference.set(newValue);
            countDownLatch.countDown();
        }).start();
        QpidProducerService producerService = new QpidProducerService(qpidConfig);
        TimeUnit.SECONDS.sleep(3);
        producerService.sendMsg("test-message", DeliveryMode.NON_PERSISTENT);
        countDownLatch.await();
        Message message = atomicReference.get();
        Assertions.assertNotNull(message);
    }

}
