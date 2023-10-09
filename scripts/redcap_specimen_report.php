<?php

declare(strict_types=1);

$usage = <<<USAGE
usage: {$argv[0]} SITE_NAME [OUTPUT_DIR]

Downloads all the specimens in a Biobank database to a CSV file.
USAGE;

$query = <<<QRY_SPECIMENS
SELECT study.name_short study, pt.pnumber patient_number, ce.visit_number, spc.inventory_id,
         stype.name, pspc.created_at time_drawn, spc.quantity
FROM specimen spc
LEFT JOIN specimen pspc ON pspc.id=spc.parent_specimen_id
JOIN specimen_type stype ON stype.id=spc.specimen_type_id
JOIN collection_event ce ON ce.id=spc.collection_event_id
LEFT JOIN processing_event pe ON pe.id=pspc.processing_event_id
JOIN patient pt ON pt.id=ce.patient_id
JOIN study ON study.id=pt.study_id
JOIN center ON center.id=spc.current_center_id
JOIN site_study ON site_study.study_id=study.id
JOIN center site ON site.id=site_study.site_id
LEFT JOIN specimen_position spos ON spos.specimen_id=spc.id
LEFT JOIN container cntr ON cntr.id=spos.container_id
LEFT JOIN container top_cntr ON top_cntr.id=cntr.top_container_id
LEFT JOIN container_type top_cntr_type ON top_cntr_type.id=top_cntr.container_type_id
WHERE top_cntr_type.name_short not like 'SS%'
AND study.name_short = 'CoCollab'
AND spc.activity_status_id = 1
ORDER by study.name_short, pt.pnumber, ce.visit_number, spc.inventory_id, spc.created_at, pspc.inventory_id, stype.name
QRY_SPECIMENS;


$host = '127.0.0.1';
$dbname = 'biobank';
$user = 'biobank';
$pass = 'biobank';
$charset = 'utf8mb4';

$outputDir = $argv[1] ?? '.';

$dsn = "mysql:host=$host;dbname=$dbname;charset=$charset";
$opt = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false
];


function runQuery($pdo, $outputDir) {
    global $query;

    $filename = $outputDir . '/specimens-' . date('Y-m-d') . '.csv';
    $data = fopen($filename, 'w');
    $stmt = $pdo->query($query);
    $outputHeaders = true;
    echo "$filename: row count: " . $stmt->rowCount() . "\n";

    while ($row = $stmt->fetch()) {
        if ($outputHeaders) {
            fputcsv($data, array_keys($row));
            $outputHeaders = false;
        }
        fputcsv($data, $row);
    }
    fclose($data);
}

$pdo = new PDO($dsn, $user, $pass, $opt);

runQuery($pdo, $outputDir);
