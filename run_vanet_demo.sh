#!/bin/bash
# SD-CPS VANET Prototypical Deployment Demo
# Reproducing the V2V, V2I, and I2V communications experimental setup.

echo "--- Initializing SD-CPS VANET Simulation ---"

# Step 1: Build the project
echo "Step 1: Compiling Research Framework..."
mvn clean install -DskipTests &> /dev/null

# Step 2: Run Case Study 10 (VANET V2X)
echo "Step 2: Executing VANET Case Study (V2V, V2I, I2V)..."
./sdcps.sh run --case 10

echo ""
echo "Simulation Complete."
echo "Observations:"
echo " - V2V Safety messages published via M4T/AMQP."
echo " - V2I Telemetry mapped to Roadside Units (RSUs) in [topology.json]."
echo " - I2V Control loop verified via Orchestrator failover logic."
echo ""
echo "Monitor the Live Research Dashboard (dashboard.html) for visual alerts."
