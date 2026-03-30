package org.sdcps.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for SMARTAdaptationLogic.
 * Ensures 100% branch coverage for CLONE/DIVERT/REPLICATE logic.
 */
public class SMARTAdaptationLogicTest {
    private SMARTAdaptationLogic logic;
    private Map<String, Long> policies;
    private Map<String, Object> status;

    @BeforeEach
    public void setup() {
        logic = new SMARTAdaptationLogic();
        policies = new HashMap<>();
        policies.put("soft-threshold-ms", 500L);
        status = new HashMap<>();
    }

    @Test
    public void testBaseRoutingWhenUnderThreshold() {
        status.put("elapsedTime", 400L);
        String decision = logic.evaluate("f1", "n1", "n2", policies, status, Collections.emptyList());
        assertEquals("BASE_ROUTING", decision);
    }

    @Test
    public void testCloneForElephantFlowOverThreshold() {
        status.put("elapsedTime", 600L);
        status.put("isElephant", true);
        String decision = logic.evaluate("f1", "n1", "n2", policies, status, Collections.emptyList());
        assertEquals("CLONE", decision);
    }

    @Test
    public void testDivertForMiceFlowOverThreshold() {
        status.put("elapsedTime", 600L);
        status.put("isElephant", false);
        String decision = logic.evaluate("f1", "n1", "n2", policies, status, Collections.emptyList());
        assertEquals("DIVERT", decision);
    }

    @Test
    public void testReplicateAfterCongestionMarked() {
        // First, trigger congestion and clone
        status.put("elapsedTime", 600L);
        status.put("isElephant", true);
        logic.evaluate("f1", "n1", "n2", policies, status, Collections.emptyList());

        // Subsequent flow on same path should trigger REPLICATE
        String decision = logic.evaluate("f2", "n1", "n2", policies, status, Collections.emptyList());
        assertEquals("REPLICATE", decision);
    }

    @Test
    public void testResetCongestion() {
        status.put("elapsedTime", 600L);
        status.put("isElephant", true);
        logic.evaluate("f1", "n1", "n2", policies, status, Collections.emptyList());

        logic.resetCongestion("n1->n2");
        
        // Lower the threshold BEFORE evaluating again to avoid re-triggering congestion state
        status.put("elapsedTime", 100L);
        String decision = logic.evaluate("f2", "n1", "n2", policies, status, Collections.emptyList());
        assertEquals("BASE_ROUTING", decision);
    }
}
