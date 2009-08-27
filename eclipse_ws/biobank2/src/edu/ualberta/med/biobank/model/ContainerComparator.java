package edu.ualberta.med.biobank.model;

import java.util.Comparator;

public class ContainerComparator implements Comparator<Container> {

    @Override
    public int compare(Container c1, Container c2) {
        String c1Name = c1.getLabel();
        String c2Name = c2.getLabel();
        return ((c1Name.compareTo(c2Name) > 0) ? 1 : (c1Name.equals(c2Name) ? 0
            : -1));
    }

}
