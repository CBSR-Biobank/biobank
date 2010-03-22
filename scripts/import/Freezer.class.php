<?php

class Freezer {
   public static function getPosition($label) {
      if (strlen($label) < 6) {
         throw new Exception("invalid length for label " . $label);
      }
      $frz = intval(substr($label, 0, 2));
      $hotel = substr($label, 2, 2);
      $pallet  = intval(substr($label, 4, 2));

      if (($frz == 2) && ($pallet >= 13)) {
         $label = Frz02::getOldLabel($label);
         $frz = intval(substr($label, 0, 2));
         $hotel = substr($label, 2, 2);
         $pallet  = intval(substr($label, 4, 2));
      }
       
      return array('frz' => $frz, 'hotel' => $hotel, 'pallet' => $pallet);
   }
   
   public static function getCell($row, $col) {      
      return sprintf("%s%d", substr(LabelingScheme::ALPHA, $row, 1),  
        $col + 1);
      
   }
}

?>