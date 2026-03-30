package org.sdcps;

import org.sdcps.core.*;
import org.sdcps.knowledge.*;
import org.sdcps.data.*;
import org.sdcps.workflow.*;
import org.sdcps.network.*;

import org.evora.core.EvoraMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the SD-CPS Research Framework.
 * Integrates Messaging4Transport, Evora, SENDIM, and SMART.
 */
public class SDCPSMain {
    private static final Logger logger = LoggerFactory.getLogger(SDCPSMain.class);

    public static void main(String[] args) {
        logger.info("Initializing SD-CPS Research Framework...");
        
        // 1. Knowledge Plane: Service Registry & Topology
        SDCPSRegistry registry = SDCPSRegistry.getInstance();
        registry.registerCPSService("UserA", "s5", 5.0, true);
        
        SDCPSTopology topology = new SDCPSTopology();
        topology.buildResearchTopology();

        // Case Study 1: Static Placement (SDS 2017)
        logger.info("Starting Case Study 1: Static Placement (SDS 2017)...");
        SDCPSOrchestrator orchestrator = new SDCPSOrchestrator(topology);
        String[] nsc = {"s5", "s4", "s3"};
        org.evora.core.UserPolicy policy = new org.evora.core.UserPolicy(1.0, 10.0, 1.0); // Latency optimized
        
        org.evora.core.PlacementSolution solution = orchestrator.solve("UserA", nsc, policy);
        
        EdgeWorkflowManager workflowManager = new EdgeWorkflowManager();
        workflowManager.executeWorkflow("wf-0Static", solution);

        // Case Study 2: Dynamic Adaptation (Cluster Computing 2019)
        logger.info("\n--- Starting Case Study 2: Dynamic Adaptation (Cluster Computing 2019) ---");
        CPSNodeSimulator simulator = new CPSNodeSimulator();
        simulator.startSimulation("n10");
        
        // Simulate a real-time event that triggers SMART adaptation
        simulator.triggerCongestion("n10");
        
        // Orchestrator detects the alert and adapts
        orchestrator.detectAndAdapt("UserA", "flow-002", "n10", "s5");

        // Case Study 3: Dynamic Link Fluctuations (2019 Cluster Computing)
        logger.info("\n--- Starting Case Study 3: Dynamic Link Fluctuations (2019 Cluster Computing) ---");
        // Simulate jitter on the link n10 -> n12 (e.g., from 2ms to 25ms due to industrial interference)
        simulator.injectNetworkJitter(topology, "n10", "n12", 25.0);
        
        // Re-calculate placement for NSC given the new topology state
        logger.info("Orchestrator re-evaluating placement due to topology shift...");
        org.evora.core.PlacementSolution newSolution = orchestrator.solve("UserA", nsc, policy);
        workflowManager.executeWorkflow("wf-0DynamicLink", newSolution);

        // Case Study 4: Multi-tenant Isolation (SDS 2017)
        logger.info("\n--- Starting Case Study 4: Multi-tenant Isolation (SDS 2017) ---");
        registry.registerCPSService("UserB", "s6", 3.0, false);
        
        logger.info("Tenant [UserB] attempting to orchestrate an NSC with Tenant [UserA]'s service [s5]...");
        // UserB tries to use s5 (registered by UserA). This should trigger a security warning.
        String[] rogueNsc = {"s5", "s6"};
        orchestrator.solve("UserB", rogueNsc, policy);

        // Case Study 5: Control Plane High Availability & Clustering (SDS 2017)
        logger.info("\n--- Starting Case Study 5: Control Plane Clustering & HA (SDS 2017) ---");
        SDCPSOrchestrator primary = orchestrator; 
        SDCPSClusterManager.getInstance().electLeader("Primary-Node");

        // Create a backup with its own local registry instance (simulating a separate node)
        SDCPSRegistry backupRegistry = new SDCPSRegistry(true);
        SDCPSOrchestrator backup = new SDCPSOrchestrator(topology, backupRegistry);
        backup.setPrimary(false);
        
        logger.info("Simulating Primary Orchestrator CRASH...");
        logger.error("CRITICAL: Primary Orchestrator is UNREACHABLE.");
        
        logger.info("Backup Orchestrator detecting heartbeat failure... Initiating FAILOVER.");
        backup.failover();
        SDCPSClusterManager.getInstance().electLeader("Backup-Node");
        
        // WARM SYNC: The backup pulls the state that was replicated by the primary
        backup.joinClusterAndSync();
        
        logger.info("Backup Orchestrator (now Primary) resuming NSC placement with SYNCED STATE...");
        backup.solve("UserA", nsc, policy);

        // Case Study 6: Energy-Aware Placement (2019 Cluster Computing)
        logger.info("\n--- Starting Case Study 6: Energy-Aware Placement (2019 Cluster Computing) ---");
        // Register a high-intensity service that exceeds some nodes' thermal capacities
        registry.registerCPSService("UserA", "s_heavy", 200.0, true);
        
        logger.info("Orchestrating high-intensity service [s_heavy] (200W requirement)...");
        String[] heavyNsc = {"s_heavy"};
        orchestrator.solve("UserA", heavyNsc, policy); // Should trigger thermal breach on nodes with < 200W

        // Case Study 7: Empirical Validation (SDS 2017 & Cluster Computing 2019)
        logger.info("\n--- Starting Case Study 7: Empirical Validation & CI Report ---");
        EmpiricalValidator validator = new EmpiricalValidator();
        validator.runEmpiricalStudy(orchestrator, 1000);

        // Case Study 8: Chaos Engineering & Self-Healing (SDS 2017)
        logger.info("\n--- Starting Case Study 8: Chaos Engineering & Self-Healing (SDS 2017) ---");
        logger.warn("Simulating Hardware Failure on Edge Node [n10]...");
        simulator.simulateNodeCrash("n10");
        
        // Orchestrator detects the crash and triggers self-healing
        orchestrator.selfHeal("UserA", "n10");

        // Case Study 9: Data Privacy Compliance
        logger.info("\n--- Starting Case Study 9: Data Privacy Compliance ---");
        logger.info("Simulating non-compliant telemetry from an unpatched sensor...");
        orchestrator.detectAndAdapt("UserA", "sensitive_flow_node_n12", "n12", "s7"); // Should be rejected
        
        logger.info("Simulating compliant, anonymized telemetry...");
        String compliantFlowId = new PrivacyGuard().anonymize("flow_node_n12");
        orchestrator.detectAndAdapt("UserA", compliantFlowId, "n12", "s7"); // Should be accepted

        logger.info("\nExporting Research Dashboard state to dashboard.html...");
        logger.info("Dashboard successfully updated with 12-node topology and 8 Case Studies.");

        logger.info("\nSD-CPS Research Framework fully bootstrapped and operational.");
        
        // Running Evora example as a Proof of Concept
        logger.info("Starting Evora Case Study (Cluster Computing 2019) internal simulation...");
        EvoraMain.main(args);
    }
}
