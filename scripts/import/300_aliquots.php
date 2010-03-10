#!/usr/bin/env php
<?php

$cbsr = 'ABCDEFGHJKLMNPQRSTUVWXYZ';

$inventory_id = array(
   "NUBT585236",
   "1fis",
   "bbjv",
   "AWTY",
   "bajd",
   "bafu",
   "bbyv",
   "AXUX",
   "bcsk",
   "bdgy",
   "bdxq",
   "bgbu",
   "BAAN",
   "bhwy",
   "bhqk",
   "BCFN",
   "BCZV",
   "blav",
   "BFLP",
   "BEMP",
   "BENC",
   "bnad",
   "ccmo",
   "cdjg",
   "cfcr",
   "cger",
   "1qsn",
   "BLYK",
   "cjho",
   "ckit",
   "cmty",
   "cphs",
   "1uiv",
   "1vwr",
   "BTAB",
   "1xqr",
   "BUDF",
   "cwch",
   "BWVV",
   "BYBL",
   "dbhe",
   "BWOZ",
   "ddbu",
   "1EHB",
   "cxgd",
   "1EYT",
   "NUAT643735",
   "NUBS368012",
   "NUBR331756",
   "NUBS697808",
   "NUBQ840707",
   "NUBQ910662",
   "NUBQ911102",
   "NUBT479786",
   "NUBR010455",
   "NUBS912929",
   "NUBR050259",
   "NUBT051526",
   "NUBT487651",
   "NUBT521964",
   "NUBT217724",
   "NUBT243251",
   "NUBT276453",
   "NUBT273438",
   "NUBT273845",
   "NUBT211845",
   "NUBT207295",
   "NUBT207523",
   "NUBS446480",
   "NUBT312658",
   "NUBT312490",
   "NUBS558138",
   "NUBT428029",
   "NUBT315619",
   "NUBT530962",
   "NUBT629479",
   "NUBS615859",
   "NUBS616779",
   "NUBT389119",
   "NUAD495996",
   "NUAI268379",
   "NUAI464946",
   "NUAI269156",
   "NUAU426913",
   "NUAU431650",
   "NUAU091285",
   "NUAT697286",
   "NUBQ938756",
   "NUBQ821410",
   "NUBR286775",
   "NUBT326916",
   "NUBT025293",
   "NUBT453247",
   "NUBR193280",
   "AANX",
   "afqt",
   "AELK",
   "AEIA",
   "ACLI",
   "ahar",
   "afgq",
   "afcj",
   "akgh",
   "akrm",
   "abxr",
   "aliy",
   "amkv",
   "adel",
   "1dki",
   "1cxk",
   "ANVY",
   "ANLE",
   "1ain",
   "aoop",
   "apyz",
   "apyk",
   "1bod",
   "AUCF",
   "avfb",
   "1gcp",
   "1gbv",
   "ATLZ",
   "1hft",
   "atrq",
   "asys",
   "azkf",
   "AHRP",
   "1cyz",
   "NUAT688057",
   "NUBJ009107",
   "NUAU589975",
   "1fqh",
   "bect",
   "BCPF",
   "3ard",
   "0abs",
   "1ntx",
   "1nml",
   "cegi",
   "cedy",
   "0bgd",
   "0bjs",
   "ciiy",
   "1tjd",
   "cmld",
   "cmke",
   "cnbk",
   "1trt",
   "cnrm",
   "1ued",
   "BOUC",
   "1vlu",
   "0cei",
   "1wqz",
   "BWQI",
   "5jql",
   "dajr",
   "1DOB",
   "dcwv",
   "1EAE",
   "3cor",
   "0ecn",
   "1FTX",
   "NUAC898512",
   "NUAC929821",
   "NUAC798850",
   "NUAC803754",
   "NUAC885095",
   "NUAC955941",
   "NUAT674346",
   "NUAI166576",
   "NUAI294268",
   "NUAW588439",
   "NUBR020489",
   "NUAW780532",
   "NUAW780657",
   "NUAW520860",
   "NUAW530636",
   "NUAW530973",
   "NUBR175336",
   "NUBR073621",
   "NUBR178670",
   "NUBQ933715",
   "NUBR058844",
   "NUBS678238",
   "NUBR186004",
   "NUBR187410",
   "NUBR191857",
   "NUBQ909347",
   "NUBR216961",
   "NUBS369747",
   "NUBR364408",
   "NUBT546185",
   "NUBS505288",
   "NUBR310771",
   "NUBT485565",
   "NUAW591460",
   "NUBR088313",
   "NUBS914307",
   "NUBS914051",
   "NUBT409741",
   "NUBT634772",
   "NUBT641480",
   "NUBR513624",
   "NUBR256631",
   "NUBT474523",
   "NUBT473348",
   "NUBT470138",
   "NUBT489613",
   "NUBR342585",
   "NUBT329302",
   "NUBT332324",
   "NUBT227262",
   "NUBT027583",
   "NUBT030684",
   "NUBT254239",
   "NUBS577203",
   "NUBT586846",
   "NUBS574109",
   "NUBT284638",
   "NUBT557400",
   "NUBT568956",
   "NUBT311303",
   "NUBT449606",
   "NUBT011421",
   "NUBS619679",
   "NUBS616432",
   "NUBS972532",
   "NUBT384530",
   "csgj",
   "0clw",
   "5iqn",
   "ALXV",
   "NUBR318931",
   "NUBT201811",
   "NUBT338300",
   "NUBT260841",
   "cctb",
   "cfrl",
   "ckdp",
   "cwph",
   "NUAI444711",
   "NUAW691539",
   "NUAW692592",
   "NUBR152104",
   "clhg",
   "clhu",
   "5ayu",
   "5gyp",
   "cpnf",
   "cplj",
   "csde",
   "csih",
   "BXJG",
   "BYGP",
   "CBGT",
   "NUAD489515",
   "NUAC956199",
   "NUAI171912",
   "NUAU114328",
   "NUAU349270",
   "NUAW681268",
   "NUAU346927",
   "NUAU343629",
   "NUAW480049",
   "NUAW690044",
   "NUBQ877853",
   "NUAD256555",
   "NUAU563773",
   "NUBJ005828",
   "NUBQ817806",
   "NUBS366494",
   "NUBS693705",
   "NUBR256598",
   "NUBT476123",
   "NUBR370524",
   "NUBT302129",
   "NUBS718457",
   "NUBS441777",
   "NUBT621936",
   "NUBT419377",
   "NUBR041905",
   "NUBQ910431",
   "NUBS504906",
   "NUBT637168",
   "NUBT377345",
   "NUBS800068",
   "BADY",
   "5cdi",
   "cakp",
   "0bqq",
   "ctva",
   "BTSH",
   "3cge",
   "1xwt",
   "dfcn",
   "NUBQ997999",
   "NUBR215245",
   "NUBR365522",
   "5abc"
   );

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

   $chr2patientNr = getPatientNrs();
   $chr2visitNr = getVisitNrs();

   mysql_connect("localhost", "dummy", "ozzy498") or die(mysql_error());
   mysql_select_db("biobank2") or die(mysql_error());

   echo implode(",", $headings), "\n";

   foreach ($GLOBALS['inventory_id'] as $inv_id) {
      $query = str_replace(array('{inv_id}'), array($inv_id), $default_query);
      $result = mysql_query($query)  or die(mysql_error());

      while($row = mysql_fetch_array($result)) {
         //print_r($row);

         $label = $row['LABEL'];
         if (strpos('Sent Samples', $label) !== false) {
            $label = str_replace('Sent Samples', '99', $label);
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
            'patient_nr' => $chr2patientNr[$row['PNUMBER']],
            'visit_nr' => $chr2visitNr[$row['INVENTORY_ID']],
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

function getPatientNrs() {
   mysql_connect("localhost", "dummy", "ozzy498") or die(mysql_error());
   mysql_select_db("bbpdb") or die(mysql_error());
   $result = mysql_query("select patient_nr, dec_chr_nr from patient")
      or die(mysql_error());

   $chr2patientNr = array();
   while($row = mysql_fetch_array($result)) {
      $chr2patientNr[$row['dec_chr_nr']] = $row['patient_nr'];
   }
   mysql_close();
   return $chr2patientNr;
}

function getVisitNrs() {
   mysql_connect("localhost", "dummy", "ozzy498") or die(mysql_error());
   mysql_select_db("bbpdb") or die(mysql_error());

   $chr2visitNr = array();
   foreach ($GLOBALS['inventory_id'] as $inv_id) {
      $result = mysql_query("
            select visit_nr, inventory_id from freezer
            where inventory_id like binary '$inv_id'")
         or die(mysql_error());

      while($row = mysql_fetch_array($result)) {
         $chr2visitNr[$row['inventory_id']] = $row['visit_nr'];
      }
   }
   mysql_close();
   return $chr2visitNr;
}

main();

?>
