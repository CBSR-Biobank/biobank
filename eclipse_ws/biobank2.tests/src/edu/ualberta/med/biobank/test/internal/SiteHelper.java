package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.test.Utils;

@Deprecated
public class SiteHelper extends CenterHelper {

    public static List<SiteWrapper> createdSites = new ArrayList<SiteWrapper>();

    public static SiteWrapper newSite(String name) throws Exception {
        SiteWrapper site = new SiteWrapper(appService);
        site.setName(name);
        site.setNameShort(name);
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

    public static void deleteSiteAndDependencies(SiteWrapper site)
        throws Exception {
        site.reload();
        deleteContainers(site.getTopContainerCollection(false));
        // in case containers with no top level type has been created without a
        // parent :
        // TODO check if still need this with last modifications
        site.reload();
        deleteContainers(site.getContainerCollection(false));
        deleteFromList(site.getContainerTypeCollection(false));
        site.reload();

        // dispatches should have been deleted before sites are deleted
        //
        // see TestDatabase.tearDown().
        Assert.isTrue(site.getSrcDispatchCollection(false).size() == 0);
        Assert.isTrue(site.getDstDispatchCollection(false).size() == 0);
        deleteCenterDependencies(site);

        site.reload();
        List<ProcessingEventWrapper> processingEvents = site
            .getProcessingEventCollection(false);
        for (ProcessingEventWrapper processingEvent : processingEvents) {
            List<SpecimenWrapper> specimens = processingEvent
                .getSpecimenCollection(false);
            deleteFromList(specimens);
        }

        deleteFromList(processingEvents);

        site.removeFromStudyCollection(site.getStudyCollection(false));
        site.persist();

        site.reload();
        site.delete();
    }

    public static void deleteCreatedSites() throws Exception {
        for (SiteWrapper site : createdSites) {
            deleteSiteAndDependencies(site);
        }
        createdSites.clear();
    }

}
