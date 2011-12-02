package edu.ualberta.med.biobank.common.action.clinic;

import java.text.MessageFormat;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;

public class ClinicPreDeleteChecks {
    private static final String HAS_STUDIES_MSG =
        "Unable to delete clinic {0}. One or more studies use its contacts.";

    private static final String COUNT_STUDIES_HQL =
        "SELECT COUNT(DISTINCT studies)" //$NON-NLS-1$
            + " FROM " + Contact.class.getName() + " AS contacts" //$NON-NLS-1$ //$NON-NLS-2$
            + " INNER JOIN contacts.studyCollection AS studies" //$NON-NLS-1$ 
            + " WHERE contacts.clinic.id=?"; //$NON-NLS-1$

    private final Clinic clinic;

    public ClinicPreDeleteChecks(Clinic clinic) {
        this.clinic = clinic;
    }

    public void performChecks(Session session) {
        checkHasStudies(session);
    }

    private void checkHasStudies(Session session) {
        Query query = session.createQuery(COUNT_STUDIES_HQL);
        query.setParameter(0, clinic.getId());

        Long studyCount = HibernateUtil.getCountFromQuery(query);

        if (studyCount != 0) {
            String hasStudiesMsg = MessageFormat.format(HAS_STUDIES_MSG,
                clinic.getName());

            throw new ActionCheckException(hasStudiesMsg);
        }
    }

}
