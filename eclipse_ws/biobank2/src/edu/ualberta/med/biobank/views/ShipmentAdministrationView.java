package edu.ualberta.med.biobank.views;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.dialogs.SelectShipmentClinicDialog;
import edu.ualberta.med.biobank.rcp.ShipmentAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.DateNode;
import edu.ualberta.med.biobank.treeview.clinicShipment.ClinicShipmentAdapter;
import edu.ualberta.med.biobank.treeview.clinicShipment.ClinicWithShipmentAdapter;
import edu.ualberta.med.biobank.treeview.clinicShipment.ShipmentSearchedNode;
import edu.ualberta.med.biobank.treeview.clinicShipment.ShipmentTodayNode;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class ShipmentAdministrationView extends
    AbstractTodaySearchAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.ShipmentAdminView";

    private static ShipmentAdministrationView currentInstance;

    private Button radioWaybill;

    private Button radioDateReceived;

    private DateTimeWidget dateReceivedWidget;

    private Composite dateComposite;

    public ShipmentAdministrationView() {
        currentInstance = this;
        SessionManager.addView(ShipmentAdministrationPerspective.ID, this);
    }

    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioWaybill = new Button(composite, SWT.RADIO);
        radioWaybill.setText("Waybill");
        radioWaybill.setSelection(true);
        radioWaybill.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioWaybill.getSelection()) {
                    showTextOnly(true);
                }
            }
        });
        radioDateReceived = new Button(composite, SWT.RADIO);
        radioDateReceived.setText("Date Received");
        radioDateReceived.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioDateReceived.getSelection()) {
                    showTextOnly(false);
                }
            }
        });

        dateComposite = new Composite(parent, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        dateComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.exclude = true;
        dateComposite.setLayoutData(gd);

        dateReceivedWidget = new DateTimeWidget(dateComposite, SWT.DATE,
            new Date());
        Button searchButton = new Button(dateComposite, SWT.PUSH);
        searchButton.setText("Go");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                internalSearch();
            }
        });
    }

    protected void showTextOnly(boolean show) {
        treeText.setVisible(show);
        ((GridData) treeText.getLayoutData()).exclude = !show;
        dateComposite.setVisible(!show);
        ((GridData) dateComposite.getLayoutData()).exclude = show;
        treeText.getParent().layout(true, true);
    }

    @Override
    protected List<? extends ModelWrapper<?>> search(String text)
        throws Exception {
        if (radioWaybill.getSelection()) {
            // with waybill, should find only one corresponding shipment, or
            // mutliple shipments from different clinics
            List<ClinicShipmentWrapper> shipments = ClinicShipmentWrapper
                .getShipmentsInSite(SessionManager.getAppService(),
                    text.trim(), SessionManager.getInstance().getCurrentSite());
            if (shipments.size() > 1) {
                SelectShipmentClinicDialog dlg = new SelectShipmentClinicDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), shipments);
                if (dlg.open() == Dialog.OK) {
                    return Arrays.asList(dlg.getSelectedShipment());
                }
            } else {
                return shipments;
            }
        } else {
            // can find more than one shipments
            Date date = dateReceivedWidget.getDate();
            if (date != null) {
                return ClinicShipmentWrapper.getShipmentsInSite(SessionManager
                    .getAppService(), date, SessionManager.getInstance()
                    .getCurrentSite());
            }
        }
        return null;
    }

    @Override
    public AdapterBase addToNode(AdapterBase parentNode, ModelWrapper<?> wrapper) {
        if (wrapper instanceof ClinicShipmentWrapper) {
            ClinicShipmentWrapper shipment = (ClinicShipmentWrapper) wrapper;

            AdapterBase topNode = parentNode;
            if (parentNode.equals(searchedNode) && !radioWaybill.getSelection()) {
                Date date = dateReceivedWidget.getDate();
                AdapterBase dateNode = parentNode.search(date);
                if (dateNode == null) {
                    dateNode = new DateNode(parentNode,
                        dateReceivedWidget.getDate());
                    parentNode.addChild(dateNode);
                }
                topNode = dateNode;
            }
            ClinicWithShipmentAdapter clinicAdapter = (ClinicWithShipmentAdapter) topNode
                .search(shipment.getClinic());
            if (clinicAdapter == null) {
                clinicAdapter = new ClinicWithShipmentAdapter(topNode,
                    shipment.getClinic());
                clinicAdapter.setEditable(false);
                clinicAdapter.setLoadChildrenInBackground(false);
                topNode.addChild(clinicAdapter);
            }
            ClinicShipmentAdapter shipmentAdapter = (ClinicShipmentAdapter) clinicAdapter
                .search(shipment);
            if (shipmentAdapter == null) {
                shipmentAdapter = new ClinicShipmentAdapter(clinicAdapter,
                    shipment);
                clinicAdapter.addChild(shipmentAdapter);
            }
            return shipmentAdapter;
        }
        return null;
    }

    @Override
    protected void notFound(String text) {
        if (radioWaybill.getSelection()) {
            boolean create = BioBankPlugin.openConfirm("Shipment not found",
                "Do you want to create this shipment ?");
            if (create) {
                ClinicShipmentWrapper shipment = new ClinicShipmentWrapper(
                    SessionManager.getAppService());
                if (radioWaybill.getSelection()) {
                    shipment.setWaybill(text);
                }
                ClinicShipmentAdapter adapter = new ClinicShipmentAdapter(
                    searchedNode, shipment);
                adapter.openEntryForm();
            }
        } else {
            BioBankPlugin.openMessage(
                "Shipment not found",
                "No shipment found for date "
                    + DateFormatter.formatAsDate(dateReceivedWidget.getDate()));
        }
    }

    @Override
    protected AbstractTodayNode getTodayNode() {
        return new ShipmentTodayNode(rootNode, 0);
    }

    @Override
    protected AbstractSearchedNode getSearchedNode() {
        return new ShipmentSearchedNode(rootNode, 1);
    }

    public static void showShipment(ClinicShipmentWrapper shipment) {
        if (currentInstance != null) {
            currentInstance.showSearchedObjectsInTree(Arrays.asList(shipment),
                false);
        }
    }

    public static ShipmentAdministrationView getCurrent() {
        return currentInstance;
    }

    public static ClinicShipmentAdapter getCurrentShipment() {
        AdapterBase selectedNode = currentInstance.getSelectedNode();
        if (selectedNode != null
            && selectedNode instanceof ClinicShipmentAdapter) {
            return (ClinicShipmentAdapter) selectedNode;
        }
        return null;
    }

}
