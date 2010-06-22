package edu.ualberta.med.biobank.common.config.calgary;

import edu.ualberta.med.biobank.common.config.ConfigSite;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CalgarySite extends ConfigSite {

    public static void createConfiguration(WritableApplicationService appServ)
        throws Exception {
        appService = appServ;
        addSite(appService);
        CalgaryClinics.createClinics(site);
        CalgaryStudies.createStudies(site);
        CalgaryContainerTypes configCT = new CalgaryContainerTypes();
        configCT.createContainerTypes(site);
    }

}
