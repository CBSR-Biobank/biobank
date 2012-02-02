package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenAttrTypeBaseWrapper;
import edu.ualberta.med.biobank.model.SpecimenAttrType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SpecimenAttrTypeWrapper extends SpecimenAttrTypeBaseWrapper {

    public SpecimenAttrTypeWrapper(WritableApplicationService appService,
        SpecimenAttrType wrappedObject) {
        super(appService, wrappedObject);
    }

    public SpecimenAttrTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<SpecimenAttrType> o) {
        return 0;
    }

    public static Map<String, SpecimenAttrTypeWrapper> getAllSpecimenAttrTypesMap(
        WritableApplicationService appService) throws ApplicationException {
        List<SpecimenAttrType> objects = appService.search(SpecimenAttrType.class,
            new SpecimenAttrType());
        Map<String, SpecimenAttrTypeWrapper> SpecimenAttrTypeMap = new HashMap<String, SpecimenAttrTypeWrapper>();
        for (SpecimenAttrType pv : objects) {
            SpecimenAttrTypeMap.put(pv.getName(), new SpecimenAttrTypeWrapper(
                appService, pv));
        }
        return SpecimenAttrTypeMap;
    }

}
