<?php

class Container {
   public static function getPosition($label, $type) {
      if (strlen($label) < 6) {
         throw new Exception("invalid length for label " . $label);
      }

      $top = intval(substr($label, 0, 2));
      $childL1 = substr($label, 2, 2);
      $childL2  = intval(substr($label, 4, 2));

      if (($type == 'Freezer') && ($top == 2) && ($childL2 >= 13)) {
         $label = Frz02::getOldLabel($label);
         $top = intval(substr($label, 0, 2));
         $childL1 = substr($label, 2, 2);
         $childL2  = intval(substr($label, 4, 2));
      }

      return array('top' => $top, 'childL1' => $childL1, 'childL2' => $childL2);
   }

   public static function getCell($row, $col) {
      return sprintf("%s%d", substr(LabelingScheme::ALPHA, $row, 1),
        $col + 1);

   }
}

?>