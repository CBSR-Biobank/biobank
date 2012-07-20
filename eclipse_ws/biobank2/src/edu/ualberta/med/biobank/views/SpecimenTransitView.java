package edu.ualberta.med.biobank.views;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.search.SpecimenTransitSearchAction;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoReadPermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.IBiobankModel;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.DateNode;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchCenterAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.OriginInfoSearchedNode;
import edu.ualberta.med.biobank.treeview.shipment.ClinicWithShipmentAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentTodayNode;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenTransitView extends AbstractTodaySearchAdministrationView {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenTransitView.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.views.SpecimenTransitView";

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

    public void createNodes() throws Exception {
        if (SessionManager.getUser().getCurrentWorkingCenter() != null) {
            centerNode = new DispatchCenterAdapter((RootNode) rootNode,
                SessionManager.getUser().getCurrentWorkingCenter());
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

    @SuppressWarnings("nls")
    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioWaybill = new Button(composite, SWT.RADIO);
        radioWaybill.setText(ShipmentInfo.PropertyName.WAYBILL.toString());
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
        radioDateReceived.setText(
            // radio button label.
            i18n.tr("Date Received"));
        radioDateReceived.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioDateReceived.getSelection()) {
                    showTextOnly(false);
                }
            }
        });

        radioDateSent = new Button(composite, SWT.RADIO);
        radioDateSent.setText(
            // radio button label.
            i18n.tr("Date Packed"));
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
        searchButton.setText(
            // button label.
            i18n.tr("Go"));
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

    @SuppressWarnings("nls")
    @Override
    public void reload() {
        if (rootNode != null) {
            rootNode.removeAll();
            try {
                createNodes();
            } catch (Exception e) {
                logger.error("Error creating nodes", e);

            }
            for (AbstractAdapterBase adaper : rootNode.getChildren()) {
                adaper.rebuild();
            }
        }
        try {
            CenterWrapper<?> center = SessionManager.getUser()
                .getCurrentWorkingCenter();
            if (center == null)
                setSearchFieldsEnablement(false);
            else
                setSearchFieldsEnablement(SessionManager.getAppService()
                    .isAllowed(
                        new DispatchReadPermission(center
                            .getWrappedObject()))
                    || SessionManager.getAppService().isAllowed(
                        new OriginInfoReadPermission(SessionManager.getUser()
                            .getCurrentWorkingCenter().getWrappedObject())));
        } catch (ApplicationException e) {
            BgcPlugin.openAccessDeniedErrorMessage();
        }
        super.reload();
    }

    @SuppressWarnings("nls")
    @Override
    protected void internalSearch() {
        try {
            List<IBiobankModel> searchedObject = search();
            if (searchedObject == null || searchedObject.size() == 0) {
                String msg;
                if (radioWaybill.getSelection()) {
                    msg =
                        // dialog message.
                        i18n.tr(
                            "No Dispatches/Shipments found for waybill {0}",
                            treeText.getText());
                } else {
                    msg =
                        // dialog message.
                        i18n.tr(
                            "No Dispatches/Shipments found for date {0}",
                            DateFormatter.formatAsDate(dateWidget.getDate()));
                }
                BgcPlugin.openMessage(
                    // dialog title.
                    i18n.tr("Dispatch not found"), msg);
            } else {
                showSearchedObjectsInTree(searchedObject);
            }
        } catch (Exception e) {
            BgcPlugin.openError(
                // dialog title.
                i18n.tr("Search error"),
                e);
        }
    }

    protected List<IBiobankModel> search() throws Exception {
        SpecimenTransitSearchAction search =
            new SpecimenTransitSearchAction(SessionManager.getUser()
                .getCurrentWorkingCenter().getId());
        if (radioWaybill.getSelection()) {
            search.setWaybill(treeText.getText().trim());
        } else if (radioDateReceived.getSelection()) {
            search.setDateReceived(dateWidget.getDate());
        } else {
            search.setDatePacked(dateWidget.getDate());
        }
        return SessionManager.getAppService().doAction(search).getList();
    }

    @SuppressWarnings("nls")
    protected void showSearchedObjectsInTree(
        List<IBiobankModel> searchedObjects) {
        for (IBiobankModel searchedObject : searchedObjects) {
            List<AbstractAdapterBase> nodeRes = rootNode.search(
                searchedObject.getClass(), searchedObject.getId());
            if (nodeRes.size() == 0) {
                searchedNode.addSearchObject(searchedObject,
                    searchedObject.getId());
                SpecimenTransitView.addToNode(searchedNode, searchedObject);
            }
        }
        if (searchedObjects.size() == 1) {
            IBiobankModel searchedWrap = searchedObjects.get(0);
            List<AbstractAdapterBase> nodeRes = rootNode.search(
                searchedWrap.getClass(), searchedWrap.getId());
            if (nodeRes.size() > 0) {
                nodeRes.get(0).performDoubleClick();
                getTreeViewer().expandToLevel(nodeRes.get(0), 1);
            }
        } else {
            searchedNode.performExpand();
            BgcPlugin.openMessage(
                // dialog title.
                i18n.tr("Shipments"),
                // dialog message.
                i18n.tr("{0} found.", searchedObjects.size()));
        }
    }

    @SuppressWarnings("nls")
    public static AbstractAdapterBase addToNode(AdapterBase parentNode,
        Object obj) {
        if (currentInstance != null && obj instanceof OriginInfo) {
            OriginInfo originInfo = (OriginInfo) obj;
            String text = StringUtil.EMPTY_STRING;
            AdapterBase topNode = parentNode;
            if (parentNode.equals(currentInstance.searchedNode)
                && !currentInstance.radioWaybill.getSelection()) {
                Date date;
                if (currentInstance.radioDateReceived.getSelection()) {
                    // label.
                    text = i18n.tr("Received");
                    date = originInfo.getShipmentInfo().getReceivedAt();
                } else {
                    // label.
                    text = i18n.tr("Packed");
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
                List<AbstractAdapterBase> dateNodeRes =
                    parentNode.search(
                        date.getClass(), DateNode.idBuilder(text, date)
                        );
                AbstractAdapterBase dateNode = null;
                if (dateNodeRes.size() > 0)
                    dateNode = dateNodeRes.get(0);
                else {
                    dateNode = new DateNode(parentNode, text, date);
                    parentNode.addChild(dateNode);
                }
                topNode = (AdapterBase) dateNode;
            }

            List<AbstractAdapterBase> centerAdapterList = topNode.search(
                ClinicWrapper.class, originInfo.getCenter()
                    .getId());
            AdapterBase centerAdapter = null;

            if (centerAdapterList.size() > 0)
                centerAdapter = (AdapterBase) centerAdapterList.get(0);
            else if (originInfo.getCenter() instanceof Clinic) {
                centerAdapter =
                    new ClinicWithShipmentAdapter(topNode,
                        new ClinicWrapper(SessionManager.getAppService(),
                            (Clinic) originInfo.getCenter()));
                topNode.addChild(centerAdapter);
            }

            if (centerAdapter != null) {
                ShipmentAdapter shipmentAdapter = null;
                List<AbstractAdapterBase> shipmentAdapterList = centerAdapter
                    .search(OriginInfo.class, originInfo.getId());
                if (shipmentAdapterList.size() > 0)
                    shipmentAdapter = (ShipmentAdapter) shipmentAdapterList
                        .get(0);
                else {
                    shipmentAdapter =
                        new ShipmentAdapter(centerAdapter,
                            new OriginInfoWrapper(
                                SessionManager.getAppService(), originInfo));
                    centerAdapter.addChild(shipmentAdapter);
                }
                return shipmentAdapter;
            }
        } else if (currentInstance != null && obj instanceof Dispatch) {
            List<AbstractAdapterBase> res = parentNode.search(
                Dispatch.class, ((Dispatch) obj).getId());
            if (res.size() == 0) {
                DispatchAdapter dispatch =
                    new DispatchAdapter(parentNode, new DispatchWrapper(
                        SessionManager.getAppService(),
                        (Dispatch) obj));
                parentNode.addChild(dispatch);
            }
        }

        return null;
    }

    public static SpecimenTransitView getCurrent() {
        return currentInstance;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTreeTextToolTip() {
        // tooltip.
        return i18n.tr("Enter a dispatch/shipment waybill and hit enter");
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected AbstractTodayNode<?> createTodayNode() {
        return new ShipmentTodayNode((RootNode) rootNode, 1);
    }

    @Override
    protected AbstractSearchedNode createSearchedNode() {
        if (searchedNode == null)
            return new OriginInfoSearchedNode((RootNode) rootNode, 2);
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
        AbstractAdapterBase selectedNode = currentInstance.getSelectedNode();
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

    @Override
    protected void createRootNode() {
        createOldRootNode();
    }

    @Override
    protected void showSearchedObjectsInTree(
        List<? extends ModelWrapper<?>> searchedObject, boolean b) {
    }

}
