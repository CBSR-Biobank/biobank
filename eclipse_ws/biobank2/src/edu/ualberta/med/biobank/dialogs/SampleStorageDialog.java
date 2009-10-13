package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;

public class SampleStorageDialog extends BiobankDialog {

    private static final String TITLE = "Sample Storage";

    private SampleStorageWrapper origSampleStorage;

    private SampleStorageWrapper sampleStorage;

    private HashMap<String, SampleTypeWrapper> sampleTypeMap;

    private CCombo sampleTypesCombo;

    public SampleStorageDialog(Shell parent,
        SampleStorageWrapper sampleStorage,
        Collection<SampleTypeWrapper> sampleTypes) {
        super(parent);
        Assert.isNotNull(sampleStorage);
        Assert.isNotNull(sampleTypes);
        this.origSampleStorage = sampleStorage;
        this.sampleStorage = new SampleStorageWrapper(null);
        this.sampleStorage.setSampleType(sampleStorage.getSampleType());
        this.sampleStorage.setVolume(sampleStorage.getVolume());
        this.sampleStorage.setQuantity(sampleStorage.getQuantity());
        sampleTypeMap = new HashMap<String, SampleTypeWrapper>();
        for (SampleTypeWrapper st : sampleTypes) {
            sampleTypeMap.put(st.getName(), st);
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        Integer id = origSampleStorage.getId();
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

        Label label = new Label(contents, SWT.NONE);
        label.setText("Sample Type:");
        sampleTypesCombo = new CCombo(contents, SWT.BORDER | SWT.READ_ONLY);
        Set<String> sortedKeys = new TreeSet<String>(sampleTypeMap.keySet());
        for (String stName : sortedKeys) {
            sampleTypesCombo.add(stName);
        }

        SampleTypeWrapper st = origSampleStorage.getSampleType();
        if (st != null) {
            sampleTypesCombo.setText(st.getName());
        }

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER,
            "Volume (ml)", new String[0], PojoObservables.observeValue(
                sampleStorage, "volume"), new DoubleNumberValidator(
                "Volume should be a real number"));

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER,
            "Quantity", new String[0], PojoObservables.observeValue(
                sampleStorage, "quantity"), new IntegerNumberValidator(
                "Quantity should be a whole number"));

        return contents;
    }

    @Override
    protected void okPressed() {
        origSampleStorage.setSampleType(sampleTypeMap.get(sampleTypesCombo
            .getText()));
        origSampleStorage.setVolume(sampleStorage.getVolume());
        origSampleStorage.setQuantity(sampleStorage.getQuantity());
        super.okPressed();
    }

    public SampleStorageWrapper getSampleStorage() {
        return origSampleStorage;
    }

}
