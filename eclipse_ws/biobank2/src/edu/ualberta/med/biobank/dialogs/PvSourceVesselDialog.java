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
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;

public class PvSourceVesselDialog extends BiobankDialog {

    private static final String TITLE = "Source Vessel";

    private PvSourceVesselWrapper pvSourceVessel;

    private ComboViewer sourceVesselsComboViewer;

    private Collection<SourceVesselWrapper> sourceVessels;

    public PvSourceVesselDialog(Shell parent,
        PvSourceVesselWrapper pvSourceVessel,
        Collection<SourceVesselWrapper> sourceVessels) {
        super(parent);
        Assert.isNotNull(pvSourceVessel);
        Assert.isNotNull(sourceVessels);
        this.pvSourceVessel = pvSourceVessel;
        this.sourceVessels = sourceVessels;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = new String();

        if (pvSourceVessel.isNew()) {
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
        if (pvSourceVessel.isNew()) {
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

        sourceVesselsComboViewer = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Source Vessel", sourceVessels,
                pvSourceVessel.getSourceVessel(),
                "A source vessel should be selected");
        sourceVesselsComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) sourceVesselsComboViewer
                        .getSelection();
                    pvSourceVessel
                        .setSourceVessel((SourceVesselWrapper) stSelection
                            .getFirstElement());
                }
            });
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        sourceVesselsComboViewer.getCombo().setLayoutData(gd);

        Control c = createBoundWidgetWithLabel(contents, Text.class,
            SWT.BORDER, "Quantity", new String[0], BeansObservables
                .observeValue(pvSourceVessel, "quantity"),
            new IntegerNumberValidator("quantity should be a whole number",
                false));
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        c.setLayoutData(gd);

        c = createDateTimeWidget(contents, "Date drawn", null, pvSourceVessel,
            "dateDrawn", "Date drawn should be selected");
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        c.setLayoutData(gd);
    }

    public PvSourceVesselWrapper getPvSourceVessel() {
        return pvSourceVessel;
    }

}
