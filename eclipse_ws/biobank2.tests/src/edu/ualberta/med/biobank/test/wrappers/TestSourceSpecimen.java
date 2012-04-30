package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.peer.SourceSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.test.TestDatabase;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

@Deprecated
public class TestSourceSpecimen extends TestDatabase {

    @Test
    public void testCompareTo() throws Exception {
        SourceSpecimenTestWrapper otherWrapper = new SourceSpecimenTestWrapper(
            appService);

        SourceSpecimenWrapper normalWrapper = new SourceSpecimenWrapper(
            appService, otherWrapper.getWrappedObject());

        SourceSpecimenWrapper normalWrapper2 = new SourceSpecimenWrapper(
            appService);

        Assert.assertTrue(normalWrapper.compareTo(otherWrapper) == 0);
        Assert.assertTrue(normalWrapper.compareTo(normalWrapper2) == 0);
    }

    private static final class SourceSpecimenTestWrapper extends
        ModelWrapper<SourceSpecimen> {

        public SourceSpecimenTestWrapper(WritableApplicationService appService) {
            super(appService);
        }

        @Override
        public Property<Integer, ? super SourceSpecimen> getIdProperty() {
            return SourceSpecimenPeer.ID;
        }

        @Override
        protected List<Property<?, ? super SourceSpecimen>> getProperties() {
            return new ArrayList<Property<?, ? super SourceSpecimen>>();
        }

        @Override
        public Class<SourceSpecimen> getWrappedClass() {
            return SourceSpecimen.class;
        }
    }
}
