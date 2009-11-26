package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShptSampleSourceDialog extends BiobankDialog {

    private static final String TITLE = "Sample Source";

    private ShptSampleSourceWrapper shptSampleSource;

    // private HashMap<String, SampleSourceWrapper> sampleSourceMap;

    private ComboViewer sampleSourcesComboViewer;

    private IObservableValue sampleSourceSelection = new WritableValue("",
        String.class);

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
        // sampleSourceMap = new HashMap<String, SampleSourceWrapper>();
        // for (SampleSourceWrapper ss : sampleSources) {
        // sampleSourceMap.put(ss.getName(), ss);
        // }
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

        // Set<String> sortedKeys = new
        // TreeSet<String>(sampleSourceMap.keySet());
        sampleSourcesComboViewer = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Sample Source", sampleSources,
                shptSampleSource.getSampleSource(),
                "A sample source should be selected");
        // sampleSourcesCombo = (Combo) createBoundWidgetWithLabel(contents,
        // Combo.class, SWT.BORDER, , sortedKeys
        // .toArray(new String[sortedKeys.size()]), sampleSourceSelection,
        // new NonEmptyString("a sample source should be selected"));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        sampleSourcesComboViewer.getCombo().setLayoutData(gd);
        // SampleSourceWrapper ss = shptSampleSource.getSampleSource();
        // if (ss != null) {
        // sampleSourcesCombo.setText(ss.getName());
        // }

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

        patientText = (Text) createBoundWidgetWithLabel(contents, Text.class,
            SWT.BORDER, "Patient Number", new String[0], null, null);
        patientText.setText(shptSampleSource.getPatientsAsString());
        splitButton = new Button(contents, SWT.TOGGLE);
        splitButton.setText("Split");

        patient2Label = getWidgetCreator().createLabel(contents,
            "Second Patient Number");
        patient2Label.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        patient2Text = (Text) getWidgetCreator().createBoundWidget(contents,
            Text.class, SWT.BORDER, null, null, null);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        patient2Text.setLayoutData(gd);
        showSecondPatient(false);

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
        // shptSampleSource.setSampleSource(sampleSourceMap.get(sampleSourcesCombo
        // .getText()));
        try {
            shptSampleSource.setPatientsFromString(patientText.getText(),
                SessionManager.getInstance().getCurrentSiteWrapper());
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.okPressed();
    }

    public ShptSampleSourceWrapper getShptSampleSource() {
        return shptSampleSource;
    }

}
