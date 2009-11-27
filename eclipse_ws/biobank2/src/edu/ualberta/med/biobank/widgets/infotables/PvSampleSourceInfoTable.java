package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;

public class PvSampleSourceInfoTable extends
    InfoTableWidget<PvSampleSourceWrapper> {

    private final static String[] headings = new String[] { "Name", "Quantity",
        "Date Drawn" };

    private final static int[] bounds = new int[] { 250, 100, -1, -1, -1 };

    public PvSampleSourceInfoTable(Composite parent,
        Collection<PvSampleSourceWrapper> collection) {
        super(parent, collection, headings, bounds);
    }

}
