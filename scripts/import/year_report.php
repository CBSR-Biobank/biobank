#!/usr/bin/env php
<?php

require_once "config.php";

$scriptname = basename($argv[0]);

class Script {

   const PV_QUERY = "
select name_short, count(*) as count
from patient_visit
join patient on patient.id=patient_visit.patient_id
join study on study.id=patient.study_id
where year(date_processed)={year}
group by study.id
";   

   const PATIENT_QUERY = "     
select name_short, count(*) as count
from              
(select name_short, pnumber, count(*)
 from patient
 join patient_visit on patient_visit.patient_id=patient.id
 join study on study.id=patient.study_id
 where year(date_processed)={year}
 group by study.id, patient.id
) as A
group by name_short
";   

   private $con = null;


   public function __construct() {
      $this->con = mysqli_connect("localhost", "dummy", "ozzy498", "biobank2");
      if (mysqli_connect_errno()) {
         die(mysqli_connect_error());
      }

      $this->fp = fopen('year_total.csv', 'w');

      fputcsv($this->fp, array("Patient Visists"));
      $this->genTable($this->getPatientVisitData());
      fputcsv($this->fp, array(""));
      fputcsv($this->fp, array("Patients"));
      $this->genTable($this->getPatientData());

      fclose($this->fp);
   }

   private function getPatientVisitData() {
      $report = array();
         for ($i = 2010; $i >=2000; --$i) {
            $query = str_replace(array('{year}'), array($i),
            self::PV_QUERY);

            $result = $this->con->query($query);
            if ($result === FALSE) {
               die("query error: {$this->con->error}");
            }
            while ($row = $result->fetch_object()) {
               if (empty($report[$row->name_short])) {
                  $report[$row->name_short] = array();
               }
               if ($row->count != 0) {
                  //$report[$row->name_short] = $report[$row->name_short] + array($i => $row->count);
                  $report[$row->name_short] += array($i => $row->count);
               }
            }
         }
      return $report;
   }

   private function getPatientData() {
      $report = array();
      for ($i = 2010; $i >=2000; --$i) {
         $query = str_replace(array('{year}'), array($i),
         self::PATIENT_QUERY);

         $result = $this->con->query($query);
         if ($result === FALSE) {
            die("query error: {$this->con->error}");
         }
         while ($row = $result->fetch_object()) {
            if (empty($report[$row->name_short])) {
               $report[$row->name_short] = array();
            }
            if ($row->count != 0) {
               $report[$row->name_short] = $report[$row->name_short] + array($i => $row->count);
            }
         }
      }
      return $report;
   }

   private function genTable($report) {
      $row = array('Study');
      for ($i = 2010; $i >=2000; --$i) {
         $row[] = $i;
      }
      fputcsv($this->fp, $row);
      
      ksort($report);

      foreach (array_keys($report) as $study) {
         $row = array(Utils::getOldStudyName($study));
         for ($i = 2010; $i >=2000; --$i) {
            if (!empty($report[$study][$i])) {
               $row[] = $report[$study][$i];
            } else {
               $row[] = '';
            }
         }
         fputcsv($this->fp, $row);
      }
   }
}

new Script();

?>