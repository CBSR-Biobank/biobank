package edu.ualberta.med.biobank.test.wrappers;

import java.util.Calendar;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestSourceVessel extends TestDatabase {

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        PatientWrapper p1 = PatientHelper.newPatient(name);
        SourceVesselTypeWrapper svType = SourceVesselTypeHelper
            .addSourceVesselType(name);

        Calendar calendar = Calendar.getInstance();
        SourceVesselWrapper sv1 = SourceVesselHelper.newSourceVessel(p1,
            svType, calendar.getTime(), 0.1);

        calendar.add(Calendar.DATE, -1); // yesterday
        SourceVesselWrapper sv2 = SourceVesselHelper.newSourceVessel(p1,
            svType, calendar.getTime(), 0.1);

        // FIXME
        // Assert.assertTrue(sv2.compareTo(sv1) < 0);
        // Assert.assertTrue(sv1.compareTo(sv2) > 0);

        calendar.add(Calendar.DATE, 1); // back to today
        SourceVesselWrapper sv3 = SourceVesselHelper.newSourceVessel(p1,
            svType, calendar.getTime(), 0.1);

        // FIXME
        // Assert.assertTrue(sv3.compareTo(sv1) == 0);
        // Assert.assertTrue(sv3.compareTo(sv2) > 0);
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);

        PatientWrapper p1 = PatientHelper.addPatient(name, study);
        SourceVesselWrapper sv = SourceVesselHelper.newSourceVessel(p1,
            Utils.getRandomDate(), 0.1);
        // FIXME
        // CollectionEventWrapper cevent = CollectionEventHelper
        // .addCollectionEvent(site,
        // ShippingMethodWrapper.getShippingMethods(appService).get(0), sv);
        // sv.setCollectionEvent(cevent);
        // sv.persist();
        //
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, p1, Utils.getRandomDate(),
        // Utils.getRandomDate());
        //
        // pevent.addToSourceVesselCollection(Arrays.asList(sv));
        // sv.setProcessingEvent(pevent);
        // sv.persist();
        // sv.reload();
        //
        // try {
        // sv.delete();
        // Assert
        // .fail("should not be allowed to delete a source vessel assocaited with a processing vent");
        // } catch (BiobankCheckException bce) {
        // Assert.assertTrue(true);
        // }
    }
}
