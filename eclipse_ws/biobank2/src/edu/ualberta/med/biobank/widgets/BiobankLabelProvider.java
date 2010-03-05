package edu.ualberta.med.biobank.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
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
                return String.valueOf(study.getPatientCollection().size());
            }
        } else if (element instanceof AliquotWrapper) {
            final AliquotWrapper sample = (AliquotWrapper) element;
            switch (columnIndex) {
            case 0:
                return sample.getInventoryId();
            case 1:
                return sample.getSampleType() == null ? "" : sample
                    .getSampleType().getName();
            case 2:
                String position = sample.getPositionString();
                return (position != null) ? position : "none";
            case 3:
                return sample.getLinkDate() == null ? "" : DateFormatter
                    .formatAsDateTime(sample.getLinkDate());
            case 4:
                return sample.getQuantity() == null ? "" : sample.getQuantity()
                    .toString();
            case 6:
                return sample.getComment() == null ? "" : sample.getComment();
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
                return source.getName();
            } else {
                Assert.isTrue(false, "invalid column index: " + columnIndex);
            }
        } else {
            Assert.isTrue(false, "invalid object type: " + element.getClass());
        }
        return "";
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ContainerTypeWrapper) {
            return ((ContainerTypeWrapper) element).getName();
        }
        if (element instanceof StudyWrapper) {
            StudyWrapper study = (StudyWrapper) element;
            return study.getNameShort() + " - " + study.getName();
        } else if (element instanceof ClinicWrapper) {
            return ((ClinicWrapper) element).getName();
        } else if (element instanceof SiteWrapper) {
            return ((SiteWrapper) element).getNameShort();
        } else if (element instanceof SampleTypeWrapper) {
            return ((SampleTypeWrapper) element).getName();
        } else if (element instanceof SiteWrapper) {
            return ((SiteWrapper) element).getName();
        }
        if (element instanceof AdapterBase) {
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
            if ((contact != null) && (contact.getPhoneNumber() != null))
                return contact.getPhoneNumber();
            break;
        case 5:
            if ((contact != null) && (contact.getFaxNumber() != null))
                return contact.getFaxNumber();
            break;
        }
        return "";
    }

}
