package com.github.shoothzj.qpid.client;

import com.google.common.io.Resources;
import org.apache.qpid.server.SystemLauncher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class QpidServerSuite {

    private static final String QPID_BROKER_CONF = "qpid-config.json";

    private static final SystemLauncher systemLauncher = new SystemLauncher();

    @BeforeAll
    public static void startQpidServer() throws Exception {
        URL resource = Resources.getResource(QPID_BROKER_CONF);
        System.setProperty("qpid.amqp_port", "5672");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("type", "Memory");
        attributes.put("initialConfigurationLocation", resource.toExternalForm());
        attributes.put("startupLoggedToSystemOut", true);
        systemLauncher.startup(attributes);
    }

    @AfterAll
    public static void shutdownQpidServer() {
        systemLauncher.shutdown();
    }

}
