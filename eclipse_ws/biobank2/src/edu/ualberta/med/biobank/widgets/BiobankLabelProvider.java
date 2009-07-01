package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyInfo;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
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
			final PatientVisit visit = ((PatientVisitAdapter) element)
				.getPatientVisit();
			switch (columnIndex) {
			case 0:
				return visit.getNumber();
			case 1:
				return "" + visit.getSampleCollection().size();
			}
		} else if (element instanceof StorageTypeAdapter) {
			final StorageTypeAdapter adapter = (StorageTypeAdapter) element;
			switch (columnIndex) {
			case 0:
				return adapter.getName();
			case 1:
				return adapter.getStorageType().getActivityStatus();
			case 2:
				return "" + adapter.getStorageType().getDefaultTemperature();
			}
		} else if (element instanceof StudyInfo) {
			final StudyInfo studyInfo = (StudyInfo) element;
			switch (columnIndex) {
			case 0:
				return studyInfo.getStudyInfoType().getType();
			case 1:
				return studyInfo.getPossibleValues();
			}
		} else if (element instanceof StorageContainer) {
			final StorageContainer container = (StorageContainer) element;
			switch (columnIndex) {
			case 0:
				return container.getName();
			case 1:
				return container.getActivityStatus();
			case 2:
				return container.getBarcode();
			case 3:
				Object o = container.getFull();
				if (o == null)
					return "";
				return (Boolean) o ? "Yes" : "No";

			case 4:
				return "" + container.getTemperature();
			}
		} else {
			Assert.isTrue(false, "invalid object type");
		}
		return "";
	}

	@Override
	public String getText(Object element) {
		if (element instanceof StorageType) {
			return ((StorageType) element).getName();
		}
		return ((Node) element).getName();
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
}
