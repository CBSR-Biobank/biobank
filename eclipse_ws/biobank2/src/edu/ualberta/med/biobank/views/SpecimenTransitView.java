package edu.ualberta.med.biobank.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.DateNode;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchCenterAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.OriginInfoSearchedNode;
import edu.ualberta.med.biobank.treeview.shipment.ClinicWithShipmentAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentTodayNode;

public class SpecimenTransitView extends AbstractTodaySearchAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.SpecimenTransitView"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger
        .getLogger(SpecimenTransitView.class.getName());

    private Button radioWaybill;

    private Composite dateComposite;

    private DateTimeWidget dateWidget;

    private DispatchCenterAdapter centerNode;

    private Button radioDateReceived;

    private Button radioDateSent;

    private static SpecimenTransitView currentInstance;

    public SpecimenTransitView() {
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
    }

    public void createNodes() throws Exception {
        if (SessionManager.getUser().getCurrentWorkingCenter() != null) {
            SessionManager.getUser().getCurrentWorkingCenter().reload();
            centerNode = new DispatchCenterAdapter(rootNode, SessionManager
                .getUser().getCurrentWorkingCenter());
            centerNode.setParent(rootNode);
            rootNode.addChild(centerNode);
        }

        todayNode = createTodayNode();
        todayNode.setParent(rootNode);
        rootNode.addChild(todayNode);

        searchedNode = createSearchedNode();
        searchedNode.setParent(rootNode);
        rootNode.addChild(searchedNode);
    }

    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioWaybill = new Button(composite, SWT.RADIO);
        radioWaybill.setText(Messages.SpecimenTransitView_waybill_label);
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
        radioDateReceived.setText(Messages.SpecimenTransitView_received_label);
        radioDateReceived.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioDateReceived.getSelection()) {
                    showTextOnly(false);
                }
            }
        });

        radioDateSent = new Button(composite, SWT.RADIO);
        radioDateSent.setText(Messages.SpecimenTransitView_packed_label);
        radioDateSent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioDateSent.getSelection()) {
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

        dateWidget = new DateTimeWidget(dateComposite, SWT.DATE, new Date());
        dateWidget.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                internalSearch();
            }
        });
        Button searchButton = new Button(dateComposite, SWT.PUSH);
        searchButton.setText(Messages.SpecimenTransitView_go_label);
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
    public void reload() {
        if (rootNode != null) {
            rootNode.removeAll();
            try {
                createNodes();
            } catch (Exception e) {
                logger.error(Messages.SpecimenTransitView_nodes_error_title, e);

            }
            for (AdapterBase adaper : rootNode.getChildren()) {
                adaper.rebuild();
            }
        }
        super.reload();
    }

    @Override
    protected void internalSearch() {
        try {
            List<? extends ModelWrapper<?>> searchedObject = search();
            if (searchedObject == null || searchedObject.size() == 0) {
                String msg;
                if (radioWaybill.getSelection()) {
                    msg = NLS
                        .bind(
                            Messages.SpecimenTransitView_notfound_waybill_error_msg,
                            treeText.getText());
                } else {
                    msg = NLS.bind(
                        Messages.SpecimenTransitView_notfound_date_error_msg,
                        DateFormatter.formatAsDate(dateWidget.getDate()));
                }
                BgcPlugin.openMessage(
                    Messages.SpecimenTransitView_notfound_error_title, msg);
            } else {
                showSearchedObjectsInTree(searchedObject, true);
                getTreeViewer().expandToLevel(searchedNode, 2);
            }
        } catch (Exception e) {
            BgcPlugin.openError(Messages.SpecimenTransitView_search_error_msg,
                e);
        }
    }

    protected List<ModelWrapper<?>> search() throws Exception {
        List<ModelWrapper<?>> wrappers = new ArrayList<ModelWrapper<?>>();
        if (radioWaybill.getSelection()) {
            wrappers.addAll(OriginInfoWrapper.getShipmentsByWaybill(
                SessionManager.getAppService(), treeText.getText().trim()));
            wrappers.addAll(DispatchWrapper.getDispatchesByWaybill(
                SessionManager.getAppService(), treeText.getText().trim()));
            return wrappers;

        } else if (radioDateReceived.getSelection()) {
            Date date = dateWidget.getDate();
            if (date != null) {
                wrappers.addAll(OriginInfoWrapper.getShipmentsByDateReceived(
                    SessionManager.getAppService(), date));
                wrappers.addAll(DispatchWrapper.getDispatchesByDateReceived(
                    SessionManager.getAppService(), date));
                return wrappers;
            }
        } else {
            Date date = dateWidget.getDate();
            if (date != null) {
                wrappers.addAll(OriginInfoWrapper.getShipmentsByDateSent(
                    SessionManager.getAppService(), date));
                wrappers.addAll(DispatchWrapper.getDispatchesByDateSent(
                    SessionManager.getAppService(), date));
                return wrappers;
            }
        }
        return null;
    }

    @Override
    protected void showSearchedObjectsInTree(
        List<? extends ModelWrapper<?>> searchedObjects, boolean doubleClick) {
        for (ModelWrapper<?> searchedObject : searchedObjects) {
            List<AdapterBase> nodeRes = rootNode.search(searchedObject);
            if (nodeRes.size() == 0)
                searchedNode.addSearchObject(searchedObject);
        }
        searchedNode.performExpand();
        if (searchedObjects.size() == 1) {
            List<AdapterBase> nodeRes = rootNode.search(searchedObjects.get(0));
            if (nodeRes.size() > 0)
                nodeRes.get(0).performDoubleClick();
        } else
            BgcPlugin.openMessage(
                Messages.SpecimenTransitView_res_dialog_title, NLS.bind(
                    Messages.SpecimenTransitView_found_multiple_msg,
                    searchedObjects.size()));
    }

    public static AdapterBase addToNode(AdapterBase parentNode,
        ModelWrapper<?> wrapper) {
        if (currentInstance != null && wrapper instanceof OriginInfoWrapper) {
            OriginInfoWrapper originInfo = (OriginInfoWrapper) wrapper;
            String text = ""; //$NON-NLS-1$
            AdapterBase topNode = parentNode;
            if (parentNode.equals(currentInstance.searchedNode)
                && !currentInstance.radioWaybill.getSelection()) {
                Date date;
                if (currentInstance.radioDateReceived.getSelection()) {
                    text = Messages.SpecimenTransitView_date_received_node_label;
                    date = originInfo.getShipmentInfo().getReceivedAt();
                } else {
                    text = Messages.SpecimenTransitView_date_packed_node_label;
                    date = originInfo.getShipmentInfo().getPackedAt();
                }
                if (date == null)
                    return null;
                date = (Date) date.clone();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.HOUR_OF_DAY, 0);
                date = c.getTime();
                List<AdapterBase> dateNodeRes = parentNode.search((int) date
                    .getTime() + text.hashCode());
                AdapterBase dateNode = null;
                if (dateNodeRes.size() > 0)
                    dateNode = dateNodeRes.get(0);
                else {
                    dateNode = new DateNode(parentNode, text, date);
                    parentNode.addChild(dateNode);
                }
                topNode = dateNode;
            }

            List<AdapterBase> centerAdapterList = topNode.search(originInfo
                .getCenter());
            AdapterBase centerAdapter = null;

            if (centerAdapterList.size() > 0)
                centerAdapter = centerAdapterList.get(0);
            else if (originInfo.getCenter() instanceof ClinicWrapper) {
                centerAdapter = new ClinicWithShipmentAdapter(topNode,
                    (ClinicWrapper) originInfo.getCenter());
                centerAdapter.setEditable(false);
                centerAdapter.setLoadChildrenInBackground(false);
                topNode.addChild(centerAdapter);
            }

            if (centerAdapter != null) {
                ShipmentAdapter shipmentAdapter = null;
                List<AdapterBase> shipmentAdapterList = centerAdapter
                    .search(originInfo);
                if (shipmentAdapterList.size() > 0)
                    shipmentAdapter = (ShipmentAdapter) shipmentAdapterList
                        .get(0);
                else {
                    shipmentAdapter = new ShipmentAdapter(centerAdapter,
                        originInfo);
                    centerAdapter.addChild(shipmentAdapter);
                }
                return shipmentAdapter;
            }
        } else if (currentInstance != null
            && wrapper instanceof DispatchWrapper) {
            List<AdapterBase> res = parentNode.search(wrapper);
            if (res.size() == 0) {
                DispatchAdapter dispatch = new DispatchAdapter(parentNode,
                    (DispatchWrapper) wrapper);
                parentNode.addChild(dispatch);
            }
        }

        return null;
    }

    public static SpecimenTransitView getCurrent() {
        return currentInstance;
    }

    @Override
    protected String getTreeTextToolTip() {
        return Messages.SpecimenTransitView_search_text_tooltip;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected AbstractTodayNode<?> createTodayNode() {
        return new ShipmentTodayNode(rootNode, 1);
    }

    @Override
    protected AbstractSearchedNode createSearchedNode() {
        if (searchedNode == null)
            return new OriginInfoSearchedNode(rootNode, 2);
        else
            return searchedNode;
    }

    @Override
    protected List<? extends ModelWrapper<?>> search(String text)
        throws Exception {
        return null;
    }

    @Override
    protected void notFound(String text) {
    }

    public static void showShipment(OriginInfoWrapper shipment) {
        if (currentInstance != null) {
            currentInstance.showSearchedObjectsInTree(Arrays.asList(shipment),
                false);
        }
    }

    public static void reloadCurrent() {
        if (currentInstance != null)
            currentInstance.reload();
    }

    @Override
    protected String getString() {
        return toString();
    }

    public static ShipmentAdapter getCurrentShipment() {
        AdapterBase selectedNode = currentInstance.getSelectedNode();
        if (selectedNode != null && selectedNode instanceof ShipmentAdapter) {
            return (ShipmentAdapter) selectedNode;
        }
        return null;
    }

    @Override
    public void clear() {
        if (centerNode != null)
            rootNode.removeChild(centerNode);
        setSearchFieldsEnablement(false);
        super.clear();
    }
}
