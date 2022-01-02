package com.github.shoothzj.amqp.client;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamUtil {

    public static void closeQuite(AutoCloseable autoCloseable) {
        if (autoCloseable == null) {
            return;
        }
        try {
            autoCloseable.close();
        } catch (Exception e) {
            log.error("ignore error ", e);
        }
    }

}
