package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ContactSaveAction;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.Utils;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicHelper extends Helper {

    public static Integer createClinic(BiobankApplicationService appService,
        String name, ActivityStatusEnum activityStatus)
        throws ApplicationException {

        ClinicSaveAction clinicSave = new ClinicSaveAction();
        clinicSave.setName(name);
        clinicSave.setNameShort(name);
        clinicSave.setSendsShipments(true);
        clinicSave.setContactIds(new HashSet<Integer>());

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

    public static Set<Integer> createClinicsWithContacts(
        BiobankApplicationService appService,
        String name, int numClinics, int numContactsPerClinic)
        throws ApplicationException {
        Set<Integer> result = new HashSet<Integer>();

        Integer clinicId;
        for (int i = 0; i < numClinics; ++i) {
            clinicId =
                createClinic(appService, name + i, ActivityStatusEnum.ACTIVE);

            for (int j = 0; j < numContactsPerClinic; ++j) {
                createContact(appService, name + "_contact" + j, clinicId);
            }
            result.add(clinicId);
        }

        return result;
    }
}
