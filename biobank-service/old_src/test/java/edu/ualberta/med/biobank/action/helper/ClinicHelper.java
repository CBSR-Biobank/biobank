package edu.ualberta.med.biobank.action.helper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.ualberta.med.biobank.NameGenerator;
import edu.ualberta.med.biobank.action.IActionExecutor;
import edu.ualberta.med.biobank.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.action.clinic.ClinicSaveAction.ContactSaveInfo;
import edu.ualberta.med.biobank.action.csvimport.specimen.SpecimenCsvHelper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.type.ActivityStatus;

public class ClinicHelper extends Helper {
    private static final NameGenerator nameGenerator = new NameGenerator(
        SpecimenCsvHelper.class.getSimpleName() + new Random());

    public static ClinicSaveAction getSaveAction(String name, String nameShort,
        ActivityStatus activityStatus, Boolean sendsShipments) {

        Address address = new Address();
        String city = name + "_city";
        if (city.length() > 50) {
            city = city.substring(city.length() - 50);
        }
        address.setCity(city);

        ClinicSaveAction saveClinic = new ClinicSaveAction();
        saveClinic.setName(name);
        saveClinic.setNameShort(nameShort);
        saveClinic.setActivityStatus(activityStatus);
        saveClinic.setSendsShipments(sendsShipments);
        saveClinic.setContactSaveInfos(new HashSet<ContactSaveInfo>());
        saveClinic.setAddress(address);
        return saveClinic;
    }

    public static Integer createClinic(IActionExecutor actionExecutor,
        String name, ActivityStatus activityStatus) {

        ClinicSaveAction clinicSave = new ClinicSaveAction();
        clinicSave.setName(name);
        clinicSave.setNameShort(name);
        clinicSave.setSendsShipments(true);
        clinicSave.setContactSaveInfos(new HashSet<ContactSaveInfo>());
        clinicSave.setActivityStatus(activityStatus);

        Address address = new Address();
        address.setCity(nameGenerator.next(String.class));
        clinicSave.setAddress(address);

        return actionExecutor.exec(clinicSave).getId();
    }

    public static Integer createClinicWithContacts(
        IActionExecutor actionExecutor, String name, int numContacts) {
        Set<ContactSaveInfo> contactsAll = new HashSet<ContactSaveInfo>();

        for (int i = 0; i < numContacts; ++i) {
            ContactSaveInfo contactSaveInfo = new ContactSaveInfo();
            contactSaveInfo.name = name + "_contact" + i;
            contactsAll.add(contactSaveInfo);
        }

        ClinicSaveAction clinicSave = ClinicHelper.getSaveAction(
            name, name, ActivityStatus.ACTIVE, true);
        clinicSave.setContactSaveInfos(contactsAll);
        return actionExecutor.exec(clinicSave).getId();
    }

    public static Set<Integer> createClinicsWithContacts(
        IActionExecutor actionExecutor, String name, int numClinics,
        int numContactsPerClinic) {
        Set<Integer> result = new HashSet<Integer>();

        for (int i = 0; i < numClinics; ++i) {
            result.add(createClinicWithContacts(actionExecutor, name + "_" + i,
                numContactsPerClinic));
        }

        return result;
    }

    public static ClinicSaveAction getSaveAction(ClinicInfo clinicInfo) {
        ClinicSaveAction saveAction = new ClinicSaveAction();

        saveAction.setId(clinicInfo.clinic.getId());
        saveAction.setName(clinicInfo.clinic.getName());
        saveAction.setName(clinicInfo.clinic.getName());
        saveAction.setActivityStatus(clinicInfo.clinic.getActivityStatus());
        saveAction.setSendsShipments(clinicInfo.clinic.getSendsShipments());
        saveAction.setAddress(clinicInfo.clinic.getAddress());

        Set<ContactSaveInfo> contactSaveInfos = new HashSet<ContactSaveInfo>();
        for (Contact contact : clinicInfo.contacts) {
            contactSaveInfos.add(new ContactSaveInfo(contact));
        }
        saveAction.setContactSaveInfos(contactSaveInfos);

        return saveAction;
    }
}
