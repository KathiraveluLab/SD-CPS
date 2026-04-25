#!/bin/bash
# SD-CPS Unified Launcher

function print_usage() {
    echo "SD-CPS Research Framework Tool"
    echo "Usage: ./sdcps.sh [command] [options]"
    echo ""
    echo "Commands:"
    echo "  build           Build the SD-CPS project"
    echo "  run             Run the research suite (default: all cases)"
    echo "  list            List and detail all available case studies"
    echo "  interactive     Start interactive simulation mode"
    echo "  verify          Run the automated parity verification script"
    echo "  dashboard       Open the Research Dashboard in the browser"
    echo "  broker          Start an ActiveMQ Artemis broker (Docker required)"
    echo "  broker-stop     Stop the ActiveMQ Artemis broker"
    echo "  help            Show this help message"
    echo ""
    echo "Options for 'run':"
    echo "  --case <N>      Run a specific case study (1-9)"
}

case "$1" in
    build)
        echo "Building SD-CPS..."
        mvn clean install -DskipTests
        ;;
    run)
        shift
        mvn exec:java -Dexec.mainClass="org.sdcps.SDCPSMain" -Dexec.args="$*"
        ;;
    list)
        mvn exec:java -Dexec.mainClass="org.sdcps.SDCPSMain" -Dexec.args="--list"
        ;;
    interactive)
        mvn exec:java -Dexec.mainClass="org.sdcps.SDCPSMain" -Dexec.args="interactive"
        ;;
    verify)
        /bin/bash ./verify_parity.sh
        ;;
    dashboard)
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            xdg-open dashboard.html &> /dev/null &
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            open dashboard.html &> /dev/null
        else
            echo "Please open dashboard.html manually in your browser."
        fi
        ;;
    broker)
        if ! command -v docker &> /dev/null; then
            echo "Error: Docker is not installed."
            exit 1
        fi
        echo "Cleaning up any existing sdcps-broker container..."
        docker rm -f sdcps-broker &> /dev/null
        echo "Starting ActiveMQ Artemis Broker on port 5672..."
        docker run -d --name sdcps-broker --rm -p 5672:5672 -p 8161:8161 \
            -e ARTEMIS_USER=admin -e ARTEMIS_PASSWORD=password \
            apache/activemq-artemis
        echo "Broker is starting. Web console available at: http://localhost:8161"
        ;;
    broker-stop)
        echo "Stopping ActiveMQ Artemis Broker..."
        docker stop sdcps-broker &> /dev/null
        echo "Broker stopped."
        ;;
    help|--help|-h)
        print_usage
        ;;
    *)
        if [ -z "$1" ]; then
            print_usage
        else
            echo "Unknown command: $1"
            print_usage
            exit 1
        fi
        ;;
esac
