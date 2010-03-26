<?php

error_reporting(E_ALL);
ini_set("display_errors", 1);

function __autoload($className) {
   $projdir = dirname(__FILE__);

   if (file_exists("{$projdir}/{$className}.class.php")) {
      require "{$projdir}/{$className}.class.php";
   } else {
      throw new Exception("Class \"{$projdir}/{$className}\" could not be autoloaded");
   }
}

function canClassBeAutloaded($className) {
   try {
      class_exists($className);
      return true;
   }
   catch (Exception $e) {
      return false;
   }
}

?>