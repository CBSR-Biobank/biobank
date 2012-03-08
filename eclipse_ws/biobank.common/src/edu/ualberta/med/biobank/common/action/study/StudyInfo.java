package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class StudyInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    Study study;
    Long patientCount;
    Long collectionEventCount;
    List<ClinicInfo> clinicInfos;
    List<SourceSpecimen> sourceSpcs;
    List<AliquotedSpecimen> aliquotedSpcs;
    List<StudyEventAttr> studyEventAttrs;

    public StudyInfo() {
        this.study = null;
        this.clinicInfos = new ArrayList<ClinicInfo>();
        this.sourceSpcs = new ArrayList<SourceSpecimen>();
        this.aliquotedSpcs = new ArrayList<AliquotedSpecimen>();
        this.studyEventAttrs = new ArrayList<StudyEventAttr>();
    }

    public StudyInfo(Study study, Long patientCount, Long ceventCount,
        List<ClinicInfo> clinicInfos,
        List<SourceSpecimen> sourceSpcs,
        List<AliquotedSpecimen> aliquotedSpcs,
        List<StudyEventAttr> studyEventAttrs) {
        this.study = study;
        this.patientCount = patientCount;
        this.collectionEventCount = ceventCount;
        this.clinicInfos = clinicInfos;
        this.sourceSpcs = sourceSpcs;
        this.aliquotedSpcs = aliquotedSpcs;
        this.studyEventAttrs = studyEventAttrs;
    }

    public Study getStudy() {
        return study;
    }

    public Long getPatientCount() {
        return patientCount;
    }

    public Long getCollectionEventCount() {
        return collectionEventCount;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public List<ClinicInfo> getClinicInfos() {
        return clinicInfos;
    }

    public void setClinicInfos(List<ClinicInfo> clinicInfos) {
        this.clinicInfos = clinicInfos;
    }

    public List<SourceSpecimen> getSourceSpecimens() {
        return sourceSpcs;
    }

    public void setSourceSpcs(List<SourceSpecimen> sourceSpcs) {
        this.sourceSpcs = sourceSpcs;
    }

    public List<AliquotedSpecimen> getAliquotedSpcs() {
        return aliquotedSpcs;
    }

    public List<StudyEventAttr> getStudyEventAttrs() {
        return studyEventAttrs;
    }
}
