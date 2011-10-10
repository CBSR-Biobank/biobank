package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.UncachedAction;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class ClinicPreDeleteChecks extends UncachedAction<Clinic> {
    private static final long serialVersionUID = 1L;
    private static final String HAS_STUDIES_MSG = Messages
        .getString("ClinicPreDeleteChecks.delete.clinic.error.msg"); //$NON-NLS-1$

    // @formatter:off
    private static final String COUNT_STUDIES_HQL =
        "SELECT COUNT(DISTINCT studies)" + //$NON-NLS-1$
        " FROM " + Contact.class.getName() + " AS contacts" + //$NON-NLS-1$ //$NON-NLS-2$
        " INNER JOIN contacts." + ContactPeer.STUDY_COLLECTION.getName() + " AS studies" + //$NON-NLS-1$ //$NON-NLS-2$
        " WHERE contacts." + ContactPeer.CLINIC.getName() + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$
    // @formatter:on

    public ClinicPreDeleteChecks(ModelWrapper<Clinic> wrapper) {
        super(wrapper);
    }

    @Override
    public void doUncachedAction(Session session)
        throws BiobankSessionException {
        checkHasStudies(session);
    }

    private void checkHasStudies(Session session)
        throws BiobankSessionException {
        Query query = session.createQuery(COUNT_STUDIES_HQL);
        query.setParameter(0, getModel());

        Long studyCount = HibernateUtil.getCountFromQuery(query);

        if (studyCount != 0) {
            String hasStudiesMsg = MessageFormat.format(HAS_STUDIES_MSG,
                getModel().getName());

            throw new BiobankSessionException(hasStudiesMsg);
        }
    }
}
