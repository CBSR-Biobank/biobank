package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.Utils;
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

        return appService.doAction(saveSite).getId();
    }

    public static List<Integer> createSites(
        BiobankApplicationService appService,
        String name, ActivityStatusEnum activityStatus, int numToCreate)
        throws ApplicationException {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < numToCreate; ++i) {
            result.add(createSite(appService, name, Utils.getRandomString(15),
                activityStatus, new HashSet<Integer>()));
        }
        return result;
    }
}
