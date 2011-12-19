package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.Utils;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteHelper extends Helper {

    public static SiteSaveAction getSaveAction(String name,
        String nameShort, ActivityStatusEnum active) {
        Address address = new Address();
        address.setCity(Utils.getRandomString(5, 10));

        SiteSaveAction siteSaveAction = new SiteSaveAction();
        siteSaveAction.setName(name);
        siteSaveAction.setNameShort(nameShort);
        siteSaveAction.setActivityStatusId(active.getId());
        siteSaveAction.setAddress(address);
        siteSaveAction.setStudyIds(new HashSet<Integer>());

        return siteSaveAction;
    }

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

    public static SiteSaveAction getSaveAction(
        BiobankApplicationService appService, SiteInfo siteInfo) {
        SiteSaveAction siteSaveAction = new SiteSaveAction();

        siteSaveAction.setId(siteInfo.site.getId());
        siteSaveAction.setName(siteInfo.site.getName());
        siteSaveAction.setNameShort(siteInfo.site.getNameShort());
        siteSaveAction.setActivityStatusId(siteInfo.site.getActivityStatus()
            .getId());
        siteSaveAction.setAddress(siteInfo.site.getAddress());

        Set<Integer> ids = new HashSet<Integer>();
        for (StudyCountInfo infos : siteInfo.studyCountInfo) {
            ids.add(infos.getStudy().getId());
        }
        siteSaveAction.setStudyIds(ids);

        return siteSaveAction;
    }
}
