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
            xdg-open dashboard.html
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            open dashboard.html
        else
            echo "Please open dashboard.html manually in your browser."
        fi
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
