package com.github.shoothzj.qpid.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QpidProducerInitRetry {

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("qpid-producer-init").build();

    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(1, threadFactory);

    private final QpidConfig qpidConfig;

    private volatile Session session;

    private volatile MessageProducer producer;

    public QpidProducerInitRetry(QpidConfig qpidConfig) {
        this.qpidConfig = qpidConfig;
    }

    public void init() {
        executorService.scheduleWithFixedDelay(this::initWithRetry, 0, 10, TimeUnit.SECONDS);
    }

    private void initWithRetry() {
        Connection connection = null;
        try {
            InitialContext context = qpidConfig.acquireInitialContext();
            JmsConnectionFactory cf = (JmsConnectionFactory) context.lookup("ConnectionURL");
            Destination queue = (Destination) context.lookup("QueueName");
            connection = cf.createConnection(qpidConfig.getUsername(), qpidConfig.getPassword());
            connection.start();
            ((JmsConnection) connection).addConnectionListener(new LogConnectionListener());
            session = connection.createSession();
            producer = session.createProducer(queue);
            executorService.shutdown();
        } catch (Exception e) {
            log.error("init qpid producer error, exception is ", e);
            StreamUtil.closeQuite(session);
            StreamUtil.closeQuite(connection);
        }
    }

    public Session getSession() {
        return session;
    }

    public MessageProducer getProducer() {
        return producer;
    }

}
