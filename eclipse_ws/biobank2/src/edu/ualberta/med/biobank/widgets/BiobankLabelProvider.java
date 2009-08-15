package edu.ualberta.med.biobank.widgets;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicStudyInfo;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyClinicInfo;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

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
        if (element instanceof StudyAdapter) {
            final Study study = ((StudyAdapter) element).getStudy();
            switch (columnIndex) {
            case 0:
                return study.getName();
            case 1:
                return study.getNameShort();
            case 2:
                return "" + study.getPatientCollection().size();
            }
        } else if (element instanceof ClinicAdapter) {
            final ClinicAdapter clinicAdapter = (ClinicAdapter) element;
            switch (columnIndex) {
            case 0:
                return clinicAdapter.getName();
            case 1:
                return ""
                    + clinicAdapter.getClinic().getStudyCollection().size();
            }
        } else if (element instanceof PatientAdapter) {
            final Patient patient = ((PatientAdapter) element).getPatient();
            switch (columnIndex) {
            case 0:
                return patient.getNumber();
            }
        } else if (element instanceof PatientVisitAdapter) {
            SimpleDateFormat sdf;
            final PatientVisit visit = ((PatientVisitAdapter) element)
                .getPatientVisit();
            switch (columnIndex) {
            case 0:
                sdf = new SimpleDateFormat(BioBankPlugin.DATE_FORMAT);
                return sdf.format(visit.getDateDrawn());
            case 1:
                return "" + visit.getSampleCollection().size();
            }
        } else if (element instanceof ContainerTypeAdapter) {
            final ContainerTypeAdapter adapter = (ContainerTypeAdapter) element;
            switch (columnIndex) {
            case 0:
                return adapter.getName();
            case 1:
                return adapter.getContainerType().getActivityStatus();
            case 2:
                return "" + adapter.getContainerType().getDefaultTemperature();
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
                Object o = container.getFull();
                if (o == null)
                    return "";
                return (Boolean) o ? "Yes" : "No";

            case 4:
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
                return ModelUtils.getSamplePosition(sample);
            case 3:
                return sample.getProcessDate() == null ? ""
                    : new SimpleDateFormat(BioBankPlugin.DATE_TIME_FORMAT)
                        .format(sample.getProcessDate());
            case 4:
                return sample.getAvailable() == null ? "" : sample
                    .getAvailable().toString();
            case 5:
                return sample.getQuantity() == null ? "" : sample.getQuantity()
                    .toString();
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
        } else if (element instanceof StudyClinicInfo) {
            StudyClinicInfo info = (StudyClinicInfo) element;
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
            }
        } else if (element instanceof SampleSource) {
            SampleSource info = (SampleSource) element;
            if (columnIndex == 0) {
                return info.getName();
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
        if (element instanceof ContainerType) {
            return ((ContainerType) element).getName();
        } else if (element instanceof Clinic) {
            return ((Clinic) element).getName();
        } else if (element instanceof ContainerLabelingScheme) {
            return ((ContainerLabelingScheme) element).getName();
        }
        return ((AdapterBase) element).getName();
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}
