package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.RequestSpecimenBaseWrapper;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import gov.nih.nci.system.applicationservice.WritableApplicationService;


/**
 *
 * Code Changes -
 * 		1> Remove the setState method for 2 reasons -
 * 				a> Results in an infinite loop and an error not thrown back to client but results in incomplete functional behavior
 * 				b> The parent class provides the implementation
 *
 * @author OHSDEV
 *
 */
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
        return getState().getLabel();
    }

    @Override
    public RequestSpecimenState getSpecimenState() {
        return getState();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RequestSpecimenWrapper) {
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
