package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public abstract class BgcLabelProvider extends LabelProvider implements
    ITableLabelProvider {

    @Override
    public abstract String getColumnText(Object element, int columnIndex);

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

}
