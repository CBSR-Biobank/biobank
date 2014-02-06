package edu.ualberta.med.biobank.test.action.batchoperation;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class CsvUtil {

    public static void showErrorsInLog(Logger log, IllegalStateException e) {
        log.error("ERROR: {}", e.getMessage());
    }

    public static void showErrorsInLog(Logger log, BatchOpErrorsException e) {
        for (BatchOpException<LString> ie : e.getErrors()) {
            log.error("ERROR: line no {}: {}", ie.getLineNumber(),
                ie.getMessage());
        }
    }

    public static void deleteSpecimen(Session session, Specimen parent) {
        Set<Specimen> parents = new HashSet<Specimen>();
        parents.add(parent);
        deleteSpecimens(session, parents);
    }

    public static void deleteSpecimens(Session session, Set<Specimen> parents) {
        Set<Specimen> children = new HashSet<Specimen>();
        for (Specimen parent : parents) {
            for (Specimen child : parent.getChildSpecimens()) {
                children.add(child);
            }
        }
        if (!children.isEmpty()) {
            deleteSpecimens(session, children);
        }
        for (Specimen parent : parents) {
            Specimen grandparent = parent.getParentSpecimen();
            if (grandparent != null) {
                grandparent.getChildSpecimens().remove(parent);
            }
            session.delete(parent);
        }
        session.flush();
    }

    public static void deletePatient(Session session, Patient patient) {
        for (CollectionEvent ce : patient.getCollectionEvents()) {
            Set<Specimen> specimens = new HashSet<Specimen>();
            for (Specimen spc : ce.getOriginalSpecimens()) {
                specimens.add(spc);
            }
            deleteSpecimens(session, specimens);
            session.delete(ce);
        }
        session.flush();

        session.delete(patient);
        session.flush();
    }

}
