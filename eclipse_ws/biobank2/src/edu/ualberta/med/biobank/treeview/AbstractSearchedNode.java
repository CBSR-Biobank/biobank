package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.views.PatientAdministrationView;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;

public abstract class AbstractSearchedNode extends AdapterBase {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AbstractSearchedNode.class.getName());

    private List<ModelWrapper<?>> searchedObjects = new ArrayList<ModelWrapper<?>>();

    public AbstractSearchedNode(AdapterBase parent, int id) {
        super(parent, id, "Searched", true, false);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Clear");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                searchedObjects.clear();
                removeAll();
            }
        });
    }

    @Override
    public void performExpand() {
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            try {
                for (AdapterBase child : getChildren()) {
                    ModelWrapper<?> childWrapper = child.getModelObject();
                    if (childWrapper != null) {
                        childWrapper.reload();
                    }
                    List<AdapterBase> subChildren = new ArrayList<AdapterBase>(
                        child.getChildren());
                    for (AdapterBase subChild : subChildren) {
                        ModelWrapper<?> subChildWrapper = subChild
                            .getModelObject();
                        subChildWrapper.reload();
                        if (!searchedObjects.contains(subChildWrapper)
                            || !isParentTo(childWrapper, subChildWrapper)) {
                            subChild.getParent().removeChild(subChild);
                        }
                    }
                }

                // add searched objects is not yet there
                for (ModelWrapper<?> wrapper : searchedObjects) {
                    assert wrapper instanceof PatientWrapper
                        || wrapper instanceof ClinicShipmentWrapper;
                    if (wrapper instanceof PatientWrapper) {
                        PatientAdministrationView.getCurrent().addToNode(this,
                            wrapper);
                    } else if (wrapper instanceof ClinicShipmentWrapper) {
                        ShipmentAdministrationView.getCurrent().addToNode(this,
                            wrapper);
                    }
                }

                // remove sub children without any children
                List<AdapterBase> children = new ArrayList<AdapterBase>(
                    getChildren());
                for (AdapterBase child : children) {
                    if (child.getChildren().size() == 0) {
                        removeChild(child);
                    }
                }
            } catch (final RemoteAccessException exp) {
                BioBankPlugin.openRemoteAccessErrorMessage();
            } catch (Exception e) {
                logger.error("Error while refreshing searched elements", e);
            }
        }
    }

    @Override
    protected void executeDoubleClick() {
        performExpand();
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
        return null;
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    public void addSearchObject(ModelWrapper<?> searchedObject) {
        searchedObjects.add(searchedObject);
    }

    protected abstract boolean isParentTo(ModelWrapper<?> parent,
        ModelWrapper<?> child);

}
