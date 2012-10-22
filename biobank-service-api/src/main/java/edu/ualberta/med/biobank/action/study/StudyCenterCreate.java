package edu.ualberta.med.biobank.action.study;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.EmptyResult;

public class StudyCenterCreate
    implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private Integer studyId;
    private Integer centerId;

    public StudyCenterCreate(Integer studyId, Integer centerId) {
        this.studyId = studyId;
        this.centerId = centerId;
    }
}
