package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;
import java.util.Date;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchSendingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchViewForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public class DispatchAdapter extends AdapterBase {

    public DispatchAdapter(AdapterBase parent, DispatchWrapper ship) {
        super(parent, ship);
    }

    public DispatchWrapper getWrapper() {
        return (DispatchWrapper) modelObject;
    }

    @Override
    public boolean isEditable() {
        boolean editable = super.isEditable();
        if (getWrapper() != null) {
            return editable
                && ((getWrapper().getSenderCenter().equals(
                    SessionManager.getUser().getCurrentWorkingCenter()) && (getWrapper()
                    .isNew()
                    || getWrapper().isInCreationState()
                    || getWrapper().isInTransitState() || getWrapper()
                    .isInLostState())) || (getWrapper().getReceiverCenter()
                    .equals(SessionManager.getUser().getCurrentWorkingCenter()) && (getWrapper()
                    .isInReceivedState() || getWrapper().isInLostState() || getWrapper()
                    .isInClosedState())));
        }
        return editable;
    }

    @Override
    protected String getLabelInternal() {
        DispatchWrapper dispatch = getWrapper();
        Assert.isNotNull(dispatch, "Dispatch is null"); //$NON-NLS-1$
        String label = ""; //$NON-NLS-1$
        if (dispatch.getSenderCenter() != null
            && dispatch.getReceiverCenter() != null)
            label += dispatch.getSenderCenter().getNameShort() + " -> " //$NON-NLS-1$
                + dispatch.getReceiverCenter().getNameShort();

        ShipmentInfoWrapper shipInfo = dispatch.getShipmentInfo();
        if ((shipInfo != null) && (shipInfo.getPackedAt() != null))
            label += " [" + dispatch.getFormattedPackedAt() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        return label;
    }

    @Override
    public String getTooltipText() {
        return getTooltipText(Messages.DispatchAdapter_dispatch_label);
    }

    @Override
    public boolean isDeletable() {
        if (SessionManager.getInstance().isConnected()
            && SessionManager.getUser().getCurrentWorkingCenter() != null)
            return SessionManager.getUser().getCurrentWorkingCenter()
                .equals(getWrapper().getSenderCenter())
                && SessionManager.canDelete(getWrapper())
                && getWrapper().isInCreationState();
        else
            return false;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        CenterWrapper<?> siteParent = SessionManager.getUser()
            .getCurrentWorkingCenter();
        addViewMenu(menu, Messages.DispatchAdapter_dispatch_label);
        try {
            if (isDeletable()) {
                addDeleteMenu(menu, Messages.DispatchAdapter_dispatch_label);
            }
            if (siteParent.equals(getWrapper().getSenderCenter())
                && SessionManager.canUpdate(getWrapper())
                && getWrapper().isInTransitState()) {
                MenuItem mi = new MenuItem(menu, SWT.PUSH);
                mi.setText(Messages.DispatchAdapter_move_creation_label);
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        setDispatchAsCreation();
                    }
                });
            }
            if (siteParent.equals(getWrapper().getReceiverCenter())
                && getWrapper().isInTransitState()) {
                MenuItem mi = new MenuItem(menu, SWT.PUSH);
                mi.setText(Messages.DispatchAdapter_receive_label);
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        doReceive();
                    }
                });
                mi = new MenuItem(menu, SWT.PUSH);
                mi.setText(Messages.DispatchAdapter_receive_process_label);
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        doReceiveAndProcess();
                    }
                });
                mi = new MenuItem(menu, SWT.PUSH);
                mi.setText(Messages.DispatchAdapter_lost_label);
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        doSetAsLost();
                    }
                });
            }
            addEditMenu(menu, Messages.DispatchAdapter_dispatch_label);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(Messages.DispatchAdapter_check_error_msg,
                e);
        }
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.DispatchAdapter_delete_confirm_msg;
    }

    public void doReceive() {
        setDispatchAsReceived();
        openViewForm();
    }

    public void doReceiveAndProcess() {
        setDispatchAsReceived();
        openEntryForm();
    }

    public void doClose() {
        getWrapper().setState(DispatchState.CLOSED);
        persistDispatch();
        openViewForm();
    }

    public void doSetAsLost() {
        getWrapper().setState(DispatchState.LOST);
        persistDispatch();
        openViewForm();
    }

    private void setDispatchAsReceived() {
        getWrapper().getShipmentInfo().setReceivedAt(new Date());
        getWrapper().setState(DispatchState.RECEIVED);
        persistDispatch();
    }

    private void setDispatchAsCreation() {
        getWrapper().setState(DispatchState.CREATION);
        getWrapper().getShipmentInfo().setPackedAt(null);
        persistDispatch();
    }

    private void persistDispatch() {
        try {
            getWrapper().persist();
        } catch (final RemoteConnectFailureException exp) {
            BgcPlugin.openRemoteConnectErrorMessage(exp);
        } catch (final RemoteAccessException exp) {
            BgcPlugin.openRemoteAccessErrorMessage(exp);
        } catch (final AccessDeniedException ade) {
            BgcPlugin.openAccessDeniedErrorMessage(ade);
        } catch (Exception ex) {
            BgcPlugin.openAsyncError(Messages.DispatchAdapter_save_error_title,
                ex);
        }
        SpecimenTransitView.getCurrent().reload();
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return DispatchViewForm.ID;
    }

    @Override
    public String getEntryFormId() {
        if (getWrapper().isInCreationState()
            || (getWrapper().isInTransitState() && SessionManager.getUser()
                .getCurrentWorkingCenter()
                .equals(getWrapper().getSenderCenter())))
            return DispatchSendingEntryForm.ID;
        return DispatchReceivingEntryForm.ID;
    }
}
