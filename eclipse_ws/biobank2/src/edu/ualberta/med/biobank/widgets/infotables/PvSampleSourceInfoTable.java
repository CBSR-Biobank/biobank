package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;

public class PvSampleSourceInfoTable extends
    InfoTableWidget<ShptSampleSourceWrapper> {

    private final static String[] headings = new String[] { "Name", "Quantity" };

    private final static int[] bounds = new int[] { 250, -1, -1, -1, -1 };

    public PvSampleSourceInfoTable(Composite parent,
        Collection<ShptSampleSourceWrapper> collection) {
        super(parent, collection, headings, bounds);
    }

}
