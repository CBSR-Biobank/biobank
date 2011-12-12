package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.AliquotedSpecimenSaveInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.SourceSpecimenSaveInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.StudyEventAttrSaveInfo;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyHelper extends Helper {

    private Map<String, GlobalEventAttr> globalEventAttrs = null;

    public static StudySaveAction getSaveAction(String name, String nameShort,
        ActivityStatusEnum activityStatus) {
        StudySaveAction saveAction = new StudySaveAction();
        saveAction.setName(name);
        saveAction.setNameShort(nameShort);
        saveAction.setActivityStatusId(activityStatus.getId());
        saveAction.setSiteIds(new HashSet<Integer>());
        saveAction.setContactIds(new HashSet<Integer>());
        saveAction
            .setSourceSpecimenSaveInfo(new HashSet<SourceSpecimenSaveInfo>());
        saveAction
            .setAliquotSpecimenSaveInfo(new HashSet<AliquotedSpecimenSaveInfo>());
        saveAction
            .setStudyEventAttrSaveInfo(new HashSet<StudyEventAttrSaveInfo>());
        return saveAction;
    }

    public static Integer createStudy(BiobankApplicationService appService,
        String name, ActivityStatusEnum activityStatus)
        throws ApplicationException {
        StudySaveAction saveStudy = getSaveAction(name, name, activityStatus);
        return appService.doAction(saveStudy).getId();
    }

    public static StudySaveAction getSaveAction(
        BiobankApplicationService appService, StudyInfo studyInfo)
        throws ApplicationException {
        return getSaveAction(appService, studyInfo, null);
    }

    public static StudySaveAction getSaveAction(
        BiobankApplicationService appService, StudyInfo studyInfo,
        Map<String, GlobalEventAttr> globalEventAttrs) {
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

        Set<SourceSpecimenSaveInfo> ssSaveInfos =
            new HashSet<SourceSpecimenSaveInfo>();
        for (SourceSpecimen ss : studyInfo.sourceSpcs) {
            SourceSpecimenSaveInfo ssSaveInfo = new SourceSpecimenSaveInfo();
            ssSaveInfo.id = ss.getId();
            ssSaveInfo.needOriginalVolume = ss.getNeedOriginalVolume();
            ssSaveInfo.specimenTypeId = ss.getSpecimenType().getId();
            ssSaveInfos.add(ssSaveInfo);
        }
        saveStudy.setSourceSpecimenSaveInfo(ssSaveInfos);

        Set<AliquotedSpecimenSaveInfo> asSaveInfos =
            new HashSet<AliquotedSpecimenSaveInfo>();
        for (AliquotedSpecimen as : studyInfo.aliquotedSpcs) {
            AliquotedSpecimenSaveInfo asSaveInfo =
                new AliquotedSpecimenSaveInfo();
            asSaveInfo.id = as.getId();
            asSaveInfo.quantity = as.getQuantity();
            asSaveInfo.volume = as.getVolume();
            asSaveInfo.aStatusId = as.getActivityStatus().getId();
            asSaveInfo.specimenTypeId = as.getSpecimenType().getId();
            asSaveInfos.add(asSaveInfo);
        }
        saveStudy.setAliquotSpecimenSaveInfo(asSaveInfos);

        Set<StudyEventAttrSaveInfo> eAttrSaveInfos =
            new HashSet<StudyEventAttrSaveInfo>();
        if (globalEventAttrs != null) {
            for (StudyEventAttr eAttr : studyInfo.studyEventAttrs) {
                StudyEventAttrSaveInfo eAttrSaveInfo =
                    new StudyEventAttrSaveInfo();
                eAttrSaveInfo.id = eAttr.getId();
                eAttrSaveInfo.globalEventAttrId =
                    globalEventAttrs.get(eAttr.getLabel()).getId();
                eAttrSaveInfo.required = eAttr.getRequired();
                eAttrSaveInfo.permissible = eAttr.getPermissible();
                eAttrSaveInfo.aStatusId = eAttr.getActivityStatus().getId();
                eAttrSaveInfos.add(eAttrSaveInfo);
            }
        }
        saveStudy.setStudyEventAttrSaveInfo(eAttrSaveInfos);

        return saveStudy;
    }
}