package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerInfoTable extends InfoTableWidget<ContainerWrapper> {

    private static final String[] HEADINGS = new String[] { "Name", "Status",
        "Product Barcode", "Temperature" };

    private static final int[] BOUNDS = new int[] { 200, 130, 130, 20, -1, -1,
        -1 };

    public ContainerInfoTable(Composite parent,
        Collection<ContainerWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
