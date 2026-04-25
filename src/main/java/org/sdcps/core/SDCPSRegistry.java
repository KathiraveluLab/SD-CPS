package org.sdcps.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Knowledge Plane: Service Registry for SD-CPS.
 * Extends Evora's registry with CPS-specific metadata (e.g., energy constraints, real-time requirements).
 */
public class SDCPSRegistry {
    private static final Logger logger = LoggerFactory.getLogger(SDCPSRegistry.class);
    private static SDCPSRegistry instance;
    private Map<String, Map<String, CPSMetadata>> tenantMetadataMap = new HashMap<>();

    private SDCPSRegistry() {}

    /**
     * For simulation purposes, allows creating a fresh instance for backup orchestrators.
     */
    public SDCPSRegistry(boolean isBackup) {
        if (!isBackup) {
            instance = this;
        }
    }

    public static synchronized SDCPSRegistry getInstance() {
        if (instance == null) {
            instance = new SDCPSRegistry();
        }
        return instance;
    }

    /**
     * Register a CPS service with metadata for a specific tenant.
     */
    public void registerCPSService(String tenantId, String serviceId, double energyConstraint, boolean isRealTime) {
        logger.info("Registering CPS service for tenant [{}]: {} (Energy: {}W, Real-time: {})", tenantId, serviceId, energyConstraint, isRealTime);
        tenantMetadataMap.computeIfAbsent(tenantId, k -> new HashMap<>())
                .put(serviceId, new CPSMetadata(energyConstraint, isRealTime));
        
        // Update Research Dashboard
        org.sdcps.knowledge.DashboardGenerator.getInstance().updateService(tenantId, serviceId, energyConstraint, isRealTime, "REGISTERED");

        // Push update to the cluster for replication
        SDCPSClusterManager.getInstance().replicateState(tenantId, tenantMetadataMap.get(tenantId));
    }

    /**
     * Simulation of MD-SAL Distributed Data Store sync.
     */
    public void importTenantState(String tenantId, Map<String, CPSMetadata> services) {
        tenantMetadataMap.put(tenantId, new HashMap<>(services));
    }

    public CPSMetadata getMetadata(String tenantId, String serviceId) {
        Map<String, CPSMetadata> services = tenantMetadataMap.get(tenantId);
        return (services != null) ? services.get(serviceId) : null;
    }

    public static class CPSMetadata {
        private double energyConstraint;
        private boolean isRealTime;

        public CPSMetadata(double energyConstraint, boolean isRealTime) {
            this.energyConstraint = energyConstraint;
            this.isRealTime = isRealTime;
        }

        public double getEnergyConstraint() { return energyConstraint; }
        public boolean isRealTime() { return isRealTime; }
    }
}
