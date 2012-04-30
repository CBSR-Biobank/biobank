package edu.ualberta.med.biobank.test.wrappers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

@Deprecated
public class TestModelWrapper extends TestDatabase {

    class TestWrapper extends ModelWrapper<Object> {

        public TestWrapper(WritableApplicationService appService) {
            super(appService);
        }

        @Override
        protected List<Property<?, ? super Object>> getProperties() {
            return null;
        }

        @Override
        public Class<Object> getWrappedClass() {
            return null;
        }

        @Override
        public int compareTo(ModelWrapper<Object> o) {
            return 0;
        }

        @Override
        public Property<Integer, ? super Object> getIdProperty() {
            return null;
        }
    }

    class TestSiteWrapper extends ModelWrapper<Site> {

        public TestSiteWrapper(WritableApplicationService appService) {
            super(appService);
        }

        public TestSiteWrapper(WritableApplicationService appService, Site site) {
            super(appService, site);
        }

        @Override
        protected List<Property<?, ? super Site>> getProperties() {
            return null;
        }

        @Override
        public Class<Site> getWrappedClass() {
            return Site.class;
        }

        @Override
        public int compareTo(ModelWrapper<Site> o) {
            return 0;
        }

        @Override
        public Property<Integer, ? super Site> getIdProperty() {
            return SitePeer.ID;
        }
    }

    @Test
    public void testConstructor() throws Exception {
        try {
            new TestWrapper(appService);
            Assert.fail("should not be allowed to create this wrapper");
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPropertyChangeListener() throws Exception {
        TestSiteWrapper wrapper = new TestSiteWrapper(appService);
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
            }
        };

        try {
            wrapper.addPropertyChangeListener("propertyX", listener);
            Assert
                .fail("should not be allowed to create listener for non exisiting property");
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }

        SiteWrapper site = SiteHelper.addSite("testPropertyChangeListener");
        site.addPropertyChangeListener("name", listener);
        site.removePropertyChangeListener(listener);
    }

    @Test
    public void testGetId() throws Exception {
        TestSiteWrapper wrapper = new TestSiteWrapper(appService);
        Assert.assertNull(wrapper.getId());
    }

    @Test
    public void testGetAppService() throws Exception {
        TestSiteWrapper wrapper = new TestSiteWrapper(appService);
        Assert.assertTrue(appService == wrapper.getAppService());
    }

    @Test
    public void testDelete() throws Exception {
        TestSiteWrapper wrapper = new TestSiteWrapper(appService);

        try {
            wrapper.delete();
            Assert
                .fail("should fail since there is no such object in database");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testEquals() throws Exception {
        TestSiteWrapper wrapper = new TestSiteWrapper(appService);
        SiteWrapper site = SiteHelper.addSite("testEquals");
        Assert.assertFalse(site.equals(null));
        Assert.assertFalse(site.equals(wrapper));

        SiteWrapper site2 = SiteHelper.addSite("testEquals_2");
        Assert.assertFalse(site.equals(site2));

        // call to equals should handle null IDs
        TestSiteWrapper wrapper2 = new TestSiteWrapper(appService);
        Assert.assertFalse(wrapper.equals(wrapper2));

        TestSiteWrapper wrapper3 = new TestSiteWrapper(appService,
            wrapper.getWrappedObject());
        Assert.assertTrue(wrapper.equals(wrapper3));
    }

    @Test
    public void testPersist() throws Exception {
        // test insert and update into database
        SiteWrapper site = SiteHelper.addSite("testEquals");
        site.setName(Utils.getRandomString(10, 15));
        site.persist();
    }

    @Test
    public void testReset() throws Exception {
        SiteWrapper wrapper = new SiteWrapper(appService);
        wrapper.reset();

        SiteWrapper site = SiteHelper.addSite("testEquals");
        site.setName(Utils.getRandomString(10, 15));
        site.reset();
    }

}
