package edu.ualberta.med.biobank.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.infotables.PatientVisitInfoTable;

public class SelectPatientVisitPage extends BiobankWizardPage {
    public static final String PAGE_NAME = SelectPatientVisitPage.class
        .getCanonicalName();
    private static final String PATIENT_VISIT_REQUIRED = "Please select a patient visit.";
    private PatientVisitInfoTable visitsTable;

    public SelectPatientVisitPage() {
        super(PAGE_NAME, "Select a patient visit", null);
    }

    public void setPatientVisitList(List<ProcessingEventWrapper> visits) {
        visitsTable.setCollection(visits);
    }

    public ProcessingEventWrapper getProcessingEvent() {
        return visitsTable.getSelection();
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final IObservableValue selection = new WritableValue(null, Object.class);
        visitsTable = new PatientVisitInfoTable(content,
            new ArrayList<ProcessingEventWrapper>()) {
            @Override
            public boolean isEditMode() {
                return true;
            }
        };
        visitsTable.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selection.setValue(getProcessingEvent());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                selection.setValue(getProcessingEvent());
            }
        });

        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new NotNullValidator(PATIENT_VISIT_REQUIRED));
        getWidgetCreator().bindValue(selection, new WritableValue(), uvs, null);

        setControl(content);
    }
}