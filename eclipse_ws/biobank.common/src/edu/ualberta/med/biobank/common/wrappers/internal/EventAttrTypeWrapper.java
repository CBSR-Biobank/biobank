package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.EventAttrTypeBaseWrapper;
import edu.ualberta.med.biobank.model.EventAttrType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class EventAttrTypeWrapper extends EventAttrTypeBaseWrapper {

    public EventAttrTypeWrapper(WritableApplicationService appService,
        EventAttrType wrappedObject) {
        super(appService, wrappedObject);
    }

    public EventAttrTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<EventAttrType> o) {
        return 0;
    }

    public static Map<String, EventAttrTypeWrapper> getAllEventAttrTypesMap(
        WritableApplicationService appService) throws ApplicationException {
        List<EventAttrType> objects = appService.query(DetachedCriteria
            .forClass(EventAttrType.class));
        Map<String, EventAttrTypeWrapper> EventAttrTypeMap =
            new HashMap<String, EventAttrTypeWrapper>();
        for (EventAttrType pv : objects) {
            EventAttrTypeMap.put(pv.getName(), new EventAttrTypeWrapper(
                appService, pv));
        }
        return EventAttrTypeMap;
    }

}
