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

import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;

/**
 * Allows the user to choose the clinic from which the shipment comes from
 */

public class SelectShipmentClinicDialog extends BiobankDialog {
    private List<ClinicShipmentWrapper> shipments;
    private ComboViewer comboViewer;
    protected ClinicShipmentWrapper selectedShipment;

    public SelectShipmentClinicDialog(Shell parent,
        List<ClinicShipmentWrapper> shipments) {
        super(parent);
        Assert.isNotNull(shipments);
        this.shipments = shipments;
    }

    @Override
    protected String getDialogShellTitle() {
        return "Shipment Select";
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);

        setTitle("Select Shipment's Clinic");
        setMessage("More than one shipment with waybill '"
            + shipments.get(0).getWaybill()
            + "' found.\nPlease choose the clinic associated with this shipment.");
        return contents;
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
                ClinicWrapper clinic = ((ClinicShipmentWrapper) element)
                    .getClinic();
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
    }

    private void saveSelectedShipment() {
        selectedShipment = (ClinicShipmentWrapper) ((IStructuredSelection) comboViewer
            .getSelection()).getFirstElement();
    }

    public ClinicShipmentWrapper getSelectedShipment() {
        return selectedShipment;
    }

}
