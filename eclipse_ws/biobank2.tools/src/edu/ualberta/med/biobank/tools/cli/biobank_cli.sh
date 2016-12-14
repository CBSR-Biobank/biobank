#!/bin/bash

SCRIPT=`basename $0`

USAGE="
This script invokes the Biobank CLI tool.

To see the available CLI commands, use the \"help\" command.

Ensure the path stored in the JAVA variable points to a version 6 JRE.

Usage: $SCRIPT [OPTIONS] COMMAND [COMMAND_ARGUMENTS]

OPTIONS
  -H | --hostname HOST     The Biobank server's hostname. Defaults to localhost if not specified.
  -p | --port     PORT     The port being used by the Biobank server. Defautls to 443.
  -u | --user     USER     The username to use when logging into the Biobank server.
  -w | --password PWD      The user's password.
  -j | --jrepath  PATH     The path to the Java Runtime Environment (JRE). Defaults to /usr/lib/jvm/java-6-oracle.
  -h | --help              Displays this help text.
"

JAVA="/usr/lib/jvm/java-6-oracle/bin/java"
JAR="BiobankCli.jar"

if [[ ! -f "$JAVA" ]]; then
   echo "Error: Java JRE not found at $JAVA"
   exit 1
fi

if [[ ! -f "$JAR" ]]; then
   echo "Error: file $JAR does not exist"
   exit 1
fi

OPTS=`getopt -o hH:p:u:w:j: --long help,hostname,port,user,password,jrepath -n 'parse-options' -- "$@"`

if [ $? != 0 ] ; then echo "Failed to parse options." >&2 ; exit 1 ; fi

#echo "$OPTS"
eval set -- "$OPTS"

HELP=false
SRV_HOST=""
SRV_PORT=""
SRV_USER=""
SRV_PWD=""
DFLT_SRV_HOST="localhost"
DFLT_SRV_PORT="443"

while true; do
    case "$1" in
        -H | --hostname ) SRV_HOST="$2"; shift; shift;;
        -p | --port )     SRV_PORT="$2"; shift; shift;;
        -u | --user )     SRV_USER="$2"; shift; shift;;
        -w | --password ) SRV_PWD="$2"; shift; shift;;
        -j | --jrepath )  JAVA="$2/bin/java"; shift; shift;;
        -h | --help )     HELP=true; shift; shift;;
        -- ) shift; break ;;
        * ) break ;;
    esac
done

if $HELP; then
    echo "$USAGE"
    exit 1
fi

if [ -z "$SRV_HOST" ]; then
    read -p "Enter the biobank server host name: [$DFLT_SRV_HOST] " SRV_HOST
    SRV_HOST=${SRV_HOST:-$DFLT_SRV_HOST}
fi

if [ -z "$SRV_PORT" ]; then
    read -p "Enter the biobank server port: [$DFLT_SRV_PORT] " SRV_PORT
    SRV_PORT=${SRV_PORT:-$DFLT_SRV_PORT}
fi

if [ -z "$SRV_USER" ]; then
    read -p "Enter biobank user name: " SRV_USER
fi

if [ -z "$SRV_PWD" ]; then
    read -s -p "Enter user's password: " SRV_PWD
    echo ""
fi

COMMAND="$*"

if [ -z "$COMMAND" ]; then
    read -p "Enter command: " COMMAND
fi

#echo "$JAVA -jar $JAR -H $SRV_HOST --port $SRV_PORT -u $SRV_USER -w $SRV_PWD \"$COMMAND\""

$JAVA -jar $JAR -H $SRV_HOST --port $SRV_PORT -u $SRV_USER -w $SRV_PWD "$COMMAND"
