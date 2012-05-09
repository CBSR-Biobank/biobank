package edu.ualberta.med.biobank.test.model;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HibernateHelper;

public class TestDispatch extends DbTest {
    // TODO: Still need to check validation tests for the following classes:
    // Dispatch.java
    // DispatchSpecimen.java
    // Domain.java
    // EventAttr.java
    // EventAttrType.java
    // GlobalEventAttr.java
    // Group.java
    // JasperTemplate.java
    // Log.java
    // Membership.java
    // Name.java
    // OriginInfo.java
    // Patient.java
    // PermissionEnum.java
    // Principal.java
    // PrintedSsInvItem.java
    // PrinterLabelTemplate.java
    // ProcessingEvent.java
    // Request.java
    // RequestSpecimen.java
    // ResearchGroup.java
    // Revision.java
    // RevisionActionData.java
    // RevisionEntityType.java
    // Role.java
    // ShipmentInfo.java
    // ShippingMethod.java
    // Site.java
    // SourceSpecimen.java
    // Specimen.java
    // SpecimenPosition.java
    // SpecimenType.java
    // Study.java
    // StudyEventAttr.java
    // User.java

    @Test
    public void stateIds() {
        Transaction tx = session.beginTransaction();

        Site sender = factory.createSite();
        Site receiver = factory.createSite();
        Dispatch dispatch = factory.createDispatch(sender, receiver);

        Query query = HibernateHelper.getDehydratedPropertyQuery(
            session, dispatch, "state");

        try {
            for (DispatchState state : DispatchState.values()) {
                dispatch.setState(state);
                session.update(dispatch);
                session.flush();

                int id = ((Number) query.uniqueResult()).intValue();
                Assert.assertEquals("persisted id does not match enum's id",
                    state.getId(), new Integer(id));
            }
        } finally {
            tx.rollback();
        }
    }
}
