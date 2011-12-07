package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ContactSaveAction;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.Utils;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicHelper extends Helper {

    public static ClinicSaveAction getSaveAction(String name, String nameShort,
        ActivityStatusEnum activityStatus, Boolean sendsShipments) {

        Address address = new Address();
        address.setCity(Utils.getRandomString(5, 10));

        ClinicSaveAction saveClinic = new ClinicSaveAction();
        saveClinic.setName(name);
        saveClinic.setNameShort(nameShort);
        saveClinic.setActivityStatusId(activityStatus.getId());
        saveClinic.setSendsShipments(sendsShipments);
        saveClinic.setContactIds(new HashSet<Integer>());
        saveClinic.setAddress(address);
        return saveClinic;
    }

    public static Integer createClinic(BiobankApplicationService appService,
        String name, ActivityStatusEnum activityStatus)
        throws ApplicationException {

        ClinicSaveAction clinicSave = new ClinicSaveAction();
        clinicSave.setName(name);
        clinicSave.setNameShort(name);
        clinicSave.setSendsShipments(true);
        clinicSave.setContactIds(new HashSet<Integer>());
        clinicSave.setActivityStatusId(activityStatus.getId());

        Address address = new Address();
        address.setCity(Utils.getRandomString(5, 10));
        clinicSave.setAddress(address);

        return appService.doAction(clinicSave).getId();
    }

    public static Integer createContact(BiobankApplicationService appService,
        String name, Integer clinicId) throws ApplicationException {
        ContactSaveAction contactSave = new ContactSaveAction();

        contactSave.setName(name);
        contactSave.setClinicId(clinicId);

        return appService.doAction(contactSave).getId();
    }

    public static Set<Integer> createContacts(
        BiobankApplicationService appService,
        Integer clinicId, String basename, int numContacts)
        throws ApplicationException {
        Set<Integer> result = new HashSet<Integer>();
        for (int j = 0; j < numContacts; ++j) {
            result.add(createContact(appService, basename + "_contact" + j,
                clinicId));
        }
        return result;
    }

    public static Set<Integer> createClinicsWithContacts(
        BiobankApplicationService appService,
        String name, int numClinics, int numContactsPerClinic)
        throws ApplicationException {
        Set<Integer> result = new HashSet<Integer>();

        Integer clinicId;
        for (int i = 0; i < numClinics; ++i) {
            clinicId =
                createClinic(appService, name + i, ActivityStatusEnum.ACTIVE);
            createContacts(appService, clinicId, name, numContactsPerClinic);
            result.add(clinicId);
        }

        return result;
    }

    public static ClinicSaveAction getSaveAction(
        BiobankApplicationService appService, ClinicInfo clinicInfo) {
        ClinicSaveAction saveAction = new ClinicSaveAction();

        saveAction.setId(clinicInfo.clinic.getId());
        saveAction.setName(clinicInfo.clinic.getName());
        saveAction.setNameShort(clinicInfo.clinic.getNameShort());
        saveAction.setActivityStatusId(clinicInfo.clinic.getActivityStatus()
            .getId());
        saveAction.setSendsShipments(clinicInfo.clinic.getSendsShipments());
        saveAction.setAddress(clinicInfo.clinic.getAddress());

        Set<Integer> ids = new HashSet<Integer>();
        for (Contact c : clinicInfo.contacts) {
            ids.add(c.getId());
        }
        saveAction.setContactIds(ids);

        return saveAction;
    }
}
