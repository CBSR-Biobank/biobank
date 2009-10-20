package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class ClinicHelper extends DbHelper {

    public static ClinicWrapper newClinic(SiteWrapper site, String name)
        throws Exception {
        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setName(name);
        clinic.setCity("");
        clinic.setSite(site);

        return clinic;
    }

    public static ClinicWrapper addClinic(SiteWrapper site, String name)
        throws Exception {
        ClinicWrapper clinic = newClinic(site, name);
        clinic.persist();
        return clinic;
    }

    public static void addClinics(SiteWrapper site, String name, int count)
        throws Exception {
        for (int i = 0; i < count; i++) {
            addClinic(site, name + (i + 1));
        }
        site.reload();
    }

}
