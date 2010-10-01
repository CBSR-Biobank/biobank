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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.DispatchShipmentReceivingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchShipmentSendingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchShipmentViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.DispatchShipmentAdministrationView;

public class DispatchShipmentAdapter extends AdapterBase {

    public DispatchShipmentAdapter(AdapterBase parent,
        DispatchShipmentWrapper ship) {
        super(parent, ship);
    }

    public DispatchShipmentWrapper getWrapper() {
        return (DispatchShipmentWrapper) modelObject;
    }

    @Override
    public boolean isEditable() {
        boolean editable = super.isEditable();
        if (getWrapper() != null) {
            SiteWrapper currentSite =
                SessionManager.getInstance().getCurrentSite();
            return editable
                && (getWrapper().isNew() || currentSite == null || !(currentSite
                    .equals(getWrapper().getReceiver()) && getWrapper()
                    .isInTransitState()));
        }
        return editable;
    }

    @Override
    protected String getLabelInternal() {
        DispatchShipmentWrapper shipment = getWrapper();
        Assert.isNotNull(shipment, "shipment is null");
        String label = new String();
        StudyWrapper study = shipment.getStudy();

        if (study != null) {
            label += study.getNameShort() + " - ";
        }

        label += shipment.getFormattedDateShipped();
        return label;

    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Dispatch Shipment");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, "Dispatch Shipment");
        SiteWrapper currentSite = SessionManager.getInstance().getCurrentSite();
        if (currentSite.equals(getWrapper().getSender())
            && SessionManager.canDelete(DispatchShipmentWrapper.class)
            && getWrapper().isInCreationState()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Delete");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    delete();
                }
            });
        }
        if (getWrapper().canBeReceivedBy(SessionManager.getUser(),
            SessionManager.getInstance().getCurrentSite())) {
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
        } else {
            addEditMenu(menu, "Shipment");
        }
    }

    public void doReceive() {
        setShipmentAsReceived();
        openViewForm();
    }

    public void doReceiveAndProcess() {
        setShipmentAsReceived();
        openEntryForm();
    }

    private void setShipmentAsReceived() {
        getWrapper().setDateReceived(new Date());
        getWrapper().setNextState();
        try {
            getWrapper().persist();
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage(exp);
        } catch (final AccessDeniedException ade) {
            BioBankPlugin.openAccessDeniedErrorMessage(ade);
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Save error", ex);
        }
        DispatchShipmentAdministrationView.getCurrent().reload();
    }

    public void doClose() {
        // if was in error, will be in close/error state
        // if was received, will be in close state
        getWrapper().setNextState();
        try {
            getWrapper().persist();
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage(exp);
        } catch (final AccessDeniedException ade) {
            BioBankPlugin.openAccessDeniedErrorMessage(ade);
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Save error", ex);
        }
        DispatchShipmentAdministrationView.getCurrent().reload();
        openViewForm();
    }

    public void doFlag() {
        getWrapper().setInErrorState();
        try {
            getWrapper().persist();
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage(exp);
        } catch (final AccessDeniedException ade) {
            BioBankPlugin.openAccessDeniedErrorMessage(ade);
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Save error", ex);
        }
        DispatchShipmentAdministrationView.getCurrent().reload();
        openViewForm();
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
        return DispatchShipmentViewForm.ID;
    }

    @Override
    public String getEntryFormId() {
        SiteWrapper currentSite = SessionManager.getInstance().getCurrentSite();
        if (currentSite.equals(getWrapper().getSender()))
            return DispatchShipmentSendingEntryForm.ID;
        return DispatchShipmentReceivingEntryForm.ID;
    }

}
