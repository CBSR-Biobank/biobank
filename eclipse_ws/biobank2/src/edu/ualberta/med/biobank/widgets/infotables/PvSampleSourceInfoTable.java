package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PvSampleSourceInfoTable extends
    InfoTableWidget<PvSampleSourceWrapper> {

    private final static String[] headings = new String[] { "Name", "Quantity",
        "Date Drawn" };

    private final static int[] bounds = new int[] { 250, 100, -1, -1, -1 };

    public PvSampleSourceInfoTable(Composite parent,
        Collection<PvSampleSourceWrapper> collection) {
        super(parent, collection, headings, bounds);
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PvSampleSourceWrapper getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PvSampleSourceWrapper> getCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        // TODO Auto-generated method stub
        return null;
    }

}
