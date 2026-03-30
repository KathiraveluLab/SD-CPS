package org.sdcps.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Control Plane: Cluster Manager for SD-CPS.
 * Simulates ODL/Akka-based clustering with state synchronization.
 * Ensures the backup orchestrator has a "Warm State" for failover.
 */
public class SDCPSClusterManager {
    private static final Logger logger = LoggerFactory.getLogger(SDCPSClusterManager.class);
    private static SDCPSClusterManager instance;
    private Map<String, Map<String, SDCPSRegistry.CPSMetadata>> replicatedRegistry = new ConcurrentHashMap<>();
    private String currentLeaderId;

    private SDCPSClusterManager() {}

    public static synchronized SDCPSClusterManager getInstance() {
        if (instance == null) {
            instance = new SDCPSClusterManager();
        }
        return instance;
    }

    /**
     * Simulates state replication from the primary orchestrator to the cluster.
     */
    public void replicateState(String tenantId, Map<String, SDCPSRegistry.CPSMetadata> tenantServices) {
        logger.info("CLUSTER SYNC: Replicating state for tenant [{}] across the Control Plane cluster.", tenantId);
        replicatedRegistry.put(tenantId, new ConcurrentHashMap<>(tenantServices));
    }

    /**
     * Performs a warm state sync for a new or backup orchestrator.
     */
    public void syncState(SDCPSRegistry localRegistry) {
        logger.info("WARM SYNC: Synchronizing local registry from distributed cluster state...");
        for (Map.Entry<String, Map<String, SDCPSRegistry.CPSMetadata>> entry : replicatedRegistry.entrySet()) {
            localRegistry.importTenantState(entry.getKey(), entry.getValue());
        }
        logger.info("Sync complete. Local registry is now cluster-consistent.");
    }

    public String electLeader(String nodeId) {
        this.currentLeaderId = nodeId;
        logger.warn("LEADER ELECTION: Node [{}] has been elected as the Cluster Leader.", nodeId);
        return currentLeaderId;
    }
}
