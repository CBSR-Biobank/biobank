package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchAliquotBaseWrapper;
import edu.ualberta.med.biobank.model.DispatchAliquot;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchAliquotWrapper extends DispatchAliquotBaseWrapper {

    public DispatchAliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchAliquotWrapper(WritableApplicationService appService,
        DispatchAliquot dsa) {
        super(appService, dsa);
    }

    @Override
    public int compareTo(ModelWrapper<DispatchAliquot> object) {
        if (object instanceof DispatchAliquotWrapper) {
            DispatchAliquotWrapper dsa = (DispatchAliquotWrapper) object;
            return getAliquot().compareTo(dsa.getAliquot());
        }
        return super.compareTo(object);
    }

    public String getStateDescription() {
        return DispatchAliquotState.getState(getState()).getLabel();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DispatchAliquotWrapper && object != null) {
            DispatchAliquotWrapper dsa = (DispatchAliquotWrapper) object;
            if (isNew() && dsa.isNew()) {
                return getAliquot() != null && dsa.getAliquot() != null
                    && getAliquot().equals(dsa.getAliquot())
                    && getDispatch() != null && dsa.getDispatch() != null
                    && getDispatch().equals(dsa.getDispatch());
            }
        }
        return super.equals(object);
    }
}
