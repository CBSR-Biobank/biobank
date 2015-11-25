#!/bin/bash

SCRIPT=${0##*/}

PAGE_SIZE=1000
OUTPUT_DIR="csv"
DB_NAME="biobank_training_v3100"

while getopts "n:" OPTION
do
    case $OPTION in
        n) DBNAME=$OPTARG
            shift $((OPTIND-1)); OPTIND=1
            ;;
        h) echo "$USAGE"; exit;;
    esac
done

if [ -z "$DBPWD" ]; then
    read -s -p "Enter MySQL Password for root : " DBPWD
    echo ""
fi

if [ ! -d "$OUTPUT_DIR" ]; then
    mkdir "$OUTPUT_DIR"
fi

SPECIMEN_FROM_CLAUSE="FROM specimen spc
JOIN specimen_type spct ON spct.id=spc.specimen_type_id
LEFT JOIN specimen pspc ON pspc.id=spc.parent_specimen_id
JOIN origin_info oi ON oi.id=spc.origin_info_id
JOIN center ocenter ON ocenter.id=oi.center_id
JOIN center ccenter ON ccenter.id=spc.current_center_id
JOIN specimen_type stype ON stype.id=spc.specimen_type_id
JOIN collection_event ce ON ce.id=spc.collection_event_id
LEFT JOIN shipment_info shinfo ON shinfo.id=oi.shipment_info_id
LEFT JOIN processing_event pe ON pe.id=spc.processing_event_id
JOIN patient p ON p.id=ce.patient_id
JOIN study s ON s.id=p.study_id
LEFT JOIN specimen_position spos ON spos.specimen_id=spc.id
LEFT JOIN container c ON c.id=spos.container_id
LEFT JOIN container_position cpos ON cpos.container_id=c.id
LEFT JOIN container_type ct ON ct.id=c.container_type_id
LEFT JOIN container topc ON topc.id=c.top_container_id
LEFT JOIN container_type topct ON topct.id=topc.container_type_id"

get_patients_csv () {
    local OUTFILE="patients.csv"
    local QRY="SELECT 'Study','Patient Number','Enrollment Date','Comment'
UNION ALL
(SELECT study.name_short,pt.pnumber,
date_format(convert_tz(pt.created_at,'GMT','Canada/Mountain'), '%Y-%m-%d %H:%i'),
IF (cmt.message IS NULL, '',
   GROUP_CONCAT(DISTINCT cmt.message,
                         ' (',
                         pr.login,
                         ': ',
                         DATE_FORMAT(CONVERT_TZ(cmt.created_at,'GMT','Canada/Mountain'), '%Y-%m-%d'),
                         ')'))
INTO OUTFILE '/tmp/$OUTFILE'
FIELDS TERMINATED BY ','
ENCLOSED BY '\"'
LINES TERMINATED BY '\n'
FROM patient pt join study ON study.id=pt.study_id
LEFT JOIN patient_comment ptc ON ptc.patient_id=pt.id
LEFT JOIN comment cmt ON cmt.id=ptc.comment_id
LEFT JOIN principal pr on pr.id=cmt.user_id
WHERE study.name_short='APCaRI'
GROUP BY pt.id)"

    #echo $QRY
    sudo rm -f /tmp/$OUTFILE
    mysql -uroot -p$DBPWD $DB_NAME -e "${QRY}"
    sudo mv /tmp/$OUTFILE $OUTPUT_DIR
}

get_shipments_csv () {
    # for import to work on 3.10.1, all shipments must have a comment field
    local OUTFILE="shipments.csv"
    local QRY="SELECT 'Received date', 'Sending clinic', 'Receiving site', 'Shipping method', 'Waybill', 'Comment'
UNION ALL
(SELECT DATE_FORMAT(CONVERT_TZ(sh.received_at,'GMT','Canada/Mountain'), '%Y-%m-%d %H:%i'),
ctr.name_short, rctr.name_short, shm.name,sh.waybill,
IF (cmt.message is null, 'Imported from test-server',
    GROUP_CONCAT(DISTINCT cmt.message,
                          ' (',
                           pr.login,
                           ': ',
                           DATE_FORMAT(CONVERT_TZ(cmt.created_at,'GMT','Canada/Mountain'), '%Y-%m-%d'),
                           ')'))
INTO OUTFILE '/tmp/$OUTFILE'
FIELDS TERMINATED BY ','
ENCLOSED BY '\"'
LINES TERMINATED BY '\n'
FROM shipment_info sh
JOIN shipping_method shm on shm.id=sh.shipping_method_id
JOIN origin_info oi on oi.shipment_info_id=sh.id
JOIN specimen spc on spc.origin_info_id=oi.id
JOIN center ctr on ctr.id=oi.center_id
JOIN center rctr on rctr.id=oi.receiver_site_id
JOIN collection_event ce on ce.id=spc.collection_event_id
JOIN patient p on p.id=ce.patient_id
JOIN study s on s.id=p.study_id
LEFT join origin_info_comment oicmt on oicmt.ORIGIN_INFO_ID=oi.id
LEFT join comment cmt on cmt.id=oicmt.comment_id
LEFT JOIN principal pr on pr.id=cmt.user_id
WHERE s.name_short='APCaRI'
GROUP BY sh.id,cmt.id
ORDER BY sh.received_at)"

    sudo rm -f /tmp/$OUTFILE
    mysql -uroot -p$DBPWD $DB_NAME -e "${QRY}"
    sudo mv /tmp/$OUTFILE $OUTPUT_DIR
}

