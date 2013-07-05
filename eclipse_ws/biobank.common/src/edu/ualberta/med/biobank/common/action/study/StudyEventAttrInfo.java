package edu.ualberta.med.biobank.common.action.study;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.eventattr.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class StudyEventAttrInfo implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    public StudyEventAttr attr;
    public EventAttrTypeEnum type;

    public String[] getStudyEventAttrPermissible() {
        String joinedPossibleValues = attr.getPermissible();
        if (joinedPossibleValues == null)
            return null;
        return joinedPossibleValues.split(";"); //$NON-NLS-1$
    }

}
