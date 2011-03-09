package edu.ualberta.med.biobank.views;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.DateNode;
import edu.ualberta.med.biobank.treeview.shipment.ClinicWithShipmentAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentSearchedNode;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentTodayNode;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class ShipmentAdministrationView extends
    AbstractTodaySearchAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.ShipmentAdminView";

    private static ShipmentAdministrationView currentInstance;

    private Button radioWaybill;

    private Button radioDateReceived;

    private DateTimeWidget dateReceivedWidget;

    private Composite dateComposite;

    private Composite searchFieldComposite;

    public ShipmentAdministrationView() {
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    protected void createTreeTextOptions(Composite parent) {
        searchFieldComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        searchFieldComposite.setLayout(layout);

        radioWaybill = new Button(searchFieldComposite, SWT.RADIO);
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
        radioDateReceived = new Button(searchFieldComposite, SWT.RADIO);
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
        dateReceivedWidget.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                internalSearch();
            }
        });
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
            List<OriginInfoWrapper> shipments = OriginInfoWrapper
                .getShipmentsByWaybill(SessionManager.getAppService(),
                    text.trim());
            // TODO: allow selection of specific clinic?
            // if (shipments.size() > 1) {
            // SelectShipmentClinicDialog dlg = new SelectShipmentClinicDialog(
            // PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            // .getShell(), shipments);
            // if (dlg.open() == Dialog.OK) {
            // return Arrays.asList(dlg.getSelectedShipment());
            // }
            // } else {
            return shipments;
            // }
        } else {
            // can find more than one shipments
            Date date = dateReceivedWidget.getDate();
            if (date != null) {
                return OriginInfoWrapper.getShipmentsByDateReceived(
                    SessionManager.getAppService(), date);
            }
        }
        return null;
    }

    public static AdapterBase addToNode(AdapterBase parentNode,
        ModelWrapper<?> wrapper) {
        if (currentInstance != null && wrapper instanceof OriginInfoWrapper) {
            OriginInfoWrapper originInfo = (OriginInfoWrapper) wrapper;

            AdapterBase topNode = parentNode;
            if (parentNode.equals(currentInstance.searchedNode)
                && !currentInstance.radioWaybill.getSelection()) {
                Date date = currentInstance.dateReceivedWidget.getDate();
                List<AdapterBase> dateNodeRes = parentNode.search(date);
                AdapterBase dateNode = null;
                if (dateNodeRes.size() > 0)
                    dateNode = dateNodeRes.get(0);
                else {
                    dateNode = new DateNode(parentNode,
                        currentInstance.dateReceivedWidget.getDate());
                    parentNode.addChild(dateNode);
                }
                topNode = dateNode;
            }

            List<AdapterBase> clinicAdapterList = topNode.search(originInfo
                .getCenter());
            ClinicWithShipmentAdapter clinicAdapter = null;
            if (clinicAdapterList.size() > 0)
                clinicAdapter = (ClinicWithShipmentAdapter) clinicAdapterList
                    .get(0);
            else if (originInfo.getCenter() instanceof ClinicWrapper) {
                clinicAdapter = new ClinicWithShipmentAdapter(topNode,
                    (ClinicWrapper) originInfo.getCenter());
                clinicAdapter.setEditable(false);
                clinicAdapter.setLoadChildrenInBackground(false);
                topNode.addChild(clinicAdapter);
            }

            if (clinicAdapter != null) {
                ShipmentAdapter shipmentAdapter = null;
                List<AdapterBase> shipmentAdapterList = clinicAdapter
                    .search(originInfo);
                if (shipmentAdapterList.size() > 0)
                    shipmentAdapter = (ShipmentAdapter) shipmentAdapterList
                        .get(0);
                else {
                    shipmentAdapter = new ShipmentAdapter(clinicAdapter,
                        originInfo);
                    clinicAdapter.addChild(shipmentAdapter);
                }
                return shipmentAdapter;
            }
        }
        return null;
    }

    @Override
    protected void notFound(String text) {
        if (radioWaybill.getSelection()) {
            boolean create = BiobankPlugin.openConfirm("Shipment not found",
                "Do you want to create this shipment ?");
            if (create) {
                // FIXME
                // CollectionEventWrapper shipment = new CollectionEventWrapper(
                // SessionManager.getAppService());
                // if (radioWaybill.getSelection()) {
                // shipment.setWaybill(text);
                // }
                // ShipmentAdapter adapter = new ShipmentAdapter(searchedNode,
                // shipment);
                // adapter.openEntryForm();
            }
        } else {
            BiobankPlugin.openMessage(
                "Shipment not found",
                "No shipment found for date "
                    + DateFormatter.formatAsDate(dateReceivedWidget.getDate()));
        }
    }

    @Override
    protected AbstractTodayNode<?> createTodayNode() {
        return new ShipmentTodayNode(rootNode, 0);
    }

    @Override
    protected AbstractSearchedNode createSearchedNode() {
        return new ShipmentSearchedNode(rootNode, 1);
    }

    public static void showShipment(OriginInfoWrapper shipment) {
        if (currentInstance != null) {
            currentInstance.showSearchedObjectsInTree(Arrays.asList(shipment),
                false);
        }
    }

    public static ShipmentAdministrationView getCurrent() {
        return currentInstance;
    }

    public static void reloadCurrent() {
        if (currentInstance != null)
            currentInstance.reload();
    }

    public static ShipmentAdapter getCurrentShipment() {
        AdapterBase selectedNode = currentInstance.getSelectedNode();
        if (selectedNode != null && selectedNode instanceof ShipmentAdapter) {
            return (ShipmentAdapter) selectedNode;
        }
        return null;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTreeTextToolTip() {
        return "Enter a shipment waybill and hit enter";
    }

    @Override
    protected String getString() {
        return toString();
    }
}
