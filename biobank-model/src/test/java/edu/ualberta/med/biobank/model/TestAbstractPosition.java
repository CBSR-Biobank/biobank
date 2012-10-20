package edu.ualberta.med.biobank.model;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.center.ContainerType;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.AssertConstraintViolation;
import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.validator.constraint.model.InBounds;
import edu.ualberta.med.biobank.validator.constraint.model.impl.InBoundsValidator;

public class TestAbstractPosition extends DbTest {
    @Test
    public void nullRow() {
        Specimen specimen = factory.createPositionedSpecimen();
        AbstractPosition pos = specimen.getSpecimenPosition();
        pos.setRow(null);

        try {
            session.update(pos);
            session.flush();
            Assert.fail("row should not be allowed to be null");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(pos)
                .withPropertyPath("row")
                .assertIn(e);
        }
    }

    @Test
    public void negativeRow() {
        Specimen specimen = factory.createPositionedSpecimen();
        AbstractPosition pos = specimen.getSpecimenPosition();
        pos.setRow(-1);

        try {
            session.update(pos);
            session.flush();
            Assert.fail("row should not be allowed to be negative");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Min.class)
                .withRootBean(pos)
                .withPropertyPath("row")
                .assertIn(e);
        }
    }

    @Test
    public void nullCol() {
        Specimen specimen = factory.createPositionedSpecimen();
        AbstractPosition pos = specimen.getSpecimenPosition();
        pos.setCol(-1);

        try {
            session.update(pos);
            session.flush();
            Assert.fail("col should not be allowed to be negative");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Min.class)
                .withRootBean(pos)
                .withPropertyPath("col")
                .assertIn(e);
        }
    }

    @Test
    public void outOfBoundsRow() {
        Specimen specimen = factory.createPositionedSpecimen();
        AbstractPosition pos = specimen.getSpecimenPosition();

        ContainerType ct = pos.getHoldingContainer().getContainerType();
        Capacity capacity = ct.getCapacity();

        pos.setRow(capacity.getRowCapacity() + 1);
        pos.setCol(0);

        try {
            session.update(pos);
            session.flush();
            Assert.fail("row should not exceed container's row capacity");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(InBounds.class)
                .withTemplate(InBoundsValidator.OUT_OF_BOUNDS)
                .assertIn(e);
        }
    }

    @Test
    public void outOfBoundsColumn() {
        Specimen specimen = factory.createPositionedSpecimen();
        AbstractPosition pos = specimen.getSpecimenPosition();

        ContainerType ct = pos.getHoldingContainer().getContainerType();
        Capacity capacity = ct.getCapacity();

        pos.setRow(0);
        pos.setCol(capacity.getColCapacity() + 1);

        try {
            session.update(pos);
            session.flush();
            Assert.fail("column should not exceed container's column capacity");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(InBounds.class)
                .withTemplate(InBoundsValidator.OUT_OF_BOUNDS)
                .assertIn(e);
        }
    }
}
