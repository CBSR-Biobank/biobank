package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.AbstractContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.DispatchContainer;
import edu.ualberta.med.biobank.model.DispatchPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DispatchPositionWrapper extends
    AbstractPositionWrapper<DispatchPosition> {

    private DispatchContainerWrapper container;
    private AliquotWrapper aliquot;

    public DispatchPositionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchPositionWrapper(WritableApplicationService appService,
        DispatchPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public Class<DispatchPosition> getWrappedClass() {
        return DispatchPosition.class;
    }

    @Override
    public int compareTo(ModelWrapper<DispatchPosition> o) {
        return 0;
    }

    public void setContainer(DispatchContainerWrapper container) {
        if (container == null) {
            this.container = null;
        } else {
            this.container = container;
        }
        DispatchContainer oldContainer = wrappedObject.getContainer();
        wrappedObject.setContainer(container.getWrappedObject());
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            container.getWrappedObject());
    }

    private DispatchContainerWrapper getContainer() {
        if (container == null) {
            DispatchContainer c = wrappedObject.getContainer();
            if (c == null) {
                return null;
            }
            container = new DispatchContainerWrapper(appService, c);
        }
        return container;
    }

    @Override
    public DispatchContainerWrapper getParent() {
        return getContainer();
    }

    @Override
    public void setParent(AbstractContainerWrapper<?> parent) {
        assert parent instanceof DispatchContainerWrapper;
        if (parent instanceof DispatchContainerWrapper) {
            setContainer((DispatchContainerWrapper) parent);
        }
    }

    @Override
    protected void checkObjectAtPosition() throws BiobankCheckException,
        ApplicationException {
        AbstractContainerWrapper<?> parent = getParent();
        if (parent != null) {
            // do a hql query because parent might need a reload - but if we are
            // in the middle of parent.persist, don't want to do that !
            HQLCriteria criteria = new HQLCriteria(
                "from " + DispatchPosition.class.getName()
                    + " where container.id=? and row=? and col=?",
                Arrays.asList(new Object[] { parent.getId(), getRow(), getCol() }));
            List<AliquotPosition> positions = appService.query(criteria);
            if (positions.size() == 0) {
                return;
            }
            AliquotPositionWrapper aliquotPosition = new AliquotPositionWrapper(
                appService, positions.get(0));
            if (!aliquotPosition.getAliquot().equals(getAliquot())) {
                throw new BiobankCheckException("Position " + getRow() + ":"
                    + getCol() + " in container " + getParent().toString()
                    + " is not available.");
            }
        }
    }

    public void setAliquot(AliquotWrapper aliquot) {
        this.aliquot = aliquot;
        Aliquot oldAliquot = wrappedObject.getAliquot();
        Aliquot newAliquot = null;
        if (aliquot != null) {
            newAliquot = aliquot.getWrappedObject();
        }
        wrappedObject.setAliquot(newAliquot);
        propertyChangeSupport.firePropertyChange("aliquot", oldAliquot,
            newAliquot);
    }

    public AliquotWrapper getAliquot() {
        if (aliquot == null) {
            Aliquot a = wrappedObject.getAliquot();
            if (a == null)
                return null;
            aliquot = new AliquotWrapper(appService, a);
        }
        return aliquot;
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

}
