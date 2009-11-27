package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class PvSampleSourceDialog extends BiobankDialog {

    private static final String TITLE = "Sample Source";

    private PvSampleSourceWrapper pvSampleSource;

    private ComboViewer sampleSourcesComboViewer;

    private DateTimeWidget dateDrawnWidget;

    private Collection<SampleSourceWrapper> sampleSources;

    public PvSampleSourceDialog(Shell parent,
        PvSampleSourceWrapper pvSampleSource,
        Collection<SampleSourceWrapper> sampleSources) {
        super(parent);
        Assert.isNotNull(pvSampleSource);
        Assert.isNotNull(sampleSources);
        this.pvSampleSource = pvSampleSource;
        this.sampleSources = sampleSources;
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
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sampleSourcesComboViewer = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Sample Source", sampleSources,
                pvSampleSource.getSampleSource(),
                "A sample source should be selected");
        sampleSourcesComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) sampleSourcesComboViewer
                        .getSelection();
                    pvSampleSource
                        .setSampleSource((SampleSourceWrapper) stSelection
                            .getFirstElement());
                }
            });
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        sampleSourcesComboViewer.getCombo().setLayoutData(gd);

        Text quantityText = (Text) createBoundWidgetWithLabel(contents,
            Text.class, SWT.BORDER, "Quantity", new String[0], BeansObservables
                .observeValue(pvSampleSource, "quantity"),
            new IntegerNumberValidator("quantity should be a whole number",
                false));
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        quantityText.setLayoutData(gd);

        dateDrawnWidget = createDateTimeWidget(contents, "Date drawn", null,
            pvSampleSource, "dateDrawn", "Date drawn should be selected");
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        dateDrawnWidget.setLayoutData(gd);

        return contents;
    }

    public PvSampleSourceWrapper getPvSampleSource() {
        return pvSampleSource;
    }

}
