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
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.IBiobankModel;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;

public class PatientAdapter extends AbstractNewAdapterBase {

    private static BgcLogger logger = BgcLogger.getLogger(PatientAdapter.class
        .getName());

    public PatientAdapter(AbstractAdapterBase parent, Patient patient,
        String label) {
        super(parent, patient, label);
        if (patient != null) {
            boolean hasChildren = false;
            try {
                // FIXME adapter data should be retrieve from an action
                hasChildren = new PatientWrapper(
                    SessionManager.getAppService(), patient)
                    .getCollectionEventCount(true) > 0;
            } catch (Exception e) {
                logger.error("error counting events in patient", e); //$NON-NLS-1$
            }
            setHasChildren(hasChildren);
        }
    }

    @Override
    public Patient getModelObject() {
        return (Patient) super.getModelObject();
    }

    // @Override
    // protected String getLabelInternal() {
    // PatientWrapper patientWrapper = getPatientWrapper();
    //        Assert.isNotNull(patientWrapper, "patient is null"); //$NON-NLS-1$
    // return patientWrapper.getPnumber();
    // }

    @Override
    public String getTooltipText() {
        // FIXME should also be part of data retrieve when retrieve tree data
        // via action ?
        Patient patient = getModelObject();
        if (patient != null) {
            Study study = patient.getStudy();
            if (study != null)
                return study.getName()
                    + " - " + getTooltipText(Messages.PatientAdapter_patient_label); //$NON-NLS-1$
        }
        return getTooltipText(Messages.PatientAdapter_patient_label);
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
        openViewForm();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.PatientAdapter_patient_label);
        addViewMenu(menu, Messages.PatientAdapter_patient_label);
        addDeleteMenu(menu, Messages.PatientAdapter_patient_label);

        if (isEditable()
            && SessionManager.canCreate(CollectionEventWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.PatientAdapter_add_cevent_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    CollectionEvent cevent = new CollectionEvent();
                    cevent.setPatient(getModelObject());
                    CollectionEventAdapter ceventAdapter = new CollectionEventAdapter(
                        PatientAdapter.this, cevent, null);
                    ceventAdapter.openEntryForm();
                }
            });
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ProcessingEventWrapper.class);
    }

    @Override
    protected CollectionEventAdapter createChildNode() {
        return new CollectionEventAdapter(this, null, null);
    }

    @Override
    protected CollectionEventAdapter createChildNode(Object child) {
        Assert.isTrue(child instanceof CollectionEvent);
        return new CollectionEventAdapter(this, (CollectionEvent) child, null);
    }

    @Override
    protected Collection<? extends IBiobankModel> getChildrenObjects()
        throws Exception {
        return getModelObject().getCollectionEventCollection();
    }

    @Override
    protected int getChildrenCount() throws Exception {
        return (getChildrenObjects() == null) ? 0 : getChildrenObjects().size();
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
        return Messages.PatientAdapter_delete_confirm_msg;
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

}
