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
 * Main entry point for the SD-CPS Framework.
 * Integrates Messaging4Transport, Evora, SENDIM, and SMART.
 */
public class SDCPSMain {
    private static final Logger logger = LoggerFactory.getLogger(SDCPSMain.class);

    public static void main(String[] args) {
        org.sdcps.knowledge.DashboardGenerator.getInstance().updateDashboard();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("interactive")) {
                runInteractiveMode();
                return;
            } else if (args[0].equalsIgnoreCase("--case") && args.length > 1) {
                try {
                    int caseNum = Integer.parseInt(args[1]);
                    runCaseStudy(caseNum);
                } catch (NumberFormatException e) {
                    logger.error("Invalid case number: {}", args[1]);
                }
                return;
            } else if (args[0].equalsIgnoreCase("--list")) {
                printCaseStudies();
                return;
            } else if (args[0].equalsIgnoreCase("--help")) {
                printHelp();
                return;
            }
        }
        runAllCaseStudies();
    }

    private static void printHelp() {
        System.out.println("SD-CPS Framework CLI");
        System.out.println("Usage:");
        System.out.println("  java -jar sdcps.jar [options]");
        System.out.println("Options:");
        System.out.println("  interactive     Start interactive simulation mode");
        System.out.println("  --case <N>      Run a specific case study (1-9)");
        System.out.println("  --list          List and detail all available case studies");
        System.out.println("  --help          Print this help message");
        System.out.println("  (no args)       Run all case studies in sequence (standard parity run)");
    }

    private static void printCaseStudies() {
        System.out.println("\nAvailable Case Studies:");
        System.out.println("--------------------------------");
        System.out.println("1. Static Placement (SDS 2017): Greedy placement of services on edge nodes.");
        System.out.println("2. SMART Adaptation (2019): Dynamic subflow cloning for elephant flows.");
        System.out.println("3. Link Fluctuations (2019): Real-time response to transient network jitter.");
        System.out.println("4. Multi-tenancy (SDS 2017): Secure namespace isolation between tenants.");
        System.out.println("5. High Availability (SDS 2017): ODL-style orchestrator clustering and failover.");
        System.out.println("6. Thermal Constraints (2019): Energy-aware placement avoiding thermal breaches.");
        System.out.println("7. Empirical Validation: 1000+ iteration simulation with 95% Confidence Intervals.");
        System.out.println("8. Chaos Engineering (SDS 2017): Automated self-healing after node crashes.");
        System.out.println("9. Privacy-by-Design: Automated PII masking and telemetry anonymization.");
        System.out.println("10. VANET V2X Deployment: Prototypical VANET with V2V, V2I, and I2V communications.");
        System.out.println("");
    }

    private static void runAllCaseStudies() {
        for (int i = 1; i <= 9; i++) {
            runCaseStudy(i);
        }
        logger.info("\nExporting Dashboard state to dashboard.html...");
        logger.info("Dashboard successfully updated with experimental results.");
        logger.info("\nSD-CPS Framework fully bootstrapped and operational.");
    }

    private static void runCaseStudy(int caseNum) {
        SDCPSRegistry registry = SDCPSRegistry.getInstance();
        SDCPSTopology topology = new SDCPSTopology();
        topology.buildResearchTopology();
        SDCPSOrchestrator orchestrator = new SDCPSOrchestrator(topology);
        EdgeWorkflowManager workflowManager = new EdgeWorkflowManager();
        CPSNodeSimulator simulator = new CPSNodeSimulator();
        org.evora.core.UserPolicy policy = new org.evora.core.UserPolicy(1.0, 10.0, 1.0); 
        String[] nsc = {"s5", "s4", "s3"};

        switch (caseNum) {
            case 1:
                logger.info("Starting Case Study 1: Static Placement (SDS 2017)...");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "CASE STUDY 1: Static Placement initiated.");
                registry.registerCPSService("UserA", "s5", 5.0, true);
                org.evora.core.PlacementSolution solution = orchestrator.solve("UserA", nsc, policy);
                workflowManager.executeWorkflow("wf-0Static", solution);
                break;
            case 2:
                logger.info("\n--- Starting Case Study 2: Dynamic Adaptation (Cluster Computing 2019) ---");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("danger", "EVENT: Congestion on n10.");
                simulator.startSimulation("n10");
                simulator.triggerCongestion("n10");
                orchestrator.detectAndAdapt("UserA", "flow-002", "n10", "s5");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "CASE STUDY 2: SMART adaptation completed.");
                break;
            case 3:
                logger.info("\n--- Starting Case Study 3: Dynamic Link Fluctuations (2019 Cluster Computing) ---");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("warning", "EVENT: Network Jitter injected.");
                simulator.injectNetworkJitter(topology, "n10", "n12", 25.0);
                logger.info("Orchestrator re-evaluating placement due to topology shift...");
                org.evora.core.PlacementSolution newSolution = orchestrator.solve("UserA", nsc, policy);
                workflowManager.executeWorkflow("wf-0DynamicLink", newSolution);
                break;
            case 4:
                logger.info("\n--- Starting Case Study 4: Multi-tenant Isolation (SDS 2017) ---");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("danger", "SECURITY ALERT: Unauthorized access attempt.");
                registry.registerCPSService("UserB", "s6", 3.0, false);
                logger.info("Tenant [UserB] attempting to orchestrate an NSC with Tenant [UserA]'s service [s5]...");
                String[] rogueNsc = {"s5", "s6"};
                orchestrator.solve("UserB", rogueNsc, policy);
                break;
            case 5:
                logger.info("\n--- Starting Case Study 5: Control Plane Clustering & HA (SDS 2017) ---");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("danger", "CRITICAL: Primary Orchestrator Crash.");
                org.sdcps.knowledge.DashboardGenerator.getInstance().updateUptime("94.2%");
                SDCPSClusterManager.getInstance().electLeader("Primary-Node");
                SDCPSRegistry backupRegistry = new SDCPSRegistry(true);
                SDCPSOrchestrator backupOrch = new SDCPSOrchestrator(topology, backupRegistry);
                backupOrch.setPrimary(false);
                logger.info("Simulating Primary Orchestrator CRASH...");
                logger.error("CRITICAL: Primary Orchestrator is UNREACHABLE.");
                logger.info("Backup Orchestrator detecting heartbeat failure... Initiating FAILOVER.");
                backupOrch.failover();
                org.sdcps.knowledge.DashboardGenerator.getInstance().updateUptime("99.9%");
                SDCPSClusterManager.getInstance().electLeader("Backup-Node");
                backupOrch.joinClusterAndSync();
                logger.info("Backup Orchestrator (now Primary) resuming NSC placement with SYNCED STATE...");
                backupOrch.solve("UserA", nsc, policy);
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "HA FAILOVER: Backup promoted to Primary.");
                break;
            case 6:
                logger.info("\n--- Starting Case Study 6: Energy-Aware Placement (2019 Cluster Computing) ---");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("warning", "THERMAL BREACH: Energy constraint exceeded on node.");
                registry.registerCPSService("UserA", "s_heavy", 200.0, true);
                logger.info("Orchestrating high-intensity service [s_heavy] (200W requirement)...");
                String[] heavyNsc = {"s_heavy"};
                orchestrator.solve("UserA", heavyNsc, policy);
                break;
            case 7:
                logger.info("\n--- Starting Case Study 7: Empirical Validation & CI Report ---");
                EmpiricalValidator validator = new EmpiricalValidator();
                validator.runEmpiricalStudy(orchestrator, 1000);
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "EMPIRICAL: 1000 iterations completed.");
                break;
            case 8:
                logger.info("\n--- Starting Case Study 8: Chaos Engineering & Self-Healing (SDS 2017) ---");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("danger", "CHAOS: Hardware Failure on n10.");
                logger.warn("Simulating Hardware Failure on Edge Node [n10]...");
                simulator.simulateNodeCrash("n10");
                orchestrator.selfHeal("UserA", "n10");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "SELF-HEAL: Service restored on alternate node.");
                break;
            case 9:
                logger.info("\n--- Starting Case Study 9: Data Privacy Compliance ---");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("warning", "COMPLIANCE: Personal Data detected in flow.");
                logger.info("Simulating non-compliant telemetry from an unpatched sensor...");
                orchestrator.detectAndAdapt("UserA", "sensitive_flow_node_n12", "n12", "s7");
                logger.info("Simulating compliant, anonymized telemetry...");
                String compliantFlowId = new PrivacyGuard().anonymize("flow_node_n12");
                orchestrator.detectAndAdapt("UserA", compliantFlowId, "n12", "s7");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "PRIVACY: Telemetry anonymized and accepted.");
                break;
            case 10:
                logger.info("\n--- Starting Case Study 10: Prototypical VANET Deployment (SDS 2017) ---");
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("warning", "VANET: Initializing V2X Communication layer.");
                
                // Register VANET Specific Services
                registry.registerCPSService("Vehicle-Alpha", "V2V-Safety", 2.0, true);
                registry.registerCPSService("Vehicle-Beta", "V2I-Telemetry", 5.0, false);
                registry.registerCPSService("Infrastructure-City", "I2V-TrafficAlert", 10.0, true);
                
                logger.info("Simulating V2V Safety Message broadcast from Vehicle-Alpha...");
                orchestrator.solve("Vehicle-Alpha", new String[]{"V2V-Safety"}, policy);
                
                logger.info("Simulating V2I Telemetry upload to RSU [n15]...");
                orchestrator.solve("Vehicle-Beta", new String[]{"V2I-Telemetry"}, policy);
                
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("danger", "EVENT: Roadside Accident detected by Vehicle-Alpha.");
                logger.warn("V2V Alert: Emergency Braking signal received by nearby vehicles.");
                
                logger.info("I2V Control Plane: Pushing rerouting instructions to Infrastructure-City...");
                orchestrator.solve("Infrastructure-City", new String[]{"I2V-TrafficAlert"}, policy);
                
                org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "VANET: V2X loop successfully closed.");
                break;
            default:
                logger.error("Unknown Case Study: {}", caseNum);
        }
    }

    private static void runInteractiveMode() {
        logger.info("Entering SD-CPS Interactive Mode...");
        SDCPSTopology topology = new SDCPSTopology();
        topology.buildResearchTopology();
        SDCPSRegistry registry = SDCPSRegistry.getInstance();
        SDCPSOrchestrator orchestrator = new SDCPSOrchestrator(topology, registry);
        CPSNodeSimulator simulator = new CPSNodeSimulator();
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        printInteractiveHelp();
        
        while (true) {
            System.out.print("sd-cps> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) break;
            if (input.equalsIgnoreCase("help")) { printInteractiveHelp(); continue; }

            String[] parts = input.split("\\s+");
            String cmd = parts[0].toLowerCase();

            try {
                switch (cmd) {
                    case "nodes":
                        System.out.println("\nActive Edge Nodes in Topology:");
                        topology.getAllNodes().forEach((id, node) -> {
                            System.out.printf(" - %s: Energy=%.1fW, Latency=%.1fms, Cost=%.1f\n", 
                                id, topology.getEnergyCapacity(id), node.getLatency(), node.getCost());
                        });
                        break;
                    case "addnode":
                        if (parts.length < 5) { System.out.println("Usage: addnode <id> <energyW> <latency> <cost> (Example: addnode n18 100.0 4.0 12.0)"); break; }
                        double eCap = Double.parseDouble(parts[2]);
                        double lat = Double.parseDouble(parts[3]);
                        double cost = Double.parseDouble(parts[4]);
                        topology.addNodeWithEnergy(new org.evora.core.Node(parts[1], new String[]{}, new String[]{}, cost, lat, 100.0), eCap);
                        org.sdcps.knowledge.DashboardGenerator.getInstance().updateNodeStatus(parts[1], "NORMAL");
                        System.out.println("Node " + parts[1] + " added to topology and dashboard.");
                        break;
                    case "users":
                        System.out.println("\nRegistered Tenants and Services:");
                        registry.getAllTenants().forEach((tenant, services) -> {
                            System.out.println(" [" + tenant + "]");
                            services.forEach((sid, meta) -> {
                                System.out.printf("   - %s: %sW, RT=%s\n", sid, meta.getEnergyConstraint(), meta.isRealTime());
                            });
                        });
                        if (registry.getAllTenants().isEmpty()) System.out.println(" No tenants registered.");
                        break;
                    case "services":
                        System.out.println("\nRegistered CPS Services (All Tenants):");
                        registry.getAllTenants().forEach((tenant, services) -> {
                            services.forEach((sid, meta) -> {
                                System.out.printf(" - %s [Tenant: %s]: %sW, RT=%s\n", 
                                    sid, tenant, meta.getEnergyConstraint(), meta.isRealTime());
                            });
                        });
                        if (registry.getAllTenants().isEmpty()) System.out.println(" No services registered.");
                        break;
                    case "register":
                        if (parts.length < 5) { System.out.println("Usage: register <tenant> <service> <energyW> <isRT> (Example: register UserA s5 10.0 true)"); break; }
                        double energy = Double.parseDouble(parts[3]);
                        boolean rt = Boolean.parseBoolean(parts[4]);
                        registry.registerCPSService(parts[1], parts[2], energy, rt);
                        System.out.println("Service registered and reported to dashboard.");
                        break;
                    case "crash":
                        if (parts.length < 2) { System.out.println("Usage: crash <node> (Example: crash n10)"); break; }
                        logger.warn("MANUAL EVENT: Crashing node {}", parts[1]);
                        org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("danger", "NODE CRASH (CHAOS): Node " + parts[1] + " Failure detected.");
                        simulator.simulateNodeCrash(parts[1]);
                        orchestrator.selfHeal("UserA", parts[1]);
                        org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "SELF-HEALING: Migrated services from " + parts[1]);
                        break;
                    case "congestion":
                        if (parts.length < 2) { System.out.println("Usage: congestion <node> (Example: congestion n12)"); break; }
                        logger.warn("MANUAL EVENT: Triggering congestion on node {}", parts[1]);
                        org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("danger", "CONGESTION: Node " + parts[1] + " capacity reached.");
                        simulator.triggerCongestion(parts[1]);
                        String anonymizedFlow = new org.sdcps.data.PrivacyGuard().anonymize("interactive-flow");
                        orchestrator.detectAndAdapt("UserA", anonymizedFlow, parts[1], "s5");
                        org.sdcps.knowledge.DashboardGenerator.getInstance().addAlert("success", "ADAPTATION: SMART Clone triggered for " + parts[1]);
                        break;
                    case "jitter":
                        if (parts.length < 4) { System.out.println("Usage: jitter <n1> <n2> <ms> (Example: jitter n10 n12 25)"); break; }
                        double ms = Double.parseDouble(parts[3]);
                        logger.warn("MANUAL EVENT: Injecting {}ms jitter between {} and {}", ms, parts[1], parts[2]);
                        simulator.injectNetworkJitter(topology, parts[1], parts[2], ms);
                        break;
                    default:
                        System.out.println("Unknown command: " + cmd + ". Type 'help' for examples.");
                }
            } catch (Exception e) {
                System.out.println("Error executing command: " + e.getMessage());
            }
        }
        System.out.println("Exiting Interactive Mode.");
    }

    private static void printInteractiveHelp() {
        System.out.println("\nSD-CPS Interactive Shell");
        System.out.println("Usage & Examples:");
        System.out.println("  nodes                             - List all edge nodes and their current metrics");
        System.out.println("  addnode <id> <W> <lat> <cost>     - e.g., addnode n18 100.0 4.0 12.0");
        System.out.println("  users                             - List all registered tenants and their services");
        System.out.println("  services                          - List all registered services and metadata");
        System.out.println("  register <tenant> <service> <W> <RT> - e.g., register UserA s5 10.0 true");
        System.out.println("  crash <node>                      - e.g., crash n10");
        System.out.println("  congestion <node>                 - e.g., congestion n12");
        System.out.println("  jitter <n1> <n2> <ms>             - e.g., jitter n10 n12 50");
        System.out.println("  help                              - Show this help message");
        System.out.println("  quit                              - Exit the interactive shell");
        System.out.println("");
    }

}
