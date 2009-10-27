package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerInfoTable extends InfoTableWidget<ContainerWrapper> {

    private static final String[] HEADINGS = new String[] { "Name",
        "Container Type", "Status", "Product Barcode", "Temperature" };

    private static final int[] BOUNDS = new int[] { 160, 130, 130, 130, 130 };

    public ContainerInfoTable(Composite parent,
        Collection<ContainerWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
