package edu.ualberta.med.biobank.client.config.calgary;

import java.util.List;

import edu.ualberta.med.biobank.client.config.ConfigSite;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CalgarySite extends ConfigSite {

    public static SiteWrapper addSite(WritableApplicationService appService)
        throws Exception {
        getSampleTypeMap(appService);
        site = new SiteWrapper(appService);
        site.setName("Calgary Foothills");
        site.setNameShort("CF");
        site.setActivityStatus(getActiveActivityStatus());
        site.setStreet1("");
        site.setStreet2("");
        site.setCity("Calgary");
        site.setProvince("Alberta");
        site.setPostalCode("");
        site.persist();
        site.reload();
        return site;
    }

    public static void deleteConfiguration(WritableApplicationService appServ)
        throws Exception {
        appService = appServ;

        List<SiteWrapper> sites = SiteWrapper.getSites(appService);
        if (sites == null)
            return;
        for (SiteWrapper site : sites) {
            if (site.getName().equals("Calgary Foothills")) {
                siteDeleteSubObjects(site);
            }
        }
    }

}
