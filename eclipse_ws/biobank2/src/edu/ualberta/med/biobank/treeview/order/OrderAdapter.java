package edu.ualberta.med.biobank.treeview.order;

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
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.OrderEntryFormBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.OrderAdministrationView;

public class OrderAdapter extends AdapterBase {

    public OrderAdapter(AdapterBase parent, OrderWrapper ship) {
        super(parent, ship);
    }

    public OrderWrapper getWrapper() {
        return (OrderWrapper) modelObject;
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
        OrderWrapper shipment = getWrapper();
        Assert.isNotNull(shipment, "Order is null");
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
        return getTooltipText("Order");
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    public void doProcess() {
        setOrderAsProcessing();
        openEntryForm();
    }

    private void setOrderAsProcessing() {
        getWrapper().setInProcessingState();
        persistOrder();
    }

    public void doClose() {
        getWrapper().setInCloseState();
        persistOrder();
        openViewForm();
    }

    public void doSetAsLost() {
        getWrapper().setInLostState();
        persistOrder();
        openViewForm();
    }

    private void setOrderAsCreation() {
        getWrapper().setInCreationState();
        persistOrder();
    }

    private void persistOrder() {
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
        OrderAdministrationView.getCurrent().reload();
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
        return OrderEntryFormBase.ID;
    }

    @Override
    public String getEntryFormId() {
        return OrderEntryFormBase.ID;
    }

}
