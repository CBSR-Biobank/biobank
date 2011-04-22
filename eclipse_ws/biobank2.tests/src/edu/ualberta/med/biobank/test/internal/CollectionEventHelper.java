package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        OriginInfoWrapper oi, SpecimenWrapper... originSpecimens)
        throws Exception {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        cevent.setPatient(patient);
        cevent.setVisitNumber(visitNumber);
        cevent.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        if ((originSpecimens != null) && (originSpecimens.length != 0)) {
            cevent.addToOriginalSpecimenCollection(Arrays
                .asList(originSpecimens));
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
        CenterWrapper<?> center, String name, Integer visitNumber)
        throws Exception {
        StudyWrapper study = StudyHelper.addStudy(name);
        study.persist();

        PatientWrapper patient = PatientHelper.addPatient(name, study);
        SpecimenWrapper originSpecimen = SpecimenHelper
            .newSpecimen(SpecimenTypeWrapper.getAllSpecimenTypes(appService,
                false).get(0));

        OriginInfoWrapper originInfo = new OriginInfoWrapper(appService);
        originInfo.setCenter(center);
        originInfo.persist();
        return addCollectionEvent(center, patient, visitNumber, originInfo,
            originSpecimen);
    }

    public static List<CollectionEventWrapper> addCollectionEvents(
        CenterWrapper<?> center, StudyWrapper study, String name)
        throws Exception {
        List<CollectionEventWrapper> cevents = new ArrayList<CollectionEventWrapper>();
        SpecimenTypeWrapper spcType = SpecimenTypeWrapper.getAllSpecimenTypes(
            appService, false).get(0);
        int num = r.nextInt(15) + 1;

        for (int i = 0; i < num; i++) {
            PatientWrapper patient = PatientHelper.addPatient(name + "_p" + i,
                study);
            SpecimenWrapper originSpecimen = SpecimenHelper
                .newSpecimen(spcType);

            OriginInfoWrapper originInfo = new OriginInfoWrapper(appService);
            originInfo.setCenter(center);
            originInfo.persist();

            cevents.add(addCollectionEvent(center, patient, 1, originInfo,
                originSpecimen));
        }
        return cevents;
    }

}
