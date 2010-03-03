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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;

public class PvSampleSourceDialog extends BiobankDialog {

    private static final String TITLE = "Source Vessel";

    private PvSourceVesselWrapper pvSampleSource;

    private ComboViewer sampleSourcesComboViewer;

    private Collection<SampleSourceWrapper> sampleSources;

    public PvSampleSourceDialog(Shell parent,
        PvSourceVesselWrapper pvSampleSource,
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
        String title = new String();

        if (pvSampleSource.isNew()) {
            title = "Add ";
        } else {
            title = "Edit ";
        }
        title += TITLE;
        shell.setText(title);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitleImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_COMPUTER_KEY));
        if (pvSampleSource.isNew()) {
            setTitle("Add Source Vessel");
            setMessage("Add a source vessel to a patient visit");
        } else {
            setTitle("Edit Source Vessel");
            setMessage("Edit a source vessel in a patient visit");
        }
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sampleSourcesComboViewer = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Source Vessel", sampleSources,
                pvSampleSource.getSourceVessel(),
                "A source vessel should be selected");
        sampleSourcesComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) sampleSourcesComboViewer
                        .getSelection();
                    pvSampleSource
                        .setSourceVessel((SampleSourceWrapper) stSelection
                            .getFirstElement());
                }
            });
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        sampleSourcesComboViewer.getCombo().setLayoutData(gd);

        Control c = createBoundWidgetWithLabel(contents, Text.class,
            SWT.BORDER, "Quantity", new String[0], BeansObservables
                .observeValue(pvSampleSource, "quantity"),
            new IntegerNumberValidator("quantity should be a whole number",
                false));
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        c.setLayoutData(gd);

        c = createDateTimeWidget(contents, "Date drawn", null, pvSampleSource,
            "dateDrawn", "Date drawn should be selected");
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        c.setLayoutData(gd);
    }

    public PvSourceVesselWrapper getPvSampleSource() {
        return pvSampleSource;
    }

}
