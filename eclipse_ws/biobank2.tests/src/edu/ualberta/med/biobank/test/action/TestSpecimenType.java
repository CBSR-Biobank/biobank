package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeDeleteAction;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeGetAllAction;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeSaveAction;
import edu.ualberta.med.biobank.model.SpecimenType;

public class TestSpecimenType extends TestAction {

    //private static Logger log = LoggerFactory
    //    .getLogger(TestSpecimenBatchOp.class.getName());

    @Test
    public void getAction() {
        session.beginTransaction();

        // create two parent specimen types and three child specimen types,
        // the two parent specimen types each have the three child ones as children
        Set<SpecimenType> parentSpcTypes = new HashSet<SpecimenType>();
        parentSpcTypes.add(factory.createSpecimenType());
        parentSpcTypes.add(factory.createSpecimenType());

        Set<SpecimenType> childSpcTypes = new HashSet<SpecimenType>();
        childSpcTypes.add(factory.createSpecimenType());
        childSpcTypes.add(factory.createSpecimenType());
        childSpcTypes.add(factory.createSpecimenType());

        for (SpecimenType parentSpcType : parentSpcTypes) {
            parentSpcType.getChildSpecimenTypes().addAll(childSpcTypes);
        }

        session.getTransaction().commit();

        // specimen types are global, it's possible that other specimen types are in the
        // database before the test was started
        List<SpecimenType> actionResult = exec(new SpecimenTypeGetAllAction()).getList();
        for (SpecimenType spcType : parentSpcTypes) {
            Assert.assertTrue(actionResult.contains(spcType));
        }
        for (SpecimenType spcType : childSpcTypes) {
            Assert.assertTrue(actionResult.contains(spcType));
        }

        for (SpecimenType actionResultSpcType : actionResult) {
            if (parentSpcTypes.contains(actionResultSpcType)) {
                Assert.assertEquals(actionResultSpcType.getChildSpecimenTypes().size(),
                    childSpcTypes.size());
            }
        }
    }

    @Test
    public void removeChildTypes() {
        session.beginTransaction();

        // create two parent specimen types and three child specimen types,
        // the two parent specimen types each have the three child ones as children
        Set<SpecimenType> parentSpcTypes = new HashSet<SpecimenType>();
        parentSpcTypes.add(factory.createSpecimenType());
        parentSpcTypes.add(factory.createSpecimenType());

        Set<SpecimenType> childSpcTypes = new HashSet<SpecimenType>();
        childSpcTypes.add(factory.createSpecimenType());
        childSpcTypes.add(factory.createSpecimenType());
        childSpcTypes.add(factory.createSpecimenType());

        for (SpecimenType parentSpcType : parentSpcTypes) {
            parentSpcType.getChildSpecimenTypes().addAll(childSpcTypes);
        }

        session.getTransaction().commit();

        // delete the child types from the first parent
        SpecimenType parentSpcType =  parentSpcTypes.iterator().next();
        SpecimenTypeSaveAction saveAction = new SpecimenTypeSaveAction(parentSpcType.getName(),
            parentSpcType.getNameShort());
        saveAction.setId(parentSpcType.getId());
        exec(saveAction);

        session.clear();
        SpecimenType specimenType = (SpecimenType) session.load(
            SpecimenType.class, parentSpcType.getId());

        Assert.assertEquals(0, specimenType.getChildSpecimenTypes().size());

        // delete the child types from the second parent
        parentSpcTypes.remove(parentSpcType);
        parentSpcType =  parentSpcTypes.iterator().next();
        saveAction = new SpecimenTypeSaveAction(parentSpcType.getName(),
            parentSpcType.getNameShort());
        saveAction.setId(parentSpcType.getId());
        exec(saveAction);

        session.clear();
        specimenType = (SpecimenType) session.load(SpecimenType.class, parentSpcType.getId());

        Assert.assertEquals(0, specimenType.getChildSpecimenTypes().size());
    }

    @Test
    public void deleteChildTypes() {
        session.beginTransaction();

        // create two parent specimen types and three child specimen types,
        // the two parent specimen types each have the three child ones as children
        Set<SpecimenType> parentSpcTypes = new HashSet<SpecimenType>();
        parentSpcTypes.add(factory.createSpecimenType());
        parentSpcTypes.add(factory.createSpecimenType());

        SpecimenType childSpcType = factory.createSpecimenType();

        for (SpecimenType parentSpcType : parentSpcTypes) {
            parentSpcType.getChildSpecimenTypes().add(childSpcType);
        }

        session.getTransaction().commit();

        // attempt to delete the child type
        try {
            exec(new SpecimenTypeDeleteAction(childSpcType));
            Assert.fail("should not be allowed to delete child specimen type");
        } catch (ConstraintViolationException e) {
            // do nothing
        }

        // remove child type from parents
        for (SpecimenType parentSpcType : parentSpcTypes) {
            SpecimenTypeSaveAction saveAction = new SpecimenTypeSaveAction(parentSpcType.getName(),
                parentSpcType.getNameShort());
            saveAction.setId(parentSpcType.getId());
            exec(saveAction);
        }

        // check that child type was removed properly from former parents
        session.clear();
        for (SpecimenType parentSpcType : parentSpcTypes) {
            SpecimenType specimenType = (SpecimenType) session.load(SpecimenType.class,
                parentSpcType.getId());
            Assert.assertEquals(0, specimenType.getChildSpecimenTypes().size());
        }

        // now delete the child type
        exec(new SpecimenTypeDeleteAction(childSpcType));
    }

    @Test
    public void mangleTypes() {
        int size = exec(new SpecimenTypeGetAllAction()).getList().size();

        String name = getMethodNameR();
        final Integer typeId = exec(new SpecimenTypeSaveAction(name, name)).getId();

        Assert.assertEquals(size + 1, exec(new SpecimenTypeGetAllAction()).getList().size());

        exec(new SpecimenTypeDeleteAction(
            (SpecimenType) session.load(SpecimenType.class, typeId)));

        Assert.assertEquals(size, exec(new SpecimenTypeGetAllAction()).getList().size());
    }
}
