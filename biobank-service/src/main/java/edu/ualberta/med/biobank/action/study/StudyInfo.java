package edu.ualberta.med.biobank.action.study;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.study.StudyGetClinicInfoAction.ClinicInfo;
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
    Set<SourceSpecimen> sourceSpcs;
    Set<AliquotedSpecimen> aliquotedSpcs;
    Set<StudyEventAttr> studyEventAttrs;

    public StudyInfo() {
        this.study = null;
        this.clinicInfos = new ArrayList<ClinicInfo>();
        this.sourceSpcs = new HashSet<SourceSpecimen>();
        this.aliquotedSpcs = new HashSet<AliquotedSpecimen>();
        this.studyEventAttrs = new HashSet<StudyEventAttr>();
    }

    public StudyInfo(Study study, Long patientCount, Long ceventCount,
        List<ClinicInfo> clinicInfos,
        Set<SourceSpecimen> sourceSpcs,
        Set<AliquotedSpecimen> aliquotedSpcs,
        Set<StudyEventAttr> studyEventAttrs) {
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

    public Set<SourceSpecimen> getSourceSpecimens() {
        return sourceSpcs;
    }

    public void setSourceSpcs(Set<SourceSpecimen> sourceSpcs) {
        this.sourceSpcs = sourceSpcs;
    }

    public Set<AliquotedSpecimen> getAliquotedSpcs() {
        return aliquotedSpcs;
    }

    public Set<StudyEventAttr> getStudyEventAttrs() {
        return studyEventAttrs;
    }
}
