package org.sdcps.knowledge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Knowledge Plane: Implementation of SMART Adaptive Routing logic.
 * 
 * Ported from the KathiraveluLab/SMART research repository to ensure autonomy.
 * Implements **Algorithm 1: SMART Enhancement** for congestion mitigation.
 * 
 * @see <a href="https://link.springer.com/article/10.1007/s10586-019-02970-0">Cluster Computing 2019 Paper</a>
 */
public class SMARTAdaptationLogic {
    private final Map<String, Boolean> globalCongestionState = new HashMap<>();

    /**
     * Executes the SMART routing evaluation for a given flow.
     * 
     * @param flowId Unique identifier for the flow
     * @param origin Source node ID
     * @param dest Destination node ID
     * @param policies Map containing threshold policies (e.g., soft-threshold-ms)
     * @param currentStatus Real-time status from the Digital Twin (elapsedTime, isElephant)
     * @param links List of links in the path
     * @return The adaptation decision (CLONE, DIVERT, or BASE_ROUTING)
     */
    public String evaluate(String flowId, String origin, String dest, Map<String, Long> policies, Map<String, Object> currentStatus, List<String> links) {
        
        // Check if we are in an adaptive replicate state for this path
        String pathKey = origin + "->" + dest;
        if (globalCongestionState.getOrDefault(pathKey, false)) {
            return "REPLICATE"; // Adaptive replicate for subsequent flows
        }

        // Identify breakpoint if soft threshold met
        boolean breakpointTriggered = isBreakpointTriggered(flowId, policies, currentStatus, links);
        
        if (breakpointTriggered) {
            // Trigger enhancement
            boolean isElephantFlow = (Boolean) currentStatus.getOrDefault("isElephant", false);
            
            if (isElephantFlow) {
                globalCongestionState.put(pathKey, true); // Mark path as congested for others
                return "CLONE"; // Clone subflow for elephant flows
            } else {
                return "DIVERT"; // Divert subflow for smaller priority flows
            }
        }

        return "BASE_ROUTING"; // Stick to shortest path/ECMP
    }

    private boolean isBreakpointTriggered(String flowId, Map<String, Long> policies, Map<String, Object> currentStatus, List<String> links) {
        long softThreshold = policies.getOrDefault("soft-threshold-ms", 800L);
        long currentTime = (Long) currentStatus.getOrDefault("elapsedTime", 0L);

        // Initial Soft-SLA Check
        return currentTime >= softThreshold;
    }

    public void resetCongestion(String path) {
        globalCongestionState.put(path, false);
    }
}
