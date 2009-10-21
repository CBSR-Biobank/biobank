package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SampleTypeHelper extends DbHelper {

    public static SampleTypeWrapper newSampleType(SiteWrapper site, String name)
        throws Exception {
        SampleTypeWrapper type = new SampleTypeWrapper(appService);
        type.setName(name);
        type.setSite(site);
        return type;
    }

    public static SampleTypeWrapper addSampleType(SiteWrapper site, String name)
        throws Exception {
        SampleTypeWrapper type = newSampleType(site, name);
        type.persist();
        return type;
    }

    public static int addSampleTypes(SiteWrapper site, String name)
        throws Exception {
        int nber = r.nextInt(15) + 1;
        for (int i = 0; i < nber; i++) {
            addSampleType(site, name + i);
        }
        site.reload();
        return nber;
    }

}
