package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.DispatchInfoWrapper;

public class DispatchInfoHelper extends DbHelper {

    public static DispatchInfoWrapper newInfo(StudyWrapper study,
        SiteWrapper srcSite, SiteWrapper... destSites) throws Exception {
        DispatchInfoWrapper info = new DispatchInfoWrapper(appService);
        info.setStudy(study);
        info.setSrcSite(srcSite);

        if (destSites != null) {
            info.addDestSites(Arrays.asList(destSites));
        }
        return info;
    }

    public static DispatchInfoWrapper addInfo(StudyWrapper study,
        SiteWrapper srcSite, SiteWrapper... destSites) throws Exception {
        DispatchInfoWrapper info = newInfo(study, srcSite, destSites);
        info.persist();
        return info;
    }

}
