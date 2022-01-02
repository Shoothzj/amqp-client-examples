package com.github.shoothzj.amqp.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

public class RabbitmqSuite {

    public static GenericContainer container;

    public static int amqpPort;

    @BeforeAll
    public static void startQpidServer() throws Exception {
        container = new GenericContainer(DockerImageName.parse("ttbb/rabbitmq:mate")).withExposedPorts(5672);
        container.start();
        amqpPort = container.getFirstMappedPort();
        TimeUnit.SECONDS.sleep(30);
    }

    @AfterAll
    public static void shutdownQpidServer() {
        container.stop();
    }

}
