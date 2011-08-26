package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.SourceSpecimenBaseWrapper;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SourceSpecimenWrapper extends SourceSpecimenBaseWrapper {
    public SourceSpecimenWrapper(WritableApplicationService appService,
        SourceSpecimen wrappedObject) {
        super(appService, wrappedObject);
    }

    public SourceSpecimenWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<SourceSpecimen> o) {
        if (o instanceof SourceSpecimenWrapper) {
            return getSpecimenType().compareTo(
                ((SourceSpecimenWrapper) o).getSpecimenType());
        }
        return 0;
    }
}
