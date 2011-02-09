package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.DispatchAliquotPeer;
import edu.ualberta.med.biobank.model.DispatchAliquot;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchAliquotWrapper extends
    DispatchItemWrapper<DispatchAliquot> {

    public DispatchAliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchAliquotWrapper(WritableApplicationService appService,
        DispatchAliquot dispatchAliquot) {
        super(appService, dispatchAliquot);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return DispatchAliquotPeer.PROP_NAMES;
    }

    @Override
    public Class<DispatchAliquot> getWrappedClass() {
        return DispatchAliquot.class;
    }

    public AliquotWrapper getAliquot() {
        return getWrappedProperty(DispatchAliquotPeer.ALIQUOT,
            AliquotWrapper.class);
    }

    public void setAliquot(AliquotWrapper aliquot) {
        setWrappedProperty(DispatchAliquotPeer.ALIQUOT, aliquot);
    }

    @Override
    public DispatchWrapper getDispatch() {
        return getWrappedProperty(DispatchAliquotPeer.DISPATCH,
            DispatchWrapper.class);
    }

    @Override
    public void setDispatch(DispatchWrapper dispatch) {
        setWrappedProperty(DispatchAliquotPeer.DISPATCH, dispatch);
    }

    @Override
    public int compareTo(ModelWrapper<DispatchAliquot> object) {
        if (object instanceof DispatchAliquotWrapper) {
            DispatchAliquotWrapper da = (DispatchAliquotWrapper) object;
            return getAliquot().compareTo(da.getAliquot());
        }
        return super.compareTo(object);
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

    @Override
    public Object getItem() {
        return getAliquot();
    }

    @Override
    public void setItem(Object item) {
        if (item instanceof AliquotWrapper) {
            setAliquot((AliquotWrapper) item);
        }
    }
}
