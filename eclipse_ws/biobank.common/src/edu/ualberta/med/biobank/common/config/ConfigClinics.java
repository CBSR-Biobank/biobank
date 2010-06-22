package edu.ualberta.med.biobank.common.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.config.calgary.CalgarySite;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class ConfigClinics {

    protected static Map<String, ClinicWrapper> clinicsMap = null;

    protected static Map<String, ContactWrapper> contactsMap = null;

    protected static ClinicWrapper addClinic(SiteWrapper site, String name,
        String nameShort, boolean sendsShipments, String activityStatusName,
        String comment, String street1, String street2, String city,
        String province, String postalCode) throws Exception {
        ClinicWrapper clinic = new ClinicWrapper(site.getAppService());
        clinic.setSite(site);
        clinic.setName(name);
        clinic.setNameShort(nameShort);
        clinic.setSendsShipments(sendsShipments);
        clinic.setActivityStatus(CalgarySite
            .getActivityStatus(activityStatusName));
        clinic.setComment(comment);
        clinic.setStreet1(street1);
        clinic.setStreet2(street2);
        clinic.setCity(city);
        clinic.setProvince(province);
        clinic.setPostalCode(postalCode);
        clinic.persist();
        clinic.reload();
        clinicsMap.put(nameShort, clinic);
        return clinic;
    }

    public static ClinicWrapper getClinic(String name) throws Exception {
        ClinicWrapper clinic = clinicsMap.get(name);
        if (clinic == null) {
            throw new Exception("clinic with name \"" + name
                + "\" does not exist");
        }
        return clinic;
    }

    public static List<String> getClinicNames() throws Exception {
        if (clinicsMap == null) {
            throw new Exception("clinics have not been added");
        }
        return Collections.unmodifiableList(new ArrayList<String>(clinicsMap
            .keySet()));
    }

    protected static ContactWrapper addContact(String clinicNameShort,
        String name, String title, String officeNumber, String faxNumber,
        String emailAddress) throws Exception {
        ClinicWrapper clinic = clinicsMap.get(clinicNameShort);

        if (clinic == null) {
            throw new Exception("no clinic with name " + clinicNameShort);
        }

        ContactWrapper contact = new ContactWrapper(clinic.getAppService());
        contact.setClinic(clinic);
        contact.setName(name);
        contact.setTitle(title);
        contact.setOfficeNumber(officeNumber);
        contact.setFaxNumber(faxNumber);
        contact.setEmailAddress(emailAddress);
        contact.persist();
        contact.reload();
        clinic.reload();
        contactsMap.put(name, contact);
        return contact;
    }

    public static ContactWrapper getContact(String name) throws Exception {
        ContactWrapper contact = contactsMap.get(name);
        if (contact == null) {
            throw new Exception("contact with name \"" + name
                + "\" does not exist");
        }
        return contact;
    }

    public static List<String> getContactNames() throws Exception {
        if (contactsMap == null) {
            throw new Exception("contacts have not been added");
        }
        return new ArrayList<String>(contactsMap.keySet());
    }

}
