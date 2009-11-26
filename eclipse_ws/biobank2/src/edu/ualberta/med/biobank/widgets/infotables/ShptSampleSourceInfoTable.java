package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;

public class ShptSampleSourceInfoTable extends
    InfoTableWidget<ShptSampleSourceWrapper> {

    private final static String[] headings = new String[] { "Name", "Quantity",
        "Patient Number(s)" };

    private final static int[] bounds = new int[] { 250, 100, -1, -1, -1 };

    public ShptSampleSourceInfoTable(Composite parent,
        Collection<ShptSampleSourceWrapper> collection) {
        super(parent, collection, headings, bounds);
    }

}
