package edu.ualberta.med.biobank.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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
public class Freezer02Importer extends FreezerImporter {

    public Freezer02Importer(WritableApplicationService appService,
        Connection con, Configuration configuration, final SiteWrapper site,
        ContainerWrapper container, int bbpdbFreezerNum) throws Exception {
        super(appService, con, configuration, site, container, bbpdbFreezerNum,
            DEFAULT_QUERY);
    }

    @Override
    protected void doImport() throws Exception {
        super.doImport();

        ResultSet rs;
        PreparedStatement ps;
        String hotelLabel;
        String dateProcessedStr;
        String palletPos;
        String sampleTypeNameShort;
        String inventoryId;
        BlowfishCipher cipher = new BlowfishCipher();
        String studyNameShort;
        int palletNr;
        String patientNr;
        String linkDateStr;
        double quantity;
        String oldLabel;
        String newLabel;
        int newPalletNr;
        ContainerWrapper newHotel;

        for (int h = LabelingScheme.CBSR_LABELLING_PATTERN.indexOf('A'); h <= LabelingScheme.CBSR_LABELLING_PATTERN
            .indexOf('J'); h++) {
            hotelLabel = "C" + LabelingScheme.CBSR_LABELLING_PATTERN.charAt(h);

            if (!configuration.importFreezerHotel(hotelLabel)) {
                logger.info("not configured for importing hotel " + hotelLabel);
                continue;
            }

            ps = con.prepareStatement(DEFAULT_QUERY);
            ps.setInt(1, bbpdbFreezerNum);
            ps.setString(2, hotelLabel);

            rs = ps.executeQuery();
            if (rs == null) {
                throw new Exception("Database query returned null");
            }

            while (rs.next()) {
                studyNameShort = rs.getString(3);
                patientNr = cipher.decode(rs.getBytes(17));
                dateProcessedStr = rs.getString(1);
                palletNr = rs.getInt(7);
                palletPos = rs.getString(15);
                inventoryId = rs.getString(11);
                sampleTypeNameShort = rs.getString(4);
                linkDateStr = rs.getString(12);
                quantity = rs.getDouble(16);

                oldLabel = String.format("02%s%02d", hotelLabel, palletNr);
                newLabel = getNewLabel(oldLabel);

                newHotel = freezer.getChildByLabel(newLabel.substring(0, 4));

                if (newHotel == null) {
                    logger.error("hotel not configured: "
                        + newLabel.substring(0, 4));
                    return;
                }

                newPalletNr = Integer.valueOf(newLabel.substring(newLabel
                    .length() - 2));

                importSample(studyNameShort, patientNr, dateProcessedStr,
                    newHotel, newPalletNr, palletPos, inventoryId,
                    sampleTypeNameShort, linkDateStr, quantity);
            }
        }

        for (int h = LabelingScheme.CBSR_LABELLING_PATTERN.indexOf('A'); h <= LabelingScheme.CBSR_LABELLING_PATTERN
            .indexOf('T'); h++) {
            hotelLabel = "A" + LabelingScheme.CBSR_LABELLING_PATTERN.charAt(h);

            if (!configuration.importFreezerHotel(hotelLabel)) {
                logger.info("not configured for importing hotel " + hotelLabel);
                continue;
            }

            ps = con.prepareStatement(DEFAULT_QUERY);
            ps.setInt(1, 4);
            ps.setString(2, hotelLabel);

            rs = ps.executeQuery();
            if (rs == null) {
                throw new Exception("Database query returned null");
            }

            while (rs.next()) {
                studyNameShort = rs.getString(3);
                patientNr = cipher.decode(rs.getBytes(17));
                dateProcessedStr = rs.getString(1);
                palletNr = rs.getInt(7);
                palletPos = rs.getString(15);
                inventoryId = rs.getString(11);
                sampleTypeNameShort = rs.getString(4);
                linkDateStr = rs.getString(12);
                quantity = rs.getDouble(16);

                oldLabel = String.format("04%s%02d", hotelLabel, palletNr);
                newLabel = getNewLabel(oldLabel);

                newHotel = freezer.getChildByLabel(newLabel.substring(0, 4));

                if (newHotel == null) {
                    logger.error("hotel not configured: "
                        + newLabel.substring(0, 4));
                    return;
                }

                newPalletNr = Integer.valueOf(newLabel.substring(newLabel
                    .length() - 2));

                importSample(studyNameShort, patientNr, dateProcessedStr,
                    newHotel, newPalletNr, palletPos, inventoryId,
                    sampleTypeNameShort, linkDateStr, quantity);
            }
        }

    }

