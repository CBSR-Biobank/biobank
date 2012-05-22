package edu.ualberta.med.biobank.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.StudyContactInfo;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;

/**
 * This code must not run in the UI thread.
 * 
 */
public class BiobankLabelProvider extends LabelProvider implements
    ITableLabelProvider {
    private static final I18n i18n = I18nFactory
        .getI18n(BiobankLabelProvider.class);

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @SuppressWarnings("nls")
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
                    BgcPlugin.openAsyncError(
                        i18n.tr("Error in count"), e);
                }
            }
        } else if (element instanceof SpecimenWrapper) {
            final SpecimenWrapper specimen = (SpecimenWrapper) element;
            switch (columnIndex) {
            case 0:
                return specimen.getInventoryId();
            case 1:
                return specimen.getSpecimenType() == null ? StringUtil.EMPTY_STRING
                    : specimen
                        .getSpecimenType().getName();
            case 2:
                String position = specimen.getPositionString();
                return (position != null) ? position
                    : i18n.tr("none");
            case 3:
                return specimen.getCreatedAt() == null ? StringUtil.EMPTY_STRING
                    : DateFormatter
                        .formatAsDateTime(specimen.getCreatedAt());
            case 4:
                return specimen.getQuantity() == null ? StringUtil.EMPTY_STRING
                    : specimen
                        .getQuantity().toString();
            case 6:
                return specimen.getCommentCollection(false) == null ? StringUtil.EMPTY_STRING
                    : CommentWrapper.commentListToString(specimen
                        .getCommentCollection(false));
            }
        } else if (element instanceof SpecimenTypeWrapper) {
            final SpecimenTypeWrapper st = (SpecimenTypeWrapper) element;
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
                return i18n.tr("loading ...");
            }
        } else if (element instanceof StudyContactInfo) {
            StudyContactInfo info = (StudyContactInfo) element;
            if (columnIndex == 0) {
                if (info.contact != null)
                    return info.contact.getClinic().getName();
                return StringUtil.EMPTY_STRING;
            }
            return getContactWrapperColumnIndex(info.contact, columnIndex);
        } else if (element instanceof DispatchSpecimenWrapper) {
            DispatchSpecimenWrapper dsa = (DispatchSpecimenWrapper) element;
            if (columnIndex == 0)
                return dsa.getSpecimen().getInventoryId();
            if (columnIndex == 1)
                return dsa.getSpecimen().getSpecimenType().getName();
            if (columnIndex == 2)
                return dsa.getSpecimen().getCollectionEvent().getPatient()
                    .getPnumber();
            if (columnIndex == 3)
                return dsa.getSpecimen().getActivityStatus().toString();
            if (columnIndex == 4)
                return CommentWrapper.commentListToString(dsa
                    .getCommentCollection(false));
        } else if (element instanceof RequestSpecimenWrapper) {
            RequestSpecimenWrapper dsa = (RequestSpecimenWrapper) element;
            if (columnIndex == 0)
                return dsa.getSpecimen().getInventoryId();
            if (columnIndex == 1)
                return dsa.getSpecimen().getSpecimenType().getName();
            if (columnIndex == 2)
                return dsa.getSpecimen().getPositionString(true, true);
            if (columnIndex == 3)
                return dsa.getClaimedBy();
        } else if (element instanceof AbstractAdapterBase)
            return ((AbstractAdapterBase) element).getLabel();
        else {
            Assert.isTrue(false, "invalid object type: " + element.getClass());
        }
        return StringUtil.EMPTY_STRING;
    }

    @SuppressWarnings("nls")
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
        } else if (element instanceof ResearchGroupWrapper) {
            return ((ResearchGroupWrapper) element).getNameShort();
        } else if (element instanceof CollectionEventWrapper) {
            return ((CollectionEventWrapper) element).getVisitNumber()
                .toString();
        } else if (element instanceof ProcessingEventWrapper) {
            ProcessingEventWrapper pevent = (ProcessingEventWrapper) element;
            StringBuffer res = new StringBuffer(pevent.getFormattedCreatedAt());
            if (pevent.getWorksheet() != null
                && !pevent.getWorksheet().isEmpty())
                res.append(" - ").append(pevent.getWorksheet());
            return res.toString();
        } else if (element instanceof SpecimenTypeWrapper) {
            return ((SpecimenTypeWrapper) element).getName();
        } else if (element instanceof SourceSpecimenWrapper) {
            return ((SourceSpecimenWrapper) element).getSpecimenType()
                .getNameShort();
        } else if (element instanceof SiteWrapper) {
            return ((SiteWrapper) element).getName();
        } else if (element instanceof ActivityStatus) {
            return ((ActivityStatus) element).getName();
        } else if (element instanceof AbstractAdapterBase) {
            return ((AbstractAdapterBase) element).getLabel();
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
        return StringUtil.EMPTY_STRING;
    }

}
