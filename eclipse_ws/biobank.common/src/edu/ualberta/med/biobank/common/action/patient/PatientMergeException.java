package edu.ualberta.med.biobank.common.action.patient;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.wrappers.Messages;

public class PatientMergeException extends ActionException {

    private static final long serialVersionUID = 1L;

    public static enum ExceptionTypeEnum {
        DEFAULT, STUDY;
    }

    private ExceptionTypeEnum type;

    public PatientMergeException(ExceptionTypeEnum type) {
        super(""); //$NON-NLS-1$
        this.type = type;
    }

    @Override
    public String getMessage() {
        if (type == ExceptionTypeEnum.STUDY)
            return Messages.getString("PatientMergeException.study.error.msg"); //$NON-NLS-1$
        return Messages.getString("PatientMergeException.default.error.msg"); //$NON-NLS-1$
    }
}
