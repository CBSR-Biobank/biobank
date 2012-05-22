package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class TestContainerPosition extends DbTest {
    @Test
    public void nullParentContainer() {
        factory.createTopContainer();
        ContainerPosition pos = factory.createContainer().getPosition();
        pos.setParentContainer(null);

        try {
            session.update(pos);
            session.flush();
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(pos)
                .withPropertyPath("parentContainer")
                .assertIn(e);
        }
    }

    @Test
    public void nullContainer() {
        factory.createTopContainer();
        ContainerPosition pos = factory.createContainer().getPosition();
        pos.setContainer(null);

        try {
            session.update(pos);
            session.flush();
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(pos)
                .withPropertyPath("container")
                .assertIn(e);
        }
    }

    @Test
    public void duplicatePositionAndParent() {
        factory.createTopContainer();
        ContainerPosition pos1 = factory.createContainer().getPosition();
        ContainerPosition pos2 = factory.createContainer().getPosition();
        pos2.setRow(pos1.getRow());
        pos2.setCol(pos1.getCol());

        try {
            session.update(pos2);
            session.flush();
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withAnnotationClass(Unique.class)
                .withRootBean(pos2)
                .withAttr("properties",
                    new String[] { "parentContainer", "row", "col" })
                .assertIn(e);
        }
    }

    @Test
    public void duplicatePositionDifferentParent() {
        factory.createTopContainer();
        ContainerPosition pos1 = factory.createContainer().getPosition();

        factory.createTopContainer(); // new default parent
        ContainerPosition pos2 = factory.createContainer().getPosition();
        pos2.setRow(pos1.getRow());
        pos2.setCol(pos1.getCol());

        try {
            session.update(pos2);
            session.flush();
        } catch (Exception e) {
            Assert.fail("should be allowed to have the same position for two" +
                " different parent containers.");
        }
    }
}
