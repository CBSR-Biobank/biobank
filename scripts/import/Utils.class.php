<?php

class Utils {

   private static $old_study_name = array("BBPSP" => "BBP");

   private static $old_sample_type_name = array(
      "DNA (White blood cells)" => "DNA (WBC)",
      "RNAlater Biopsies" => "RNA Later"
      );


   public static function getOldStudyName($name) {
      if (!empty(self::$old_study_name[$name])) {
         return self::$old_study_name[$name];
      }
      return $name;
   }

   public static function getOldSampleTypeName($name) {
      if (isset(self::$old_sample_type_name[$name])) {
         return self::$old_sample_type_name[$name];
      }
      return $name;
   }

   public static function getBbpdbPatientNrs() {
      $con = mysqli_connect("localhost", "dummy", "ozzy498", "bbpdb");
      if (mysqli_connect_errno()) {
         die(mysqli_connect_error());
      }

      $patientNrs = array();
      $result = $con->query("select patient_nr, dec_chr_nr from patient");
      while ($row = $result->fetch_object()) {
         $patientNrs[$row->dec_chr_nr] = $row->patient_nr;
      }
      return $patientNrs;
   }

   public static function getBbpdbVisitNrs() {
      $con = mysqli_connect("localhost", "dummy", "ozzy498", "bbpdb");
      if (mysqli_connect_errno()) {
         die(mysqli_connect_error());
      }

      $visitNrs = array();
      $result = $con->query("select patient_nr, date_taken, visit_nr from patient_visit");
      while ($row = $result->fetch_object()) {
         if (empty($visitNrs[$row->patient_nr])) {
            $visitNrs[$row->patient_nr] = array();
         }
         $date_taken = date('d-M-y', strtotime($row->date_taken));
         $visitNrs[$row->patient_nr][$date_taken] = $row->visit_nr;
      }
      return $visitNrs;
   }
  }

?>