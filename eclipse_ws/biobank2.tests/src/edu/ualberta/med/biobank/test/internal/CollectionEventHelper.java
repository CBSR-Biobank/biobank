package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class CollectionEventHelper extends DbHelper {

    public static CollectionEventWrapper newCollectionEvent(
        CenterWrapper<?> center, PatientWrapper patient, int visitNumber,
        SpecimenWrapper... spcs) throws Exception {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        cevent.setPatient(patient);
        cevent.setVisitNumber(visitNumber);

        if ((spcs != null) && (spcs.length != 0)) {
            cevent.addToSourceSpecimenCollection(Arrays.asList(spcs));
            for (SpecimenWrapper spc : spcs) {
                spc.setCollectionEvent(cevent);
                spc.setCurrentCenter(center);
            }
        }
        return cevent;
    }

    public static CollectionEventWrapper newCollectionEvent(
        CenterWrapper<?> center, PatientWrapper patient, int visitNumber)
        throws Exception {
        return newCollectionEvent(center, patient, visitNumber);
    }

    public static CollectionEventWrapper addCollectionEvent(
        CenterWrapper<?> center, PatientWrapper patient, int visitNumber,
        SpecimenWrapper... svs) throws Exception {
        CollectionEventWrapper ce = newCollectionEvent(center, patient,
            visitNumber, svs);
        ce.persist();
        return ce;
    }

    @Deprecated
    public static CollectionEventWrapper addCollectionEvent(
        CenterWrapper<?> center, SpecimenWrapper... svs) throws Exception {
        return null;
    }

    @Deprecated
    public static CollectionEventWrapper addCollectionEvent(
        CenterWrapper<?> center, String waybill, SpecimenWrapper... svs)
        throws Exception {
        return null;
    }

    public static CollectionEventWrapper addCollectionEventWithRandomPatient(
        CenterWrapper<?> center, String name) throws Exception {
        StudyWrapper study = StudyHelper.addStudy(name);
        study.persist();

        PatientWrapper patient = PatientHelper.addPatient(name, study);
        SpecimenWrapper sv = SpecimenHelper.newSpecimen(SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false).get(0));

        return addCollectionEvent(center, patient, 1, sv);

    }

    @Deprecated
    public static CollectionEventWrapper addCollectionEventNoWaybill(
        CenterWrapper<?> center, SpecimenWrapper... svs) throws Exception {
        return null;
    }
}
