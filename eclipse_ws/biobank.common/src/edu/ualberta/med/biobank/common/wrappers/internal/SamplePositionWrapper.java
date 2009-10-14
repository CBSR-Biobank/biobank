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
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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
    protected void deleteChecks() throws BiobankCheckException, Exception {

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

    public void setContainer(Container container) {
        Container oldContainer = wrappedObject.getContainer();
        wrappedObject.setContainer(container);
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            container);
    }

    public void setContainer(ContainerWrapper container) {
        setContainer(container.getWrappedObject());
    }

    public ContainerWrapper getContainer() {
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

}
