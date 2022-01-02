package com.github.shoothzj.qpid.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.InitialContext;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QpidConsumerThread extends Thread {

    private final QpidConfig qpidConfig;

    private final MessageListener messageListener;

    private boolean initSuccess;

    private MessageConsumer messageConsumer;

    public QpidConsumerThread(QpidConfig qpidConfig, MessageListener messageListener) {
        this.qpidConfig = qpidConfig;
        this.messageListener = messageListener;
        this.setName("qpid-consumer-" + qpidConfig.getQueueName());
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                safeRun();
            } catch (Exception e) {
                log.error("topic {} error, loop exited ", qpidConfig.getQueueName(), e);
            }
        }
    }

    private void safeRun() {
        if (!initSuccess) {
            Connection connection = null;
            Session session = null;
            try {
                final InitialContext context = qpidConfig.acquireInitialContext();
                final JmsConnectionFactory cf = (JmsConnectionFactory) context.lookup("ConnectionURL");
                final Destination queue = (Destination) context.lookup("QueueName");
                connection = cf.createConnection(qpidConfig.getUsername(), qpidConfig.getPassword());
                connection.start();
                ((JmsConnection) connection).addConnectionListener(new LogConnectionListener());
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                messageConsumer = session.createConsumer(queue);
                initSuccess = true;
            } catch (Exception ex) {
                log.error("init qpid consumer error, exception is ", ex);
                StreamUtil.closeQuite(session);
                StreamUtil.closeQuite(connection);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (initSuccess) {
            try {
                Message message = messageConsumer.receive();
                messageListener.onMessage(message);
            } catch (Exception e) {
                log.error("process message receive error ", e);
            }
        }
    }

}
