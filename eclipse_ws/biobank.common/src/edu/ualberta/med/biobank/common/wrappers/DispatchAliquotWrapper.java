package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

@SuppressWarnings("unused")
@Deprecated
public class DispatchAliquotWrapper {

    private static class DispatchAliquot {

    }

    public DispatchAliquotWrapper(WritableApplicationService appService) {
    }

    public DispatchAliquotWrapper(WritableApplicationService appService,
        DispatchAliquot dsa) {
    }

    public int compareTo(ModelWrapper<DispatchAliquot> object) {
        return 0;
    }

    public String getStateDescription() {
        return null;
    }
}
