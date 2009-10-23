package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class StudyHelper extends DbHelper {

    public static StudyWrapper newStudy(SiteWrapper site, String name)
        throws Exception {
        StudyWrapper study = new StudyWrapper(appService);
        study.setName(name);
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
