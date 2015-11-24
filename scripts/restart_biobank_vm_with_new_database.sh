#!/bin/bash

# TODO check if jboss has started with command:
#   <jboss_dir/bin/twiddle.sh get "jboss.system:type=Server" Started
# It returns "Started=true" when ready.

SCRIPT=${0##*/}

USAGE="
Usage: $SCRIPT [OPTIONS] VM DBDUMP

Connect to virtual machine VM and restarts the JBoss server with a new Biobank database.

OPTIONS
  -n DBNAME   The name of the database to create.
"

VM=$1
DBDUMP=$2
DBNAME="biobank_v3100"
SSH="/usr/bin/ssh"
SCP="/usr/bin/scp"

while getopts "n:" OPTION
do
    case $OPTION in
        n) DBNAME=$OPTARG
            shift $((OPTIND-1)); OPTIND=1
            ;;
        h) echo "$USAGE"; exit;;
    esac
done

if [ $# -ne 2 ]; then
    echo "USAGE: ${0##*/} VM DBDUMP"
    exit 1
fi

if [ -z "$SUDO_PWD" ]; then
    read -s -p "Sudo password on $VM: " SUDO_PWD
    echo ""
fi

if [ -z "$DB_PWD" ]; then
    read -s -p "Mysql password for root user on $VM: " DB_PWD
    echo ""
fi

DBDUMP_NOPATH=${DBDUMP##*/}

stop_jboss () {
    echo "stopping JBOSS"
    $SSH $VM "echo $SUDO_PWD | sudo -S /etc/init.d/jboss stop &> /dev/null"
}

start_jboss () {
    echo "starting JBOSS"
    $SSH $VM "echo $SUDO_PWD | sudo -S /etc/init.d/jboss start &> /dev/null"
}

scp_database () {
    echo "sending database dump: $DBDUMP"
    $SCP $DBDUMP $VM:$DBDUMP_NOPATH
}

initialize_database () {
    echo "resetting database: $DBNAME"
    $SSH $VM "mysql -uroot -p$DB_PWD -f drop $DBNAME create $DBNAME &> /dev/null"
}

apply_database_dump () {
    echo "initializing database \"$DBNAME\" with $DBDUMP_NOPATH"
    $SSH $VM "pv $DBDUMP_NOPATH | gzip -dc | mysql -uroot -p$DB_PWD $DBNAME"
}

# polls the JBoss server waiting up to 40 seconds for it to start
wait_for_jboss_start () {
    local NEXT_WAIT_TIME=0
    local JBOSS_CHK_CMD="~/jboss-4.0.5.GA/bin/twiddle.sh get 'jboss.system:type=Server' Started"
    local RESULT=""

    echo "Waiting for JBoss to start"
    sleep 10

    until [ "$RESULT" == "Started=true" ] || [ $NEXT_WAIT_TIME -ge 20 ]; do
        sleep $(( NEXT_WAIT_TIME += 5 ))
        RESULT=$($SSH $VM $JBOSS_CHK_CMD)
        echo "Waiting for JBoss to start"
    done

    if [ "$RESULT" == "Started=true" ]; then
        echo "JBoss started"
    else
        echo "JBoss did not start"
    fi
}

stop_jboss

scp_database
initialize_database
apply_database_dump

start_jboss
wait_for_jboss_start
