package edu.ualberta.med.biobank.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.AssertConstraintViolation;
import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.validator.constraint.Empty;

public class TestSite extends DbTest {
    @Test
    public void deleteWithContainers() {
        Transaction tx = session.beginTransaction();

        Container container = factory.createContainer();

        try {
            Site site = container.getSite();
            session.delete(site);
            tx.commit();
            Assert.fail("cannot delete site with containers");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Empty.class)
                .withAttr("property", "containers")
                .assertIn(e);
        }
    }

    @Test
    public void deleteWithContainerTypes() {
        Transaction tx = session.beginTransaction();

        ContainerType containerType = factory.createContainerType();

        try {
            Site site = containerType.getSite();
            session.delete(site);
            tx.commit();
            Assert.fail("cannot delete site with container types");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Empty.class)
                .withAttr("property", "containerTypes")
                .assertIn(e);
        }
    }

    @Test
    public void deleteWithProcessingEvents() {
        Transaction tx = session.beginTransaction();

        Site site = factory.createSite();
        ProcessingEvent event = factory.createProcessingEvent();

        if (!event.getCenter().equals(site)) {
            Assert.fail("unexpected center");
        }

        try {
            session.delete(site);
            tx.commit();
            Assert.fail("cannot delete site with container types");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Empty.class)
                .withAttr("property", "processingEvents")
                .assertIn(e);
        }
    }
}