get_containers_csv () {
    local OUTFILE="containers.csv"
    local SELECT_CLAUSE=""
    local QRY="SELECT 'siteNameShort','containerTypeNameShort','label','productBarcode','row','col'
UNION ALL
(SELECT 'CBSR',ct.name_short,c.label,c.product_barcode,cpos.row,cpos.col
INTO OUTFILE '/tmp/$OUTFILE'
FIELDS TERMINATED BY ','
ENCLOSED BY '\"'
LINES TERMINATED BY '\n'
$SPECIMEN_FROM_CLAUSE
WHERE s.name_short='APCaRI'
AND c.label IS NOT null
GROUP BY c.label)"

    sudo rm -f /tmp/$OUTFILE
    mysql -uroot -p$DBPWD $DB_NAME -e "${QRY}"
    sudo mv /tmp/$OUTFILE $OUTPUT_DIR
}

# splits results into multiple CSV files with each having 1000 rows max.
get_specimens_csv () {
    if [ "$#" -eq 0 ]; then
        echo "Usage: get_specimens_csv WHERE_CLAUSE FILE_BASE_NAME"
        return 1
    fi

    local FILE_BASE_NAME=$1
    local WHERE_CLAUSE=$2

    # sets the waybill to APCaRI so that origin center is assigned correctly
    local HEADERS_SELECT_CLAUSE="SELECT 'Inventory ID','Specimen type','Parent Inventory ID','Volume',
'Created time','Patient number','Visit number','Waybill','Source Specimen','Worksheet','Pallet product barcode',
'Top parent container type','Pallet label','Specimen position in pallet','Comment'"

    local DATA_SELECT_CLAUSE="SELECT spc.inventory_id,
IFNULL(pspc.inventory_id, ''),
IFNULL(spc.quantity, ''),
spct.name,
date_format(convert_tz(spc.created_at,'GMT','Canada/Mountain'), '%Y-%m-%d %H:%i'),
p.pnumber,
ce.visit_number,
IFNULL(shinfo.waybill, ''),
IF (spc.parent_specimen_id is null, 'Y', 'N'),
IFNULL(pe.worksheet, ''),
IFNULL(topc.product_barcode, ''),
IFNULL(topct.name_short, ''),
IFNULL(c.label, ''),
IFNULL(spos.position_string, ''),
IF (cmt.message is NULL, '',
   GROUP_CONCAT(DISTINCT cmt.message,
                         ' (',
                         pr.login,
                         ': ',
                         DATE_FORMAT(CONVERT_TZ(cmt.created_at,'GMT','Canada/Mountain'), '%Y-%m-%d'),
                         ')'))"

    local JOIN_CLAUSE="LEFT JOIN specimen_comment spccmt on spccmt.specimen_id=spc.id
LEFT JOIN comment cmt on cmt.id=spccmt.comment_id
LEFT JOIN principal pr on pr.id=cmt.user_id"

    local ORDER_BY_CLAUSE="ORDER BY s.name_short,p.pnumber,spc.inventory_id,spc.created_at"
    local GROUP_BY_CLAUSE="GROUP BY spc.id"
    local FROM_WHERE_CLAUSE="${SPECIMEN_FROM_CLAUSE} ${JOIN_CLAUSE} ${WHERE_CLAUSE}"
    local NUM_SPECIMENS_QRY="SELECT count(DISTINCT spc.id) ${FROM_WHERE_CLAUSE}"

    local NUM_SPECIMENS=$(mysql --skip-column-names --raw --batch -uroot -p$DBPWD -e "${NUM_SPECIMENS_QRY}" $DB_NAME)
    local NUM_FILES=$(($NUM_SPECIMENS / $PAGE_SIZE))
    local OUTFILE=""
    local OUTFILE_CLAUSE=""
    local QRY=""

    #echo "${QRY} ${GROUP_BY_CLAUSE} ${ORDER_BY_CLAUSE}"

    for OFFSET in $(seq 0 $NUM_FILES); do
        OUTFILE="${FILE_BASE_NAME}_${OFFSET}.csv"
        OUTFILE_CLAUSE="INTO OUTFILE '/tmp/$OUTFILE'
FIELDS TERMINATED BY ','
ENCLOSED BY '\"'
LINES TERMINATED BY '\n'"
        QRY="${HEADERS_SELECT_CLAUSE}
UNION
(${DATA_SELECT_CLAUSE}
${OUTFILE_CLAUSE}
${FROM_WHERE_CLAUSE}
${GROUP_BY_CLAUSE}
${ORDER_BY_CLAUSE}
LIMIT $((1000 * $OFFSET)),1000)"

        #echo $QRY

        sudo rm -f /tmp/$OUTFILE
        mysql -uroot -p$DBPWD $DB_NAME -e "${QRY}"
        sudo mv /tmp/$OUTFILE $OUTPUT_DIR
    done
}

