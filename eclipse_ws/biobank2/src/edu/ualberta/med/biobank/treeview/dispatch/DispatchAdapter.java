package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchChangeStateAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchDeleteAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchDeletePermission;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchUpdatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchSendingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchViewForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchAdapter extends AdapterBase {

    public DispatchAdapter(AdapterBase parent, DispatchWrapper ship) {
        super(parent, ship);
    }

    private DispatchWrapper getDispatchWrapper() {
        return (DispatchWrapper) getModelObject();
    }

    @Override
    public void init() {
        this.isDeletable =
            isAllowed(new DispatchDeletePermission(getModelObject().getId()));
        this.isReadable =
            isAllowed(new DispatchReadPermission(getModelObject().getId()));
        this.isEditable =
            isAllowed(new DispatchUpdatePermission(getModelObject().getId()));
    }

    @Override
    public boolean isEditable() {
        boolean editable = super.isEditable();
        if (getDispatchWrapper() != null) {
            return editable
                && ((getDispatchWrapper().getSenderCenter().equals(
                    SessionManager.getUser().getCurrentWorkingCenter()) && (getDispatchWrapper()
                    .isNew()
                    || getDispatchWrapper().isInCreationState()
                    || getDispatchWrapper().isInTransitState() || getDispatchWrapper()
                    .isInLostState())) || (getDispatchWrapper()
                    .getReceiverCenter().equals(
                        SessionManager.getUser().getCurrentWorkingCenter()) && (getDispatchWrapper()
                    .isInReceivedState()
                    || getDispatchWrapper().isInLostState() || getDispatchWrapper()
                    .isInClosedState())));
        }
        return editable;
    }

    @Override
    protected String getLabelInternal() {
        DispatchWrapper dispatch = getDispatchWrapper();
        Assert.isNotNull(dispatch, "Dispatch is null");
        String label = StringUtil.EMPTY_STRING;
        if (dispatch.getSenderCenter() != null
            && dispatch.getReceiverCenter() != null)
            label += dispatch.getSenderCenter().getNameShort() + " -> "
                + dispatch.getReceiverCenter().getNameShort();

        ShipmentInfoWrapper shipInfo = dispatch.getShipmentInfo();
        if ((shipInfo != null) && (shipInfo.getPackedAt() != null))
            label += " [" + dispatch.getFormattedPackedAt() + "]";
        return label;
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Dispatch.NAME.singular().toString());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        CenterWrapper<?> siteParent = SessionManager.getUser()
            .getCurrentWorkingCenter();
        addViewMenu(menu, Dispatch.NAME.singular().toString());
        try {
            if (isDeletable()) {
                addDeleteMenu(menu, Dispatch.NAME.singular().toString());
            }
            if (siteParent.equals(getDispatchWrapper().getReceiverCenter())
                && isEditable
                && getDispatchWrapper().hasErrors()) {
                MenuItem mi = new MenuItem(menu, SWT.PUSH);
                mi.setText("Close");
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        doClose();
                    }
                });
            }
            if (siteParent.equals(getDispatchWrapper().getSenderCenter())
                && isEditable
                && getDispatchWrapper().isInTransitState()) {
                MenuItem mi = new MenuItem(menu, SWT.PUSH);
                mi.setText("Move to Creation");
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        setDispatchAsCreation();
                    }
                });
            }
            if (siteParent.equals(getDispatchWrapper().getReceiverCenter())
                && getDispatchWrapper().isInTransitState()) {
                MenuItem mi = new MenuItem(menu, SWT.PUSH);
                mi.setText("Receive");
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        doReceive();
                    }
                });
                mi = new MenuItem(menu, SWT.PUSH);
                mi.setText("Receive and Process");
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        doReceiveAndProcess();
                    }
                });
                mi = new MenuItem(menu, SWT.PUSH);
                mi.setText("Mark as Lost");
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        doSetAsLost();
                    }
                });
            }
            addEditMenu(menu, Dispatch.NAME.singular().toString());
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Error checking permissions",
                e);
        }
    }

    @Override
    public void runDelete() throws Exception {
        DispatchDeleteAction delete =
            new DispatchDeleteAction(getDispatchWrapper().getWrappedObject());
        SessionManager.getAppService().doAction(delete);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this dispatch?";
    }

    public void doReceive() {
        setDispatchAsReceived();
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                openViewForm();
            }
        });

    }

    public void doSend() {
        setDispatchAsSent();
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                openViewForm();
            }
        });

    }

    private void setDispatchAsSent() {
        getDispatchWrapper().setState(DispatchState.IN_TRANSIT);
        persistDispatch();
    }

    public void doReceiveAndProcess() {
        setDispatchAsReceived();
        openEntryForm();
    }

    public void doClose() {
        getDispatchWrapper().setState(DispatchState.CLOSED);
        persistDispatch();
        openViewForm();
    }

    public void doSetAsLost() {
        try {
            // to be sure has last database data.
            getDispatchWrapper().reload();
        } catch (Exception ex) {
            BgcPlugin.openAsyncError("Error reloading", ex);
        }
        getDispatchWrapper().setState(DispatchState.LOST);
        persistDispatch();
        openViewForm();
    }

    private void setDispatchAsReceived() {
        getDispatchWrapper().getShipmentInfo().setReceivedAt(new Date());
        getDispatchWrapper().setState(DispatchState.RECEIVED);
        persistDispatch();
    }

    private void persistDispatch() {
        DispatchChangeStateAction action =
            new DispatchChangeStateAction(getDispatchWrapper().getId(),
                getDispatchWrapper().getDispatchState(),
                DispatchSaveAction.prepareShipInfo(getDispatchWrapper()
                    .getShipmentInfo()));
        try {
            SessionManager.getAppService().doAction(action);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Unable to save changes", e);
        }
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                SpecimenTransitView.reloadCurrent();
            }
        });

    }

    private void setDispatchAsCreation() {
        getDispatchWrapper().setState(DispatchState.CREATION);
        getDispatchWrapper().setShipmentInfo(null);
        persistDispatch();
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    public String getViewFormId() {
        return DispatchViewForm.ID;
    }

    @Override
    public String getEntryFormId() {
        if (getDispatchWrapper().isInCreationState()
            || (getDispatchWrapper().isInTransitState() && SessionManager
                .getUser().getCurrentWorkingCenter()
                .equals(getDispatchWrapper().getSenderCenter())))
            return DispatchSendingEntryForm.ID;
        return DispatchReceivingEntryForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof DispatchAdapter)
            return internalCompareTo(o);
        return 0;
    }

}
