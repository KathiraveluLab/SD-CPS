# SD-CPS: Software-Defined Cyber-Physical Systems

SD-CPS is a research framework for orchestrating and managing Industrial Edge Cyber-Physical Systems (CPS) using a software-defined approach. It implements advanced features like SMART adaptation, control plane high-availability, chaos engineering, and privacy-by-design.

## Architecture
The framework is structured into a modular hierarchy:
- `org.sdcps`: System Bootstrap (`SDCPSMain`).
- `org.sdcps.core`: Orchestration and Topology Management.
- `org.sdcps.knowledge`: SMART Adaptation Logic and Empirical Validation.
- `org.sdcps.data`: Digital Twin Simulation and Privacy Guards.
- `org.sdcps.network`: Messaging and Transport Layer (M4T).
- `org.sdcps.workflow`: Automation and Edge Workflow Management.

## Quick Start
SD-CPS provides a unified launcher script `./sdcps.sh` for all operations.

### 1. Build & Setup
```bash
./sdcps.sh build
```

### 2. Live Research Dashboard
Open the visual dashboard to monitor simulation events. The dashboard is **dynamic** and syncs with the framework in real-time.
```bash
./sdcps.sh dashboard
```
*Note: The dashboard auto-refreshes to show events like node crashes and adaptations as they occur.*

### 3. Interactive CLI Shell
Trigger manual simulation events and observe system adaptation in the logs and dashboard:
```bash
./sdcps.sh interactive
```
*Available Commands:*
- `nodes`: List edge node metrics and energy constraints.
- `register`: Onboard new tenants and services.
- `addnode`: Scale the topology with new nodes.
- `crash <node>`: Simulates hardware failure and triggers self-healing.
- `congestion <node>`: Triggers SMART subflow cloning.

### Documentation
See the [USER-GUIDE.md](USER-GUIDE.md) for a full command reference and research dashboard walkthrough.
*Available Commands:*
- `crash <node>`: Simulates hardware failure and triggers self-healing.
- `congestion <node>`: Triggers SMART subflow cloning for elephant flows.
- `jitter <n1> <n2> <ms>`: Injects network interference between nodes.

### 4. Run Specific Case Studies
List and detail all 9 available research scenarios:
```bash
./sdcps.sh list
```

Run a specific scenario (e.g., High Availability):
```bash
./sdcps.sh run --case 5
```

### 5. Automated Parity Verification
Verify 100% research parity across all case studies:
```bash
./sdcps.sh verify
```

### 6. Message Broker (Optional)
To enable real-time AMQP messaging (M4T) without connection warnings:
```bash
./sdcps.sh broker
```
*(Requires Docker)*

## Verified Research Case Studies
SD-CPS includes an automated suite to verify 10 critical research case studies. Use `./sdcps.sh list` for full details on each:
1. **Static Placement**: (SDS 2017)
2. **SMART Adaptation**: (Cluster Computing 2019)
3. **Link Fluctuations**: (2019)
4. **Multi-tenancy**: (SDS 2017)
5. **High Availability**: (SDS 2017)
6. **Thermal Constraints**: (2019)
7. **Empirical Validation**: (SDS 2017 & 2019)
8. **Chaos Engineering**: (SDS 2017)
9. **Privacy-by-Design**: Integrated PII masking.
10. **Unit Testing**: 100% logic coverage.

## Citing SD-CPS
If you use SD-CPS in your research, please cite the following papers:

* Kathiravelu, P., Van Roy, P. and Veiga, L., 2019. **SD-CPS: software-defined cyber-physical systems. Taming the challenges of CPS with workflows at the edge**. Cluster Computing, 22(3), pp.661-677.

* Kathiravelu, P. and Veiga, L., 2017, May. **SD-CPS: taming the challenges of cyber-physical systems with a software-defined approach**. In 2017 Fourth International Conference on Software Defined Systems (SDS) (pp. 6-13). IEEE.
