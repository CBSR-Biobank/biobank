package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.menus.IMenuService;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetRequestAction;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchChangeStatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchSpecimenListInfoTable;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchViewForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory
        .getI18n(DispatchViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.DispatchViewForm";

    private DispatchAdapter dispatchAdapter;

    private final DispatchWrapper dispatch = new DispatchWrapper(
        SessionManager.getAppService());

    private BgcBaseText senderLabel;

    private BgcBaseText receiverLabel;

    private BgcBaseText departedLabel;

    private BgcBaseText shippingMethodLabel;

    private BgcBaseText waybillLabel;

    private BgcBaseText dateReceivedLabel;

    private DispatchSpecimensTreeTable specimensTree;

    private DispatchSpecimenListInfoTable specimensNonProcessedTable;

    private CommentsInfoTable commentTable;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof DispatchAdapter),
            "Invalid editor input: object of type " + adapter.getClass().getName());

        dispatchAdapter = (DispatchAdapter) adapter;
        setDispatchInfo(adapter.getId());
        setPartName(Dispatch.NAME.singular().toString());
    }

    private void setDispatchInfo(Integer id) throws Exception {
        if (id == null) {
            Dispatch d = new Dispatch();
            dispatch.setWrappedObject(d);
        } else {
            DispatchReadInfo read = SessionManager.getAppService().doAction(
                new DispatchGetInfoAction(adapter.getId()));
            read.dispatch.setDispatchSpecimens(read.dispatchSpecimens);
            dispatch.setWrappedObject(read.dispatch);
            SessionManager.logLookup(read.dispatch);
        }
    }

    @Override
    public void setValues() throws Exception {
        commentTable.setList(dispatch.getCommentCollection(false));
        setDispatchValues();

        if (specimensTree != null) {
            specimensTree.refresh();
        }
    }

    @SuppressWarnings({ "nls" })
    @Override
    protected void createFormContent() throws Exception {
        String dateString = null;
        if (dispatch.getShipmentInfo() != null
            && dispatch.getShipmentInfo().getPackedAt() != null) {
            dateString = dispatch.getFormattedPackedAt();
        }
        if (dateString == null) {
            form.setText(i18n.tr("Dispatch created for {0}",
                dispatch.getSenderCenter().getNameShort()));
        } else {
            form.setText(i18n.tr("Dispatch sent on {0} from {1}",
                dateString, dispatch.getSenderCenter().getNameShort()));
        }
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();

        DispatchChangeStatePermission perm = new DispatchChangeStatePermission(dispatch.getId());
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
        if (SessionManager.getAppService().isAllowed(perm)) {
            if (dispatch.isInCreationState()
                && SessionManager.getUser().getCurrentWorkingCenter().equals(
                    dispatch.getSenderCenter())
                    // OHSDEV  Suppress Sending Dispatch in Creation mode that attached to Request
                    && !checkRequest(dispatch.getId())) {
                createSendButton();
            } else if (dispatch.isInTransitState()
                && SessionManager.getUser().getCurrentWorkingCenter().equals(
                    dispatch.getReceiverCenter())) {
                createReceiveButtons();
            } else if (dispatch.isInReceivedState()
                && SessionManager.getUser().getCurrentWorkingCenter().equals(
                    dispatch.getReceiverCenter())
                && dispatch.getNonProcessedDispatchSpecimenCollection().size() == 0) {
                createCloseButton();
            }
        }

        createTreeTableSection();

        setDispatchValues();

        commentTable.setList(dispatch.getCommentCollection(false));
    }

    private void createTreeTableSection() {
        if (dispatch.isInCreationState()) {
            @SuppressWarnings("nls")
            Composite parent =
                createSectionWithClient(i18n.tr("Specimens added"));
            specimensNonProcessedTable = new DispatchSpecimenListInfoTable(parent, dispatch, false) {
                @Override
                public List<DispatchSpecimenWrapper> getInternalDispatchSpecimens() {
                    return dispatch.getDispatchSpecimenCollection(false);
                }

            };
            specimensNonProcessedTable.adaptToToolkit(toolkit, true);
            specimensNonProcessedTable.addClickListener(
                new IInfoTableDoubleClickItemListener<DispatchSpecimenWrapper>() {
                    @Override
                    public void doubleClick(
                        InfoTableEvent<DispatchSpecimenWrapper> event) {
                        Object selection = event.getSelection();
                        if (selection instanceof InfoTableSelection) {
                            InfoTableSelection tableSelection =
                                (InfoTableSelection) selection;
                            DispatchSpecimenWrapper dsa =
                                (DispatchSpecimenWrapper) tableSelection
                                    .getObject();
                            if (dsa != null) {
                                SessionManager.openViewForm(dsa.getSpecimen());
                            }
                        }
                    }
                });
            specimensNonProcessedTable.addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    specimensNonProcessedTable.reloadCollection();
                }
            });
            specimensNonProcessedTable.addEditItemListener(
                new IInfoTableEditItemListener<DispatchSpecimenWrapper>() {

                    @Override
                    public void editItem(
                        InfoTableEvent<DispatchSpecimenWrapper> event) {
                        Object selection = event.getSelection();
                        if (selection instanceof InfoTableSelection) {
                            InfoTableSelection tableSelection =
                                (InfoTableSelection) selection;
                            DispatchSpecimenWrapper dsa =
                                (DispatchSpecimenWrapper) tableSelection
                                    .getObject();
                            if (dsa != null) {
                                new SpecimenAdapter(null, dsa.getSpecimen())
                                    .openEntryForm();
                            }
                        }
                    }
                });
        } else {
            specimensTree = new DispatchSpecimensTreeTable(page, dispatch, false);
            specimensTree.addClickListener();
        }
    }

    private void createReceiveButtons() {
        Composite composite = toolkit.createComposite(page);
        composite.setLayout(new GridLayout(3, false));
        @SuppressWarnings("nls")
        Button sendButton = toolkit.createButton(composite, i18n.tr("Receive"), SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doReceive();
            }
        });

        @SuppressWarnings("nls")
        Button sendProcessButton = toolkit.createButton(
            composite, i18n.tr("Receive and Process"), SWT.PUSH);
        sendProcessButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doReceiveAndProcess();
            }
        });

        @SuppressWarnings("nls")
        Button lostProcessButton = toolkit.createButton(composite, i18n.tr("Lost"), SWT.PUSH);
        lostProcessButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doSetAsLost();
            }
        });
    }

    private void createCloseButton() {
        Composite composite = toolkit.createComposite(page);
        composite.setLayout(new GridLayout(2, false));
        @SuppressWarnings("nls")
        Button sendButton =
            toolkit.createButton(composite,
                // button label.
                i18n.tr("Done"), SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doClose();
            }
        });
    }

    @SuppressWarnings("nls")
    private void createSendButton() {
        ToolBarManager manager = (ToolBarManager) form.getToolBarManager();

        IMenuService menuService = (IMenuService) getSite().getService(IMenuService.class);
        menuService.populateContributionManager(manager, "popup:dispatchCreationFormsToolBar");
        manager.update(true);
    }

    @SuppressWarnings("nls")
    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        String stateMessage = null;
        if (dispatch.isInLostState()) {
            stateMessage = i18n.tr(" Dispatch Lost ");
        } else if (dispatch.isInClosedState()) {
            stateMessage = i18n.tr(" Dispatch Complete ");
        }

        if (stateMessage != null) {
            Label label = widgetCreator.createLabel(client, stateMessage, SWT.CENTER, false);
            label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            GridData gd = new GridData();
            gd.horizontalAlignment = SWT.CENTER;
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalSpan = 2;
            label.setLayoutData(gd);
        }

        senderLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Dispatch.PropertyName.SENDER_CENTER.toString());
        receiverLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Dispatch.PropertyName.RECEIVER_CENTER.toString());
        if (!dispatch.isInCreationState()) {
            departedLabel = createReadOnlyLabelledField(client, SWT.NONE, "Packed at");
            shippingMethodLabel = createReadOnlyLabelledField(client, SWT.NONE,
                ShippingMethod.NAME.singular().toString());
            waybillLabel = createReadOnlyLabelledField(client, SWT.NONE,
                ShipmentInfo.PropertyName.WAYBILL.toString());
        }
        if (dispatch.hasBeenReceived()) {
            dateReceivedLabel = createReadOnlyLabelledField(client, SWT.NONE, "Date received");
        }
        createCommentsSection();
    }

    private void createCommentsSection() {
        Section section = createSection(Comment.NAME.plural().toString());

        Composite client = toolkit.createComposite(section);
        section.setClient(client);
        section.setExpanded(false);

        client.setLayout(new GridLayout(1, false));
        commentTable = new CommentsInfoTable(client, dispatch.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void setDispatchValues() {
        setTextValue(
            senderLabel,
            dispatch.getSenderCenter().getName());
        setTextValue(
            receiverLabel,
            dispatch.getReceiverCenter().getName());
        if (departedLabel != null)
            setTextValue(departedLabel, dispatch.getFormattedPackedAt());

        ShipmentInfoWrapper shipInfo = dispatch.getShipmentInfo();

        if (shipInfo != null) {
            if (shippingMethodLabel != null)
                setTextValue(
                    shippingMethodLabel,
                    shipInfo.getShippingMethod() == null ? StringUtil.EMPTY_STRING
                        : shipInfo
                            .getShippingMethod().getName());
            if (waybillLabel != null)
                setTextValue(waybillLabel, shipInfo.getWaybill());
            if (dateReceivedLabel != null)
                setTextValue(dateReceivedLabel,
                    shipInfo.getFormattedDateReceived());
        }
    }

    public void dispatchSend() {
        DispatchSendingEntryForm.sendDispatch(this, dispatch, dispatchAdapter);
    }

    @SuppressWarnings("nls")
    private boolean checkRequest(Integer id) {

	BooleanResult ret = null;
		try {
			ret = SessionManager.getAppService().doAction(
			        new DispatchGetRequestAction(id));
		} catch (ApplicationException e) {
			BgcPlugin.openAsyncError(
			// dialog title.
			i18n.tr("Error while checking request id in Dispatch"), e);
		}
	return ret.isTrue();
    }

}
