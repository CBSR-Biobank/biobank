package edu.ualberta.med.biobank.action.collectionEvent;

import java.io.Serializable;

import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.util.NotAProxy;

public class EventAttrInfo implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    public EventAttr attr;
    public EventAttrTypeEnum type;

}
