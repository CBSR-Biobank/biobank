package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction.ContactSaveInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.IActionExecutor;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicHelper extends Helper {

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
        String name, ActivityStatus activityStatus)
        throws ApplicationException {

        ClinicSaveAction clinicSave = new ClinicSaveAction();
        clinicSave.setName(name);
        clinicSave.setNameShort(name);
        clinicSave.setSendsShipments(true);
        clinicSave.setContactSaveInfos(new HashSet<ContactSaveInfo>());
        clinicSave.setActivityStatus(activityStatus);

        Address address = new Address();
        address.setCity(Utils.getRandomString(5, 10));
        clinicSave.setAddress(address);

        return actionExecutor.exec(clinicSave).getId();
    }

    public static Integer createClinicWithContacts(
        IActionExecutor actionExecutor, String name, int numContacts)
        throws ApplicationException {
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
        int numContactsPerClinic)
        throws ApplicationException {
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
        saveAction.setNameShort(clinicInfo.clinic.getNameShort());
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
