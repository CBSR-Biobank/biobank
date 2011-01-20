package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Container;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AliquotPositionWrapper extends
    AbstractPositionWrapper<AliquotPosition> {

    private AliquotWrapper aliquot;
    private ContainerWrapper container;

    public AliquotPositionWrapper(WritableApplicationService appService,
        AliquotPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    public AliquotPositionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        List<String> properties = new ArrayList<String>(Arrays.asList(super
            .getPropertyChangeNames()));
        properties.add("aliquot");
        properties.add("container");
        return properties.toArray(new String[properties.size()]);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {

    }

    @Override
    public Class<AliquotPosition> getWrappedClass() {
        return AliquotPosition.class;
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
    public void setRow(Integer row) {
        if (row == null || !row.equals(getRow())) {
            updatePositionString();
        }

        super.setRow(row);
    }

    @Override
    public void setCol(Integer col) {
        if (col == null || !col.equals(getCol())) {
            updatePositionString();
        }

        super.setCol(col);
    }

    private void updatePositionString() {
        ContainerWrapper container = getContainer();
        if (container != null && getRow() != null && getCol() != null) {
            ContainerTypeWrapper containerType = container.getContainerType();
            if (containerType != null) {
                String positionString = containerType
                    .getPositionString(new RowColPos(getRow(), getCol()));
                wrappedObject.setPositionString(positionString);
            }
        }
    }

    private void setContainer(ContainerWrapper container) {
        this.container = container;
        Container oldContainer = wrappedObject.getContainer();
        Container newContainer = null;
        if (container != null) {
            newContainer = container.getWrappedObject();
        }
        wrappedObject.setContainer(newContainer);
        updatePositionString();
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            newContainer);
    }

    private ContainerWrapper getContainer() {
        if (container == null) {
            Container c = wrappedObject.getContainer();
            if (c == null) {
                return null;
            }
            container = new ContainerWrapper(appService, c);
        }
        return container;
    }

    @Override
    public int compareTo(ModelWrapper<AliquotPosition> o) {
        return 0;
    }

    @Override
    public ContainerWrapper getParent() {
        return getContainer();
    }

    @Override
    public void setParent(ContainerWrapper parent) {
        setContainer(parent);
    }

    @Override
    protected void checkObjectAtPosition() throws BiobankCheckException,
        ApplicationException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            // do a hql query because parent might need a reload - but if we are
            // in the middle of parent.persist, don't want to do that !
            HQLCriteria criteria = new HQLCriteria(
                "from " + AliquotPosition.class.getName()
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

    @Override
    public void resetInternalFields() {
        aliquot = null;
        container = null;
    }
}
