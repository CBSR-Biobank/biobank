#!/usr/bin/env php
<?php

function getReport($filename) {
   if (($handle = fopen($filename, "r")) === FALSE) return null;

   $data = array();
   while (($csv = fgetcsv($handle, 1000, ",")) !== FALSE) {
      //print_r($csv);
      $num = count($csv);
      if (($num != 3) || (strpos($csv[0], '#') !== FALSE)) {
         continue;
      }
      $data[$csv[0]] = array($csv[1] => $csv[2]);
   }
   fclose($handle);
   return $data;
}

$data = getReport($argv[1]);
ksort($data);
print_r($data);
foreach ($data as $study => $year_data) {
   echo $study;
   for ($i = 2010; i >= 2000; --$i) {
      echo $year_data[$i];
   }
   echo "\n";
}