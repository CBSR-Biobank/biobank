package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.base.RequestSpecimenBaseWrapper;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RequestSpecimenWrapper extends RequestSpecimenBaseWrapper
    implements ItemWrapper {

    public RequestSpecimenWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestSpecimenWrapper(WritableApplicationService appService,
        RequestSpecimen dsa) {
        super(appService, dsa);
    }

    @Override
    public int compareTo(ModelWrapper<RequestSpecimen> object) {
        if (object instanceof RequestSpecimenWrapper) {
            RequestSpecimenWrapper dsa = (RequestSpecimenWrapper) object;
            return getSpecimen().compareTo(dsa.getSpecimen());
        }
        return super.compareTo(object);
    }

    @Override
    public String getStateDescription() {
        return RequestSpecimenState.getState(getState()).getLabel();
    }

    @Override
    public RequestSpecimenState getSpecimenState() {
        return RequestSpecimenState.getState(getState());
    }

    public void setState(RequestSpecimenState state) {
        setState(state.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RequestSpecimenWrapper && object != null) {
            RequestSpecimenWrapper dsa = (RequestSpecimenWrapper) object;
            if (isNew() && dsa.isNew()) {
                return getSpecimen() != null && dsa.getSpecimen() != null
                    && getSpecimen().equals(dsa.getSpecimen())
                    && getSpecimen() != null && dsa.getSpecimen() != null
                    && getSpecimen().equals(dsa.getSpecimen());
            }
        }
        return super.equals(object);
    }
}
