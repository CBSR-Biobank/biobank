package edu.ualberta.med.biobank.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.StudyContactInfo;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;

/**
 * This code must not run in the UI thread.
 * 
 */
public class BiobankLabelProvider extends LabelProvider implements
    ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof SiteWrapper) {
            final SiteWrapper site = (SiteWrapper) element;
            switch (columnIndex) {
            case 0:
                return site.getName();
            }
        } else if (element instanceof ClinicWrapper) {
            final ClinicWrapper clinic = (ClinicWrapper) element;
            switch (columnIndex) {
            case 0:
                return clinic.getName();
            }
        } else if (element instanceof StudyWrapper) {
            final StudyWrapper study = (StudyWrapper) element;
            switch (columnIndex) {
            case 0:
                return study.getName();
            case 1:
                return study.getNameShort();
            case 2:
                try {
                    return String.valueOf(study.getPatientCount(true));
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError("Error in count", e);
                }
            }
        } else if (element instanceof AliquotWrapper) {
            final AliquotWrapper aliquot = (AliquotWrapper) element;
            switch (columnIndex) {
            case 0:
                return aliquot.getInventoryId();
            case 1:
                return aliquot.getSampleType() == null ? "" : aliquot
                    .getSampleType().getName();
            case 2:
                String position = aliquot.getPositionString();
                return (position != null) ? position : "none";
            case 3:
                return aliquot.getLinkDate() == null ? "" : DateFormatter
                    .formatAsDateTime(aliquot.getLinkDate());
            case 4:
                return aliquot.getQuantity() == null ? "" : aliquot
                    .getQuantity().toString();
            case 6:
                return aliquot.getComment() == null ? "" : aliquot.getComment();
            }
        } else if (element instanceof SampleTypeWrapper) {
            final SampleTypeWrapper st = (SampleTypeWrapper) element;
            switch (columnIndex) {
            case 0:
                return st.getName();
            case 1:
                return st.getNameShort();
            case 2:
                return String.valueOf(st.getId());
            }
        } else if (element instanceof BiobankCollectionModel) {
            BiobankCollectionModel m = (BiobankCollectionModel) element;
            if (m.o != null) {
                return getColumnText(m.o, columnIndex);
            } else if (columnIndex == 0) {
                return "loading ...";
            }
        } else if (element instanceof StudyContactInfo) {
            StudyContactInfo info = (StudyContactInfo) element;
            if (columnIndex == 0) {
                if (info.contact != null)
                    return info.contact.getClinic().getName();
                return "";
            }
            return getContactWrapperColumnIndex(info.contact, columnIndex);
        } else if (element instanceof SourceVesselWrapper) {
            SourceVesselWrapper source = (SourceVesselWrapper) element;
            if (columnIndex == 0) {
                return source.getSourceVesselType().getName();
            } else {
                Assert.isTrue(false, "invalid column index: " + columnIndex);
            }
        } else if (element instanceof DispatchAliquotWrapper) {
            DispatchAliquotWrapper dsa = (DispatchAliquotWrapper) element;
            if (columnIndex == 0)
                return dsa.getAliquot().getInventoryId();
            if (columnIndex == 1)
                return dsa.getAliquot().getSampleType().getNameShort();
            if (columnIndex == 2)
                return dsa.getAliquot().getProcessingEvent().getPatient()
                    .getPnumber();
            if (columnIndex == 3)
                return dsa.getAliquot().getActivityStatus().toString();
            if (columnIndex == 4)
                return dsa.getComment();
        } else if (element instanceof RequestAliquotWrapper) {
            RequestAliquotWrapper dsa = (RequestAliquotWrapper) element;
            if (columnIndex == 0)
                return dsa.getAliquot().getInventoryId();
            if (columnIndex == 1)
                return dsa.getAliquot().getSampleType().getNameShort();
            if (columnIndex == 2)
                return dsa.getAliquot().getPositionString(true, true);
            if (columnIndex == 3)
                return dsa.getClaimedBy();
        } else if (element instanceof AdapterBase)
            return ((AdapterBase) element).getLabel();
        else {
            Assert.isTrue(false, "invalid object type: " + element.getClass());
        }
        return "";
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ContainerTypeWrapper) {
            return ((ContainerTypeWrapper) element).getName();
        } else if (element instanceof StudyWrapper) {
            StudyWrapper study = (StudyWrapper) element;
            return study.getNameShort() + " - " + study.getName();
        } else if (element instanceof ClinicWrapper) {
            return ((ClinicWrapper) element).getName();
        } else if (element instanceof SiteWrapper) {
            return ((SiteWrapper) element).getNameShort();
        } else if (element instanceof SampleTypeWrapper) {
            return ((SampleTypeWrapper) element).getNameShort();
        } else if (element instanceof SiteWrapper) {
            return ((SiteWrapper) element).getName();
        } else if (element instanceof ActivityStatusWrapper) {
            return ((ActivityStatusWrapper) element).getName();
        } else if (element instanceof StudySourceVesselWrapper) {
            return ((StudySourceVesselWrapper) element).getSourceVesselType()
                .getName();
        } else if (element instanceof AdapterBase) {
            return ((AdapterBase) element).getLabel();
        }
        return element.toString();
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    private String getContactWrapperColumnIndex(ContactWrapper contact,
        int columnIndex) {
        switch (columnIndex) {
        case 1:
            if ((contact != null) && (contact.getName() != null))
                return contact.getName();
            break;
        case 2:
            if ((contact != null) && (contact.getTitle() != null))
                return contact.getTitle();
            break;
        case 3:
            if ((contact != null) && (contact.getEmailAddress() != null))
                return contact.getEmailAddress();
            break;
        case 4:
            if ((contact != null) && (contact.getMobileNumber() != null))
                return contact.getMobileNumber();
            break;
        case 5:
            if ((contact != null) && (contact.getPagerNumber() != null))
                return contact.getPagerNumber();
            break;
        case 6:
            if ((contact != null) && (contact.getOfficeNumber() != null))
                return contact.getOfficeNumber();
            break;
        case 7:
            if ((contact != null) && (contact.getFaxNumber() != null))
                return contact.getFaxNumber();
            break;
        }
        return "";
    }

}
