package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.util.RequestAliquotState;
import edu.ualberta.med.biobank.common.wrappers.base.RequestAliquotBaseWrapper;
import edu.ualberta.med.biobank.model.RequestAliquot;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RequestAliquotWrapper extends RequestAliquotBaseWrapper {

    public RequestAliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RequestAliquotWrapper(WritableApplicationService appService,
        RequestAliquot dsa) {
        super(appService, dsa);
    }

    @Override
    public int compareTo(ModelWrapper<RequestAliquot> object) {
        if (object instanceof RequestAliquotWrapper) {
            RequestAliquotWrapper dsa = (RequestAliquotWrapper) object;
            return getAliquot().compareTo(dsa.getAliquot());
        }
        return super.compareTo(object);
    }

    public String getStateDescription() {
        return RequestAliquotState.getState(getState()).getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RequestAliquotWrapper && object != null) {
            RequestAliquotWrapper dsa = (RequestAliquotWrapper) object;
            if (isNew() && dsa.isNew()) {
                return getAliquot() != null && dsa.getAliquot() != null
                    && getAliquot().equals(dsa.getAliquot())
                    && getAliquot() != null && dsa.getAliquot() != null
                    && getAliquot().equals(dsa.getAliquot());
            }
        }
        return super.equals(object);
    }
}
