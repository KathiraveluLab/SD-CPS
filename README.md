# SD-CPS
Software-Defined Cyber-Physical Systems

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
### Prerequisites
- Java 21+
- Maven 3.8+

### 1. Build the System
```bash
mvn clean install
```

### 2. Run the Research Suite
```bash
mvn exec:java -Dexec.mainClass="org.sdcps.SDCPSMain"
```

### 3. Automated Parity Verification
```bash
/bin/bash ./verify_parity.sh
```

## Verified Research Case Studies
The project includes an automated suite to verify 10 critical research case studies:
1. **Static Placement**: Greedy placement of services on edge nodes (SDS 2017).
2. **SMART Adaptation**: Dynamic subflow cloning for elephant flows (Cluster Computing 2019).
3. **Link Fluctuations**: Real-time response to transient network jitter.
4. **Multi-tenancy**: Secure namespace isolation between tenants (SDS 2017).
5. **High Availability**: ODL-style orchestrator clustering and warm failover.
6. **Thermal Constraints**: Energy-aware placement avoiding thermal breaches.
7. **Empirical Validation**: 1000+ iteration simulation with 95% Confidence Intervals.
8. **Chaos Engineering**: Automated self-healing after simulated node crashes.
9. **Privacy-by-Design**: Automated PII masking and telemetry anonymization.
10. **Unit Testing**: 100% logic coverage for core adaptation decision paths.

## Citing SD-CPS
If you use SD-CPS in your research, please cite the following papers:

* Kathiravelu, P., Van Roy, P. and Veiga, L., 2019. **SD-CPS: software-defined cyber-physical systems. Taming the challenges of CPS with workflows at the edge**. Cluster Computing, 22(3), pp.661-677.

* Kathiravelu, P. and Veiga, L., 2017, May. **SD-CPS: taming the challenges of cyber-physical systems with a software-defined approach**. In 2017 Fourth International Conference on Software Defined Systems (SDS) (pp. 6-13). IEEE.