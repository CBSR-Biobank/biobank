package edu.ualberta.med.biobank.test.action;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ContainerTypeContainerType;
import edu.ualberta.med.biobank.model.Site;

public class TestContainerTypeContainerType extends TestAction {
    @Test
    public void testCtCt1() {
        Transaction tx = session.beginTransaction();

        String methodNameR = getMethodNameR();

        Site site = new Site();

        site.setActivityStatus(ActivityStatus.ACTIVE);
        site.getAddress().setCity("something");
        site.setName(methodNameR);
        site.setNameShort(methodNameR);

        session.save(site);

        ContainerLabelingScheme sbsScheme = (ContainerLabelingScheme) session
            .createCriteria(ContainerLabelingScheme.class)
            .add(Restrictions.idEq(1))
            .uniqueResult();

        ContainerType ct1 = new ContainerType();
        ct1.setName(methodNameR + "_1");
        ct1.setNameShort(methodNameR + "_1");
        ct1.setActivityStatus(ActivityStatus.ACTIVE);
        ct1.setChildLabelingScheme(sbsScheme);
        ct1.setSite(site);
        ct1.setTopLevel(true);
        ct1.getCapacity().setRowCapacity(5);
        ct1.getCapacity().setColCapacity(5);

        ContainerType ct2 = new ContainerType();
        ct2.setName(methodNameR + "_2");
        ct2.setNameShort(methodNameR + "_2");
        ct2.setActivityStatus(ActivityStatus.ACTIVE);
        ct2.setChildLabelingScheme(sbsScheme);
        ct2.setSite(site);
        ct2.getCapacity().setRowCapacity(5);
        ct2.getCapacity().setColCapacity(5);

        ContainerTypeContainerType link = new ContainerTypeContainerType();
        link.setParentContainerType(ct1);
        link.setChildContainerType(ct2);

        ct1.getChildContainerTypeContainerTypes().add(link);

        Container c1 = new Container();
        c1.setLabel(methodNameR + "_1");
        c1.setActivityStatus(ActivityStatus.ACTIVE);
        c1.setContainerType(ct1);
        c1.setSite(site);

        session.save(ct1);
        session.save(ct2);

        tx.commit();
    }
}
