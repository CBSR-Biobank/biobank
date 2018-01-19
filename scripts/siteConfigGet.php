<?php

$usage = <<<USAGE
USAGE: {$argv[0]} SITE_NAME [OUTPUT_DIR]

Generates several CSV files that provide how Biobank is configured for a site.
USAGE;

$host = 'localhost';
$db   = 'cntrp_biobank';
$user = 'dummy';
$pass = 'ozzy498';
$charset = 'utf8mb4';
//$charset = 'utf8';

if (count($argv) < 2) {
exit('missing site argument');
}

$siteName = $argv[1];
$outputDir = $argv[2] ?? '.';
$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
$opt = [
  PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
  PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
  PDO::ATTR_EMULATE_PREPARES   => false
];

$queries = [];

$queries['clinicAdresses'] = <<<QRY_CLINIC_ADRESSES
  SELECT clinic.name, clinic.name_short, clinic.sends_shipments, clinic.activity_status_id,
         street1, street2, city,province, postal_code
  FROM center clinic
  JOIN address ON address.id=clinic.address_id
  JOIN contact ON contact.clinic_id=clinic.id
  JOIN study_contact ON study_contact.contact_id=contact.id
  JOIN site_study ON site_study.study_id=study_contact.study_id
  JOIN center site ON site.id=site_study.site_id
  WHERE clinic.discriminator='Clinic'
  AND site.discriminator='Site'
  AND site.name_short='$siteName'
  ORDER BY clinic.name
QRY_CLINIC_ADRESSES;

$queries['clinicContacts'] = <<<QRY_CLINIC_CONTACTS
  SELECT clinic.name_short, contact.name, contact.title,
  contact.office_number, contact.fax_number, contact.email_address
  FROM center clinic
  JOIN contact on contact.clinic_id=clinic.id
  JOIN study_contact ON study_contact.contact_id=contact.id
  JOIN site_study ON site_study.study_id=study_contact.study_id
  JOIN center site ON site.id=site_study.site_id
  WHERE clinic.discriminator='Clinic'
  AND site.discriminator='Site'
  AND site.name_short='$siteName'
  ORDER BY clinic.name, contact.name
QRY_CLINIC_CONTACTS;

$queries['containers'] = <<<QRY_CONTAINERS
  SELECT container_type.name_short type_name_short,container.label, container.product_barcode,
  container.activity_status_id, container.temperature
  FROM container
  JOIN container_type on container_type.id=container.container_type_id
  JOIN center site on site.id=container.site_id
  WHERE site.name_short='$siteName'
  ORDER BY label
QRY_CONTAINERS;

$queries['containerTypes'] = <<<QRY_CONTAINER_TYPES
  SELECT container_type.name, container_type.name_short, top_level,
  default_temperature, container_type.activity_status_id, row_capacity, col_capacity,
  container_labeling_scheme.name
  FROM container_type
  LEFT JOIN container_labeling_scheme ON container_labeling_scheme.id=container_type.child_labeling_scheme_id
  JOIN center site ON site.id=container_type.site_id
  WHERE site.name_short='$siteName'
  ORDER BY container_type.name_short
QRY_CONTAINER_TYPES;

$queries['childContainerTypes'] = <<<QRY_CHILD_CONTAINER_TYPES
  SELECT parent_ct.name_short parent_name_short,container_type.name_short child_name_short
  FROM container_type
  JOIN (SELECT name_short, child_container_type_id
          FROM container_type
          JOIN container_type_container_type
          ON container_type_container_type.parent_container_type_id=container_type.id) as parent_ct
  ON parent_ct.child_container_type_id=container_type.id
  JOIN center site on site.id=container_type.site_id
  WHERE site.name_short='$siteName'
  ORDER BY parent_ct.name_short, container_type.name_short
QRY_CHILD_CONTAINER_TYPES;

$queries['containerTypesSpecimenTypes'] = <<<QRY_CONTAINER_TYPES_SPECIMEN_TYPES
  SELECT container_type.name_short container_type_name_short, specimen_type.name_short specimen_type_name_short
  FROM container_type
  JOIN container_type_specimen_type ON container_type_specimen_type.container_type_id=container_type.id
  JOIN specimen_type ON specimen_type.id=container_type_specimen_type.specimen_type_id
  JOIN center site ON site.id=container_type.site_id
  WHERE site.name_short='$siteName'
  ORDER BY container_type.name_short,specimen_type.name_short
QRY_CONTAINER_TYPES_SPECIMEN_TYPES;

