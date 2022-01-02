package com.github.shoothzj.qpid.client;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import lombok.extern.slf4j.Slf4j;

import javax.jms.CompletionListener;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QpidProducerService {

    private final QpidProducerInitRetry qpidProducerInitRetry;

    public QpidProducerService(QpidConfig qpidConfig) {
        this.qpidProducerInitRetry = new QpidProducerInitRetry(qpidConfig);
        this.qpidProducerInitRetry.init();
    }

    public void sendMsg(String content, int deliveryMode) {
        MessageProducer producer = qpidProducerInitRetry.getProducer();
        if (producer == null) {
            // haven't init success
            log.error("producer haven't init success, send msg failed");
            return;
        }
        try {
            TextMessage message = qpidProducerInitRetry.getSession().createTextMessage(content);
            producer.send(message, deliveryMode, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE, new CompletionListener() {
                @Override
                public void onCompletion(Message message) {
                    log.info("complete message is {}", QpidUtil.getMessageId(message));
                }

                @Override
                public void onException(Message message, Exception e) {
                    log.error("exception is ", e);
                }
            });
        } catch (Exception e) {
            log.error("qpid msg send failed, exception is ", e);
        }
    }

    private final Timer timer = new HashedWheelTimer();

    public void sendMsgWithRetry(String content, int deliveryMode, int maxRetryTimes) {
        this.sendMsgWithRetry(content, deliveryMode, 0, maxRetryTimes);
    }

    private void sendMsgWithRetry(String content, int deliveryMode, int retryTimes, int maxRetryTimes) {
        MessageProducer producer = qpidProducerInitRetry.getProducer();
        if (producer == null) {
            // haven't init success
            log.error("producer haven't init success, send msg failed");
            return;
        }
        try {
            TextMessage message = qpidProducerInitRetry.getSession().createTextMessage(content);
            producer.send(message, deliveryMode, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE, new CompletionListener() {
                @Override
                public void onCompletion(Message message) {
                    log.info("complete message is {}", QpidUtil.getMessageId(message));
                }

                @Override
                public void onException(Message message, Exception e) {
                    if (retryTimes < maxRetryTimes) {
                        log.warn("qpid msg send failed, begin to retry {} times exception is ", retryTimes, e);
                        timer.newTimeout(timeout -> QpidProducerService.this.sendMsgWithRetry(content, deliveryMode, retryTimes + 1, maxRetryTimes), 1L << retryTimes, TimeUnit.SECONDS);
                        return;
                    }
                    log.error("exception is ", e);
                }
            });
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
