<?php

error_reporting(E_ALL);
ini_set("display_errors", 1);

function __autoload($className) {
    if (file_exists($className . '.class.php')) require $className . '.class.php';
    else throw new Exception('Class "' . $className . '" could not be autoloaded');
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