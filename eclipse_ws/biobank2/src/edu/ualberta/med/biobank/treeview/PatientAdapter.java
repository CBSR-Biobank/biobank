package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;

public class PatientAdapter extends AdapterBase {

    public PatientAdapter(AdapterBase parent, PatientWrapper patientWrapper) {
        super(parent, patientWrapper);
        if (patientWrapper != null) {
            setHasChildren(patientWrapper.getPatientVisitCollection() != null
                && patientWrapper.getPatientVisitCollection().size() > 0);
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
        StudyWrapper study = patient.getStudy();
        if (study != null) {
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
        addDeleteMenu(menu, "Patient",
            "Are you sure you want to delete this patient?");

        if (isEditable()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Patient Visit");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    PatientVisitAdapter adapter = new PatientVisitAdapter(
                        PatientAdapter.this, new PatientVisitWrapper(
                            getAppService()));
                    adapter.getWrapper().setPatient(getWrapper());
                    adapter.openEntryForm();
                }
            });
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new PatientVisitAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof PatientVisitWrapper);
        return new PatientVisitAdapter(this, (PatientVisitWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        getWrapper().reload();
        return getWrapper().getPatientVisitCollection();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return getWrapperChildren().size();
    }

    @Override
    public String getEntryFormId() {
        return PatientEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return PatientViewForm.ID;
    }
}
