package test.ualberta.med.biobank.internal;

import java.util.ArrayList;
import java.util.List;

import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SiteHelper extends DbHelper {

    protected static List<SiteWrapper> createdSites = new ArrayList<SiteWrapper>();

    public static SiteWrapper newSite(String name) {
        SiteWrapper site = new SiteWrapper(appService);
        site.setName(name);
        site.setStreet1(Utils.getRandomString(32));
        site.setCity(Utils.getRandomString(32));
        return site;
    }

    public static SiteWrapper addSite(String name, boolean addToCreatedList)
        throws Exception {
        SiteWrapper site = newSite(name);
        site.persist();
        if (addToCreatedList) {
            createdSites.add(site);
        }
        return site;
    }

    public static SiteWrapper addSite(String name) throws Exception {
        return addSite(name, true);
    }

    public static void addSites(String basename, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            addSite(basename + i);
        }
    }

    public static void deleteSite(SiteWrapper site) throws Exception {
        site.reload();
        deleteContainers(site.getTopContainerCollection());
        deleteContainers(site.getContainerCollection());
        deleteStudies(site.getStudyCollection());
        deleteClinics(site.getClinicCollection());
        deleteFromList(site.getContainerTypeCollection());
        site.reload();
        site.delete();
    }

    public static void deleteCreatedSites() throws Exception {
        for (SiteWrapper site : createdSites) {
            deleteSite(site);
        }
        createdSites.clear();
    }

}
