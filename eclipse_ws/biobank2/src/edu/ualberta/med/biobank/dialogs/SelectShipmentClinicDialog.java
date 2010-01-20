package edu.ualberta.med.biobank.dialogs;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;

/**
 * Allows the user to choose the clinic from which the shipment comes from
 */

public class SelectShipmentClinicDialog extends BiobankDialog {
    private List<ShipmentWrapper> shipments;
    private ComboViewer comboViewer;
    protected ShipmentWrapper selectedShipment;

    public SelectShipmentClinicDialog(Shell parent,
        List<ShipmentWrapper> shipments) {
        super(parent);
        Assert.isNotNull(shipments);
        this.shipments = shipments;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Selecting shipment");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        setStatusMessage("More than one shipment with waybill '"
            + shipments.get(0).getWaybill()
            + "' found.\nPlease choose the clinic associated with this shipment.");

        getWidgetCreator().createLabel(contents, "Clinics");
        comboViewer = new ComboViewer(contents, SWT.READ_ONLY | SWT.BORDER);
        comboViewer.getCombo().setLayoutData(
            new GridData(SWT.FILL, SWT.TOP, true, false));
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                ClinicWrapper clinic = ((ShipmentWrapper) element).getClinic();
                return clinic.getName();
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
        return parentComposite;
    }

    private void saveSelectedShipment() {
        selectedShipment = (ShipmentWrapper) ((IStructuredSelection) comboViewer
            .getSelection()).getFirstElement();
    }

    public ShipmentWrapper getSelectedShipment() {
        return selectedShipment;
    }

}
