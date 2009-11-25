package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShptSampleSourceDialog extends BiobankDialog {

    private static final String TITLE = "Sample Source";

    private ShptSampleSourceWrapper shptSampleSource;

    private HashMap<String, SampleSourceWrapper> sampleSourceMap;

    private Combo sampleSourcesCombo;

    private IObservableValue sampleSourceSelection = new WritableValue("",
        String.class);

    private Text patientsText;

    public ShptSampleSourceDialog(Shell parent,
        ShptSampleSourceWrapper pvSampleSource,
        Collection<SampleSourceWrapper> sampleSources) {
        super(parent);
        Assert.isNotNull(pvSampleSource);
        Assert.isNotNull(sampleSources);
        this.shptSampleSource = pvSampleSource;
        sampleSourceMap = new HashMap<String, SampleSourceWrapper>();
        for (SampleSourceWrapper ss : sampleSources) {
            sampleSourceMap.put(ss.getName(), ss);
        }
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
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Set<String> sortedKeys = new TreeSet<String>(sampleSourceMap.keySet());
        sampleSourcesCombo = (Combo) createBoundWidgetWithLabel(contents,
            Combo.class, SWT.BORDER, "Sample Source", sortedKeys
                .toArray(new String[sortedKeys.size()]), sampleSourceSelection,
            new NonEmptyString("a sample source should be selected"));

        SampleSourceWrapper ss = shptSampleSource.getSampleSource();
        if (ss != null) {
            sampleSourcesCombo.setText(ss.getName());
        }

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER,
            "Quantity", new String[0], BeansObservables.observeValue(
                shptSampleSource, "quantity"), new IntegerNumberValidator(
                "quantity should be a whole number", false));

        patientsText = (Text) createBoundWidgetWithLabel(contents, Text.class,
            SWT.BORDER, "Patient(s)", new String[0], null, null);
        patientsText.setText(shptSampleSource.getPatientsAsString());

        return contents;
    }

    @Override
    protected void okPressed() {
        shptSampleSource.setSampleSource(sampleSourceMap.get(sampleSourcesCombo
            .getText()));
        try {
            shptSampleSource.setPatientsFromString(patientsText.getText(),
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
