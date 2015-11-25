#!/bin/bash

# TODO set activity status to closed on parent specimens
#

JAVA=/usr/lib/jvm/java-6-oracle/bin/java
JAR=BiobankCli.jar
BB_HOST=192.168.2.43
BB_USER=loyola
CSV_DIR="csv"

if [ -z "$BB_PWD" ]; then
    read -s -p "Biobank password for user $BB_USER: " BB_PWD
    echo ""
fi

if [ ! -d "$CSV_DIR" ]; then
    echo "directory not found: $CSV_DIR"
    exit 1
fi

CLI_CMD="$JAVA -jar $JAR -H $BB_HOST -u $BB_USER -w $BB_PWD"

do_command () {
    if [ "$#" -eq 0 ]; then
        echo "Usage: do_command ARGS"
        return 1
    fi

    local CMD="$CLI_CMD $*"

    $CMD

    if [ ! $? -eq 0 ]; then
        echo "command failed: $CMD"
        exit 1
    fi
}

do_command shipment_import_csv CBSR $CSV_DIR/shipments.csv

do_command create_container_csv $CSV_DIR/containers.csv

do_command patient_import_csv CBSR $CSV_DIR/patients.csv

for CSV in $CSV_DIR/parent_specimens_*.csv; do
    do_command specimen_import_csv CBSR $CSV
done

for CSV in $CSV_DIR/child_specimens_*.csv; do
    do_command specimen_import_csv CBSR $CSV
done

for CSV in $CSV_DIR/closed_specimens_*.csv; do
    do_command specimen_update_activity_status_csv CBSR $CSV
done

do_command processing_event_udpate_csv $CSV_DIR/processing_events.csv
