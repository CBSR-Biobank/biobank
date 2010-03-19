#!/usr/bin/env php
<?php

  /**
   * this script is used to test the information for samples selected at random.
   */

$progname = basename($argv[0]);

$usage = <<<USAGE_END
  USAGE: {$progname} CSV_FILE

  Retrieves inventory IDs from the 10th column in CSV_FILE and outputs the corresponding fields
  in the BioBank2 database.
USAGE_END;

$cbsr = 'ABCDEFGHJKLMNPQRSTUVWXYZ';

function main() {
   $cbsr = $GLOBALS['cbsr'];

   $default_query = "
select *
from aliquot
join patient_visit on patient_visit.id=aliquot.PATIENT_VISIT_ID
join patient on patient.id=patient_visit.PATIENT_ID
join pv_source_vessel on pv_source_vessel.patient_visit_id=patient_visit.id
join pv_attr on pv_attr.PATIENT_VISIT_ID=patient_visit.id
join study_pv_attr on study_pv_attr.id=pv_attr.STUDY_PV_ATTR_ID
join aliquot_position on aliquot_position.aliquot_ID=aliquot.id
join abstract_position on abstract_position.ID=aliquot_position.ABSTRACT_POSITION_ID
join container on container.id=aliquot_position.CONTAINER_ID
where inventory_id like binary '{inv_id}'
and study_pv_attr.label='Worksheet'
";

   $headings = array('patient_nr', 'visit_nr', 'date_taken', 'date_received',
                     'worksheet', 'fnum', 'rack', 'box', 'cell', 'inventory_id');

   if ($GLOBALS['argc'] != 2) {
      die($GLOBALS['usage']);
   }

   $csvfile = $GLOBALS['argv'][1];
   $inventory_ids = getInventoryIds($csvfile);

   mysql_connect("localhost", "dummy", "ozzy498") or die(mysql_error());
   mysql_select_db("biobank2") or die(mysql_error());

   echo implode(",", $headings), "\n";


   foreach (array_keys($inventory_ids) as $inv_id) {
      $query = str_replace(array('{inv_id}'), array($inv_id), $default_query);
      $result = mysql_query($query)  or die(mysql_error());

      while($row = mysql_fetch_array($result)) {
         //print_r($row);

         $label = $row['LABEL'];
         if (strpos($label, 'SS') !== FALSE) {
            $label = str_replace('SS', '99', $label);
         }

         $frz = intval(substr($label, 0, 2));
         $hotel = substr($label, 2, 2);
         $pallet  = intval(substr($label, 4, 2));

         if (($frz == 2) && ($pallet >= 13)) {
            $label = getFrz2OldLabel($label);
            $frz = intval(substr($label, 0, 2));
            $hotel = substr($label, 2, 2);
            $pallet  = intval(substr($label, 4, 2));
         }

         $data = array(
            'patient_nr' => $inventory_ids[$inv_id]['patient_nr'],
            'visit_nr' => $inventory_ids[$inv_id]['visit_nr'],
            'date_taken' => date('d-M-y', strtotime($row['DATE_DRAWN'])),
            'date_received'=> date('d-M-y', strtotime($row['DATE_PROCESSED'])),
            'worksheet' => '"' . $row['VALUE'] . '"',
            'fnum' => $frz,
            'rack' => '"' . $hotel . '"',
            'box' => $pallet,
            'cell' => sprintf("\"%s%d\"", substr($cbsr, $row['ROW'], 1),  $row['COL'] + 1),
            'inventory_id' => '"' . $row['INVENTORY_ID'] . '"'
            );
         echo implode(",", $data), "\n";
      }
   }
   mysql_close();
}

function getInventoryIds($filename) {
   if (($handle = fopen($filename, "r")) === FALSE) return null;

   $inventory_ids = array();
   $row = 1;
   while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
      $num = count($data);
      if ($num != 10) {
         throw new Exception("invalid field count at line $row");
      }
      $inventory_ids[$data[9]] = array('patient_nr' => $data[0], 'visit_nr' => $data[1]);
      $row++;
   }
   fclose($handle);
   return $inventory_ids;
}


function getFrz2OldLabel($label) {
   $cbsr = $GLOBALS['cbsr'];

   if (strlen($label) != 6) {
      throw new Exception("label length is invalid: " . $label);
   }

   $frz = intval(substr($label, 0, 2));
   if ($frz != 2) {
      throw new Exception("invalid label: " . $label);

   }
   $pallet = intval(substr($label, 4, 2));
   if (($pallet < 13) || ($pallet > 18)) {
      throw new Exception("pallet in label is invalid: " . $label);
   }

   $hotel = substr($label, 2, 2);
   $hotel2 = substr($hotel, 1);

   if (substr($hotel, 0, 1) == 'A') {
      if (($hotel2 >= 'A') && ($hotel2 <= 'K')) {
         $hotel2 = strpos($cbsr, $hotel2);
         $hotel = 'C' . substr($cbsr, $hotel2 / 2, 1);
         $pallet -= 12;
         if ($hotel2 % 2 > 0) {
            $pallet += 6;
         }
      } else {
         $frz = 4;
         $frz4 = getFrz4Label($hotel, $pallet);
         $pallet = $frz4['pallet'];
         $hotel = 'A' . substr($cbsr, $frz4['hotel'], 1);
      }
   } if (substr($hotel, 0, 1) == 'B') {
      if (($hotel2 >= 'S') && ($hotel2 <= 'Z')) {
         $hotel2 = strpos($cbsr, $hotel2) - strpos($cbsr, 'S');
         $hotel = 'C' . substr($cbsr, strpos($cbsr, 'F') + $hotel2 / 2, 1);
         $pallet -= 12;
         if ($hotel2 % 2 > 0) {
            $pallet += 6;
         }
      } else {
         $frz = 4;
         $frz4 = getFrz4Label($hotel, $pallet);
         $pallet = $frz4['pallet'];
         $hotel = 'A' . substr($cbsr, $frz4['hotel'], 1);
      }
   }
   return sprintf('%02d%s%02d', $frz, $hotel, $pallet);
}

function getFrz4Label($hotel, $pallet) {
   $cbsr = $GLOBALS['cbsr'];

   $hotel2 = strpos($cbsr, substr($hotel, 0, 1)) * strlen($cbsr) + strpos($cbsr, substr($hotel, 1)) - strpos($cbsr, 'L');
   $group = intval($hotel2 / 5);
   $withinGroup = $hotel2 % 5;
   switch ($withinGroup) {
      case 0:
         $hotelOffset = 0;
         $pallet -= 12;
         break;
      case 1:
         if ($pallet < 17) {
            $hotelOffset = 0;
            $pallet -= 6;
         } else {
            $hotelOffset = 1;
            $pallet -= 16;
         }
         break;
      case 2:
         $hotelOffset = 1;
         $pallet -= 10;
         break;
      case 3:
         if ($pallet < 15) {
            $hotelOffset = 1;
            $pallet -= 4;
         } else {
            $hotelOffset = 2;
            $pallet -= 14;
         }
         break;
      case 4:
         $hotelOffset = 2;
         $pallet -= 8;
         break;
   }
   return array('hotel' => 3 * $group + $hotelOffset, 'pallet' => $pallet);
}

main();

?>
