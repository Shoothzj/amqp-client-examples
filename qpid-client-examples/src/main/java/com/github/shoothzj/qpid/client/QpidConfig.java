package com.github.shoothzj.qpid.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class QpidConfig {

    private boolean ssl;

    private String qpidHost;

    private int qpidPort;

    private String vhost;

    private String saslMechanisms;

    private String username;

    private String password;

    private String queueName;

    public QpidConfig() {
    }

    public InitialContext acquireInitialContext() throws NamingException {
        final Hashtable<String, String> map = new Hashtable<>();
        map.put("connectionfactory.ConnectionURL", acquireConnectionUrl());
        map.put("queue.QueueName", queueName);
        map.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        return new InitialContext(map);
    }

    private String acquireConnectionUrl() {
        final StringBuilder sb = new StringBuilder("amqp");
        if (ssl) {
            sb.append("s");
        }
        sb.append(String.format("://%s:%d", qpidHost, qpidPort));
        sb.append("?amqp.vhost=").append(vhost);
        if (saslMechanisms != null) {
            sb.append("&amqp.saslMechanisms=").append(saslMechanisms);
        }
        return sb.toString();
    }

}
