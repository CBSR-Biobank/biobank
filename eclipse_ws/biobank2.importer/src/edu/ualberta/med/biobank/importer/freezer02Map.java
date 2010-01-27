package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.common.LabelingScheme;

/**
 * Freezer 02 in BBPDB is a 4x12 freezer, but due to timing and space problems
 * the freezer had to hold samples that were labelled for freezer 04 in the
 * bottom 6 pallets of each hotel. As well, only the top 12 pallets in each
 * hotel were originally used. After space became short pallets labelled 02CAXX
 * through 02CHXX were added to the bottom 6 pallets of each hotel.
 * 
 * This file remaps the freezer 04 and pallets labelled 02CAXX through 02CHXX to
 * their corresponding positions in a correctly layed out freezer 02.
 * 
 */
public class freezer02Map {

    public static String getNewLabel(String oldLabel) throws Exception {
        if (oldLabel.length() != 6) {
            throw new Exception("invalid label length: " + oldLabel);
        }

        String freezerLabel = oldLabel.substring(0, 2);
        String hotelLabel = oldLabel.substring(2, 4);
        String palletLabel = oldLabel.substring(4, 6);

        if (freezerLabel.equals("02")) {
            int hotelOffset = 0;
            freezerLabel = "02";
            if (hotelLabel.charAt(0) != 'C') {
                throw new Exception("invalid hotel in label: " + oldLabel);
            }

            int palletNum = Integer.valueOf(palletLabel);
            if ((palletNum < 1) || (palletNum > 12)) {
                throw new Exception("invalid pallet in label: " + oldLabel);
            }

            int newPalletNum = 12 + palletNum;
            if (palletNum > 6) {
                newPalletNum -= 6;
                hotelOffset = 1;
            }

            if ((hotelLabel.charAt(1) >= 'A') && (hotelLabel.charAt(1) <= 'E')) {
                hotelLabel = "A"
                    + LabelingScheme.CBSR_LABELLING_PATTERN.charAt(2
                        * LabelingScheme.CBSR_LABELLING_PATTERN
                            .indexOf(hotelLabel.charAt(1)) + hotelOffset);
                palletLabel = String.format("%02d", newPalletNum);

            } else if ((hotelLabel.charAt(1) >= 'F')
                && (hotelLabel.charAt(1) <= 'J')) {
                hotelLabel = "B"
                    + LabelingScheme.CBSR_LABELLING_PATTERN
                        .charAt(LabelingScheme.CBSR_LABELLING_PATTERN
                            .indexOf('S')
                            + 2
                            * (LabelingScheme.CBSR_LABELLING_PATTERN
                                .indexOf(hotelLabel.charAt(1)) - LabelingScheme.CBSR_LABELLING_PATTERN
                                .indexOf('F')) + hotelOffset);
                palletLabel = String.format("%02d", newPalletNum);

            } else {
                throw new Exception("invalid hotel in label: " + oldLabel);
            }
        } else if (freezerLabel.equals("04")) {

        } else {
            throw new Exception("invalid freezer number in label: " + oldLabel);
        }
        return freezerLabel + hotelLabel + palletLabel;
    }

    public static void main(String[] args) throws Exception {
        for (int hotel = 0; hotel < 9; hotel++) {
            for (int pallet = 0; pallet < 12; pallet++) {
                String oldLabel = String.format("02C%c%02d",
                    LabelingScheme.CBSR_LABELLING_PATTERN.charAt(hotel),
                    pallet + 1);
                System.out.println(oldLabel + " " + getNewLabel(oldLabel));
            }
        }

    }
}
