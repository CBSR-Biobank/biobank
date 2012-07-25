package edu.ualberta.med.biobank.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.action.study.StudyInfo;
import edu.ualberta.med.biobank.action.study.StudySaveAction;
import edu.ualberta.med.biobank.action.study.StudySaveAction.AliquotedSpecimenSaveInfo;
import edu.ualberta.med.biobank.action.study.StudySaveAction.SourceSpecimenSaveInfo;
import edu.ualberta.med.biobank.action.study.StudySaveAction.StudyEventAttrSaveInfo;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.type.ActivityStatus;
import edu.ualberta.med.biobank.action.IActionExecutor;

public class StudyHelper extends Helper {
    public static StudySaveAction getSaveAction(String name, String nameShort,
        ActivityStatus activityStatus) {
        StudySaveAction saveAction = new StudySaveAction();
        saveAction.setName(name);
        saveAction.setNameShort(nameShort);
        saveAction.setActivityStatus(activityStatus);
        saveAction.setContactIds(new HashSet<Integer>());
        saveAction
            .setSourceSpecimenSaveInfo(new HashSet<SourceSpecimenSaveInfo>());
        saveAction
            .setAliquotSpecimenSaveInfo(new HashSet<AliquotedSpecimenSaveInfo>());
        saveAction
            .setStudyEventAttrSaveInfo(new HashSet<StudyEventAttrSaveInfo>());
        return saveAction;
    }

    public static Integer createStudy(IActionExecutor actionExecutor,
        String name, ActivityStatus activityStatus) {
        StudySaveAction saveStudy = getSaveAction(name, name, activityStatus);
        return actionExecutor.exec(saveStudy).getId();
    }

    public static StudySaveAction getSaveAction(StudyInfo studyInfo) {
        StudySaveAction saveStudy = new StudySaveAction();
        saveStudy.setId(studyInfo.getStudy().getId());
        saveStudy.setName(studyInfo.getStudy().getName());
        saveStudy.setNameShort(studyInfo.getStudy().getNameShort());
        saveStudy.setActivityStatus(studyInfo.getStudy().getActivityStatus());

        Set<Integer> ids = new HashSet<Integer>();
        for (ClinicInfo infos : studyInfo.getClinicInfos()) {
            for (Contact c : infos.getContacts()) {
                ids.add(c.getId());
            }
        }
        saveStudy.setContactIds(ids);

        Set<SourceSpecimenSaveInfo> ssSaveInfos =
            new HashSet<SourceSpecimenSaveInfo>();
        for (SourceSpecimen ss : studyInfo.getSourceSpecimens()) {
            ssSaveInfos.add(new SourceSpecimenSaveInfo(ss));
        }
        saveStudy.setSourceSpecimenSaveInfo(ssSaveInfos);

        Set<AliquotedSpecimenSaveInfo> asSaveInfos =
            new HashSet<AliquotedSpecimenSaveInfo>();
        for (AliquotedSpecimen as : studyInfo.getAliquotedSpcs()) {
            asSaveInfos.add(new AliquotedSpecimenSaveInfo(as));
        }
        saveStudy.setAliquotSpecimenSaveInfo(asSaveInfos);

        Set<StudyEventAttrSaveInfo> seAttrSaveInfos =
            new HashSet<StudyEventAttrSaveInfo>();
        for (StudyEventAttr seAttr : studyInfo.getStudyEventAttrs()) {
            seAttrSaveInfos.add(new StudyEventAttrSaveInfo(seAttr));
        }
        saveStudy.setStudyEventAttrSaveInfo(seAttrSaveInfos);

        return saveStudy;
    }
}