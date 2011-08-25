package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class StudyHelper extends DbHelper {

    public static List<StudyWrapper> createdStudies = new ArrayList<StudyWrapper>();

    public static StudyWrapper newStudy(String name) throws Exception {
        StudyWrapper study = new StudyWrapper(appService);
        study.setName(name);
        study.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, ActivityStatusWrapper.ACTIVE_STATUS_STRING));
        if (name != null) {
            if (name.length() < 50) {
                study.setNameShort(name);
            } else {
                study.setNameShort(name.substring(name.length() - 49));
            }
        }
        return study;
    }

    public static StudyWrapper addStudy(String name, boolean addToCreatedList)
        throws Exception {
        StudyWrapper study = newStudy(name);
        study.persist();
        if (addToCreatedList) {
            createdStudies.add(study);
        }
        return study;
    }

    public static StudyWrapper addStudy(String name) throws Exception {
        return addStudy(name, true);
    }

    public static List<StudyWrapper> addStudies(String name, int count)
        throws Exception {
        List<StudyWrapper> studies = new ArrayList<StudyWrapper>();
        for (int i = 0; i < count; i++) {
            studies.add(addStudy(name + i, true));
        }
        return studies;
    }

    public static void deleteCreatedStudies() throws Exception {
        Assert.assertNotNull("appService is null", appService);
        for (StudyWrapper study : createdStudies) {
            deleteCreatedStudy(study);
        }
        createdStudies.clear();
    }

    public static void deleteStudyDependencies() throws Exception {
        for (StudyWrapper study : createdStudies) {
            study.reload();
            deletePatients(study.getPatientCollection(false));
            deleteFromList(study.getAliquotedSpecimenCollection(false));
            deleteFromList(study.getSourceSpecimenCollection(false));
            study.reload();
        }

    }

    public static void deleteCreatedStudy(StudyWrapper study) throws Exception {
        if (!createdStudies.contains(study)) {
            throw new Exception("Study " + study.getNameShort()
                + " was not created by this helper");
        }
        study.delete();
    }

}
