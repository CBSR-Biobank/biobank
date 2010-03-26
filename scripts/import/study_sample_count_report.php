#!/usr/bin/env php
<?php

require_once "config.php";

$scriptname = basename($argv[0]);

class Script {

   const BASE_QUERY = "
from aliquot
join patient_visit on patient_visit.id=aliquot.patient_visit_id
join patient on patient.id=patient_visit.patient_id
join study on study.id=patient.study_id
join sample_type on sample_type.id=aliquot.sample_type_id
join aliquot_position on aliquot_position.aliquot_id=aliquot.id
join abstract_position on abstract_position.id=aliquot_position.abstract_position_id
join container on container.id=aliquot_position.container_id
join container_path on container_path.container_id=container.id
where study.name_short='{study_name_short}'
and locate('/', path)<>0
and substr(path, 1, locate('/', path)-1) not in
(SELECT container.id FROM container where label='SS')
";

   const ALIQUOT_QUERY = "
select pnumber, patient_visit.id as visit_id, count(*) as count,
date_processed, study.name_short as study_name_short
{BASE_QUERY}
group by patient_visit.id
order by date_processed,patient.id,study.name_short
";

   const COUNT_QUERY = "select count(*) as count {BASE_QUERY}";

   private static $studies = array('BBPSP', 'CEGIIR', 'KDCS', 'RVS');

   private static $headings = array('patient_nr', 'visit_nr',
   'CountOfinventory_id', 'date_received', 'study_name_short');

   private $con = null;

   private $fp;

   private $patientNrs;

   private $visitNrs;

   public function __construct() {
      $this->patientNrs = Utils::getBbpdbPatientNrs();
      $this->visitNrs = Utils::getBbpdbVisitNrs();

      $this->con = mysqli_connect("localhost", "dummy", "ozzy498", "biobank2");
      if (mysqli_connect_errno()) {
         die(mysqli_connect_error());
      }
      foreach (self::$studies as $study) {
         $this->fp = fopen(Utils::getOldStudyName($study) . '_total_samples.csv', 'w');
         $this->showAliquots($study);
         fputcsv($this->fp, array(""));
         $this->showTotals($study);
         fclose($this->fp);
      }
   }

   private function showTotals($study) {;

         $query = str_replace('{study_name_short}', $study,
         self::BASE_QUERY);

         $query = str_replace('{BASE_QUERY}', $query, self::COUNT_QUERY);

         $result = $this->con->query($query);
         if ($result === FALSE) {
            die("query error: {$this->con->error}");
         }

         while ($row = $result->fetch_object()) {
            fputcsv($this->fp, array('Total Samples', $row->count));
         }
   }

   private function showAliquots($study) {
      fputcsv($this->fp, self::$headings);

      $query = str_replace('{study_name_short}', $study,
      self::BASE_QUERY);

      $query = str_replace('{BASE_QUERY}', $query, self::ALIQUOT_QUERY);

      $result = $this->con->query($query);
      if ($result === FALSE) {
         die("query error: {$this->con->error}");
      }

      while ($row = $result->fetch_object()) {
         //print_r($row);

         $patient_nr = $this->patientNrs[$row->pnumber];
         $date_received = date('d-M-y', strtotime($row->date_processed));

         $data = array(
            'pnumber' => $patient_nr,
            'visit_id' => $this->visitNrs[$patient_nr][$date_received],
            'count' => $row->count,
            'date_received' => date('d-M-y', strtotime($row->date_processed)),
            'study_name_short' => Utils::getOldStudyName($row->study_name_short),
         );

         fputcsv($this->fp, array_values($data));
      }
   }

}

new Script();

?>