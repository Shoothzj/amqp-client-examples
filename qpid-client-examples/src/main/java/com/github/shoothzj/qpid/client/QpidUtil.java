package com.github.shoothzj.qpid.client;

import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.Message;

@Slf4j
public class QpidUtil {

    public static final String UNKNOWN_ID = "unknown-id";

    public static String getMessageId(Message message) {
        try {
            return message.getJMSMessageID();
        } catch (JMSException e) {
            log.error("ignore ", e);
            return UNKNOWN_ID;
        }
    }

}
