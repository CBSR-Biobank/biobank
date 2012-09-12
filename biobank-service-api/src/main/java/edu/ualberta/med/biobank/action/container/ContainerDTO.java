package edu.ualberta.med.biobank.action.container;

import edu.ualberta.med.biobank.model.ContainerConstraints;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ParentContainer;

public class ContainerDTO {
    private String productBarcode;
    private ContainerType containerType;
    private ContainerConstraints constraints;
    private Boolean enabled;
    private ParentContainer parent;
    private String label;
}
