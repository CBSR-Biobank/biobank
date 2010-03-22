#!/usr/bin/env php
<?php

require_once "config.php";

$scriptname = basename($argv[0]);

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
join container_type on container_type.id=container.container_type_id
join sample_type on sample_type.id=aliquot.sample_type_id
join study on study.id=patient.study_id
join shipment on shipment.id=patient_visit.shipment_id
join clinic on clinic.id=shipment.clinic_id
where study.name_short like binary '{study_short_name}'
and container_type.name_short not like binary '%Bin%'
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

   private static $headings = array('patient_nr', 'study_name_short',
    'sample_name_short', 'date_taken', 'fnum', 'rack', 'box', 'cell',  
    'inventory_id', 'clinic_site', 'Label'
    );

    private $con = null;


    public function __construct() {
       $this->con = mysqli_connect("localhost", "dummy", "ozzy498", "biobank2");
       if (mysqli_connect_errno()) {
          die(mysqli_connect_error());
       }
       $this->showAliquots();
       echo "\n";
       $this->showTotals();
    }

    private function showTotals() {
       echo "Freezer,Total Aliquots\n";
       foreach (self::$studies as $study) {
          for ($i = 1; $i <= 5; ++$i) {
             $frz = sprintf("%02d", $i);

             $query = str_replace('{study_short_name}', $study,
             self::BASE_QUERY);

             $query = str_replace(array('{BASE_QUERY}', '{frz_label}'),
             array($query, $frz), self::COUNT_QUERY);

             $result = $this->con->query($query);
             if ($result === FALSE) {
                die("query error: {$this->con->error}");
             }
              
             while ($row = $result->fetch_object()) {
                echo $i, ",", $row->count, "\n";
             }
          }
       }
    }

    private function showAliquots() {
       echo implode(",", self::$headings), "\n";

       foreach (self::$studies as $study) {
          $query = str_replace('{study_short_name}', $study,
          self::BASE_QUERY);

          $query = str_replace('{BASE_QUERY}', $query, self::ALIQUOT_QUERY);

          $result = $this->con->query($query);
          if ($result === FALSE) {
             die("query error: {$this->con->error}");
          }
          
          $patientNrs = Utils::getBbpdbPatientNrs();

          while ($row = $result->fetch_object()) {
             //print_r($row);

             $pos = Freezer::getPosition($row->label);
             $cell = Freezer::getCell($row->row, $row->col);
             $label = sprintf("%02d%s%02d%s", $pos['frz'], $pos['hotel'], $pos['pallet'], $cell);

             $data = array(
                'patient_nr' => $patientNrs[$row->pnumber], 
                'study_name_short' => '"' . Utils::getOldStudyName($row->study_name_short) . '"',
                'sample_name_short' => '"' . $row->sample_name_short . '"', 
                'date_taken' => '"' . date('d-M-y', strtotime($row->date_drawn)) . '"', 
                'fnum' => $pos['frz'],
                'rack' => '"' . $pos['hotel'] . '"',
                'box' => $pos['pallet'],
                'cell' =>  '"' . $cell . '"',
                'inventory_id' => '"' . $row->inventory_id . '"', 
                'clinic_site' => '"' . $row->clinic_site . '"',
                'fullpos' => $label
             );

             echo implode(',', $data), "\n";
          }
       }
    }
}

new Script();

?>
