package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.test.Utils;

@SuppressWarnings("unused")
@Deprecated
public class TestCommon {

    private static List<String> usedBarcodes;

    private static List<String> inventoryIds;

    private static List<Date> usedDates;

    public static List<String> usedWaybills;

    public static List<String> usedWorksheets;

    public static ContainerTypeWrapper addSampleTypes(ContainerTypeWrapper ct,
        List<SpecimenTypeWrapper> sampleTypes) throws Exception {
        Assert.assertTrue("not enough sample types for test",
            (sampleTypes.size() > 10));
        ct.addToSpecimenTypeCollection(sampleTypes);
        ct.persist();
        ct.reload();
        return ct;
    }

    public static List<SpecimenTypeWrapper> getRandomSampleTypeList(Random r,
        List<SpecimenTypeWrapper> list) {
        List<SpecimenTypeWrapper> result = new ArrayList<SpecimenTypeWrapper>();
        for (SpecimenTypeWrapper st : list) {
            if (r.nextBoolean()) {
                result.add(st);
            }
        }
        return result;
    }

    public static String getNewBarcode(Random r) {
        if (usedBarcodes == null) {
            usedBarcodes = new ArrayList<String>();
        }

        String newBarcode;
        do {
            newBarcode = Utils.getRandomString(10, 12);
        } while (usedBarcodes.contains(newBarcode));
        usedBarcodes.add(newBarcode);
        return newBarcode;
    }

    public static String getNewInventoryId(Random r) {
        if (inventoryIds == null) {
            inventoryIds = new ArrayList<String>();
        }
        String id;
        do {
            id = Utils.getRandomString(10, 20);
        } while (inventoryIds.contains(id));
        inventoryIds.add(id);
        return id;
    }

    public static Date getUniqueDate(Random r) {
        if (usedDates == null) {
            usedDates = new ArrayList<Date>();
        }
        Date id;
        do {
            id = Utils.getRandomDate();
        } while (usedDates.contains(id));
        usedDates.add(id);
        return id;
    }

    public static String getNewWaybill(Random r) {
        if (usedWaybills == null) {
            usedWaybills = new ArrayList<String>();
        }

        String waybill;
        do {
            waybill = Utils.getRandomString(10);
        } while (usedWaybills.contains(waybill));
        usedWaybills.add(waybill);
        return waybill;
    }

    public static String getUniqueWorksheet(Random r) {
        if (usedWorksheets == null) {
            usedWorksheets = new ArrayList<String>();
        }

        String worksheet;
        do {
            worksheet = Utils.getRandomString(20);
        } while (usedWorksheets.contains(worksheet));
        usedWorksheets.add(worksheet);
        return worksheet;
    }
}
