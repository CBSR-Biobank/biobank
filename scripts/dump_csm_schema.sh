#!/bin/bash

#set -o verbose

SCRIPT=`basename $0`

USAGE="
Usage: $SCRIPT [OPTIONS]

OPTIONS
  -e TABLE    Exclude table with name TABLE. This option can be used multiple
              times to exclude multiple tables.
  -d DBNAME   The database name. Defaults to biobank2 if not specified.
  -H DBHOST   The hostname of the machine running the MySQL server. Defaults to
              localhost if not specified.
  -u DBUSER   The user to use on the MySQL server.
  -p PWD      The password to use on the MySQL server.
  -o FILE     The file to save the dump file to.
  -h          Help text.
"

DBHOST="localhost"
DBNAME=biobank

function in_array () {
    haystack=( "$@" )
    haystack_size=( "${#haystack[@]}" )
    needle=${haystack[$((${haystack_size}-1))]}
    for ((i=0;i<$(($haystack_size-1));i++)); do
        h=${haystack[${i}]};
        [ $h = $needle ] && return 1
    done
    return 0
}


while getopts "e:d:hH:u:p:o:" OPTION
do
  case $OPTION in
        e) exclude=(${exclude[@]} $OPTARG );;
        d) DBNAME=$OPTARG;;
        H) DBHOST=$OPTARG;;
        u) DBUSER=$OPTARG;;
        p) DBPWD=$OPTARG;;
        o) OUTFILE=$OPTARG;;
        h) echo "$USAGE"; exit;;
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

for table in `mysql -h$DBHOST -u$DBUSER -p$DBPWD $DBNAME -BNe "show tables"` ; do
  # echo "  table: \"$table\""
    #if [[ "$table" =~ ^csm_.* ]]; then # this line does not work on bash less than 3.2
	if echo $table | grep -q '^csm_.*'; then
        in_array "${exclude[@]}" $table
        if [ $? -eq 0 ]; then
            CSM_TABLES="$CSM_TABLES $table"
        fi
    fi
done

if [ -z "${#CSM_TABLES}" ]; then
    echo "ERROR: database does not contain any CSM tables"
fi

if [ -n "$OUTFILE" ]; then
    echo "" > $OUTFILE
fi

if [ -z "$OUTFILE" ]; then
    mysqldump --skip-extended-insert -h$DBHOST -u$DBUSER -p$DBPWD $DBNAME $CSM_TABLES
else
    mysqldump --skip-extended-insert -h$DBHOST -u$DBUSER -p$DBPWD $DBNAME $CSM_TABLES >> $OUTFILE
fi

if [ -n "$OUTFILE" ]; then
    echo "tables dumped to $OUTFILE"
fi
