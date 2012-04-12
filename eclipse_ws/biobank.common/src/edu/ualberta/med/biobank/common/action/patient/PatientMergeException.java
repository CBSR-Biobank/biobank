package edu.ualberta.med.biobank.common.action.patient;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.i18n.LocalizedString;

@SuppressWarnings("nls")
public class PatientMergeException extends ActionException {
    private static final long serialVersionUID = 1L;

    public static enum ExceptionTypeEnum {
        DEFAULT,
        STUDY;
    }

    private ExceptionTypeEnum type;

    public PatientMergeException(ExceptionTypeEnum type) {
        super(LocalizedString.tr("Problem merging patients"));
        this.type = type;
    }

    @Override
    public String getMessage() {
        if (type == ExceptionTypeEnum.STUDY)
            return "";
        return "";
    }
}
