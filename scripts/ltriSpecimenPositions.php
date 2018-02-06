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
      fputcsv($outhandle, [ $data[3], "", $data[6], "", "", $data[0], "" ]);
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
