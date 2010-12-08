package edu.ualberta.med.biobank.treeview.request;

import java.util.Collection;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
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
        boolean editable = super.isEditable();
        if (getWrapper() != null) {
            return editable
                && (getWrapper().isNew() || !getWrapper().isInCreationState());
        }
        return editable;
    }

    @Override
    protected String getLabelInternal() {
        RequestWrapper shipment = getWrapper();
        Assert.isNotNull(shipment, "Request is null");
        String label = new String();
        StudyWrapper study = shipment.getStudy();

        if (study != null) {
            label += study.getNameShort() + " - ";
        }

        label += shipment.getDateCreated();
        return label;

    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Request");
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    public void doAccept() {
        setRequestAsAccepted();
        openEntryForm();
    }

    private void setRequestAsAccepted() {
        getWrapper().setInAcceptedState();
        persistRequest();
    }

    public void doClose() {
        getWrapper().setInCloseState();
        persistRequest();
        openViewForm();
    }

    public void doSetAsLost() {
        getWrapper().setInLostState();
        persistRequest();
        openViewForm();
    }

    private void setRequestAsNew() {
        getWrapper().setInNewState();
        persistRequest();
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
