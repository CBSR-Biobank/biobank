package edu.ualberta.med.biobank.action.info;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.model.Study;

/**
 * 
 * @author jferland
 * 
 */
public class StudyCountInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final Study study;
    private final Long patientCount;
    private final Long collectionEventCount;

    public StudyCountInfo(Study study, Long patientCount,
        Long collectionEventCount) {
        this.study = study;
        this.patientCount = patientCount;
        this.collectionEventCount = collectionEventCount;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((study == null) ? 0 : study.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        StudyCountInfo other = (StudyCountInfo) obj;
        if (study == null) {
            if (other.study != null) return false;
        } else if (!study.equals(other.study)) return false;
        return true;
    }
}
