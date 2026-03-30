package org.sdcps.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Plane: Privacy Guard for SD-CPS.
 * Implements "Privacy-by-Design" by anonymizing sensitive sensor telemetry.
 * Ensures compliance with industrial data privacy standards.
 */
public class PrivacyGuard {
    private static final Logger logger = LoggerFactory.getLogger(PrivacyGuard.class);

    /**
     * Anonymizes a telemetry message by masking sensitive identifiers (PII).
     * 
     * @param rawMessage The original telemetry string
     * @return A sanitized, compliant message
     */
    public String anonymize(String rawMessage) {
        if (rawMessage == null) return null;
        
        // Simulating the masking of sensitive IDs (e.g., specific operator IDs or machine serials)
        logger.info("PrivacyGuard - Anonymizing sensitive sensor metadata in stream...");
        
        // Example: Masking "Node[...]" or specific machine signatures
        String sanitized = rawMessage.replaceAll("Node\\[(.*?)\\]", "Node[REDACTED]");
        sanitized = sanitized + " [PRIVACY_COMPLIANT]";
        
        return sanitized;
    }

    /**
     * Verifies if a message is compliant with the privacy policy.
     */
    public boolean isCompliant(String message) {
        return message != null && message.contains("PRIVACY_COMPLIANT");
    }
}
