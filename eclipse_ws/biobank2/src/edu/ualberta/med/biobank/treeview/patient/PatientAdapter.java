package edu.ualberta.med.biobank.treeview.patient;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.cevent.GetSimplePatientCollectionEventInfosAction;
import edu.ualberta.med.biobank.common.action.cevent.GetSimplePatientCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.SearchPatientAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;

public class PatientAdapter extends AbstractNewAdapterBase {

    private Patient patient;
    private Study study;
    private Long ceventsCount;

    public PatientAdapter(AbstractAdapterBase parent, SearchedPatientInfo pinfo) {
        super(parent, pinfo == null ? null : pinfo.patient.getId(), null, null,
            (pinfo == null || pinfo.ceventsCount == null) ? false
                : pinfo.ceventsCount > 0);
        if (pinfo != null) {
            this.patient = pinfo.patient;
            this.study = pinfo.study;
            this.ceventsCount = pinfo.ceventsCount;
        }
    }

    @Override
    protected String getLabelInternal() {
        if (patient == null)
            return "no patient - should not see this"; //$NON-NLS-1$
        return patient.getPnumber();
    }

    @Override
    public String getTooltipTextInternal() {
        if (patient != null && study != null)
            return study.getName()
                + " - " + getTooltipText(Messages.PatientAdapter_patient_label); //$NON-NLS-1$
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
                    SimpleCEventInfo cevent = new SimpleCEventInfo();
                    cevent.cevent = new CollectionEvent();
                    cevent.cevent.setPatient(patient);
                    CollectionEventAdapter ceventAdapter = new CollectionEventAdapter(
                        null, cevent);
                    ceventAdapter.openEntryForm();
                }
            });
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId,
            CollectionEvent.class);
    }

    @Override
    protected CollectionEventAdapter createChildNode() {
        return new CollectionEventAdapter(this, null);
    }

    @Override
    protected CollectionEventAdapter createChildNode(Object child) {
        Assert.isTrue(child instanceof SimpleCEventInfo);
        return new CollectionEventAdapter(this, (SimpleCEventInfo) child);
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        return SessionManager.getAppService().doAction(
            new GetSimplePatientCollectionEventInfosAction(patient.getId()));
    }

    @Override
    protected int getChildrenCount() throws Exception {
        return ceventsCount.intValue();
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

    public Patient getPatient() {
        return patient;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof PatientAdapter)
            return patient.getPnumber().compareTo(
                ((PatientAdapter) o).patient.getPnumber());
        return 0;
    }
}
