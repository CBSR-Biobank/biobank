package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sdata;
import edu.ualberta.med.biobank.model.StorageContainer;
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
        else if (element instanceof Patient) {
            final Patient patient = (Patient) element;
            switch (columnIndex) {
                case 0: return patient.getNumber();
            }
        }
        else if (element instanceof StorageContainer) {
            final StorageContainer container = (StorageContainer) element;
            switch (columnIndex) {
                case 0: return container.getName();
                case 1: return container.getActivityStatus();
                case 2: return container.getBarcode();
                case 3: return container.getFull() ? "Y" : "N";
                case 4: return "" + container.getTemperature();
            }
        }
        else if (element instanceof Sdata) {
            final Sdata sdata = (Sdata) element;
            switch (columnIndex) {
                case 0: return sdata.getSdataType().getType();
                case 1: return sdata.getValue();
            }
        }
        return "";
    }
    
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}
