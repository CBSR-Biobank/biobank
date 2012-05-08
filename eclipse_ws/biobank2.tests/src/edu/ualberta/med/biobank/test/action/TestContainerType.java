package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Query;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeDeleteAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;

public class TestContainerType extends ActionTest {

    private String name;

    private Integer siteId;

    private ContainerTypeSaveAction containerTypeSaveAction;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();

        siteId = exec(SiteHelper.getSaveAction(
            name, name, ActivityStatus.ACTIVE)).getId();

        containerTypeSaveAction = ContainerTypeHelper.getSaveAction(
            "FREEZER_3x10", "FR3x10", siteId, true, 3, 10,
            getContainerLabelingSchemes().get("CBSR 2 char alphabetic")
                .getId(), getR().nextDouble());
    }

    @Test
    public void checkGetAction() throws Exception {
        Integer containerTypeId =
            exec(containerTypeSaveAction).getId();
        ContainerTypeInfo topContainerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));

        Assert.assertEquals("FREEZER_3x10",
            topContainerTypeInfo.getContainerType().getName());
        Assert.assertEquals(ActivityStatus.ACTIVE, topContainerTypeInfo
            .getContainerType()
            .getActivityStatus());
        Assert.assertEquals(3, topContainerTypeInfo.getContainerType()
            .getCapacity().getRowCapacity().intValue());
        Assert.assertEquals(10, topContainerTypeInfo.getContainerType()
            .getCapacity().getColCapacity().intValue());
        Assert.assertEquals(0, topContainerTypeInfo.getContainerType()
            .getChildContainerTypes().size());
        Assert.assertEquals(0, topContainerTypeInfo.getContainerType()
            .getComments().size());

        containerTypeSaveAction =
            ContainerTypeHelper.getSaveAction(
                "HOTEL13", "H13", siteId, false, 13, 1,
                getContainerLabelingSchemes().get("2 char numeric").getId(),
                getR().nextDouble());
        exec(containerTypeSaveAction).getId();
        topContainerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));

    }

    @Test
    public void nameChecks() throws Exception {
        // ensure we can change name on existing container type
        Integer containerTypeId =
            exec(containerTypeSaveAction).getId();

        containerTypeSaveAction =
            ContainerTypeHelper.getSaveAction(
                exec(new ContainerTypeGetInfoAction(containerTypeId)));
        containerTypeSaveAction.setName("FREEZER_4x12");
        exec(containerTypeSaveAction);

        containerTypeSaveAction =
            ContainerTypeHelper.getSaveAction(
                exec(new ContainerTypeGetInfoAction(containerTypeId)));
        containerTypeSaveAction.setNameShort("FR4x12");
        exec(containerTypeSaveAction);
    }

    @Test
    public void specimenTypeCollection() throws Exception {
        // get random sample types
        Set<Integer> ctSpecimenTypeIds = new HashSet<Integer>();
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();
        List<SpecimenType> allSpecimenTypes = getSpecimenTypes();
        Assert.assertTrue(allSpecimenTypes.size() > 20);
        Collections.shuffle(allSpecimenTypes);

        ContainerTypeInfo containerTypeInfo;
        Integer containerTypeId;

        // add specimens types one by one
        for (int i = 0; i < 10; ++i) {
            ctSpecimenTypeIds.add(allSpecimenTypes.get(i).getId());
            set1.add(allSpecimenTypes.get(i).getId());

            containerTypeSaveAction.setSpecimenTypeIds(ctSpecimenTypeIds);
            containerTypeId = exec(containerTypeSaveAction).getId();
            containerTypeInfo =
                exec(new ContainerTypeGetInfoAction(containerTypeId));
            Assert.assertEquals(ctSpecimenTypeIds,
                getSpecimenTypeIds(containerTypeInfo));
            containerTypeSaveAction =
                ContainerTypeHelper.getSaveAction(containerTypeInfo);
        }

        // add another 10 specimen types all at once
        for (int i = 0; i < 10; ++i) {
            ctSpecimenTypeIds.add(allSpecimenTypes.get(i + 10).getId());
            set2.add(allSpecimenTypes.get(i + 10).getId());
        }

        containerTypeSaveAction.setSpecimenTypeIds(ctSpecimenTypeIds);
        containerTypeId = exec(containerTypeSaveAction).getId();
        containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));
        Assert.assertEquals(ctSpecimenTypeIds,
            getSpecimenTypeIds(containerTypeInfo));

        // remove set1 one by one
        for (Integer specimenTypeId : set1) {
            ctSpecimenTypeIds.remove(specimenTypeId);
            containerTypeSaveAction.setSpecimenTypeIds(ctSpecimenTypeIds);
            exec(containerTypeSaveAction);
            containerTypeInfo =
                exec(new ContainerTypeGetInfoAction(containerTypeId));
            Assert.assertEquals(ctSpecimenTypeIds,
                getSpecimenTypeIds(containerTypeInfo));
        }

        // remove set2 all at once
        ctSpecimenTypeIds.removeAll(set2);
        containerTypeSaveAction.setSpecimenTypeIds(ctSpecimenTypeIds);
        exec(containerTypeSaveAction);
        containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));
        Assert.assertEquals(0, containerTypeInfo.getContainerType()
            .getSpecimenTypes().size());
    }

    private Set<Integer> getSpecimenTypeIds(ContainerTypeInfo containerTypeInfo) {
        HashSet<Integer> result = new HashSet<Integer>();
        for (SpecimenType specimenType : containerTypeInfo.getContainerType()
            .getSpecimenTypes()) {
            result.add(specimenType.getId());
        }
        return result;
    }

    @Test
    public void childContainerTypeCollection() throws Exception {
        // get random sample types
        Set<Integer> ctContainerTypeIds = new HashSet<Integer>();
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();
        List<ContainerType> allContainerTypes = createChildContainerTypes();
        Assert.assertEquals(4, allContainerTypes.size());
        // Collections.shuffle(allContainerTypes);

        ContainerTypeInfo containerTypeInfo;
        Integer containerTypeId;

        // add container types one by one
        for (int i = 0; i < 2; ++i) {
            ctContainerTypeIds.add(allContainerTypes.get(i).getId());
            set1.add(allContainerTypes.get(i).getId());

            containerTypeSaveAction
                .setChildContainerTypeIds(ctContainerTypeIds);
            containerTypeId = exec(containerTypeSaveAction).getId();
            containerTypeInfo =
                exec(new ContainerTypeGetInfoAction(containerTypeId));
            Assert.assertEquals(ctContainerTypeIds,
                getChildContainerTypeIds(containerTypeInfo));
            containerTypeSaveAction =
                ContainerTypeHelper.getSaveAction(containerTypeInfo);
        }

        // add another 2 container types all at once
        for (int i = 0; i < 2; ++i) {
            ctContainerTypeIds.add(allContainerTypes.get(i + 2).getId());
            set2.add(allContainerTypes.get(i + 2).getId());
        }

        containerTypeSaveAction.setChildContainerTypeIds(ctContainerTypeIds);
        containerTypeId = exec(containerTypeSaveAction).getId();
        containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));
        Assert.assertEquals(ctContainerTypeIds,
            getChildContainerTypeIds(containerTypeInfo));

        // remove set1 one by one
        for (Integer specimenTypeId : set1) {
            ctContainerTypeIds.remove(specimenTypeId);
            containerTypeSaveAction
                .setChildContainerTypeIds(ctContainerTypeIds);
            exec(containerTypeSaveAction);
            containerTypeInfo =
                exec(new ContainerTypeGetInfoAction(containerTypeId));
            Assert.assertEquals(ctContainerTypeIds,
                getChildContainerTypeIds(containerTypeInfo));
        }

        // remove set2 all at once
        ctContainerTypeIds.removeAll(set2);
        containerTypeSaveAction.setChildContainerTypeIds(ctContainerTypeIds);
        exec(containerTypeSaveAction);
        containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));
        Assert.assertEquals(0, containerTypeInfo.getContainerType()
            .getChildContainerTypes().size());
    }

    private Set<Integer> getChildContainerTypeIds(
        ContainerTypeInfo containerTypeInfo) {
        HashSet<Integer> result = new HashSet<Integer>();
        for (ContainerType childContainerType : containerTypeInfo
            .getContainerType()
            .getChildContainerTypes()) {
            result.add(childContainerType.getId());
        }
        return result;
    }

    /*
     * Creates 4 child containers for the top level container
     */
    private List<ContainerType> createChildContainerTypes() {
        List<ContainerType> result = new ArrayList<ContainerType>();

        ContainerTypeSaveAction hotelCtSaveAction =
            ContainerTypeHelper.getSaveAction(
                "HOTEL12", "H12", siteId, false, 12, 1,
                getContainerLabelingSchemes().get("2 char numeric").getId(),
                getR().nextDouble());
        Integer ctId = exec(hotelCtSaveAction).getId();
        ContainerTypeInfo containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(ctId));
        result.add(containerTypeInfo.getContainerType());

        hotelCtSaveAction =
            ContainerTypeHelper.getSaveAction(
                "HOTEL13", "H13", siteId, false, 13, 1,
                getContainerLabelingSchemes().get("2 char numeric").getId(),
                getR().nextDouble());
        ctId = exec(hotelCtSaveAction).getId();
        containerTypeInfo = exec(new ContainerTypeGetInfoAction(ctId));
        result.add(containerTypeInfo.getContainerType());

        hotelCtSaveAction =
            ContainerTypeHelper.getSaveAction(
                "HOTEL18", "H18", siteId, false, 18, 1,
                getContainerLabelingSchemes().get("2 char numeric").getId(),
                getR().nextDouble());
        ctId = exec(hotelCtSaveAction).getId();
        containerTypeInfo = exec(new ContainerTypeGetInfoAction(ctId));
        result.add(containerTypeInfo.getContainerType());

        hotelCtSaveAction =
            ContainerTypeHelper.getSaveAction(
                "HOTEL19", "H19", siteId, false, 19, 1,
                getContainerLabelingSchemes().get("2 char numeric").getId(),
                getR().nextDouble());
        ctId = exec(hotelCtSaveAction).getId();
        containerTypeInfo = exec(new ContainerTypeGetInfoAction(ctId));
        result.add(containerTypeInfo.getContainerType());

        return result;
    }

    @Test
    public void comments() {
        // save with no comments
        Integer containerTypeId =
            exec(containerTypeSaveAction).getId();
        ContainerTypeInfo containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));
        Assert.assertEquals(0, containerTypeInfo.getContainerType()
            .getComments().size());

        containerTypeInfo = addComment(containerTypeId);
        Assert.assertEquals(1, containerTypeInfo.getContainerType()
            .getComments().size());

        containerTypeInfo = addComment(containerTypeId);
        Assert.assertEquals(2, containerTypeInfo.getContainerType()
            .getComments().size());

        // TODO: check full name on each comment's user
        // for (Comment comment :
        // containerTypeInfo.containerType.getCommentCollection()) {
        //
        // }

    }

    private ContainerTypeInfo addComment(Integer containerTypeId) {
        ContainerTypeSaveAction containerTypeSaveAction =
            ContainerTypeHelper.getSaveAction(
                exec(new ContainerTypeGetInfoAction(containerTypeId)));
        containerTypeSaveAction
            .setCommentMessage(Utils.getRandomString(20, 30));
        exec(containerTypeSaveAction).getId();
        return exec(new ContainerTypeGetInfoAction(containerTypeId));
    }

    @Test
    public void delete() {
        // save with no comments
        Integer containerTypeId =
            exec(containerTypeSaveAction).getId();
        ContainerTypeInfo containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));
        exec(new ContainerTypeDeleteAction(containerTypeInfo
            .getContainerType()));

        // hql query for container type should return empty
        Query q =
            session.createQuery("SELECT COUNT(*) FROM "
                + ContainerType.class.getName() + " WHERE id=?");
        q.setParameter(0, containerTypeId);
        Long result = HibernateUtil.getCountFromQuery(q);

        Assert.assertTrue(result.equals(0L));
    }
}
