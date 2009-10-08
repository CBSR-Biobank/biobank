package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
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
        return (String[]) properties.toArray();
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public Class<SamplePosition> getWrappedClass() {
        return SamplePosition.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub

    }

    public void setSample(Sample sample) {
        Sample oldSample = wrappedObject.getSample();
        wrappedObject.setSample(sample);
        propertyChangeSupport.firePropertyChange("sample", oldSample, sample);
    }

    public void setSample(SampleWrapper sample) {
        setSample(sample.wrappedObject);
    }

    public SampleWrapper getSample() {
        return new SampleWrapper(appService, wrappedObject.getSample());
    }

    public void setContainer(Container container) {
        Container oldContainer = wrappedObject.getContainer();
        wrappedObject.setContainer(container);
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            container);
    }

    public void setContainer(ContainerWrapper container) {
        setContainer(container.wrappedObject);
    }

    public ContainerWrapper getContainer() {
        return new ContainerWrapper(appService, wrappedObject.getContainer());
    }

    @Override
    public int compareTo(ModelWrapper<SamplePosition> o) {
        return 0;
    }

}
