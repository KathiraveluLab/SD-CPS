package org.sdcps.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Plane: M4T Publisher for SD-CPS.
 * Uses Messaging4Transport for inter-cloud and edge-to-cloud communication.
 */
public class M4TPublisher {
    private static final Logger logger = LoggerFactory.getLogger(M4TPublisher.class);

    public void publish(String topic, String message) {
        logger.info("Publishing message to topic [{}]: {}", topic, message);
        try {
            org.opendaylight.messaging4transport.impl.AmqpPublisher.publish(topic, message);
            logger.info("Message successfully published to the AMQP broker via M4T.");
        } catch (javax.jms.JMSException e) {
            logger.error("Failed to publish message via M4T: {}", e.getMessage());
        } catch (NoClassDefFoundError | Exception e) {
            logger.error("Messaging4Transport implementation details missing or uninitialized: {}", e.getMessage());
        }
    }
}
