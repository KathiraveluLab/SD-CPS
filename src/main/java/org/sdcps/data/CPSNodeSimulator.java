package org.sdcps.data;

import org.sdcps.core.SDCPSTopology;
import org.sdcps.network.M4TPublisher;
import org.sendim.core.StateRegistry;
import org.sendim.sdnsim.model.SimulationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

/**
 * Data Plane: CPS Node Simulator for SD-CPS.
 * Integrates with SENDIM for Digital Twin state synchronization.
 */
public class CPSNodeSimulator {
    private static final Logger logger = LoggerFactory.getLogger(CPSNodeSimulator.class);
    private PrivacyGuard privacyGuard = new PrivacyGuard();

    public void startSimulation(String nodeId) {
        logger.info("Starting CPS node simulation for [{}].", nodeId);
        
        // 1. Digital Twin Synchronization (SENDIM)
        logger.info("Syncing with SENDIM Digital Twin for node: {}", nodeId);
        SimulationState twinState = StateRegistry.loadSnapshot(nodeId);
        
        if (twinState != null) {
            logger.info("Restored Digital Twin state for [{}] from timestamp: {}", nodeId, twinState.getTimestamp());
        } else {
            logger.info("No existing Digital Twin found for [{}]. Initializing new twin state.", nodeId);
            StateRegistry.saveSnapshot(nodeId, new HashMap<>());
        }

        logger.info("Node [{}] is now generating real-time CPS traffic, mirrored in Digital Twin.", nodeId);
    }

    /**
     * Simulates a dynamic congestion event on a CPS node.
     */
    public void triggerCongestion(String nodeId) {
        logger.info("Triggering adaptive congestion on [{}].", nodeId);
        org.sdcps.knowledge.DashboardGenerator.getInstance().updateNodeStatus(nodeId, "HOT");
        
        M4TPublisher publisher = new M4TPublisher();
        String message = "Congestion detected on edge node " + nodeId;
        publisher.publish("topic://cps/alerts", privacyGuard.anonymize(message));
        
        logger.info("Congestion alert broadcasted via M4T for node [{}].", nodeId);
    }

    /**
     * Simulates dynamic network fluctuations (jitter/latency shifts) on a specific link.
     */
    public void injectNetworkJitter(SDCPSTopology topology, String srcNode, String destNode, double spikeLatency) {
        logger.warn("INDUSTRIAL INTERFERENCE DETECTED: Injecting network jitter on link [{} -> {}]", srcNode, destNode);
        topology.updateLinkLatency(srcNode, destNode, spikeLatency);
        
        M4TPublisher publisher = new M4TPublisher();
        String message = "Link latency breach detected on " + srcNode + " to " + destNode;
        publisher.publish("topic://cps/topology", privacyGuard.anonymize(message));
    }

    /**
     * Simulates a critical node failure (CRASH) in the CPS cluster.
     * Part of the Chaos Engineering suite for SDS 2017 parity.
     */
    public void simulateNodeCrash(String nodeId) {
        logger.error("CRITICAL NODE FAILURE: Node [{}] has CRASHED (Hardware Failure).", nodeId);
        org.sdcps.knowledge.DashboardGenerator.getInstance().updateNodeStatus(nodeId, "CRASHED");
        
        M4TPublisher publisher = new M4TPublisher();
        String message = "NODE_CRASH_EVENT: " + nodeId;
        publisher.publish("topic://cps/chaos", privacyGuard.anonymize(message));
        
        logger.info("Chaos alert broadcasted via M4T for node crash [{}].", nodeId);
    }
}
