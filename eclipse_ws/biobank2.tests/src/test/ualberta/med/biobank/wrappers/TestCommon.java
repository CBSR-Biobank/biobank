package test.ualberta.med.biobank.wrappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;
import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;

public class TestCommon {

    private static List<String> usedBarcodes;

    private static List<String> inventoryIds;

    private static List<Date> usedDates;

    public static List<String> usedWaybills;

    public static ContainerTypeWrapper addSampleTypes(ContainerTypeWrapper ct,
        List<SampleTypeWrapper> sampleTypes) throws Exception {
        Assert.assertTrue("not enough sample types for test", (sampleTypes
            .size() > 10));
        ct.addSampleTypes(sampleTypes);
        ct.persist();
        ct.reload();
        return ct;
    }

    public static List<SampleTypeWrapper> getRandomSampleTypeList(Random r,
        List<SampleTypeWrapper> list) {
        List<SampleTypeWrapper> result = new ArrayList<SampleTypeWrapper>();
        for (SampleTypeWrapper st : list) {
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
}
