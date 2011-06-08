package edu.ualberta.med.biobank.treeview.patient;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.gui.common.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class PatientAdapter extends AdapterBase {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientAdapter.class.getName());

    public PatientAdapter(AdapterBase parent, PatientWrapper patientWrapper) {
        super(parent, patientWrapper);
        if (patientWrapper != null) {
            boolean hasChildren = false;
            try {
                hasChildren = patientWrapper.getCollectionEventCount(true) > 0;
            } catch (Exception e) {
                logger.error("error counting events in patient", e);
            }
            setHasChildren(hasChildren);
        }
    }

    public PatientWrapper getWrapper() {
        return (PatientWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        PatientWrapper patientWrapper = getWrapper();
        Assert.isNotNull(patientWrapper, "patient is null");
        return patientWrapper.getPnumber();
    }

    @Override
    public String getTooltipText() {
        PatientWrapper patient = getWrapper();
        if (patient != null) {
            StudyWrapper study = patient.getStudy();
            if (study != null)
                return study.getName() + " - " + getTooltipText("Patient");
        }
        return getTooltipText("Patient");
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
        openViewForm();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Patient");
        addViewMenu(menu, "Patient");
        addDeleteMenu(menu, "Patient");

        if (isEditable()
            && SessionManager.canCreate(CollectionEventWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Collection Event");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    CollectionEventAdapter adapter = new CollectionEventAdapter(
                        PatientAdapter.this, new CollectionEventWrapper(
                            getAppService()));
                    adapter.getWrapper().setPatient(getWrapper());
                    adapter.openEntryForm();
                }
            });
        }
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ProcessingEventWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new CollectionEventAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof ProcessingEventWrapper);
        return new CollectionEventAdapter(this, (CollectionEventWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return getWrapper().getCollectionEventCollection(true);
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return (getWrapperChildren() == null) ? 0 : getWrapperChildren().size();
    }

    @Override
    public String getEntryFormId() {
        return PatientEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return PatientViewForm.ID;
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this patient?";
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }
}
