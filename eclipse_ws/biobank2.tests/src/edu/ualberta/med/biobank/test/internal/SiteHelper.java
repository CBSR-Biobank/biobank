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
                site.setNameShort(name.substring(0, 49));
            }
        }
        site.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, ActivityStatusWrapper.ACTIVE_STATUS_STRING));
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

    public static List<SiteWrapper> addSites(String basename, int count)
        throws Exception {
        List<SiteWrapper> sites = new ArrayList<SiteWrapper>();
        for (int i = 0; i < count; i++) {
            sites.add(addSite(basename + i));
        }
        return sites;
    }

    public static void deleteSite(SiteWrapper site) throws Exception {
        if (!createdSites.contains(site)) {
            throw new Exception("Site " + site.getName()
                + " not created with SiteHelper");
        }
        createdSites.remove(site);
        site.delete();
    }

    private static void deleteSiteInternal(SiteWrapper site) throws Exception {
        site.reload();
        deleteContainers(site.getTopContainerCollection(false));
        // in case containers with no top level type has been created without a
        // parent :
        // TODO check if still need this with last modifications
        site.reload();
        deleteContainers(site.getContainerCollection(false));
        deleteFromList(site.getContainerTypeCollection(false));
        site.reload();
        deleteFromList(site.getProcessingEventCollection(false));
        site.reload();
        deleteDispatchs(site.getSrcDispatchCollection(false));
        deleteDispatchs(site.getDstDispatchCollection(false));
        site.reload();

        site.delete();
    }

    public static void deleteCreatedSites() throws Exception {
        for (SiteWrapper site : createdSites) {
            deleteSiteInternal(site);
        }
        createdSites.clear();
    }

}
