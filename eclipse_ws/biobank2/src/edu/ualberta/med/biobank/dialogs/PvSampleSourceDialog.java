package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class PvSampleSourceDialog extends BiobankDialog {

    private static final String TITLE = "Sample Source";

    private PvSampleSourceWrapper pvSampleSource;

    private HashMap<String, SampleSource> sampleSourceMap;

    private CCombo sampleSourcesCombo;

    private IObservableValue sampleSourceSelection = new WritableValue("",
        String.class);

    public PvSampleSourceDialog(Shell parent,
        PvSampleSourceWrapper pvSampleSource,
        Collection<SampleSource> sampleSources) {
        super(parent);
        Assert.isNotNull(pvSampleSource);
        Assert.isNotNull(sampleSources);
        this.pvSampleSource = pvSampleSource;
        sampleSourceMap = new HashMap<String, SampleSource>();
        for (SampleSource ss : sampleSources) {
            sampleSourceMap.put(ss.getName(), ss);
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        Integer id = pvSampleSource.getId();
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
        sampleSourcesCombo = (CCombo) createBoundWidgetWithLabel(contents,
            CCombo.class, SWT.BORDER, "Sample Source", sortedKeys
                .toArray(new String[sortedKeys.size()]), sampleSourceSelection,
            new NonEmptyString("a sample source should be selected"));

        SampleSource ss = pvSampleSource.getSampleSource();
        if (ss != null) {
            sampleSourcesCombo.setText(ss.getName());
        }

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER,
            "Quantity", new String[0], PojoObservables.observeValue(
                pvSampleSource, "quantity"), new IntegerNumberValidator(
                "quantity should be a whole number", false));

        return contents;
    }

    @Override
    protected void okPressed() {
        pvSampleSource.setSampleSource(sampleSourceMap.get(sampleSourcesCombo
            .getText()));
        super.okPressed();
    }

    public PvSampleSourceWrapper getPvSampleSource() {
        return pvSampleSource;
    }

}
