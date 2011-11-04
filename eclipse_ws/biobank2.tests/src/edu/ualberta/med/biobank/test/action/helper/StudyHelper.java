package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.study.GlobalEventAttrInfo;
import edu.ualberta.med.biobank.common.action.study.GlobalEventAttrInfoGetAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfo.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.StudyEventAttrSaveInfo;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyHelper extends Helper {

    public static Integer createStudy(BiobankApplicationService appService,
        String name, ActivityStatusEnum activityStatus)
        throws ApplicationException {
        StudySaveAction saveStudy = new StudySaveAction();
        saveStudy.setName(name);
        saveStudy.setNameShort(name);
        saveStudy.setActivityStatusId(activityStatus.getId());
        saveStudy.setContactIds(new HashSet<Integer>());
        saveStudy.setSourceSpcIds(new HashSet<Integer>());
        saveStudy.setAliquotSpcIds(new HashSet<Integer>());
        saveStudy
            .setStudyEventAttrSaveInfo(new ArrayList<StudyEventAttrSaveInfo>());

        return appService.doAction(saveStudy);
    }

    public static Integer createStudy(BiobankApplicationService appService,
        String name, ActivityStatusEnum activityStatus,
        List<StudyEventAttrSaveInfo> attrInfos) throws ApplicationException {
        StudySaveAction saveStudy = new StudySaveAction();
        saveStudy.setName(name);
        saveStudy.setNameShort(name);
        saveStudy.setActivityStatusId(activityStatus.getId());
        saveStudy.setContactIds(new HashSet<Integer>());
        saveStudy.setSourceSpcIds(new HashSet<Integer>());
        saveStudy.setAliquotSpcIds(new HashSet<Integer>());
        saveStudy.setStudyEventAttrSaveInfo(attrInfos);

        return appService.doAction(saveStudy);
    }

    public static StudySaveAction getSaveAction(
        BiobankApplicationService appService, StudyInfo studyInfo)
        throws ApplicationException {
        StudySaveAction saveStudy = new StudySaveAction();
        saveStudy.setId(studyInfo.study.getId());
        saveStudy.setName(studyInfo.study.getName());
        saveStudy.setNameShort(studyInfo.study.getNameShort());
        saveStudy.setActivityStatusId(studyInfo.study.getActivityStatus()
            .getId());

        Set<Integer> ids = new HashSet<Integer>();
        for (ClinicInfo info : studyInfo.clinicInfos) {
            Contact c = info.getContact();
            if (c != null) {
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

        Map<Integer, GlobalEventAttrInfo> globalEventAttrInfoList =
            appService.doAction(new GlobalEventAttrInfoGetAction());
        HashMap<String, GlobalEventAttrInfo> globalEventAttrByLabel =
            new HashMap<String, GlobalEventAttrInfo>();

        for (GlobalEventAttrInfo geAttr : globalEventAttrInfoList.values()) {
            globalEventAttrByLabel.put(geAttr.attr.getLabel(), geAttr);
        }

        List<StudyEventAttrSaveInfo> saveSeAttrs =
            new ArrayList<StudyEventAttrSaveInfo>();
        for (StudyEventAttr seAttr : studyInfo.studyEventAttrs) {
            StudyEventAttrSaveInfo saveSeAttr = new StudyEventAttrSaveInfo();
            saveSeAttr.globalEventAttrId =
                globalEventAttrByLabel.get(seAttr.getLabel()).attr.getId();
            saveSeAttr.type =
                EventAttrTypeEnum.getEventAttrType(seAttr.getEventAttrType()
                    .getName());
            saveSeAttr.required = seAttr.getRequired();
            saveSeAttr.permissible = seAttr.getPermissible();
            saveSeAttr.aStatusId = seAttr.getActivityStatus().getId();
            saveSeAttrs.add(saveSeAttr);
        }

        saveStudy.setStudyEventAttrSaveInfo(saveSeAttrs);

        return saveStudy;
    }

}