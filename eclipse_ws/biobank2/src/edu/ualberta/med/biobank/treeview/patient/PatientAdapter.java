package edu.ualberta.med.biobank.treeview.patient;

import java.util.ArrayList;
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
import edu.ualberta.med.biobank.common.action.cevent.CollectionEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientInfo;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;

public class PatientAdapter extends AbstractNewAdapterBase {

    private PatientInfo pinfo;

    public PatientAdapter(AbstractAdapterBase parent, PatientInfo pinfo) {
        super(parent, pinfo.getClass(), pinfo.patient.getId(), null, null,
            pinfo.cevents.size() > 0);
        this.pinfo = pinfo;
    }

    @Override
    protected String getLabelInternal() {
        Assert.isNotNull(pinfo, "patient is null"); //$NON-NLS-1$
        return pinfo.patient.getPnumber();
    }

    @Override
    public String getTooltipTextInternal() {
        if (pinfo != null) {
            Study study = pinfo.patient.getStudy();
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
                    cevent.setPatient(pinfo.patient);
                    CollectionEventAdapter ceventAdapter = new CollectionEventAdapter(
                        PatientAdapter.this, null);
                    ceventAdapter.openEntryForm();
                }
            });
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId,
            CollectionEventInfo.class);
    }

    @Override
    protected CollectionEventAdapter createChildNode() {
        return new CollectionEventAdapter(this, null);
    }

    @Override
    protected CollectionEventAdapter createChildNode(Object child) {
        Assert.isTrue(child instanceof CollectionEventInfo);
        return new CollectionEventAdapter(this, (CollectionEventInfo) child);
    }

    @Override
    protected List<CollectionEventInfo> getChildrenObjects() throws Exception {
        return pinfo.cevents;
    }

    @Override
    protected List<Integer> getChildrenObjectIds() throws Exception {
        List<Integer> ids = new ArrayList<Integer>();
        for (CollectionEventInfo cevent : pinfo.cevents) {
            ids.add(cevent.cevent.getId());
        }
        return ids;
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

    // FIXME do we want to do this ?
    public Patient getPatient() {
        return pinfo.patient;
    }

}
