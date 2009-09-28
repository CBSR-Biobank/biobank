package edu.ualberta.med.biobank.widgets;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicStudyInfo;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyContactAndPatientInfo;
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
        if (element instanceof Study) {
            final Study study = (Study) element;
            switch (columnIndex) {
            case 0:
                return study.getName();
            case 1:
                return study.getNameShort();
            case 2:
                return "" + study.getPatientCollection().size();
            }
        } else if (element instanceof Clinic) {
            final Clinic clinic = (Clinic) element;
            switch (columnIndex) {
            case 0:
                return clinic.getName();
            }
        } else if (element instanceof Site) {
            final Site site = (Site) element;
            switch (columnIndex) {
            case 0:
                return site.getName();
            }
        } else if (element instanceof Patient) {
            final Patient patient = (Patient) element;
            switch (columnIndex) {
            case 0:
                return patient.getNumber();
            }
        } else if (element instanceof PatientVisit) {
            SimpleDateFormat sdf;
            final PatientVisit visit = (PatientVisit) element;
            switch (columnIndex) {
            case 0:
                sdf = new SimpleDateFormat(BioBankPlugin.DATE_FORMAT);
                return sdf.format(visit.getDateDrawn());
            case 1:
                return "" + visit.getSampleCollection().size();
            }
        } else if (element instanceof ContainerType) {
            final ContainerType ct = (ContainerType) element;
            switch (columnIndex) {
            case 0:
                return ct.getName();
            case 1:
                return ct.getActivityStatus();
            case 2:
                return "" + ct.getDefaultTemperature();
            }
        } else if (element instanceof PvInfo) {
            final PvInfo pvInfo = (PvInfo) element;
            Integer type = pvInfo.getPvInfoType().getId();
            switch (columnIndex) {
            case 0:
                return pvInfo.getLabel();
            case 1:
                if ((type > 1) && (type <= 3))
                    return "N/A";
                return pvInfo.getPossibleValues();
            }
        } else if (element instanceof Container) {
            final Container container = (Container) element;
            switch (columnIndex) {
            case 0:
                return container.getLabel();
            case 1:
                return container.getActivityStatus();
            case 2:
                return container.getProductBarcode();
            case 3:
                return "" + container.getTemperature();
            }
        } else if (element instanceof Sample) {
            final Sample sample = (Sample) element;
            switch (columnIndex) {
            case 0:
                return sample.getInventoryId();
            case 1:
                return sample.getSampleType() == null ? "" : sample
                    .getSampleType().getName();
            case 2:
                return SampleWrapper.getPositionString(sample);
            case 3:
                return sample.getLinkDate() == null ? ""
                    : new SimpleDateFormat(BioBankPlugin.DATE_TIME_FORMAT)
                        .format(sample.getLinkDate());
            case 4:
                return sample.getQuantity() == null ? "" : sample.getQuantity()
                    .toString();
            case 5:
                return sample.getQuantityUsed() == null ? "" : sample
                    .getQuantityUsed().toString();
            case 6:
                return sample.getComment() == null ? "" : sample.getComment();
            }
        } else if (element instanceof SampleStorage) {
            final SampleStorage ss = (SampleStorage) element;
            switch (columnIndex) {
            case 0:
                return ss.getSampleType().getName();
            case 1:
                return "" + ss.getVolume();
            case 2:
                return "" + ss.getQuantity();
            }
        } else if (element instanceof SampleType) {
            final SampleType st = (SampleType) element;
            switch (columnIndex) {
            case 0:
                return st.getName();
            case 1:
                return "" + st.getNameShort();
            case 2:
                return "" + st.getId();
            }
        } else if (element instanceof BiobankCollectionModel) {
            BiobankCollectionModel m = (BiobankCollectionModel) element;
            if (m.o != null) {
                return getColumnText(m.o, columnIndex);
            } else if (columnIndex == 0) {
                return "loading ...";
            }
        } else if (element instanceof ClinicStudyInfo) {
            ClinicStudyInfo info = (ClinicStudyInfo) element;
            switch (columnIndex) {
            case 0:
                if (info.studyShortName != null)
                    return info.studyShortName;
                return "";
            case 1:
                return "" + info.patients;
            case 2:
                if (info.patientVisits != null)
                    return "" + info.patientVisits;
                return "";
            }
        } else if (element instanceof StudyContactAndPatientInfo) {
            StudyContactAndPatientInfo info = (StudyContactAndPatientInfo) element;
            switch (columnIndex) {
            case 0:
                if (info.clinicName != null)
                    return info.clinicName;
                return "";
            case 1:
                return "" + info.patients;
            case 2:
                if (info.patientVisits != null)
                    return "" + info.patientVisits;
                return "";
            default:
            }
            return getContactColumnIndex(info.contact, columnIndex - 2);
        } else if (element instanceof StudyContactInfo) {
            StudyContactInfo info = (StudyContactInfo) element;
            if (columnIndex == 0) {
                if (info.contact != null)
                    return info.contact.getClinic().getName();
                return "";
            }
            return getContactColumnIndex(info.contact, columnIndex);
        } else if (element instanceof SampleSource) {
            SampleSource info = (SampleSource) element;
            if (columnIndex == 0) {
                return info.getName();
            } else {
                Assert.isTrue(false, "invalid column index: " + columnIndex);
            }
        } else if (element instanceof PvSampleSource) {
            PvSampleSource info = (PvSampleSource) element;
            switch (columnIndex) {
            case 0:
                return info.getSampleSource().getName();
            case 1:
                return info.getQuantity().toString();
            }
        } else if (element instanceof Contact) {
            Contact contact = (Contact) element;
            switch (columnIndex) {
            case 0:
                return contact.getName();
            case 1:
                return contact.getTitle();
            case 2:
                return contact.getEmailAddress();
            case 3:
                return contact.getPhoneNumber();
            case 4:
                return contact.getFaxNumber();
            }
        } else if (element instanceof ModelWrapper<?>) {
            return getColumnText(
                ((ModelWrapper<?>) element).getWrappedObject(), columnIndex);
        } else {
            Assert.isTrue(false, "invalid object type: " + element.getClass());
        }
        return "";
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ContainerType) {
            return ((ContainerType) element).getName();
        } else if (element instanceof Study) {
            Study study = (Study) element;
            return study.getNameShort() + " - " + study.getName();
        } else if (element instanceof Clinic) {
            return ((Clinic) element).getName();
        } else if (element instanceof ContainerLabelingScheme) {
            return ((ContainerLabelingScheme) element).getName();
        } else if (element instanceof Site) {
            return ((Site) element).getName();
        } else if (element instanceof SampleType) {
            return ((SampleType) element).getName();
        }
        return ((AdapterBase) element).getName();
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    private String getContactColumnIndex(Contact contact, int columnIndex) {
        switch (columnIndex) {
        case 1:
            if ((contact != null) && (contact.getName() != null))
                return "" + contact.getName();
            break;
        case 2:
            if ((contact != null) && (contact.getTitle() != null))
                return "" + contact.getTitle();
            break;
        case 3:
            if ((contact != null) && (contact.getEmailAddress() != null))
                return "" + contact.getEmailAddress();
            break;
        case 4:
            if ((contact != null) && (contact.getPhoneNumber() != null))
                return "" + contact.getPhoneNumber();
            break;
        case 5:
            if ((contact != null) && (contact.getFaxNumber() != null))
                return "" + contact.getFaxNumber();
            break;
        }
        return "";
    }

}
