package edu.ualberta.med.biobank.model;

import java.util.Comparator;

public class StudyComparator implements Comparator<Study> {

    @Override
    public int compare(Study s1, Study s2) {
        String s1Name = s1.getNameShort();
        String s2Name = s2.getNameShort();
        return ((s1Name.compareTo(s2Name) > 0) ? 1 : (s1Name.equals(s2Name) ? 0
            : -1));
    }

}
