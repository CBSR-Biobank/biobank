package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SamplePositionWrapper extends
    AbstractPositionWrapper<SamplePosition> {

    public SamplePositionWrapper(WritableApplicationService appService,
        SamplePosition wrappedObject) {
        super(appService, wrappedObject);
    }

    public SamplePositionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        List<String> properties = new ArrayList<String>(Arrays.asList(super
            .getPropertyChangesNames()));
        properties.add("sample");
        properties.add("container");
        return properties.toArray(new String[properties.size()]);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {

    }

    @Override
    public Class<SamplePosition> getWrappedClass() {
        return SamplePosition.class;
    }

    public void setSample(Sample sample) {
        Sample oldSample = wrappedObject.getSample();
        wrappedObject.setSample(sample);
        propertyChangeSupport.firePropertyChange("sample", oldSample, sample);
    }

    public void setSample(SampleWrapper sample) {
        setSample(sample.getWrappedObject());
    }

    public SampleWrapper getSample() {
        Sample sample = wrappedObject.getSample();
        if (sample == null) {
            return null;
        }
        return new SampleWrapper(appService, sample);
    }

    private void setContainer(Container container) {
        Container oldContainer = wrappedObject.getContainer();
        wrappedObject.setContainer(container);
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            container);
    }

    private void setContainer(ContainerWrapper container) {
        setContainer(container.getWrappedObject());
    }

    private ContainerWrapper getContainer() {
        Container container = wrappedObject.getContainer();
        if (container == null) {
            return null;
        }
        return new ContainerWrapper(appService, container);
    }

    @Override
    public int compareTo(ModelWrapper<SamplePosition> o) {
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
            HQLCriteria criteria = new HQLCriteria("from "
                + SamplePosition.class.getName()
                + " where container.id=? and row=? and col=?", Arrays
                .asList(new Object[] { parent.getId(), getRow(), getCol() }));
            List<SamplePosition> positions = appService.query(criteria);
            if (positions.size() == 0) {
                return;
            }
            SamplePositionWrapper samplePosition = new SamplePositionWrapper(
                appService, positions.get(0));
            if (!samplePosition.getSample().equals(getSample())) {
                throw new BiobankCheckException("Position " + getRow() + ":"
                    + getCol() + " in container " + getParent().toString()
                    + " is not available.");
            }
        }

    }

}
