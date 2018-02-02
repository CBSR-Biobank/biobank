package edu.ualberta.med.biobank.test.action.batchoperation.shipment;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;
import edu.ualberta.med.biobank.test.action.batchoperation.specimen.TestSpecimenBatchOp;

/**
 *
 * @author Nelson Loyola
 *
 */
public class TestShipmentCsvInfo extends TestAction {

    private static Logger log = LoggerFactory.getLogger(TestSpecimenBatchOp.class.getName());

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

        Set<ShippingMethod> shippingMethods = getShippingMethodsFromDb(3);
        Set<ShipmentBatchOpInputRow> csvInfos =
            shipmentCsvHelper.createShipments(factory.getDefaultClinic(),
                                              factory.getDefaultSite(),
                                              shippingMethods,
                                              5);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction = new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void noComments() throws IOException {
        tx.commit();

        Set<ShippingMethod> shippingMethods = getShippingMethodsFromDb(3);
        Set<ShipmentBatchOpInputRow> csvInfos =
            shipmentCsvHelper.createShipments(factory.getDefaultClinic(),
                                              factory.getDefaultSite(), shippingMethods, 10, false);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction = new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void badSendingCenter() throws IOException {
        // create a new shipping method
        Set<ShippingMethod> shippingMethods = new HashSet<ShippingMethod>();
        shippingMethods.add(factory.createShippingMethod());

        tx.commit();

        // do not persist this clinic to the database
        Clinic badClinic = new Clinic();
        badClinic.setNameShort(factory.getNameGenerator().next(Center.class));

        Set<ShipmentBatchOpInputRow> csvInfos =
            shipmentCsvHelper.createShipments(badClinic,
                                              factory.getDefaultSite(),
                                              shippingMethods,
                                              1);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction = new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
            Assert.fail("errors should have been reported in CVS data");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(ShipmentBatchOpAction.CSV_SENDING_CENTER_ERROR
                    .format(badClinic.getNameShort()));
        }
    }

    @Test
    public void badReceivingCenter() throws IOException {
        // create a new shipping method
        Set<ShippingMethod> shippingMethods = new HashSet<ShippingMethod>();
        shippingMethods.add(factory.createShippingMethod());

        tx.commit();

        // do not persist this clinic to the database
        Site badSite = new Site();
        badSite.setNameShort(factory.getNameGenerator().next(Center.class));

        Set<ShipmentBatchOpInputRow> csvInfos =
            shipmentCsvHelper.createShipments(factory.getDefaultClinic(),
                                              badSite,
                                              shippingMethods,
                                              1);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction = new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
            Assert.fail("errors should have been reported in CVS data");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(ShipmentBatchOpAction.CSV_RECEIVING_CENTER_ERROR
                    .format(badSite.getNameShort()));
        }
    }

    @Test
    public void badShippingMethods() throws IOException {
        tx.commit();

        // create new shipping methods but do not persist them
        // to the database
        ShippingMethod badShippingMethod = shipmentCsvHelper.getNewShippingMethod();
        Set<ShippingMethod> shippingMethods = new HashSet<ShippingMethod>();
        shippingMethods.add(badShippingMethod);

        Set<ShipmentBatchOpInputRow> csvInfos =
            shipmentCsvHelper.createShipments(factory.getDefaultClinic(),
                                              factory.getDefaultSite(),
                                              shippingMethods,
                                              1);
        ShipmentCsvWriter.write(CSV_NAME, csvInfos);

        try {
            ShipmentBatchOpAction importAction = new ShipmentBatchOpAction(CSV_NAME);
            exec(importAction);
            Assert.fail("errors should have been reported in CVS data");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(ShipmentBatchOpAction.CSV_SHIPPING_METHOD_ERROR
                    .format(badShippingMethod.getName())).assertIn(e);
        }
    }

    private Set<ShippingMethod> getShippingMethodsFromDb(int max) {
        @SuppressWarnings("unchecked")
        List<ShippingMethod> allShippingMethods =
            session.createCriteria(ShippingMethod.class, "sm").list();
        assertTrue(allShippingMethods.size() > max);

        return new HashSet<ShippingMethod>(allShippingMethods.subList(0, max - 1));
    }

    private void checkCsvInfoAgainstDb(Set<ShipmentBatchOpInputRow> csvInfos) {
        for (ShipmentBatchOpInputRow csvInfo : csvInfos) {
            Criteria c = session.createCriteria(OriginInfo.class, "oi")
                .createAlias("oi.shipmentInfo", "si", Criteria.LEFT_JOIN)
                .add(Restrictions.eq("si.waybill", csvInfo.getWaybill()));

            OriginInfo originInfo = (OriginInfo) c.uniqueResult();

            assertNotNull(originInfo.getShipmentInfo());
            assertEquals(0, DateCompare.compareWithoutSeconds(csvInfo.getDateReceived(),
                                                              originInfo.getShipmentInfo()
                                                                  .getReceivedAt()));
            assertEquals(csvInfo.getSendingCenter(),
                         originInfo.getCenter().getNameShort());
            assertEquals(csvInfo.getReceivingCenter(),
                         originInfo.getReceiverCenter().getNameShort());
            assertEquals(csvInfo.getShippingMethod(),
                         originInfo.getShipmentInfo().getShippingMethod().getName());
            if (!originInfo.getComments().isEmpty()) {
                assertEquals(csvInfo.getComment(),
                             originInfo.getComments().iterator().next().getMessage());
            }

        }
    }
}
