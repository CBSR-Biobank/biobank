package edu.ualberta.med.biobank.test;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestHibernateLocking extends TestDatabase {

    @Test
    public void testHibernateLocking() {
        try {
            SpecimenWrapper newSpec = new SpecimenWrapper(appService);
            newSpec.setInventoryId("aaaa");
            newSpec.setCreatedAt(new Date());
            newSpec.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));

            StudyWrapper study = StudyHelper.addStudy("teststudy");
            SiteWrapper site = SiteHelper.addSite("testsite");
            PatientWrapper patient = PatientHelper.addPatient("testp", study);
            OriginInfoWrapper oi = new OriginInfoWrapper(appService);
            oi.setCenter(site);
            oi.persist();
            SpecimenTypeWrapper st = SpecimenTypeHelper
                .addSpecimenType("testst");
            st.persist();
            newSpec.setSpecimenType(st);
            newSpec.setOriginInfo(oi);

            CollectionEventHelper.addCollectionEvent(site, patient, 1, oi,
                newSpec);

            // TEST BEGINS
            SpecimenWrapper ob1 = SpecimenWrapper.getSpecimen(appService,
                "aaaa", null);
            SpecimenWrapper ob2 = SpecimenWrapper.getSpecimen(appService,
                "aaaa", null);

            DispatchSpecimenWrapper ds1 = new DispatchSpecimenWrapper(
                appService);
            ds1.setSpecimen(newSpec);

            DispatchSpecimenWrapper ds2 = new DispatchSpecimenWrapper(
                appService);
            ds2.setSpecimen(newSpec);

            ob1.addToDispatchSpecimenCollection(Arrays.asList(ds1));
            ob2.addToDispatchSpecimenCollection(Arrays.asList(ds2));
            ob1.persist();
            System.out.println("test 1 saved successfully");
            Thread.sleep(1000);
            ob2.persist();
            System.out.println("test 2 saved successfully");

        } catch (Exception e) {
            e.printStackTrace();
            SpecimenWrapper ob;
            try {
                ob = SpecimenWrapper.getSpecimen(appService, "aaaa", null);
                System.out.println(ob.getCurrentCenter());
            } catch (BiobankCheckException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ApplicationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (Exception ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }

    }
}
