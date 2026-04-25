package org.sdcps.core;

import org.evora.core.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Knowledge Plane: Topology Manager for Software-Defined Cyber-Physical Systems (SD-CPS).
 * 
 * Manages the graph-based representation of edge nodes and communication links.
 * Features research-grade metadata for energy-aware placement (Cluster Computing 2019).
 * 
 * @see <a href="https://link.springer.com/article/10.1007/s10586-019-02970-0">Cluster Computing 2019 Paper</a>
 */
public class SDCPSTopology {
    private static final Logger logger = LoggerFactory.getLogger(SDCPSTopology.class);
    private Map<String, Node> nodes = new HashMap<>();
    private Map<String, Double> nodeEnergyCapacities = new HashMap<>();

    public void addNode(Node node) {
        logger.info("Adding node to topology: {} (Cost: {}, Latency: {})", node.getId(), node.getCost(), node.getLatency());
        nodes.put(node.getId(), node);
    }

    public void addNodeWithEnergy(Node node, double energyCapacity) {
        addNode(node);
        nodeEnergyCapacities.put(node.getId(), energyCapacity);
    }

    public double getEnergyCapacity(String nodeId) {
        return nodeEnergyCapacities.getOrDefault(nodeId, 0.0);
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public Map<String, Node> getAllNodes() {
        return nodes;
    }

    /**
     * Build the topology. Now loads from topology.json.
     */
    public void buildResearchTopology() {
        java.io.File file = new java.io.File("topology.json");
        if (!file.exists()) {
            logger.warn("topology.json not found! Using emergency hardcoded default (n9).");
            addNodeWithEnergy(new Node("n9", new String[]{}, new String[] {}, 12.0, 4.0, 150.0), 100.0);
            return;
        }

        try (java.io.FileReader reader = new java.io.FileReader(file)) {
            com.google.gson.JsonArray array = com.google.gson.JsonParser.parseReader(reader).getAsJsonArray();
            logger.info("Loading {} nodes from topology.json...", array.size());
            
            for (com.google.gson.JsonElement el : array) {
                com.google.gson.JsonObject obj = el.getAsJsonObject();
                String id = obj.get("id").getAsString();
                double cost = obj.get("cost").getAsDouble();
                double lat = obj.get("latency").getAsDouble();
                double res = obj.get("resources").getAsDouble();
                double energy = obj.get("energy").getAsDouble();
                
                com.google.gson.JsonArray neighArr = obj.getAsJsonArray("neighbors");
                String[] neighbors = new String[neighArr.size()];
                for (int i = 0; i < neighArr.size(); i++) neighbors[i] = neighArr.get(i).getAsString();
                
                com.google.gson.JsonArray servArr = obj.getAsJsonArray("services");
                String[] services = new String[servArr.size()];
                for (int i = 0; i < servArr.size(); i++) services[i] = servArr.get(i).getAsString();
                
                addNodeWithEnergy(new Node(id, neighbors, services, cost, lat, res), energy);
            }
        } catch (Exception e) {
            logger.error("Failed to load topology.json: {}", e.getMessage());
        }
        
        // Linking latencies (ms)
        nodes.get("n10").addNeighborLatency("n12", 2.0);
        nodes.get("n12").addNeighborLatency("n16", 5.0);
        nodes.get("n16").addNeighborLatency("n15", 3.0);
        nodes.get("n15").addNeighborLatency("n6", 12.0);
        nodes.get("n6").addNeighborLatency("n7", 4.0);
    }

    /**
     * Updates the latency of a link between two nodes.
     * Simulates real-time network fluctuations.
     */
    public void updateLinkLatency(String srcId, String destId, double newLatency) {
        Node src = nodes.get(srcId);
        if (src != null) {
            src.addNeighborLatency(destId, newLatency);
            logger.info("Dynamic Link Update: [{} -> {}] Latency shifted to {}ms", srcId, destId, newLatency);
        }
    }
}
