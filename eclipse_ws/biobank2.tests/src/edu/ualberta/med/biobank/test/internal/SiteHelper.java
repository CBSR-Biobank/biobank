package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.test.Utils;

public class SiteHelper extends DbHelper {

    public static List<SiteWrapper> createdSites = new ArrayList<SiteWrapper>();

    public static SiteWrapper newSite(String name) throws Exception {
        SiteWrapper site = new SiteWrapper(appService);
        site.setName(name);
        if (name != null) {
            if (name.length() <= 50) {
                site.setNameShort(name);
            } else {
                site.setNameShort(name.substring(50));
            }
        }
        site.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
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
        // in case containers with no top level type has been created without a
        // parent :
        // TODO check if still need this with last modifications
        site.reload();
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
