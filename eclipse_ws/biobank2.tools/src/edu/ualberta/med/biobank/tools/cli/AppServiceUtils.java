package edu.ualberta.med.biobank.tools.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.action.clinic.ClinicGetAllAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetAllAction;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class AppServiceUtils {

    public static Map<String, Site> getSitesByNameShort(BiobankApplicationService appService)
        throws Exception {
        List<Site> sites = appService.doAction(new SiteGetAllAction()).getList();
        Map<String, Site> siteMap = new HashMap<String, Site>(sites.size());

        for (Site site : sites) {
            siteMap.put(site.getNameShort(), site);
        }
        return siteMap;
    }

    public static Map<String, Clinic> getClinicsByNameShort(BiobankApplicationService appService)
        throws Exception {
        List<Clinic> clinics = appService.doAction(new ClinicGetAllAction()).getList();
        Map<String, Clinic> clinicMap = new HashMap<String, Clinic>(clinics.size());

        for (Clinic clinic : clinics) {
            clinicMap.put(clinic.getNameShort(), clinic);
        }
        return clinicMap;
    }
}
