package edu.ualberta.med.biobank.action.eventattr;

import java.io.Serializable;

import edu.ualberta.med.biobank.action.collectionEvent.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.util.NotAProxy;

public class GlobalEventAttrInfo implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    public GlobalEventAttr attr;
    public EventAttrTypeEnum type;

}
