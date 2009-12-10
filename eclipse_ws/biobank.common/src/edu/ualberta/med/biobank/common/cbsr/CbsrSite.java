package edu.ualberta.med.biobank.common.cbsr;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CbsrSite {

    public static SiteWrapper cbsrSite = null;

    public static void createConfiguration(WritableApplicationService appService)
        throws Exception {
        addSite(appService);
        CbsrClinics.createClinics(cbsrSite);
        CbsrStudies.createStudies(cbsrSite);
        CbsrContainerTypes.createContainerTypes(cbsrSite);
        CbsrContainers.createContainers(cbsrSite);
    }

    public static SiteWrapper addSite(WritableApplicationService appService)
        throws Exception {
        cbsrSite = new SiteWrapper(appService);
        cbsrSite.setName("Canadian BioSample Repository");
        cbsrSite.setStreet1("471 Medical Sciences Building");
        cbsrSite.setStreet2("University of Alberta");
        cbsrSite.setCity("Edmonton");
        cbsrSite.setProvince("Alberta");
        cbsrSite.setPostalCode("T6G2H7");
        cbsrSite.persist();
        cbsrSite.reload();
        return cbsrSite;
    }

    public static SiteWrapper getSite() {
        return cbsrSite;
    }

}
