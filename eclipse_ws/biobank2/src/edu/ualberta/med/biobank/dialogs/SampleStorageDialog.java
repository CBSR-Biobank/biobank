package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;
import java.util.HashMap;

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

import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;

public class SampleStorageDialog extends BiobankDialog {

    private static final String TITLE = " Sample Storage";

    private SampleStorage sampleStorage;

    private HashMap<String, SampleType> sampleTypeMap;

    private CCombo sampleTypesCombo;

    public SampleStorageDialog(Shell parent, SampleStorage sampleStorage,
        Collection<SampleType> sampleTypes) {
        super(parent);
        Assert.isNotNull(sampleStorage);
        Assert.isNotNull(sampleTypes);
        this.sampleStorage = sampleStorage;
        sampleTypeMap = new HashMap<String, SampleType>();
        for (SampleType st : sampleTypes) {
            sampleTypeMap.put(st.getName(), st);
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        Integer id = sampleStorage.getId();
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
        for (String stName : sampleTypeMap.keySet()) {
            sampleTypesCombo.add(stName);
        }

        createBoundWidgetWithLabel(contents, Text.class, SWT.NONE, "Volume",
            new String[0], PojoObservables
                .observeValue(sampleStorage, "volume"), DoubleNumber.class, "");

        createBoundWidgetWithLabel(contents, Text.class, SWT.NONE, "Quantity",
            new String[0], PojoObservables.observeValue(sampleStorage,
                "quantity"), IntegerNumber.class, "");

        return contents;

    }

    @Override
    protected void okPressed() {
        sampleStorage.setSampleType(sampleTypeMap.get(sampleTypesCombo
            .getText()));
        super.okPressed();
    }

    public SampleStorage getSampleStorage() {
        return sampleStorage;
    }

}
