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
    private int activeNodes = 12;
    private String systemUptime = "99.9%";

    private DashboardGenerator() {
        // Initialize with default research parity alerts
        alerts.add("<div class='success-item'><strong>SYSTEM BOOTSTRAP</strong><br><small>SD-CPS Research Framework initialized.</small></div>");
    }

    public static DashboardGenerator getInstance() {
        if (instance == null) {
            instance = new DashboardGenerator();
        }
        return instance;
    }

    public void addAlert(String type, String message) {
        String cssClass = type.equalsIgnoreCase("success") ? "success-item" : "alert-item";
        String alertTitle = type.toUpperCase();
        String alertHtml = String.format("<div class='%s'><strong>%s</strong><br><small>%s</small></div>", cssClass, alertTitle, message);
        alerts.add(0, alertHtml); // Newest first
        updateDashboard();
    }

    public void updateNodeCount(int count) {
        this.activeNodes = count;
        updateDashboard();
    }

    public void updateDashboard() {
        try (PrintWriter out = new PrintWriter(new FileWriter("dashboard_data.js"))) {
            out.println("const dashboardData = {");
            out.println("  nodeCount: " + activeNodes + ",");
            out.println("  uptime: \"" + systemUptime + "\",");
            out.println("  lastUpdate: \"" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "\",");
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
