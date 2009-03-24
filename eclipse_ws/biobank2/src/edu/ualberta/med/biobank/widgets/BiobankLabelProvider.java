package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;


public class BiobankLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof Study) {
            final Study study = (Study) element;
            switch (columnIndex) {
            case 0: return study.getName();
            case 1: return study.getNameShort();
            }
        }
        else if (element instanceof Clinic) {
            final Clinic clinic = (Clinic) element;
            switch (columnIndex) {
            case 0: return clinic.getName();
            }
        }
        return "";
    }
    
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}
