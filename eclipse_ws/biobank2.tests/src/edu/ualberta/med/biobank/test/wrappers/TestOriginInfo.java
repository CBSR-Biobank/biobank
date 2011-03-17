package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestOriginInfo extends TestDatabase {

    @Test
    public void testGetSetShippingMethod() throws Exception {
        String name = "testGetSetShippingMethod" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ShippingMethodWrapper company = ShippingMethodHelper
            .addShippingMethod(name);

        StudyWrapper study = StudyHelper.addStudy(name);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        SpecimenWrapper spc = SpecimenHelper.newSpecimen(name);

        // CollectionEventWrapper cevent = CollectionEventHelper
        // .addCollectionEvent(site, patient, 1, spc);

        // cevent.persist();
        // cevent.reload();

        // Assert.assertEquals(company, cevent.getShippingMethod());
        Assert.fail("test needs implementation");
    }

}
