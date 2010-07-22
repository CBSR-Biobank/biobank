package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class StudyHelper extends DbHelper {

    public static StudyWrapper newStudy(String name) throws Exception {
        StudyWrapper study = new StudyWrapper(appService);
        study.setName(name);
        study.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        if (name != null) {
            if (name.length() < 50) {
                study.setNameShort(name);
            } else {
                study.setNameShort(name.substring(name.length() - 49));
            }
        }
        return study;
    }

    public static StudyWrapper addStudy(String name) throws Exception {
        StudyWrapper study = newStudy(name);
        study.persist();
        return study;
    }

    public static void addStudies(String name, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            addStudy(name + i);
        }
    }

}
