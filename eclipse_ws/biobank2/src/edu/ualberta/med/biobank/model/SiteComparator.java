package edu.ualberta.med.biobank.model;

import java.util.Comparator;

public class SiteComparator implements Comparator<Site> {

    @Override
    public int compare(Site s1, Site s2) {
        String s1Name = s1.getName();
        String s2Name = s2.getName();
        return ((s1Name.compareTo(s2Name) > 0) ? 1 : (s1Name.equals(s2Name) ? 0
            : -1));
    }

}
