package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.DispatchAliquotPeer;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.model.DispatchAliquot;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class DispatchAliquotWrapper extends ModelWrapper<DispatchAliquot> {

    public DispatchAliquotWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchAliquotWrapper(WritableApplicationService appService,
        DispatchAliquot dsa) {
        super(appService, dsa);
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

    public DispatchWrapper getDispatch() {
        return getWrappedProperty(DispatchAliquotPeer.DISPATCH,
            DispatchWrapper.class);
    }

    public void setDispatch(DispatchWrapper dispatch) {
        setWrappedProperty(DispatchAliquotPeer.DISPATCH, dispatch);
    }

    public Integer getState() {
        return getProperty(DispatchAliquotPeer.STATE);
    }

    public void setState(Integer state) {
        setProperty(DispatchAliquotPeer.STATE, state);
    }

    public String getComment() {
        return getProperty(DispatchAliquotPeer.COMMENT);
    }

    public void setComment(String comment) {
        setProperty(DispatchAliquotPeer.COMMENT, comment);
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
