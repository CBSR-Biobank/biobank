package edu.ualberta.med.biobank.treeview.request;

import java.util.Collection;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.dialogs.RequestShippedDialog;
import edu.ualberta.med.biobank.forms.RequestEntryFormBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.RequestAdministrationView;

public class RequestAdapter extends AdapterBase {

    public RequestAdapter(AdapterBase parent, RequestWrapper ship) {
        super(parent, ship);
    }

    public RequestWrapper getWrapper() {
        return (RequestWrapper) modelObject;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    protected String getLabelInternal() {
        RequestWrapper shipment = getWrapper();
        Assert.isNotNull(shipment, "Request is null");
        String label = new String();
        StudyWrapper study = shipment.getStudy();

        label += shipment.getId() + " - ";
        label += study.getNameShort() + " - ";
        label += DateFormatter.formatAsDate(shipment.getDateCreated());
        return label;

    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Request");
    }

    public void persistAndRebuild() {
        try {
            persistRequest();
        } catch (Exception e1) {
            BioBankPlugin.openAsyncError("Unable to save", e1);
        }
        getParent().getParent().rebuild();
        openViewForm();
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, "Request");
        final RequestWrapper request = getWrapper();
        Integer orderState = request.getState();
        if (RequestState.getState(orderState).equals(RequestState.APPROVED)) {
            MenuItem mi = new MenuItem(menu, SWT.NONE);
            mi.setText("Accept Order");
            mi.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    request.setInAcceptedState();
                    persistAndRebuild();
                }

            });
        } else if (RequestState.getState(orderState).equals(
            RequestState.ACCEPTED)) {
            MenuItem mi = new MenuItem(menu, SWT.NONE);
            mi.setText("Mark as Filled");
            mi.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    request.setInFilledState();
                    persistAndRebuild();
                }

            });
            mi.setEnabled(request.isAllProcessed());
        } else if (RequestState.getState(orderState)
            .equals(RequestState.FILLED)) {
            MenuItem mi = new MenuItem(menu, SWT.NONE);
            mi.setText("Mark as Shipped");
            mi.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    BiobankDialog rfd = new RequestShippedDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        request);
                    if (rfd.open() == Dialog.OK) {
                        request.setInShippedState();
                        persistAndRebuild();
                    }
                }

            });
        } else if (RequestState.getState(orderState).equals(
            RequestState.SHIPPED)) {
            MenuItem mi = new MenuItem(menu, SWT.NONE);
            mi.setText("Close");
            mi.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    request.setState(RequestState.CLOSED);
                    persistAndRebuild();
                }

            });
        }
    }

    private void persistRequest() {
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
        RequestAdministrationView.getCurrent().reload();
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
        return RequestEntryFormBase.ID;
    }

    @Override
    public String getEntryFormId() {
        return RequestEntryFormBase.ID;
    }

}
