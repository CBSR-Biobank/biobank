package edu.ualberta.med.biobank.test.action.csvimport.shipment;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.shipment.ShipmentBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.shipment.ShipmentBatchOpInputRow;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.csvimport.AssertCsvImportException;
import edu.ualberta.med.biobank.test.action.csvimport.CsvUtil;
import edu.ualberta.med.biobank.test.action.csvimport.specimen.TestSpecimenCsvImport;

/**
 * 
 * @author loyola
 * 
 */
public class TestShipmentCsvInfo extends TestAction {

    private static Logger log = LoggerFactory
        .getLogger(TestSpecimenCsvImport.class.getName());

    private static final String CSV_NAME = "import_shipments.csv";

    private ShipmentCsvHelper shipmentCsvHelper;

    private Transaction tx;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        shipmentCsvHelper = new ShipmentCsvHelper(factory.getNameGenerator());

        tx = session.beginTransaction();
        factory.createSite();
        factory.createClinic();
        factory.createStudy();
    }

    @Test
    public void noErrors() throws IOException {
        tx.commit();

        Criteria c = session.createCriteria(ShippingMethod.class, "sm");

        @SuppressWarnings("unchecked")
        Set<ShippingMethod> shippingMethods =
            new HashSet<ShippingMethod>(c.list());

        Assert.assertTrue(shippingMethods.size() > 0);

        Set<ShipmentBatchOpInputRow> csvInfos =
            shipmentCsvHelper.createShipments(factory.getDefaultClinic(),
                factory.getDefaultSite(), shippingMethods, 10);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction =
                new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
        } catch (CsvImportException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void badSendingCenter() throws IOException {
        // create a new shipping method
        Set<ShippingMethod> shippingMethods =
            new HashSet<ShippingMethod>();
        shippingMethods.add(factory.createShippingMethod());

        tx.commit();

        // do not persist this clinic to the database
        Clinic badClinic = new Clinic();
        badClinic.setNameShort(factory.getNameGenerator().next(Center.class));

        Set<ShipmentBatchOpInputRow> csvInfos = shipmentCsvHelper.createShipments(
            badClinic, factory.getDefaultSite(), shippingMethods, 1);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction =
                new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
            Assert.fail("errors should have been reported in CVS data");
        } catch (CsvImportException e) {
            new AssertCsvImportException()
                .withMessage(ShipmentBatchOpAction.CSV_SENDING_CENTER_ERROR
                    .format(badClinic.getNameShort()));
        }
    }

    @Test
    public void badReceivingCenter() throws IOException {
        // create a new shipping method
        Set<ShippingMethod> shippingMethods =
            new HashSet<ShippingMethod>();
        shippingMethods.add(factory.createShippingMethod());

        tx.commit();

        // do not persist this clinic to the database
        Site badSite = new Site();
        badSite.setNameShort(factory.getNameGenerator().next(Center.class));

        Set<ShipmentBatchOpInputRow> csvInfos = shipmentCsvHelper.createShipments(
            factory.getDefaultClinic(), badSite, shippingMethods, 1);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction =
                new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
            Assert.fail("errors should have been reported in CVS data");
        } catch (CsvImportException e) {
            new AssertCsvImportException()
                .withMessage(ShipmentBatchOpAction.CSV_RECEIVING_CENTER_ERROR
                    .format(badSite.getNameShort()));
        }
    }

    @Test
    public void badShippingMethods() throws IOException {
        tx.commit();

        // create new shipping methods but do not persist them
        // to the database
        ShippingMethod badShippingMethod =
            shipmentCsvHelper.getNewShippingMethod();
        Set<ShippingMethod> shippingMethods = new HashSet<ShippingMethod>();
        shippingMethods.add(badShippingMethod);

        Set<ShipmentBatchOpInputRow> csvInfos =
            shipmentCsvHelper.createShipments(factory.getDefaultClinic(),
                factory.getDefaultSite(), shippingMethods, 1);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction =
                new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
            Assert.fail("errors should have been reported in CVS data");
        } catch (CsvImportException e) {
            new AssertCsvImportException()
                .withMessage(ShipmentBatchOpAction.CSV_SHIPPING_METHOD_ERROR
                    .format(badShippingMethod.getName())).assertIn(e);
        }
    }

    private void checkCsvInfoAgainstDb(Set<ShipmentBatchOpInputRow> csvInfos) {
        for (ShipmentBatchOpInputRow csvInfo : csvInfos) {
            Criteria c = session.createCriteria(OriginInfo.class, "oi")
                .createAlias("oi.shipmentInfo", "si", Criteria.LEFT_JOIN)
                .add(Restrictions.eq("si.waybill", csvInfo.getWaybill()));

            OriginInfo originInfo = (OriginInfo) c.uniqueResult();

            Assert.assertNotNull(originInfo.getShipmentInfo());
            Assert.assertEquals(0,
                DateCompare.compare(csvInfo.getDateReceived(), originInfo
                    .getShipmentInfo().getReceivedAt()));
            Assert.assertEquals(csvInfo.getSendingCenter(), originInfo
                .getCenter().getNameShort());
            Assert.assertEquals(csvInfo.getReceivingCenter(), originInfo
                .getReceiverSite().getNameShort());
            Assert.assertEquals(csvInfo.getShippingMethod(), originInfo
                .getShipmentInfo().getShippingMethod().getName());
            Assert.assertTrue(originInfo.getComments().size() > 0);
            Assert.assertEquals(csvInfo.getComment(), originInfo
                .getComments().iterator().next().getMessage());

        }
    }
}
