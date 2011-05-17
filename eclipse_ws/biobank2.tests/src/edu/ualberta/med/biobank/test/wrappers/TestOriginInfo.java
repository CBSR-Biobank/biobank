package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentInfoHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestOriginInfo extends TestDatabase {

    @Test
    public void testGetSetShippingMethod() throws Exception {
        String name = "testGetSetShippingMethod" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic1" + name);
        StudyWrapper study = StudyHelper.addStudy(name);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        SpecimenWrapper spc = SpecimenHelper.newSpecimen(name);

        ShippingMethodWrapper method = ShippingMethodHelper
            .addShippingMethod(name);

        CollectionEventHelper.addCollectionEvent(clinic, patient, 1, spc);
        ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.addShipment(clinic,
            method, Utils.getRandomString(20), Utils.getRandomDate(), spc);

        Assert.assertEquals(method, shipInfo.getShippingMethod());
    }

}
