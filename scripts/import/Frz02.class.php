<?php

class Frz02 {

   public static function getOldLabel($label) {
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
            $hotel2 = strpos(LabelingScheme::ALPHA, $hotel2);
            $hotel = 'C' . substr(LabelingScheme::ALPHA, $hotel2 / 2, 1);
            $pallet -= 12;
            if ($hotel2 % 2 > 0) {
               $pallet += 6;
            }
         } else {
            $frz = 4;
            $frz4 = self::getFrz4Label($hotel, $pallet);
            $pallet = $frz4['pallet'];
            $hotel = 'A' . substr(LabelingScheme::ALPHA, $frz4['hotel'], 1);
         }
      } if (substr($hotel, 0, 1) == 'B') {
         if (($hotel2 >= 'S') && ($hotel2 <= 'Z')) {
            $hotel2 = strpos(LabelingScheme::ALPHA, $hotel2) - strpos(LabelingScheme::ALPHA, 'S');
            $hotel = 'C' . substr(LabelingScheme::ALPHA, strpos(LabelingScheme::ALPHA, 'F') + $hotel2 / 2, 1);
            $pallet -= 12;
            if ($hotel2 % 2 > 0) {
               $pallet += 6;
            }
         } else {
            $frz = 4;
            $frz4 = self::getFrz4Label($hotel, $pallet);
            $pallet = $frz4['pallet'];
            $hotel = 'A' . substr(LabelingScheme::ALPHA, $frz4['hotel'], 1);
         }
      }
      return sprintf('%02d%s%02d', $frz, $hotel, $pallet);
   }

   private static function getFrz4Label($hotel, $pallet) {
      $hotel2 = strpos(LabelingScheme::ALPHA, substr($hotel, 0, 1))
      * strlen(LabelingScheme::ALPHA)
      + strpos(LabelingScheme::ALPHA, substr($hotel, 1))
      - strpos(LabelingScheme::ALPHA, 'L');
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
}

?>