get_parent_specimens () {
    local WHERE_CLAUSE="WHERE s.name_short='APCaRI' AND pspc.inventory_id is null"
    get_specimens_csv "parent_specimens" "$WHERE_CLAUSE"
}

get_child_specimens () {
    local WHERE_CLAUSE="WHERE s.name_short='APCaRI' AND pspc.inventory_id is not null"
    get_specimens_csv "child_specimens" "$WHERE_CLAUSE"
}

get_closed_specimens () {
    local HEADERS_SELECT_CLAUSE="SELECT 'inventoryId','activityStatus'"
    local DATA_SELECT_CLAUSE="SELECT spc.inventory_id,spc.activity_status_id"

    local WHERE_CLAUSE="WHERE s.name_short='APCaRI' AND spc.activity_status_id!=1"
    local FROM_WHERE_CLAUSE="${SPECIMEN_FROM_CLAUSE} ${WHERE_CLAUSE}"
    local NUM_SPECIMENS_QRY="SELECT count(DISTINCT spc.id) ${FROM_WHERE_CLAUSE}"

    local NUM_SPECIMENS=$(mysql --skip-column-names --raw --batch -uroot -p$DBPWD -e "${NUM_SPECIMENS_QRY}" $DB_NAME)

    local NUM_FILES=$(($NUM_SPECIMENS / $PAGE_SIZE))
    local FILE_BASE_NAME="closed_specimens"
    local OUTFILE=""
    local OUTFILE_CLAUSE=""
    local QRY=""

    for OFFSET in $(seq 0 $NUM_FILES); do
        OUTFILE="${FILE_BASE_NAME}_${OFFSET}.csv"
        OUTFILE_CLAUSE="INTO OUTFILE '/tmp/$OUTFILE' FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\n'"
        QRY="${HEADERS_SELECT_CLAUSE}
UNION
(${DATA_SELECT_CLAUSE}
${OUTFILE_CLAUSE}
${FROM_WHERE_CLAUSE}
LIMIT $((1000 * $OFFSET)),1000)"

        #echo $QRY

        sudo rm -f /tmp/$OUTFILE
        mysql -uroot -p$DBPWD $DB_NAME -e "${QRY}"
        sudo mv /tmp/$OUTFILE $OUTPUT_DIR
    done
}

get_processing_events_csv () {
    local OUTFILE="processing_events.csv"
    local QRY="SELECT 'worksheet','createdAt','activityStatus','siteNameShort'
UNION ALL
(SELECT pe.worksheet,
date_format(convert_tz(pe.created_at,'GMT','Canada/Mountain'), '%Y-%m-%d %H:%i'),
pe.activity_status_id,ctr.name_short
INTO OUTFILE '/tmp/$OUTFILE'
FIELDS TERMINATED BY ','
ENCLOSED BY '\"'
LINES TERMINATED BY '\n'
FROM processing_event pe
JOIN specimen spc ON spc.processing_event_id=pe.id
JOIN collection_event ce ON ce.id=spc.collection_event_id
JOIN patient p ON p.id=ce.patient_id
JOIN study s ON s.id=p.study_id
JOIN center ctr ON ctr.id=pe.center_id
WHERE s.name_short='APCaRI'
GROUP by pe.id)"

    #echo $QRY
    sudo rm -f /tmp/$OUTFILE
    mysql -uroot -p$DBPWD $DB_NAME -e "${QRY}"
    sudo mv /tmp/$OUTFILE $OUTPUT_DIR
}

get_patients_csv
get_shipments_csv
get_containers_csv
get_parent_specimens
get_child_specimens
get_closed_specimens
get_processing_events_csv
