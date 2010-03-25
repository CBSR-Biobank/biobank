#!/usr/bin/env php
<?php

require_once "config.php";

$scriptname = basename($argv[0]);

/*
 * Aliquot clinic errors:
 * use bbpdb;
 * select * from freezer
 * join patient_visit on patient_visit.visit_nr=freezer.visit_nr
 * where (freezer.patient_nr=7189 and freezer.visit_nr=10011)
 * or (freezer.patient_nr=129 and freezer.visit_nr=10010)
 * order by fnum, rack, box, cell
 *
 */

$usage = <<<USAGE_END
   USAGE: {$scriptname} CSV_FILE

   Retrieves inventory IDs from the 10th column in CSV_FILE and outputs the corresponding fields
   in the BioBank2 database.
USAGE_END;

class Script {

   const BASE_QUERY = "
from aliquot
join patient_visit on patient_visit.id=aliquot.PATIENT_VISIT_ID
join patient on patient.id=patient_visit.PATIENT_ID
join pv_source_vessel on pv_source_vessel.patient_visit_id=patient_visit.id
join aliquot_position on aliquot_position.aliquot_ID=aliquot.id
join abstract_position on abstract_position.ID=aliquot_position.ABSTRACT_POSITION_ID
join container on container.id=aliquot_position.CONTAINER_ID
join container_path on container_path.container_id=container.id
join sample_type on sample_type.id=aliquot.sample_type_id
join study on study.id=patient.study_id
join shipment on shipment.id=patient_visit.shipment_id
join clinic on clinic.id=shipment.clinic_id
where study.name_short like binary '{study_name_short}'
and locate('/', path)<>0
and substr(path, 1, locate('/', path)-1) in
(SELECT container.id
  FROM container
  join container_type on container_type.id=container.container_type_id
  where name like '{container_type}%')
";

   const ALIQUOT_QUERY = "
select pnumber,study.name_short as study_name_short,
sample_type.name_short as sample_name_short,
date_drawn,label,row,col,inventory_id,clinic.name as clinic_site
{BASE_QUERY}
";

   const COUNT_QUERY = "
select count(*) as count {BASE_QUERY}
and label like '{frz_label}%'
";

   private static $studies = array('BBPSP');

   private static $container_types = array('Freezer', 'Cabinet');

   private static $headings = array(
      'patient_nr', 'study_name_short', 'sample_name_short', 'date_taken', 'fnum',
      'rack', 'box', 'cell', 'inventory_id', 'clinic_site');

   private $con = null;

   private $fp;

   public function __construct() {
      $this->con = mysqli_connect("localhost", "dummy", "ozzy498", "biobank2");
      if (mysqli_connect_errno()) {
         die(mysqli_connect_error());
      }

      foreach (self::$container_types as $container_type) {
         foreach (self::$studies as $study) {
            $filename = Utils::getOldStudyName($study) . "_{$container_type}_aliquots.csv";
            $tmpfile = tempnam('/tmp', $GLOBALS['scriptname']);
            $this->fp = fopen($filename, 'w');
            $this->showAliquots($study, $container_type);
            fclose($this->fp);
            system("tail -n+2 $filename | sort -t, -k 11,11  | cut -d, -f1-10 > $tmpfile");
            system("head -n1 $filename > {$tmpfile}2");
            system("cat {$tmpfile}2 $tmpfile > $filename");

            $this->fp = fopen($filename, 'a');
            fputcsv($this->fp, array("\n"));
            $this->showTotals($study, $container_type);
            fclose($this->fp);
         }
      }
   }

   private function showTotals($study, $container_type) {
      for ($i = 1; $i <= 5; ++$i) {
         $frz = sprintf("%02d", $i);

         $query = str_replace(array('{study_name_short}', '{container_type}'),
                              array($study, $container_type),
                              self::BASE_QUERY);

         $query = str_replace(array('{BASE_QUERY}', '{frz_label}'),
                              array($query, $frz), self::COUNT_QUERY);

         $result = $this->con->query($query);
         if ($result === FALSE) {
            die("query error: {$this->con->error}");
         }

         while ($row = $result->fetch_object()) {
            fputcsv($this->fp, array($i, $row->count));
         }
      }
   }

   private function showAliquots($study, $container_type) {
      fputcsv($this->fp, self::$headings);

      $query = str_replace(array('{study_name_short}', '{container_type}'),
                           array($study, $container_type),
                           self::BASE_QUERY);

      $query = str_replace('{BASE_QUERY}', $query, self::ALIQUOT_QUERY);

      $result = $this->con->query($query);
      if ($result === FALSE) {
         die("query error: {$this->con->error}");
      }

      $patientNrs = Utils::getBbpdbPatientNrs();

      while ($row = $result->fetch_object()) {
         //print_r($row);

         $pos = Container::getPosition($row->label, $container_type);
         $cell = Container::getCell($row->row, $row->col);
         $label = sprintf("%02d%s%02d%s", $pos['top'], $pos['childL1'], $pos['childL2'],
                          $cell);

         $data = array(
            'patient_nr' => $patientNrs[$row->pnumber],
            'study_name_short' => Utils::getOldStudyName($row->study_name_short),
            'sample_name_short' => $row->sample_name_short,
            'date_taken' => date('d-M-y', strtotime($row->date_drawn)),
            'fnum' => $pos['top'],
            'rack' => $pos['childL1'],
            'box' => $pos['childL2'],
            'cell' =>  $cell,
            'inventory_id' => $row->inventory_id,
            'clinic_site' => $row->clinic_site,
            'fullpos' => $label
            );

         fputcsv($this->fp, array_values($data));
      }
   }
}

new Script();

?>
