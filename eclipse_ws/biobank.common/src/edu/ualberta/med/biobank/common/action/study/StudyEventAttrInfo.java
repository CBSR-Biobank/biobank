package edu.ualberta.med.biobank.common.action.study;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class StudyEventAttrInfo implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    public StudyEventAttr attr;
    public EventAttrTypeEnum type;

}
