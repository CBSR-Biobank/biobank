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

    public static ClinicWrapper addClinic(SiteWrapper site, String name,
        boolean addContacts) throws Exception {
        ClinicWrapper clinic = newClinic(site, name);
        clinic.persist();
        if (addContacts) {
            for (int i = 0; i < (r.nextInt(5) + 1); i++) {
                ContactHelper.addContact(clinic, name);
            }
            clinic.reload();
        }
        return clinic;
    }

    public static ClinicWrapper addClinic(SiteWrapper site, String name)
        throws Exception {
        return addClinic(site, name, false);
    }

    public static void addClinics(SiteWrapper site, String name, int count,
        boolean addContacts) throws Exception {
        for (int i = 0; i < count; i++) {
            addClinic(site, name + i, addContacts);
        }
        site.reload();
    }

    public static void addClinics(SiteWrapper site, String name, int count)
        throws Exception {
        addClinics(site, name, count, false);
    }

}