    public static String getNewLabel(String oldLabel) throws Exception {
        if (oldLabel.length() != 6) {
            throw new Exception("invalid label length: " + oldLabel);
        }

        String freezerLabel = oldLabel.substring(0, 2);

        if (freezerLabel.equals("02")) {
            return getNewLabelFromFreezer02(oldLabel);
        } else if (freezerLabel.equals("04")) {
            return getNewLabelFromFreezer04(oldLabel);
        }
        throw new Exception("invalid freezer number in label: " + oldLabel);
    }

    private static String getNewLabelFromFreezer02(String oldLabel)
        throws Exception {
        String freezerLabel = "02";
        String hotelLabel = oldLabel.substring(2, 4);
        String palletLabel = oldLabel.substring(4, 6);
        int hotelOffset = 0;

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
                    * LabelingScheme.CBSR_LABELLING_PATTERN.indexOf(hotelLabel
                        .charAt(1)) + hotelOffset);

        } else if ((hotelLabel.charAt(1) >= 'F')
            && (hotelLabel.charAt(1) <= 'J')) {
            hotelLabel = "B"
                + LabelingScheme.CBSR_LABELLING_PATTERN
                    .charAt(LabelingScheme.CBSR_LABELLING_PATTERN.indexOf('S')
                        + 2
                        * (LabelingScheme.CBSR_LABELLING_PATTERN
                            .indexOf(hotelLabel.charAt(1)) - LabelingScheme.CBSR_LABELLING_PATTERN
                            .indexOf('F')) + hotelOffset);

        } else {
            throw new Exception("invalid hotel in label: " + oldLabel);
        }

        if ((newPalletNum < 13) || (newPalletNum > 18)) {
            throw new Exception("invalid new pallet number");
        }

        palletLabel = String.format("%02d", newPalletNum);
        return freezerLabel + hotelLabel + palletLabel;
    }

    private static String getNewLabelFromFreezer04(String oldLabel)
        throws Exception {
        String freezerLabel = "02";
        String hotelLabel = oldLabel.substring(2, 4);
        String palletLabel = oldLabel.substring(4, 6);
        int palletNum = Integer.valueOf(palletLabel);
        int hotelOffset = 0;
        int newPalletNum;

        if (hotelLabel.charAt(0) != 'A') {
            throw new Exception("invalid hotel in label: " + oldLabel);
        }

        if ((palletNum < 1) || (palletNum > 10)) {
            throw new Exception("invalid pallet in label: " + oldLabel);
        }

        // there are 6 groups of 3
        int hotelIndex = LabelingScheme.CBSR_LABELLING_PATTERN
            .indexOf(hotelLabel.charAt(1));

        if (hotelIndex > 18) {
            throw new Exception("invalid hotel in label: " + oldLabel);
        }

        int group = hotelIndex / 3;
        int withinGroup = hotelIndex % 3;

        switch (withinGroup) {
        case 0:
            newPalletNum = 12 + palletNum;
            if (palletNum > 6) {
                newPalletNum -= 6;
                hotelOffset = 1;
            }
            break;
        case 1:
            hotelOffset = 1;
            if (palletNum < 3) {
                newPalletNum = 16 + palletNum;
            } else if ((palletNum >= 3) && (palletNum <= 8)) {
                newPalletNum = 10 + palletNum;
                hotelOffset += 1;
            } else {
                newPalletNum = 4 + palletNum;
                hotelOffset += 2;
            }
            break;
        case 2:
        default:
            hotelOffset = 3;
            newPalletNum = 14 + palletNum;
            if (palletNum > 4) {
                newPalletNum -= 6;
                hotelOffset += 1;
            }
            break;
        }

        hotelIndex = LabelingScheme.CBSR_LABELLING_PATTERN.indexOf('L') + 5
            * group + hotelOffset;

        if (hotelIndex < 24) {
            hotelLabel = "A"
                + LabelingScheme.CBSR_LABELLING_PATTERN.charAt(hotelIndex);
        } else {
            hotelLabel = "B"
                + LabelingScheme.CBSR_LABELLING_PATTERN.charAt(hotelIndex - 24);
        }

        if ((newPalletNum < 13) || (newPalletNum > 18)) {
            throw new Exception("invalid new pallet number");
        }

        palletLabel = String.format("%02d", newPalletNum);
        return freezerLabel + hotelLabel + palletLabel;
    }
}
