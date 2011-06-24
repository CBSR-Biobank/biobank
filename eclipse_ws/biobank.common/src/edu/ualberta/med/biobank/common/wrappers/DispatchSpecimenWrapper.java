package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchSpecimenBaseWrapper;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchSpecimenWrapper extends DispatchSpecimenBaseWrapper
    implements ItemWrapper {

    public DispatchSpecimenWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchSpecimenWrapper(WritableApplicationService appService,
        DispatchSpecimen DispatchSpecimen) {
        super(appService, DispatchSpecimen);
    }

    @Override
    public int compareTo(ModelWrapper<DispatchSpecimen> object) {
        if (object instanceof DispatchSpecimenWrapper) {
            DispatchSpecimenWrapper da = (DispatchSpecimenWrapper) object;
            return getSpecimen().compareTo(da.getSpecimen());
        }
        return super.compareTo(object);
    }

    // TODO: uhhhh, don't override equals?
    @Override
    public boolean equals(Object object) {
        if (object instanceof DispatchSpecimenWrapper && object != null) {
            DispatchSpecimenWrapper dsa = (DispatchSpecimenWrapper) object;
            if (isNew() && dsa.isNew()) {
                return getSpecimen() != null && dsa.getSpecimen() != null
                    && getSpecimen().equals(dsa.getSpecimen())
                    && getDispatch() != null && dsa.getDispatch() != null
                    && getDispatch().equals(dsa.getDispatch());
            }
        }
        return super.equals(object);
    }

    @Override
    public DispatchSpecimenState getSpecimenState() {
        return DispatchSpecimenState.getState(getState());
    }

    public void setDispatchSpecimenState(DispatchSpecimenState ds) {
        setState(ds.getId());
    }

    @Override
    public String getStateDescription() {
        return DispatchSpecimenState.getState(getState()).getLabel();
    }
}
