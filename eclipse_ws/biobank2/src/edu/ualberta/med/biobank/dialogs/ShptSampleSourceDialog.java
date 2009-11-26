package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperException;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class ShptSampleSourceDialog extends BiobankDialog {

    private static final String TITLE = "Sample Source";

    private ShptSampleSourceWrapper shptSampleSource;

    private ComboViewer sampleSourcesComboViewer;

    private Text patientText;

    private Text patient2Text;

    private Label patient2Label;

    private DateTimeWidget dateDrawnWidget;

    private Button splitButton;

    private Collection<SampleSourceWrapper> sampleSources;

    public ShptSampleSourceDialog(Shell parent,
        ShptSampleSourceWrapper pvSampleSource,
        Collection<SampleSourceWrapper> sampleSources) {
        super(parent);
        Assert.isNotNull(pvSampleSource);
        Assert.isNotNull(sampleSources);
        this.shptSampleSource = pvSampleSource;
        this.sampleSources = sampleSources;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        Integer id = shptSampleSource.getId();
        String title = new String();

        if (id == null) {
            title = "Add";
        } else {
            title = "Edit ";
        }
        title += TITLE;
        shell.setText(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sampleSourcesComboViewer = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Sample Source", sampleSources,
                shptSampleSource.getSampleSource(),
                "A sample source should be selected");
        sampleSourcesComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) sampleSourcesComboViewer
                        .getSelection();
                    shptSampleSource
                        .setSampleSource((SampleSourceWrapper) stSelection
                            .getFirstElement());
                }
            });
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        sampleSourcesComboViewer.getCombo().setLayoutData(gd);

        Text quantityText = (Text) createBoundWidgetWithLabel(contents,
            Text.class, SWT.BORDER, "Quantity", new String[0], BeansObservables
                .observeValue(shptSampleSource, "quantity"),
            new IntegerNumberValidator("quantity should be a whole number",
                false));
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        quantityText.setLayoutData(gd);

        dateDrawnWidget = createDateTimeWidget(contents, "Date drawn", null,
            shptSampleSource, "dateDrawn", null, true);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        dateDrawnWidget.setLayoutData(gd);

        List<PatientWrapper> patients = shptSampleSource.getPatientCollection();
        String firstPatient = "";
        String secondPatient = "";
        if (patients != null && patients.size() > 0) {
            firstPatient = patients.get(0).getNumber();
            if (patients.size() == 2) {
                secondPatient = patients.get(1).getNumber();
            }
        }
        patientText = (Text) createBoundWidgetWithLabel(contents, Text.class,
            SWT.BORDER, "Patient Number", new String[0], null, null);
        patientText.setText(firstPatient);
        splitButton = new Button(contents, SWT.TOGGLE);
        splitButton.setText("Split");

        patient2Label = getWidgetCreator().createLabel(contents,
            "Second Patient");
        patient2Label.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        patient2Text = (Text) getWidgetCreator().createBoundWidget(contents,
            Text.class, SWT.BORDER, null, null, null);
        patient2Text.setText(secondPatient);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        patient2Text.setLayoutData(gd);
        splitButton.setSelection(!secondPatient.isEmpty());
        showSecondPatient(!secondPatient.isEmpty());

        splitButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showSecondPatient(splitButton.getSelection());
                getShell().layout(true);
                getShell().pack();

            }
        });
        return contents;
    }

    private void showSecondPatient(boolean show) {
        patient2Label.setVisible(show);
        ((GridData) patient2Label.getLayoutData()).exclude = !show;
        patient2Text.setVisible(show);
        ((GridData) patient2Text.getLayoutData()).exclude = !show;
    }

    @Override
    protected void okPressed() {
        List<String> patients = new ArrayList<String>();
        patients.add(patientText.getText());
        if (splitButton.getSelection()) {
            patients.add(patient2Text.getText());
        }
        try {
            shptSampleSource.setPatientsFromString(patients, SessionManager
                .getInstance().getCurrentSiteWrapper());
        } catch (WrapperException e) {
            BioBankPlugin.openAsyncError("Patient error", e);
            return;
        }
        super.okPressed();
    }

    public ShptSampleSourceWrapper getShptSampleSource() {
        return shptSampleSource;
    }

}