$queries['studySourceSpecimens'] = <<<QRY_STUDY_SOURCE_SPECIMENS
  SELECT study.name_short, st.name, need_original_volume
  FROM study
  JOIN source_specimen ON source_specimen.study_id=study.id
  JOIN specimen_type st ON st.id=source_specimen.specimen_type_id
  JOIN site_study ON site_study.study_id=study.id
  JOIN center site ON site.id=site_study.site_id
  WHERE site.name_short='$siteName'
  ORDER BY study.name_short,st.name
QRY_STUDY_SOURCE_SPECIMENS;

$queries['studyAliquotSpecimens'] = <<<QRY_STUDY_ALIQUOT_SPECIMENS
  SELECT study.name_short study_name_short, specimen_type.name_short specimen_type_name_short, quantity, volume
  FROM study
  JOIN aliquoted_specimen ON aliquoted_specimen.study_id=study.id
  JOIN specimen_type ON specimen_type.id=aliquoted_specimen.specimen_type_id
  JOIN site_study ON site_study.study_id=study.id
  JOIN center site ON site.id=site_study.site_id
  WHERE site.name_short='$siteName'
  ORDER BY study.name_short, specimen_type.name_short
QRY_STUDY_ALIQUOT_SPECIMENS;

$queries['specimenTypes'] = <<<QRY_SPECIMEN_TYPES
  SELECT ptype.name parent_type_name, ctype.name child_type_name, ctype.name_short child_type_name_short
  FROM specimen_type ctype
  JOIN specimen_type_specimen_type on specimen_type_specimen_type.child_specimen_type_id=ctype.id
  JOIN specimen_type ptype ON ptype.id=specimen_type_specimen_type.parent_specimen_type_id
  ORDER BY ptype.name, ctype.name
QRY_SPECIMEN_TYPES;

$queries['studies'] = <<<QRY_STUDIES
  SELECT study.name_short, study.name, study.activity_status_id
  FROM study
  JOIN site_study ON site_study.study_id=study.id
  JOIN center site ON site.id=site_study.site_id
  WHERE site.name_short='$siteName'
  ORDER BY study.name_short
QRY_STUDIES;

$queries['patients'] = <<<QRY_PATIENTS
  SELECT study.name_short, patient.pnumber, patient.created_at
  FROM patient
  JOIN study on study.id=patient.study_id
  JOIN site_study ON site_study.study_id=study.id
  JOIN center site ON site.id=site_study.site_id
  WHERE site.name_short='$siteName'
  ORDER BY study.name_short,patient.pnumber
QRY_PATIENTS;

$queries['collectionEvents'] = <<<QRY_PATIENTS
  SELECT study.name_short, patient.pnumber, ce.visit_number, ce.activity_status_id
  FROM collection_event ce
  JOIN patient on patient.id=ce.patient_id
  JOIN study on study.id=patient.study_id
  JOIN site_study ON site_study.study_id=study.id
  JOIN center site ON site.id=site_study.site_id
  WHERE site.name_short='$siteName'
  ORDER BY study.name_short,patient.pnumber, ce.visit_number
QRY_PATIENTS;

$queries['specimens'] = <<<QRY_SPECIMENS
  SELECT study.name_short study, pt.pnumber patient_number, ce.visit_number, spc.inventory_id, stype.name,
         pspc.inventory_id parent_inventory_id, spc.created_at, spc.quantity, center.name_short center,
         top_cntr_type.name_short top_container, cntr.label, spos.position_string
  FROM specimen spc
  LEFT JOIN specimen pspc ON pspc.id=spc.parent_specimen_id
  JOIN specimen_type stype ON stype.id=spc.specimen_type_id
  JOIN collection_event ce ON ce.id=spc.collection_event_id
  JOIN patient pt ON pt.id=ce.patient_id
  JOIN study ON study.id=pt.study_id
  JOIN center ON center.id=spc.current_center_id
  JOIN site_study ON site_study.study_id=study.id
  JOIN center site ON site.id=site_study.site_id
  LEFT JOIN specimen_position spos ON spos.specimen_id=spc.id
  LEFT JOIN container cntr ON cntr.id=spos.container_id
  LEFT JOIN container top_cntr ON top_cntr.id=cntr.top_container_id
  LEFT JOIN container_type top_cntr_type ON top_cntr_type.id=top_cntr.container_type_id
  WHERE site.name_short='$siteName'
  ORDER by pt.pnumber, ce.visit_number, spc.inventory_id, spc.created_at, pspc.inventory_id, stype.name
QRY_SPECIMENS;

function allQueries($pdo, $outputDir, $siteName, $queries) {
  foreach ($queries as $filename => $query) {
    $filename = $outputDir . '/' . $siteName . '-' . $filename . '-' . date('Y-m-d') . '.csv';
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
}

$pdo = new PDO($dsn, $user, $pass, $opt);

allQueries($pdo, $outputDir, $siteName, $queries);
