package org.sdcps.knowledge;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates dynamic dashboard data for SD-CPS.
 */
public class DashboardGenerator {
    private static DashboardGenerator instance;
    private List<String> alerts = new ArrayList<>();
    private List<ServiceEntry> services = new ArrayList<>();
    private java.util.Map<String, String> nodeStatuses = new java.util.TreeMap<>();

    public static class ServiceEntry {
        public String tenant, service, status;
        public double energy;
        public boolean realTime;
        public ServiceEntry(String t, String s, double e, boolean rt, String st) {
            this.tenant = t; this.service = s; this.energy = e; this.realTime = rt; this.status = st;
        }
    }
    private String systemUptime = "99.9%";
    private String tenantIsolationStatus = "ACTIVE";
    private String brokerStatus = "OFFLINE";

    private DashboardGenerator() {
        // Initialize nodes with default status
        for (int i = 6; i <= 17; i++) nodeStatuses.put("n" + i, "NORMAL");
        alerts.add("<div class='success-item'><strong>SYSTEM BOOTSTRAP</strong><br><small>SD-CPS Framework initialized.</small></div>");
        
        // Background Probe for M4T Broker
        java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }).scheduleAtFixedRate(() -> {
            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress("localhost", 5672), 100);
                this.updateBrokerStatus(true);
            } catch (java.io.IOException e) {
                this.updateBrokerStatus(false);
            }
        }, 0, 10, java.util.concurrent.TimeUnit.SECONDS);
    }

    public static DashboardGenerator getInstance() {
        if (instance == null) {
            instance = new DashboardGenerator();
        }
        return instance;
    }

    public void updateNodeStatus(String nodeId, String status) {
        nodeStatuses.put(nodeId, status);
        updateDashboard();
    }

    public void updateService(String tenant, String service, double energy, boolean rt, String status) {
        services.removeIf(s -> s.tenant.equals(tenant) && s.service.equals(service));
        services.add(new ServiceEntry(tenant, service, energy, rt, status));
        updateDashboard();
    }

    public void updateUptime(String uptime) {
        this.systemUptime = uptime;
        updateDashboard();
    }

    public void updateBrokerStatus(boolean online) {
        this.brokerStatus = online ? "ONLINE" : "OFFLINE";
        updateDashboard();
    }

    public void addAlert(String type, String message) {
        String cssClass = type.equalsIgnoreCase("success") ? "success-item" : "alert-item";
        String alertTitle = type.toUpperCase();
        String alertHtml = String.format("<div class='%s'><strong>%s</strong><br><small>%s</small></div>", cssClass, alertTitle, message);
        alerts.add(0, alertHtml); // Newest first
        updateDashboard();
    }

    public void updateDashboard() {
        try (PrintWriter out = new PrintWriter(new FileWriter("dashboard_data.js"))) {
            long activeCount = nodeStatuses.values().stream()
                .filter(s -> !s.equals("CRASHED"))
                .count();
            long tenantCount = services.stream().map(s -> s.tenant).distinct().count();
            String isolationStr = tenantCount <= 1 ? "ACTIVE" : tenantCount + " NAMESPACES";
            
            out.println("const dashboardData = {");
            out.println("  nodeCount: " + activeCount + ",");
            out.println("  uptime: \"" + systemUptime + "\",");
            out.println("  tenantStatus: \"" + isolationStr + "\",");
            out.println("  brokerStatus: \"" + brokerStatus + "\",");
            out.println("  lastUpdate: \"" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "\",");
            out.println("  nodeStatuses: {");
            int j = 0;
            for (java.util.Map.Entry<String, String> entry : nodeStatuses.entrySet()) {
                out.println("    \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"" + (++j < nodeStatuses.size() ? "," : ""));
            }
            out.println("  },");
            out.println("  services: [");
            for (int i = 0; i < services.size(); i++) {
                ServiceEntry s = services.get(i);
                out.printf("    {tenant: \"%s\", service: \"%s\", energy: \"%.1fW\", realTime: \"%s\", status: \"%s\"}%s\n",
                    s.tenant, s.service, s.energy, s.realTime ? "YES" : "NO", s.status, (i < services.size() - 1 ? "," : ""));
            }
            out.println("  ],");
            out.println("  alerts: [");
            for (int i = 0; i < alerts.size() && i < 15; i++) { // Increased to 15 alerts
                out.println("    `" + alerts.get(i).replace("`", "'") + "`" + (i < alerts.size() - 1 ? "," : ""));
            }
            out.println("  ]");
            out.println("};");
            out.println("if (typeof updateDashboardUI === 'function') updateDashboardUI();");
        } catch (IOException e) {
            System.err.println("Failed to update dashboard data: " + e.getMessage());
        }
    }
}
