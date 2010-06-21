package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class StudyHelper extends DbHelper {

    public static StudyWrapper newStudy(SiteWrapper site, String name)
        throws Exception {
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
        study.setSite(site);
        return study;
    }

    public static StudyWrapper addStudy(SiteWrapper site, String name)
        throws Exception {
        StudyWrapper study = newStudy(site, name);
        study.persist();
        return study;
    }

    public static void addStudies(SiteWrapper site, String name, int count)
        throws Exception {
        for (int i = 0; i < count; i++) {
            addStudy(site, name + i);
        }
        site.reload();
    }

}
