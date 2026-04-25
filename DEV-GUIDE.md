# SD-CPS Developer Guide

This guide provides instructions for developers looking to extend the SD-CPS (Software-Defined Cyber-Physical Systems) framework or integrate it with a full **OpenDaylight (ODL)** controller distribution.

## 1. Architectural Overview

SD-CPS follows a modular architecture where the Control Plane logic is decoupled from the Data Plane messaging.

- **Orchestration**: Managed by `SDCPSOrchestrator`, which uses the Evora engine.
- **Messaging**: Managed by the **Messaging4Transport (M4T)** library, which connects to an AMQP Broker (ActiveMQ Artemis).
- **Observability**: A dynamic Research Dashboard (`dashboard.html`) powered by `DashboardGenerator`.

---

## 2. Integrating with OpenDaylight

To run SD-CPS as a native OpenDaylight extension (as described in the original SDS 2017 paper), follow these steps:

### 2.1. Prerequisites
- **JDK 21** (Matches current project version)
- **Apache Maven 3.9+**
- **OpenDaylight Distribution** (e.g., Beryllium, Boron, or a modern managed version supporting M4T).

### 2.2. Packaging as an ODL Bundle
Currently, SD-CPS is configured as a standalone JAR. To host it inside ODL's Karaf container:

1. **Update `pom.xml`**: Change the packaging to `bundle` and add the `maven-bundle-plugin`.
2. **Add ODL Parent**: Point the parent POM to `org.opendaylight.mdsal:binding-parent`.
3. **OSGi Manifest**: Ensure that `org.sdcps.*` packages are exported and `org.opendaylight.messaging4transport.*` is imported.

### 2.3. Enabling M4T in OpenDaylight
Messaging4Transport must be active in the ODL container for the controller to communicate with the edge nodes.

1. Start OpenDaylight/Karaf.
2. Install the M4T feature:
   ```bash
   feature:install odl-messaging4transport-impl
   ```
3. Configure the AMQP Broker connection in `etc/org.opendaylight.messaging4transport.cfg`.

### 2.4. Deployment Logic
In "hosted mode," the `SDCPSMain` initialization should be moved to an **Activator** or a **Blueprint** XML file located in `src/main/resources/org/opendaylight/blueprint/`. This allows ODL to manage the lifecycle of the SD-CPS orchestrator.

---

## 3. Development Workflow

### 3.1. Building the Project
```bash
./sdcps.sh build
# Or manually
mvn clean install -DskipTests
```

### 3.2. Running Standalone Research Simulations
For rapid prototyping without ODL, use the launcher:
```bash
./sdcps.sh run --case <N>
```

### 3.3. Extending the Orchestrator
To add new research logic (e.g., a new AI-based placement engine):
1. Create a class in `org.sdcps.knowledge`.
2. Integrate it into `SDCPSOrchestrator.solve()`.
3. Update `DashboardGenerator` to reflect any new metrics.

---

## 4. Testing & Parity
Always verify changes against the research parity suite:
```bash
./sdcps.sh verify
```
This ensures that your modifications haven't broken the core case studies (Static Placement, SMART, HA, etc.).
