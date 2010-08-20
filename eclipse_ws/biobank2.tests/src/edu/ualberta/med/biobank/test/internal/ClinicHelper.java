package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class ClinicHelper extends DbHelper {

    public static List<ClinicWrapper> createdClinics = new ArrayList<ClinicWrapper>();

    @Deprecated
    public static ClinicWrapper newClinic(SiteWrapper site, String name)
        throws Exception {
        return null;
    }

    public static ClinicWrapper newClinic(String name) throws Exception {
        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setName(name);
        if (name != null) {
            if (name.length() <= 50) {
                clinic.setNameShort(name);
            } else {
                clinic.setNameShort(name.substring(name.length() - 49));
            }
        }
        clinic.setCity("");
        clinic.setSendsShipments(true);
        clinic.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));

        return clinic;
    }

    @Deprecated
    public static ClinicWrapper addClinic(SiteWrapper site, String name,
        boolean addContacts, boolean addToCreatedList) throws Exception {
        return null;
    }

    public static ClinicWrapper addClinic(String name, boolean addContacts,
        boolean addToCreatedList) throws Exception {
        ClinicWrapper clinic = newClinic(name);
        clinic.persist();
        if (addContacts) {
            ContactHelper.addContactsToClinic(clinic, name);
        }
        if (addToCreatedList) {
            createdClinics.add(clinic);
        }
        return clinic;
    }

    @Deprecated
    public static ClinicWrapper addClinic(SiteWrapper site, String name,
        boolean addContacts) throws Exception {
        return null;
    }

    public static ClinicWrapper addClinic(String name, boolean addContacts)
        throws Exception {
        return addClinic(name, addContacts, true);
    }

    @Deprecated
    public static ClinicWrapper addClinic(SiteWrapper site, String name)
        throws Exception {
        return null;

    }

    public static ClinicWrapper addClinic(String name) throws Exception {
        return addClinic(name, false);
    }

    @Deprecated
    public static void addClinics(SiteWrapper site, String name, int count,
        boolean addContacts) throws Exception {

    }

    public static void addClinics(String name, int count, boolean addContacts)
        throws Exception {
        for (int i = 0; i < count; i++) {
            addClinic(name + i, addContacts);
        }
    }

    @Deprecated
    public static void addClinics(SiteWrapper site, String name, int count) {
    }

    public static void addClinics(String name, int count) throws Exception {
        addClinics(name, count, false);
    }

    public static void deleteCreatedClinics() throws Exception {
        deleteClinics(createdClinics);
        createdClinics.clear();
    }

}
