package edu.ualberta.med.biobank.model.study;

import java.util.Date;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.ConstraintViolationAssertion;
import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.provider.EntityProcessor;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class CollectionEventTest
    extends DbTest {
    @Test
    public void persist() {
        getProvider(CollectionEvent.class).create();
        session.flush();
    }

    @Test
    public void patientNotNull() {
        persistEntity(CollectionEvent.class,
            new EntityProcessor<CollectionEvent>() {
                @Override
                public void process(CollectionEvent entity) {
                    entity.setPatient(null);
                }
            },
            new ConstraintViolationAssertion()
                .withAnnotationClass(NotNull.class)
                .withPropertyPath("patient"));
    }

    @Test
    public void typeNotNull() {
        persistEntity(CollectionEvent.class,
            new EntityProcessor<CollectionEvent>() {
                @Override
                public void process(CollectionEvent entity) {
                    entity.setType(null);
                }
            },
            new ConstraintViolationAssertion()
                .withAnnotationClass(NotNull.class)
                .withPropertyPath("type"));
    }

    @Test
    public void visitNumberNotNull() {
        persistEntity(CollectionEvent.class,
            new EntityProcessor<CollectionEvent>() {
                @Override
                public void process(CollectionEvent entity) {
                    entity.setVisitNumber(null);
                }
            },
            new ConstraintViolationAssertion()
                .withAnnotationClass(NotNull.class)
                .withPropertyPath("visitNumber"));
    }

    @Test
    public void visitNumberMin() {
        final Integer[] invalidValues = new Integer[] { -1, 0 };

        for (final Integer invalidValue : invalidValues) {
            persistEntity(CollectionEvent.class,
                new EntityProcessor<CollectionEvent>() {
                    @Override
                    public void process(CollectionEvent entity) {
                        entity.setVisitNumber(invalidValue);
                    }
                },
                new ConstraintViolationAssertion()
                    .withAnnotationClass(Min.class)
                    .withInvalidValue(invalidValue)
                    .withPropertyPath("visitNumber"));
        }
    }

    @Test
    public void timeDoneNotNull() {
        persistEntity(CollectionEvent.class,
            new EntityProcessor<CollectionEvent>() {
                @Override
                public void process(CollectionEvent entity) {
                    entity.setTimeDone(null);
                }
            },
            new ConstraintViolationAssertion()
                .withAnnotationClass(NotNull.class)
                .withPropertyPath("timeDone"));
    }

    @Test
    public void timeDonePast() {
        final long future = System.currentTimeMillis() + 1000;
        persistEntity(CollectionEvent.class,
            new EntityProcessor<CollectionEvent>() {
                @Override
                public void process(CollectionEvent entity) {
                    entity.setTimeDone(new Date(future));
                }
            },
            new ConstraintViolationAssertion()
                .withAnnotationClass(Past.class)
                .withPropertyPath("timeDone"));
    }

    @Test
    public void naturalIdUnique() {
        CollectionEvent ce1 = getProvider(CollectionEvent.class).create();
        CollectionEvent ce2 = getProvider(CollectionEvent.class).create();

        Assert.assertSame("must have the same patients",
            ce1.getPatient(), ce2.getPatient());

        Assert.assertSame("must have the same type",
            ce1.getType(), ce2.getType());

        try {
            ce2.setVisitNumber(ce1.getVisitNumber());
            session.update(ce2);
            session.flush();
            Assert.fail("cannot have duplicate visit number for same patient");
        } catch (ConstraintViolationException e) {
            new ConstraintViolationAssertion()
                .withAnnotationClass(Unique.class)
                .withRootBean(ce2)
                .withAttr("properties",
                    new String[] { "patient", "type", "visitNumber" })
                .assertIn(e);
        }

        // if the type is different, the visit number can be the same
        ce2.setType(getProvider(CollectionEventType.class).create());
        session.update(ce2);
        session.flush();
    }

    @Test
    public void studiesMatch() {
        // check that the Patient and CollectionEventType are in the same Study
        // this could be done at insert time and also at update time only if the
        // properties have changed, to avoid lazy-loading
        // TODO: move to action tests
    }

    @Test
    public void deleteWithSpecimens() {
        SpecimenCollectionEvent sce =
            getProvider(SpecimenCollectionEvent.class).get();

        session.flush();
        CollectionEvent ce = sce.getCollectionEvent();

        try {
            session.delete(ce);
            session.flush();
        } catch (ConstraintViolationException e) {
            new ConstraintViolationAssertion()
                .withAnnotationClass(NotUsed.class)
                .withRootBean(ce)
                .withAttr("by", SpecimenCollectionEvent.class)
                .withAttr("property", "collectionEvent")
                .assertIn(e);
        }
    }
}
