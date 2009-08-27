package edu.ualberta.med.biobank.model;

import java.util.Comparator;

public class ClinicComparator implements Comparator<Clinic> {

    @Override
    public int compare(Clinic c1, Clinic c2) {
        String c1Name = c1.getName();
        String c2Name = c2.getName();
        return ((c1Name.compareTo(c2Name) > 0) ? 1 : (c1Name.equals(c2Name) ? 0
            : -1));
    }

}
