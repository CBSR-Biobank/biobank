<?php

$usage = <<<USAGE
USAGE: {$argv[0]} CSV_FILE_NAME

Updates the volume on specimen inventory IDs provided in a CSV file.

Options:

  -H, --dbhost  The DNS name for host running the MySQL server.
  -d, --dbname  The MySQL database name.
  -u, --dbuser  The user name for the MySQL server.
USAGE;

$charset = 'utf8mb4';

function getPassword($prompt, $stars = false) {
  fwrite(STDOUT, $prompt);

  // Get current style
  $oldStyle = shell_exec('stty -g');

  if ($stars === false) {
    shell_exec('stty -echo');
    $password = rtrim(fgets(STDIN), "\n");
  } else {
    shell_exec('stty -icanon -echo min 1 time 0');

    $password = '';
    while (true) {
      $char = fgetc(STDIN);

      if ($char === "\n") {
        break;
      } else if (ord($char) === 127) {
        if (strlen($password) > 0) {
          fwrite(STDOUT, "\x08 \x08");
          $password = substr($password, 0, -1);
        }
      } else {
        fwrite(STDOUT, "*");
        $password .= $char;
      }
    }
  }

  // Reset old style
  shell_exec('stty ' . $oldStyle);

  // Return the password
  return $password;
}

function getSpecimenInfoFromCsv($csvname) {
  $handle = fopen($csvname, "r");
  if ($handle === FALSE) {
    echo "cannot open CSV file {$csvname}";
    return;
  }

  $specimenInfo = [];

  $row = 0;
  while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
    $row++;

    if ($row === 1) {
      continue;
    }

    $inventoryId = $data[0];
    $volume = $data[2];

    $specimenInfo[] = [ $inventoryId, $volume ];

  }

  fclose($handle);
  return $specimenInfo;
}

function updateQuantity($pdo, $inventoryId, $volume) {
  $update = "UPDATE specimen SET quantity=:volume WHERE inventory_id=:inventoryId";
  $stmt = $pdo->prepare($update)->execute([ 'inventoryId' => $inventoryId, 'volume' => $volume ]);
}

function getOptions($argv, $defaultDbHost, $defaultDbName) {
  $options = getopt("H:d:u:", [ "dbhost:", "dbname:", "dbuser:" ], $optind);
  $pos_args = array_slice($argv, $optind);

  $opts['dbhost'] = $options['dbhost'] ?? $options['H'] ?? $defaultDbHost;
  $opts['dbname'] = $options['dbname'] ?? $options['d'] ?? $defaultDbName;
  $opts['dbuser'] = $options['dbuser'] ?? $options['u'] ?? "";

  return [ $opts, $pos_args ];
}

[ $options, $args] = getOptions($argv, 'localhost', 'ltri_biobank');

if (count($args) != 1) {
  exit("missing CSV file name argument\n");
}

$dbHost  = $options['dbhost'];
$dbName  = $options['dbname'];
$dbUser  = $options['dbuser'];
$csvname = $args[0];

if ($dbUser == "") {
  exit("database user name is required\n");
}

$dbPassword = getPassword("DB Password: ", true);
echo "\n";

$dsn = "mysql:host=$dbHost;dbname=$dbName;charset=$charset";
$opt = [
  PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
  PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
  PDO::ATTR_EMULATE_PREPARES   => false
];

$pdo = new PDO($dsn, $dbUser, $dbPassword, $opt);

$specimenData = getSpecimenInfoFromCsv($csvname);
foreach ($specimenData as $specimenInfo) {
  [ $inventoryId, $volume ] = $specimenInfo;
  updateQuantity($pdo, $inventoryId, $volume);
}
echo "done!\n";
