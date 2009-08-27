package edu.ualberta.med.biobank.model;

import java.util.Comparator;
import java.util.Date;

public class PatientVisitComparator implements Comparator<PatientVisit> {

    @Override
    public int compare(PatientVisit v1, PatientVisit v2) {
        Date v1Date = v1.getDateDrawn();
        Date v2Date = v2.getDateDrawn();
        return ((v1Date.compareTo(v2Date) > 0) ? 1 : (v1Date.equals(v2Date) ? 0
            : -1));
    }

}
