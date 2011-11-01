package edu.ualberta.med.biobank.test.action.helper;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteHelper extends Helper {

    public static Integer createSite(BiobankApplicationService appService,
        String name, String city, ActivityStatusEnum activityStatus,
        Set<Integer> studyIds) throws ApplicationException {

        Address address = new Address();
        address.setCity(city);

        SiteSaveAction saveSite = new SiteSaveAction();
        saveSite.setName(name);
        saveSite.setNameShort(name);
        saveSite.setAddress(address);
        saveSite.setActivityStatusId(activityStatus.getId());
        saveSite.setStudyIds(studyIds);

        return appService.doAction(saveSite);
    }

}
