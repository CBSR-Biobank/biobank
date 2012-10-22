package edu.ualberta.med.biobank.action.study;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.IdResult;

public class StudyCreate
    implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
}
