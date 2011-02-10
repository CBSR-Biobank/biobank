package edu.ualberta.med.biobank.client.config.cbsr;

import java.util.List;

import edu.ualberta.med.biobank.client.config.ConfigSite;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CbsrSite extends ConfigSite {

    public static SiteWrapper addSite(WritableApplicationService appService)
        throws Exception {
        populateSampleTypeMap(appService);
        site = new SiteWrapper(appService);
        site.setName("Canadian BioSample Repository");
        site.setNameShort("CBSR");
        site.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        site.setStreet1("471 Medical Sciences Building");
        site.setStreet2("University of Alberta");
        site.setCity("Edmonton");
        site.setProvince("Alberta");
        site.setPostalCode("T6G2H7");
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
            if (site.getName().equals("Canadian BioSample Repository")) {
                siteDeleteSubObjects(site);
            }
        }
    }

}
