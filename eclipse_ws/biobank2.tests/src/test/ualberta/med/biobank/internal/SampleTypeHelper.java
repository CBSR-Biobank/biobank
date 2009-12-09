package test.ualberta.med.biobank.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SampleTypeHelper extends DbHelper {

    public static List<SampleTypeWrapper> createdSampleTypes = new ArrayList<SampleTypeWrapper>();

    public static SampleTypeWrapper newSampleType(SiteWrapper site, String name)
        throws Exception {
        SampleTypeWrapper type = new SampleTypeWrapper(appService);
        type.setName(name);
        type.setNameShort(name);
        type.setSite(site);
        return type;
    }

    public static SampleTypeWrapper addSampleType(SiteWrapper site,
        String name, boolean addToCreatedList) throws Exception {
        SampleTypeWrapper type = newSampleType(site, name);
        type.persist();
        if (addToCreatedList) {
            createdSampleTypes.add(type);
        }
        return type;
    }

    public static SampleTypeWrapper addSampleType(SiteWrapper site, String name)
        throws Exception {
        return addSampleType(site, name, true);
    }

    public static int addSampleTypes(SiteWrapper site, String name)
        throws Exception {
        int nber = r.nextInt(15) + 2;
        for (int i = 0; i < nber; i++) {
            addSampleType(site, name + i);
        }
        site.reload();
        return nber;
    }

    public static void deleteCreatedSampleTypes() throws Exception {
        for (SampleTypeWrapper type : createdSampleTypes) {
            type.reload();
            if (type.getSite() == null) { // others will be deleted with the
                type.delete();
            }
        }
        createdSampleTypes.clear();
    }

    public static void removeFromCreated(SampleTypeWrapper type) {
        createdSampleTypes.remove(type);
    }

}
