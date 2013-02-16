package edu.ualberta.med.biobank.test.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import junit.framework.Assert;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.search.SpecimenByInventorySearchAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetPossibleTypesAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetPossibleTypesAction.SpecimenTypeData;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class TestSpecimen extends TestAction {

    @Test
    public void saveNew() throws Exception {

    }

    private Container createContainer() {
        ContainerLabelingScheme labeling = (ContainerLabelingScheme)
            session.createCriteria(ContainerLabelingScheme.class)
                .add(Restrictions.eq("name", "SBS Standard")).uniqueResult();

        Container topContainer = factory.createTopContainer();

        Capacity capacity = new Capacity();
        capacity.setRowCapacity(3);
        capacity.setColCapacity(1);
        factory.setDefaultCapacity(capacity);

        ContainerType ctype = factory.createContainerType();
        topContainer.getContainerType().setChildLabelingScheme(labeling);
        topContainer.getContainerType().getChildContainerTypes().add(ctype);
        return factory.createContainer();
    }

    @Test
    public void checkGetAction() throws Exception {
        session.beginTransaction();
        Specimen specimen = factory.createParentSpecimen();
        session.getTransaction().commit();

        SpecimenBriefInfo specimenBriefInfo = exec(
            new SpecimenGetInfoAction(specimen.getId()));

        Assert.assertEquals(specimen, specimenBriefInfo.getSpecimen());
    }

    @Test
    public void checkGetActionWithPosition() throws Exception {
        session.beginTransaction();
        Container specimenContainer = createContainer();
        factory.createParentSpecimen();
        factory.createProcessingEvent();
        session.flush();

        SpecimenType childSpecimenType = null;
        Map<Specimen, String> specimenPosString = new HashMap<Specimen, String>();
        Set<Specimen> childSpecimens = new HashSet<Specimen>();
        for (int i = 0; i < 3; ++i) {
            Specimen childSpecimen = factory.createChildSpecimen();
            childSpecimens.add(childSpecimen);

            if (i == 0) {
                childSpecimenType = childSpecimen.getSpecimenType();
                ContainerType ctype = specimenContainer.getContainerType();
                ctype.getSpecimenTypes().add(childSpecimenType);
                session.update(ctype);
                session.flush();
            }

            SpecimenPosition specimenPosition = new SpecimenPosition();
            specimenPosition.setRow(i);
            specimenPosition.setCol(0);

            RowColPos pos = new RowColPos(i, 0);
            String positionString = ContainerLabelingSchemeWrapper.getPositionString(pos,
                specimenContainer.getContainerType().getChildLabelingScheme().getId(),
                specimenContainer.getContainerType().getCapacity().getRowCapacity(),
                specimenContainer.getContainerType().getCapacity().getColCapacity());
            specimenPosition.setPositionString(positionString);
            specimenPosString.put(childSpecimen, positionString);

            specimenPosition.setSpecimen(childSpecimen);
            childSpecimen.setSpecimenPosition(specimenPosition);

            specimenPosition.setContainer(specimenContainer);
            specimenContainer.getSpecimenPositions().add(specimenPosition);

            session.update(childSpecimen);
        }
        session.getTransaction().commit();

        for (Specimen childSpecimen : childSpecimens) {
            SpecimenBriefInfo specimenBriefInfo = exec(
                new SpecimenGetInfoAction(childSpecimen.getId()));
            Assert.assertEquals(childSpecimen, specimenBriefInfo.getSpecimen());

            Stack<Container> containerStack = specimenBriefInfo.getParents();
            Assert.assertEquals(2, containerStack.size());
            Assert.assertEquals(specimenContainer, containerStack.get(0));
            Assert.assertEquals(specimenContainer.getParentContainer(),
                containerStack.get(1));

            Assert.assertEquals(
                specimenContainer.getLabel() + specimenPosString.get(childSpecimen),
                SpecimenActionHelper.getPositionString(
                    specimenBriefInfo.getSpecimen(), true, false));
        }

    }

    // search for an invalid specimenId
    @Test
    public void searchByInventoryIdActionBadId() {
        session.beginTransaction();
        Site site = factory.createSite();
        session.getTransaction().commit();

        String badInventoryId = new UUID(128, 256).toString();

        final List<Integer> actionResult = exec(
            new SpecimenByInventorySearchAction(badInventoryId, site.getId())).getList();
        Assert.assertEquals(0, actionResult.size());
    }

    @Test
    public void searchByInventoryIdAction() {
        session.beginTransaction();
        factory.createClinic();
        Site site = factory.createSite();
        factory.createStudy();
        Specimen spc = factory.createParentSpecimen();
        session.getTransaction().commit();

        final List<Integer> actionResult = exec(new SpecimenByInventorySearchAction(
            spc.getInventoryId(), site.getId())).getList();
        Assert.assertEquals(1, actionResult.size());
        Assert.assertEquals(spc.getId(), actionResult.get(0));
    }

    @Test
    public void getPossibleTypesParentSpecimen() {
        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>();
        session.beginTransaction();
        factory.createStudy();
        specimenTypes.add(factory.createSpecimenType());
        factory.createSourceSpecimen();
        specimenTypes.add(factory.createSpecimenType());
        factory.createSourceSpecimen();
        factory.createCollectionEvent();
        Specimen specimen = factory.createParentSpecimen();
        session.getTransaction().commit();

        SpecimenTypeData SpecimenTypeData = exec(new SpecimenGetPossibleTypesAction(specimen));
        Assert.assertEquals(specimenTypes.size(), SpecimenTypeData.getSpecimenTypes().size());
        Assert.assertEquals(0, SpecimenTypeData.getVolumeMap().size());
    }

    @Test
    public void getPossibleTypesChildSpecimen() {
        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>();
        session.beginTransaction();
        factory.createStudy();

        factory.createSpecimenType();
        factory.createSourceSpecimen();
        factory.createSpecimenType();
        factory.createSourceSpecimen();

        specimenTypes.add(factory.createSpecimenType());
        factory.createAliquotedSpecimen();
        specimenTypes.add(factory.createSpecimenType());
        factory.createAliquotedSpecimen();

        factory.createCollectionEvent();
        factory.createParentSpecimen();
        factory.createProcessingEvent();
        Specimen specimen = factory.createChildSpecimen();
        session.getTransaction().commit();

        SpecimenTypeData SpecimenTypeData = exec(new SpecimenGetPossibleTypesAction(specimen));
        Assert.assertEquals(specimenTypes.size(), SpecimenTypeData.getSpecimenTypes().size());
        Assert.assertEquals(specimenTypes.size(), SpecimenTypeData.getVolumeMap().size());
    }
}
