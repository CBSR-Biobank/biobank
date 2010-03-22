#!/usr/bin/env php
<?php

main();

function main() {
   $invalid_samples = array(
      array("RVS","Peritoneal Dialysate"),
      );

   $headings = array(
      'fnum', 'rack', 'box', 'cell', 'patient_nr', 'visit_nr', 'date_received',
      'inventory_id', 'study_name_short', 'sample_name');

   $queries = array(
      "select *
from freezer
join patient_visit on patient_visit.visit_nr=freezer.visit_nr
join study_list on study_list.study_nr=patient_visit.study_nr
join sample_list on sample_list.sample_nr=freezer.sample_nr
where study_list.study_name_short='{study}'
and sample_list.sample_name='{sample_type}'",

      "select *
from cabinet
join patient_visit on patient_visit.visit_nr=cabinet.visit_nr
join study_list on study_list.study_nr=patient_visit.study_nr
join sample_list on sample_list.sample_nr=cabinet.sample_nr
where study_list.study_name_short='{study}'
and sample_list.sample_name='{sample_type}'",

      "select fnum, rack, box, cell, patient_visit.patient_nr,
patient_visit.visit_nr, date_received, freezer_link.inventory_id,
study_name_short, sample_name
from freezer_link
left join freezer on freezer.inventory_id=freezer_link.inventory_id
join patient_visit on patient_visit.visit_nr=freezer_link.visit_nr
join study_list on study_list.study_nr=patient_visit.study_nr
join sample_list on sample_list.sample_nr=freezer_link.sample_nr
where freezer.inventory_id is null
and study_list.study_name_short='{study}'
and sample_list.sample_name='{sample_type}'"
      );

   foreach ($invalid_samples as $invalid_sample) {
      $study_name = Utils::getOldStudyName($invalid_sample[0]);
      $sample_type_name = Utils::getOldSampleTypeName($invalid_sample[1]);
      echo "study \"{$study_name}\" has no sample storage for sample type \"{$sample_type_name}\"\n";
   }

   mysql_connect("localhost", "dummy", "ozzy498") or die(mysql_error());
   mysql_select_db("bbpdb") or die(mysql_error());

   echo implode(",", $headings), "\n";

   foreach ($invalid_samples as $invalid_sample) {
      $study_name = Utils::getOldStudyName($invalid_sample[0]);
      $sample_type_name = Utils::getOldSampleTypeName($invalid_sample[1]);

      foreach ($queries as $key => $query) {
         $query = str_replace(array('{study}', '{sample_type}'),
                              array($study_name, $sample_type_name),
                              $query);
         $result = mysql_query($query)  or die(mysql_error());
         $num_rows = mysql_num_rows($result);

         if ($num_rows == 0) {
            error_log("zero rows for " . $key . ' - ' . $study_name
                      . ' - ' . $sample_type_name);
            continue;
         }

         while($row = mysql_fetch_array($result)) {
            $op = array();
            foreach ($headings as $heading) {
               $op[] = "\"{$row[$heading]}\"";
            }
            echo implode(",", $op), "\n";
         }
      }
   }
}

?>
