package edu.ualberta.med.biobank.common.action.patient;

import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;

public class PatientMergeException extends ActionCheckException {

    private static final long serialVersionUID = 1L;

    public static enum ExceptionTypeEnum {
        DEFAULT,
        STUDY;
    }

    private ExceptionTypeEnum type;

    public PatientMergeException(ExceptionTypeEnum type) {
        super("");
        this.type = type;
    }

    @Override
    public String getMessage() {
        if (type == ExceptionTypeEnum.STUDY)
            return "";
        return "";
    }
}
