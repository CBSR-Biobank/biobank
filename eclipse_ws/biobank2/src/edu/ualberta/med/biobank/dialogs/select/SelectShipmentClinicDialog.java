package edu.ualberta.med.biobank.dialogs.select;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;

/**
 * Allows the user to choose the clinic from which the shipment comes from
 */

@Deprecated
public class SelectShipmentClinicDialog extends BgcBaseDialog {
    private List<CollectionEventWrapper> shipments;
    private ComboViewer comboViewer;
    protected CollectionEventWrapper selectedShipment;

    public SelectShipmentClinicDialog(Shell parent,
        List<CollectionEventWrapper> shipments) {
        super(parent);
        Assert.isNotNull(shipments);
        this.shipments = shipments;
    }

    @Override
    protected String getDialogShellTitle() {
        return "Shipment Select";
    }

    @Override
    protected String getTitleAreaMessage() {
        // return "More than one shipment with waybill '"
        // + shipments.get(0).getWaybill()
        // +
        // "' found.\nPlease choose the clinic associated with this shipment.";
        return null;
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Select Shipment's Clinic";
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        getWidgetCreator().createLabel(contents, "Clinics");
        comboViewer = new ComboViewer(contents, SWT.READ_ONLY | SWT.BORDER);
        comboViewer.getCombo().setLayoutData(
            new GridData(SWT.FILL, SWT.TOP, true, false));
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                // FIXME
                // ClinicWrapper clinic = ((CollectionEventWrapper) element)
                // .getClinic();
                // return clinic.getName();
                return null;
            }
        });
        comboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    saveSelectedShipment();
                }
            });
        comboViewer.setComparator(new ViewerComparator());
        comboViewer.setInput(shipments);
        saveSelectedShipment();
    }

    private void saveSelectedShipment() {
        selectedShipment = (CollectionEventWrapper) ((IStructuredSelection) comboViewer
            .getSelection()).getFirstElement();
    }

    public CollectionEventWrapper getSelectedShipment() {
        return selectedShipment;
    }

}
