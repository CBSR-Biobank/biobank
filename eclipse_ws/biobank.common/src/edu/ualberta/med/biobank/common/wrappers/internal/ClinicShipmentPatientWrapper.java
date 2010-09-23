package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Collection;
import java.util.LinkedList;

import edu.ualberta.med.biobank.model.ClinicShipmentPatient;
import edu.ualberta.med.biobank.model.PatientVisit;

public class ClinicShipmentPatientWrapper {
    public static Collection<PatientVisit> getUnwrappedPatientVisitCollection(
        Collection<ClinicShipmentPatient> csps) {
        Collection<PatientVisit> results = new LinkedList<PatientVisit>();

        Collection<ClinicShipmentPatient> cspCollection = csps;
        if (cspCollection != null) {
            for (ClinicShipmentPatient csp : cspCollection) {
                Collection<PatientVisit> pvCollection = csp
                    .getPatientVisitCollection();
                if (pvCollection != null) {
                    results.addAll(pvCollection);
                }
            }
        }

        return results;
    }
}
