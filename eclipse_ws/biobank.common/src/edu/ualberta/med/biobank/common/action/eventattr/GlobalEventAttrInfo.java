package edu.ualberta.med.biobank.common.action.eventattr;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.GlobalEventAttr;

public class GlobalEventAttrInfo implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    public GlobalEventAttr attr;
    public EventAttrTypeEnum type;

}
