#!/usr/bin/env php
<?php

$invalid_samples = array(
array("AHFEM", "RNA-Transverse Colon"),
array("BBPSP", "Cells"),
array("BBPSP", "DNA (White blood cells)"),
array("BBPSP", "Filtered Urine"),
array("BBPSP", "RNAlater Biopsies"),
array("BBPSP", "Serum"),
array("BBPSP", "Serum (Beige top)"),
array("BBPSP", "Urine"),
array("CCCS", "Serum"),
array("CEGIIR", "Centrifuged Urine"),
array("CEGIIR", "Hemodialysate"),
array("CEGIIR", "Serum"),
array("CEGIIR", "Urine"),
array("CEGIIR", "Whole Blood EDTA"),
array("CHILD", "Cells"),
array("CHILD", "Serum (Beige top)"),
array("ERCIN", "Serum"),
array("KDCS", "Filtered Urine"),
array("KDCS", "RNAlater Biopsies"),
array("KDCS", "Urine"),
array("RVS", "Centrifuged Urine"),
array("RVS", "RNAlater Biopsies"),
array("RVS", "Serum (Beige top)"),
array("RVS", "Whole Blood EDTA"),
array("SPARK", "Urine"),
array("TCKS", "Centrifuged Urine"),
array("TCKS", "Filtered Urine"),
array("TCKS", "Hemodialysate"),
array("TCKS", "Serum"),
array("TCKS", "Urine"),
array("TCKS", "WB DMSO"),
array("VAS", "Finger Nails"),
array("VAS", "Hemodialysate"),
array("VAS", "Toe Nails")
);

$old_study_name = array("BBPSP" => "BBP");

$old_sample_type_name = array(
    "DNA (White blood cells)" => "DNA (WBC)",
    "RNAlater Biopsies" => "RNA Later"
);

mysql_connect("localhost", "dummy", "ozzy498") or die(mysql_error());
mysql_select_db("bbpdb") or die(mysql_error());

$headings = array('fnum', 'rack', 'box', 'cell', 'patient_nr', 'visit_nr', 
    'date_received','inventory_id', 'study_num_name', 'sample_name');

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
and sample_list.sample_name='{sample_type}'"
);

function getOldStudyName($name) {
    global $old_study_name;
    
    if (isset($old_study_name[$name])) {
        return $old_study_name[$name];
    } 
    return $name;
}

function getOldSampleTypeName($name) {
    global $old_study_name;
    
    if (isset($old_sample_type_name[$name])) {
        $sample_type_name = $old_sample_type_name[$name];
    }
    return $name;
}

foreach ($invalid_samples as $invalid_sample) {
    $study_name = getOldStudyName($invalid_sample[0]);    
    $sample_type_name = getOldSampleTypeName($invalid_sample[1]);
    echo "study \"{$study_name}\" has no sample storage for sample type \"{$sample_type_name}\"\n";
}

echo "\n";

echo implode(",", $headings), "\n";

foreach ($invalid_samples as $invalid_sample) {
    $study_name = getOldStudyName($invalid_sample[0]);    
    $sample_type_name = getOldSampleTypeName($invalid_sample[1]);

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
        
        error_log("rows for " . $key . ' - ' . $study_name 
            . ' - ' . $sample_type_name . ': ' . $num_rows);
            
        error_log("rows for " . $key . ' - ' . $study_name 
            . ' - ' . $sample_type_name . ': ' . $num_rows);
    
        while($row = mysql_fetch_array($result)) {
            $op = array();
            foreach ($headings as $heading) {
                $op[] = "\"{$row[$heading]}\"";
            }
            echo implode(",", $op), "\n";
        }
    }
}

?>
