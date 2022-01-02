plugins {
    java
    checkstyle
}

group = "com.github.shoothzj"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    compileOnly("org.projectlombok:lombok:1.18.22")
    implementation("org.apache.qpid:qpid-jms-client:1.5.0")
    implementation("io.netty:netty-common:4.1.72.Final")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.apache.logging.log4j:log4j-core:2.17.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.0")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testImplementation("org.apache.qpid:qpid-broker-core:8.0.6")
    testImplementation("org.apache.qpid:qpid-broker-plugins-amqp-1-0-protocol:8.0.6")
    testImplementation("org.apache.qpid:qpid-broker-plugins-memory-store:8.0.6")
    testImplementation("org.testcontainers:testcontainers:1.16.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}