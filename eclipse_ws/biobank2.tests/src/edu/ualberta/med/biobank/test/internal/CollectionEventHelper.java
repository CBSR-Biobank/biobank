package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class CollectionEventHelper extends DbHelper {

    public static CollectionEventWrapper newCollectionEvent(
        CenterWrapper<?> center, PatientWrapper patient, int visitNumber,
        OriginInfoWrapper oi, SpecimenWrapper... originSpecimens) throws Exception {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        cevent.setPatient(patient);
        cevent.setVisitNumber(visitNumber);
        cevent.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        if ((originSpecimens != null) && (originSpecimens.length != 0)) {
            cevent.addToOriginalSpecimenCollection(Arrays.asList(originSpecimens));
            for (SpecimenWrapper spc : originSpecimens) {
                spc.setOriginInfo(oi);
                spc.setCollectionEvent(cevent);
                spc.setOriginalCollectionEvent(cevent);
                spc.setCurrentCenter(center);
            }
        }
        return cevent;
    }

    public static CollectionEventWrapper addCollectionEvent(
        CenterWrapper<?> center, PatientWrapper patient, int visitNumber,
        OriginInfoWrapper oi, SpecimenWrapper... originSpecimens)
        throws Exception {
        CollectionEventWrapper ce = newCollectionEvent(center, patient,
            visitNumber, oi, originSpecimens);
        ce.persist();
        return ce;
    }

    public static CollectionEventWrapper addCollectionEventWithRandomPatient(
        CenterWrapper<?> center, String name) throws Exception {
        StudyWrapper study = StudyHelper.addStudy(name);
        study.persist();

        PatientWrapper patient = PatientHelper.addPatient(name, study);
        SpecimenWrapper originSpecimen = SpecimenHelper.newSpecimen(SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false).get(0));

        OriginInfoWrapper originInfo = new OriginInfoWrapper(appService);
        originInfo.setCenter(center);
        originInfo.persist();
        return addCollectionEvent(center, patient, 1, originInfo, originSpecimen);
    }

}
