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
     * Build the 12-node topology from the 2019 paper.
     */
    public void buildResearchTopology() {
        logger.info("Building 12-node topology from Cluster Computing 2019 paper...");
        // Replicating industrial automation topology with energy constraints (Watts)
        addNodeWithEnergy(new Node("n9", new String[]{"n6"}, new String[] {"s4"}, 12.0, 4.0, 150.0), 100.0);
        addNodeWithEnergy(new Node("n6", new String[]{"n9", "n7"}, new String[] {"s2", "s3", "S4"}, 15.0, 3.0, 200.0), 50.0); // Low energy (thermal)
        addNodeWithEnergy(new Node("n7", new String[]{"n6", "n8"}, new String[] {"s3", "S1"}, 8.0, 6.0, 80.0), 150.0);
        addNodeWithEnergy(new Node("n8", new String[]{"n7", "n10"}, new String[] {"s2"}, 10.0, 5.0, 100.0), 120.0);
        addNodeWithEnergy(new Node("n10", new String[]{"n8", "n11", "n12"}, new String[] {"s5", "s_heavy"}, 20.0, 2.0, 300.0), 30.0); // Low energy (vulnerable)
        addNodeWithEnergy(new Node("n11", new String[]{"n10", "n12", "n13"}, new String[] {"s1"}, 14.0, 4.0, 120.0), 80.0);
        addNodeWithEnergy(new Node("n12", new String[]{"n10", "n11"}, new String[] {"s3", "s4"}, 11.0, 4.5, 110.0), 90.0);
        addNodeWithEnergy(new Node("n13", new String[]{"n11", "n15"}, new String[] {"s2"}, 9.0, 5.5, 90.0), 110.0);
        addNodeWithEnergy(new Node("n15", new String[]{"n13", "n14", "n16", "n17"}, new String[] {"s1"}, 18.0, 2.5, 250.0), 70.0);
        addNodeWithEnergy(new Node("n14", new String[]{"n15"}, new String[] {"s2"}, 7.0, 7.0, 70.0), 130.0);
        addNodeWithEnergy(new Node("n17", new String[]{"n15"}, new String[] {"s2"}, 10.0, 5.0, 100.0), 140.0);
        addNodeWithEnergy(new Node("n16", new String[]{"n15"}, new String[] {"s3", "s4"}, 13.0, 3.5, 140.0), 160.0);
        
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
