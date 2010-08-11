package edu.ualberta.med.biobank.common.config.calgary;

import java.util.List;

import edu.ualberta.med.biobank.common.config.ConfigSite;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CalgarySite extends ConfigSite {

    public static SiteWrapper addSite(WritableApplicationService appService)
        throws Exception {
        getSampleTypeMap(appService);
        site = new SiteWrapper(appService);
        site.setName("Calgary Foothills");
        site.setNameShort("Calgary-F");
        site.setActivityStatus(getActiveActivityStatus());
        site.setStreet1("");
        site.setStreet2("");
        site.setCity("Calgary");
        site.setProvince("Alberta");
        site.setPostalCode("");
        site.persist();
        site.reload();

        site.setSitePvAttr("Consent", "select_multiple");
        site.setSitePvAttr("PBMC Count (x10^6)", "number");
        site.setSitePvAttr("Phlebotomist", "text");
        site.setSitePvAttr("Worksheet", "text");
        site.setSitePvAttr("Biopsy Length", "number");

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
