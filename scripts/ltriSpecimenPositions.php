<?php

$usage = <<<USAGE
USAGE: {$argv[0]} CSV_FILE_NAME [OUTPUT_DIR]

Converts a file containing specimens positions, provided by LTRI, into several files that
can be imported into Biobank.
USAGE;

if (count($argv) < 2) {
  exit("missing CSV file name argument\n");
}

$csvname = $argv[1];
$outdir = $argv[2] ?? ".";

$discardedSpecimens = [
  "0116251646",
  "0116251645",
  "0116251644",
  "0188050920",
  "0188050921",
  "0188050922",
  "0188050923",
  "0188050924",
  "0188050925",
  "0188036870",
  "0188036869",
  "0188036868",
  "0116251649",
  "0116251648",
  "0116251647",
  "0188125320",
  "0188125321",
  "0188125322",
  "0188125323",
  "0188125324",
  "0188125325",
  "0188049734",
  "0188049733",
  "0188049732",
  "G200361757",
  "G200865391",
  "G200865353",
  "G200361753",
  "G200891229",
  "G200865162",
  "G200886151",
  "OBSMS0000048LEU",
  "OBSMS0001841LEU",
  "OBSMS0001849LEU",
  "OBSMS0002162LEU",
  "G200886101",
  "G200361762",
  "G200865333",
  "G200865349",
  "G200006464",
  "G200006453",
  "G200002989",
  "G200002939",
  "G200002709",
  "G200002735",
  "G200002837",
  "G200002560",
  "G200882903",
  "G200882913",
  "G200850018",
  "G200850016",
  "G200886127"
];

function getNewOutputfile($outdir, $count) {
  $date = date("Y-m-d");
  $outfilename = $outdir . "/ltri-specimen-positions-{$date}_" . sprintf("%02d", $count) . ".csv";
  $outhandle = fopen($outfilename, "w");

  if ($outhandle === FALSE) {
    exit("cannot open output file for writing: {$outfilename}");
  }

  return $outhandle;
}

function convertAndGenerate($csvname, $outdir) {
  global $discardedSpecimens;

  $handle = fopen($csvname, "r");
  if ($handle === FALSE) {
    echo "cannot open CSV file {$csvname}";
    return;
  }

  $header = [
    "inventoryId",
    "currentPalletLabel",
    "palletProductBarcode",
    "rootContainerType",
    "palletLabel",
    "palletPosition",
    "comment"
  ];

  $rowCount = 0;
  $outfilecount = 1;
  $outhandle = getNewOutputfile($outdir, $outfilecount);
  while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
    if ($rowCount === 0) {
      fputcsv($outhandle, $header);
      $rowCount++;
      continue;
    }

    $inventoryId = $data[3];
    if (in_array($inventoryId, $discardedSpecimens)) {
      echo "ignoring discarded specimen {$inventoryId}\n";
      continue;
    }

    fputcsv($outhandle, [ $inventoryId, "", $data[6], "", "", $data[0], "" ], ",", "\"");
    $rowCount++;
    if (($rowCount % 1000) === 0) {
      $outfilecount++;
      $outhandle = getNewOutputfile($outdir, $outfilecount);
      fputcsv($outhandle, $header);
    }
  }
  fclose($handle);
}

convertAndGenerate($csvname, $outdir);
