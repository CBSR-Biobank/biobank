package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyHelper extends Helper {

    public static StudySaveAction getSaveAction(String name, String nameShort,
        ActivityStatusEnum activityStatus) {
        StudySaveAction saveAction = new StudySaveAction();
        saveAction.setName(name);
        saveAction.setNameShort(nameShort);
        saveAction.setActivityStatusId(activityStatus.getId());
        saveAction.setSiteIds(new HashSet<Integer>());
        saveAction.setContactIds(new HashSet<Integer>());
        saveAction.setSourceSpcIds(new HashSet<Integer>());
        saveAction.setAliquotSpcIds(new HashSet<Integer>());
        saveAction.setStudyEventAttrIds(new HashSet<Integer>());
        return saveAction;
    }

    public static Integer createStudy(BiobankApplicationService appService,
        String name, ActivityStatusEnum activityStatus)
        throws ApplicationException {
        StudySaveAction saveStudy = getSaveAction(name, name, activityStatus);
        return appService.doAction(saveStudy).getId();
    }

    @SuppressWarnings("unused")
    public static StudySaveAction getSaveAction(
        BiobankApplicationService appService, StudyInfo studyInfo) {
        StudySaveAction saveStudy = new StudySaveAction();
        saveStudy.setId(studyInfo.study.getId());
        saveStudy.setName(studyInfo.study.getName());
        saveStudy.setNameShort(studyInfo.study.getNameShort());
        saveStudy.setActivityStatusId(studyInfo.study.getActivityStatus()
            .getId());

        saveStudy.setSiteIds(new HashSet<Integer>());

        Set<Integer> ids = new HashSet<Integer>();
        for (ClinicInfo infos : studyInfo.clinicInfos) {
            for (Contact c : infos.getContacts()) {
                ids.add(c.getId());
            }
        }
        saveStudy.setContactIds(ids);

        ids = new HashSet<Integer>();
        for (SourceSpecimen spc : studyInfo.sourceSpcs) {
            ids.add(spc.getId());
        }
        saveStudy.setSourceSpcIds(ids);

        ids = new HashSet<Integer>();
        for (AliquotedSpecimen spc : studyInfo.aliquotedSpcs) {
            ids.add(spc.getId());
        }
        saveStudy.setAliquotSpcIds(ids);

        ids = new HashSet<Integer>();
        for (StudyEventAttr attr : studyInfo.studyEventAttrs) {
            ids.add(attr.getId());
        }
        saveStudy.setStudyEventAttrIds(ids);

        return saveStudy;
    }

}