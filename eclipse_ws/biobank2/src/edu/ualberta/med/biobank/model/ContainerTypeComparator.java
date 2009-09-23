package edu.ualberta.med.biobank.model;

import java.util.Comparator;

public class ContainerTypeComparator implements Comparator<ContainerType> {

    @Override
    public int compare(ContainerType c1, ContainerType c2) {
        String c1Name = c1.getName();
        String c2Name = c2.getName();

        return ((c1Name.compareTo(c2Name) > 0) ? 1 : (c1Name.equals(c2Name) ? 0
            : -1));
    }
}