#!/bin/bash

SCRIPT=`basename $0`

USAGE="Uses mysqldump to dump all the user related tables in a Biobank database.

Usage: $SCRIPT [OPTIONS] QRY

OPTIONS
  -d DBNAME   The name of the database.
  -H DBHOST   The hostname of the machine running the MySQL server. Defaults to
              localhost if not specified.
  -u DBUSER   The user to use on the MySQL server.
  -o FNAME    File name to save output to. If not specified output is 'user_data.sql'.
  -p PWD      The password to use on the MySQL server.
  -h          Help text.
"

MYSQLDUMP=/usr/bin/mysqldump
SED=/bin/sed
DBHOST="localhost"
OUTPUT="user_data.sql"

while getopts "d:H:u:p:o:h" OPTION; do
    case $OPTION in
        d)
            DBNAME="$OPTARG"
            shift $((OPTIND-1)); OPTIND=1
            ;;
        H)
            DBHOST="$OPTARG"
            shift $((OPTIND-1)); OPTIND=1
            ;;
        u)
            DBUSER="$OPTARG"
            shift $((OPTIND-1)); OPTIND=1
            ;;
        o)
            OUTPUT="$OPTARG"
            shift $((OPTIND-1)); OPTIND=1
            ;;
        p)
            DBPWD="$OPTARG"
            shift $((OPTIND-1)); OPTIND=1
            ;;
        h)
            echo "$USAGE"
            exit 1
            ;;
        ?)
            echo "$USAGE"
            exit 1
            ;;
    esac
done

if [ -z "$DBUSER" ]; then
    echo "ERROR: user not specified"
    echo "$USAGE"
    exit
fi

if [ -z "$DBPWD" ]; then
    echo "ERROR: password not specified"
    echo "$USAGE"
    exit
fi

if [ -z "$DBNAME" ]; then
    echo "ERROR: database name not specified"
    echo "$USAGE"
    exit
fi

$MYSQLDUMP --skip-extended-insert -h$DBHOST -u$DBUSER -p$DBPWD $DBNAME csm_application csm_filter_clause csm_group csm_pg_pe csm_privilege csm_protection_element csm_protection_group csm_role csm_role_privilege csm_user csm_user_group csm_user_group_role_pg csm_user_pe domain domain_center domain_study group_user membership membership_permission membership_role principal role role_permission > $OUTPUT

