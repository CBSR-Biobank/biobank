package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.Container;

public class ContainerInfoTable extends InfoTableWidget<Container> {

    private static final String[] HEADINGS = new String[] { "Name", "Status",
        "Bar Code", "Full", "Temperature" };

    private static final int[] BOUNDS = new int[] { 200, 130, 130, 20, 20, -1,
        -1 };

    public ContainerInfoTable(Composite parent, Collection<Container> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
