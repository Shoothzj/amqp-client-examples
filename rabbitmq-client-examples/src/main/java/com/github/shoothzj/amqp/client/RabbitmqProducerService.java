package com.github.shoothzj.amqp.client;

import com.rabbitmq.client.Channel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RabbitmqProducerService {

    private final RabbitmqConfig rabbitmqConfig;

    private final RabbitmqProducerInitRetry rabbitmqProducerInitRetry;

    public RabbitmqProducerService(RabbitmqConfig rabbitmqConfig) {
        this.rabbitmqConfig = rabbitmqConfig;
        this.rabbitmqProducerInitRetry = new RabbitmqProducerInitRetry(rabbitmqConfig);
        this.rabbitmqProducerInitRetry.init();
    }

    public void sendMsg(String content) {
        Channel channel = rabbitmqProducerInitRetry.getChannel();
        if (channel == null) {
            // haven't init success
            log.error("producer haven't init success, send msg failed");
            return;
        }
        try {
            channel.basicPublish("", rabbitmqConfig.getQueueName(),
                    null, content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("qpid msg send failed, exception is ", e);
        }
    }

    private final Timer timer = new HashedWheelTimer();

    public void sendMsgWithRetry(String content, int maxRetryTimes) {
        this.sendMsgWithRetry(content, 0, maxRetryTimes);
    }

    private void sendMsgWithRetry(String content, int retryTimes, int maxRetryTimes) {
        Channel channel = rabbitmqProducerInitRetry.getChannel();
        if (channel == null) {
            // haven't init success
            log.error("producer haven't init success, send msg failed");
            return;
        }
        try {
            channel.basicPublish("", rabbitmqConfig.getQueueName(),
                    null, content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            if (retryTimes < maxRetryTimes) {
                log.warn("qpid msg send failed, begin to retry {} times, exception is ", retryTimes, e);
                timer.newTimeout(timeout -> this.sendMsgWithRetry(content, retryTimes + 1, maxRetryTimes), 1L << retryTimes, TimeUnit.SECONDS);
            } else {
                log.error("qpid msg send failed, exception is ", e);
            }
        }
    }

}
