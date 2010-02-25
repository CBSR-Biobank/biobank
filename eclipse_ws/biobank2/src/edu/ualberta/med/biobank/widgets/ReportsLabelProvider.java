package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;

/**
 * This code must not run in the UI thread.
 * 
 */
public class ReportsLabelProvider extends LabelProvider implements
    ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {

        // if (element instanceof Study) {
        // final Study study = (Study) element;
        // switch (columnIndex) {
        // case 0:
        // return study.getName();
        // case 1:
        // return study.getActivityStatus();
        // case 2:
        // return study.getComment();
        // case 3:
        // return String.valueOf(study.getContactCollection().size());
        // case 4:
        // return study.getNameShort();
        // case 5:
        // return String.valueOf(study.getStudyPvAttrCollection().size());
        // case 6:
        // return String.valueOf(study.getSampleSourceCollection().size());
        // case 7:
        // return String
        // .valueOf(study.getSampleStorageCollection().size());
        // case 8:
        // return study.getSite().getName();
        // }
        // } else if (element instanceof Clinic) {
        // final Clinic clinic = (Clinic) element;
        // switch (columnIndex) {
        // case 0:
        // return clinic.getId().toString();
        // case 1:
        // return clinic.getActivityStatus();
        // case 2:
        // return clinic.getAddress().getStreet1() + " "
        // + clinic.getAddress().getStreet2() + " "
        // + clinic.getAddress().getCity();
        // case 3:
        // return clinic.getComment();
        // case 4:
        // return clinic.getSite().getName();
        // }
        // } else if (element instanceof Site) {
        // final Site site = (Site) element;
        // switch (columnIndex) {
        // case 0:
        // return site.getName();
        // }
        // } else if (element instanceof Patient) {
        // final Patient patient = (Patient) element;
        // switch (columnIndex) {
        // case 0:
        // return patient.getId().toString();
        // case 1:
        // return patient.getNumber();
        // case 2:
        // return patient.getStudy().getName();
        // }
        // } else if (element instanceof ContainerType) {
        // final ContainerType ct = (ContainerType) element;
        // switch (columnIndex) {
        // case 0:
        // return ct.getId().toString();
        // case 1:
        // return ct.getActivityStatus();
        // case 2:
        // return ct.getCapacity().getRowCapacity() + "x"
        // + ct.getCapacity().getColCapacity();
        // case 3:
        // return ct.getChildLabelingScheme().getName();
        // case 4:
        // return ct.getComment();
        // case 5:
        // if (ct.getDefaultTemperature() == null)
        // return "";
        // else
        // return ct.getDefaultTemperature().toString();
        // case 6:
        // return ct.getName();
        // case 7:
        // return ct.getNameShort();
        // case 8:
        // return ct.getSite().getName();
        // case 9:
        // return ct.getTopLevel().toString();
        // }
        // } else if (element instanceof Container) {
        // final Container container = (Container) element;
        // switch (columnIndex) {
        // case 0:
        // return container.getActivityStatus();
        // case 1:
        // return String.valueOf(container.getChildPositionCollection()
        // .size());
        // case 2:
        // return container.getComment();
        // case 3:
        // return container.getContainerType().getNameShort();
        // case 4:
        // return container.getLabel();
        // case 5:
        // if (container.getPosition() == null)
        // return "Top";
        // else
        // return container.getPosition().getRow() + ","
        // + container.getPosition().getCol();
        // case 6:
        // return container.getProductBarcode();
        // case 7:
        // return String.valueOf(container.getSamplePositionCollection()
        // .size());
        // case 8:
        // return container.getSite().getName();
        // case 9:
        // if (container.getTemperature() == null)
        // return "";
        // else
        // return container.getTemperature().toString();
        //
        // }
        // } else if (element instanceof Sample) {
        // final Sample sample = (Sample) element;
        // switch (columnIndex) {
        // case 0:
        // return sample.getId().toString();
        // case 1:
        // return sample.getComment();
        // case 2:
        // return sample.getInventoryId();
        // case 3:
        // return sample.getLinkDate().toString();
        // case 4:
        // if (sample.getPatientVisit() != null
        // && sample.getPatientVisit().getDateProcessed() == null)
        // return "";
        // else
        // return sample.getPatientVisit().getDateProcessed()
        // .toString();
        // case 5:
        // if (sample.getQuantity() == null)
        // return "";
        // else
        // return sample.getQuantity().toString();
        // case 6:
        // if (sample.getQuantityUsed() == null)
        // return "";
        // else
        // return sample.getQuantityUsed().toString();
        // case 7:
        // if (sample.getSamplePosition() == null)
        // return "";
        // else
        // return sample.getSamplePosition().getRow() + ","
        // + sample.getSamplePosition();
        // case 8:
        // if (sample.getSampleType() == null)
        // return "";
        // else
        // return sample.getSampleType().getName();
        // }
        // } else
        if (element instanceof BiobankCollectionModel) {
            BiobankCollectionModel m = (BiobankCollectionModel) element;
            if (m.o != null) {
                return getColumnText(m.o, columnIndex);
            } else if (columnIndex == 0) {
                return "loading ...";
            }
        } else if (element instanceof Object[]) {
            Object[] castedVals = (Object[]) element;
            if (castedVals[columnIndex] == null)
                return "";
            else
                return castedVals[columnIndex].toString();
        }
        return "no label provider";

    }

    // @Override
    // public String getText(Object element) {
    // if (element instanceof ContainerType) {
    // return ((ContainerType) element).getName();
    // } else if (element instanceof Study) {
    // Study study = (Study) element;
    // return study.getNameShort() + " - " + study.getName();
    // } else if (element instanceof Clinic) {
    // return ((Clinic) element).getName();
    // } else if (element instanceof ContainerLabelingScheme) {
    // return ((ContainerLabelingScheme) element).getName();
    // } else if (element instanceof Site) {
    // return ((Site) element).getName();
    // } else if (element instanceof SampleType) {
    // return ((SampleType) element).getName();
    // }
    // return ((AdapterBase) element).getName();
    // }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}
