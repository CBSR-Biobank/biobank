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
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class PatientAdapter extends AdapterBase {

    public PatientAdapter(AdapterBase parent, PatientWrapper patientWrapper) {
        this(parent, patientWrapper, true);
    }

    public PatientAdapter(AdapterBase parent, PatientWrapper patientWrapper,
        boolean enableActions) {
        super(parent, patientWrapper, enableActions);
        setHasChildren(true);
    }

    public PatientWrapper getWrapper() {
        return (PatientWrapper) modelObject;
    }

    @Override
    public String getName() {
        PatientWrapper patientWrapper = getWrapper();
        Assert.isNotNull(patientWrapper.getWrappedObject(), "patient is null");
        return patientWrapper.getPnumber();
    }

    @Override
    public String getTitle() {
        return getTitle("Patient");
    }

    @Override
    public void performDoubleClick() {
        performExpand();
        openForm(new FormInput(this), PatientViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Patient", PatientEntryForm.ID);
        addViewMenu(menu, "Patient", PatientViewForm.ID);

        if (enableActions) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Patient Visit");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    PatientVisitAdapter adapter = new PatientVisitAdapter(
                        PatientAdapter.this, new PatientVisitWrapper(
                            getAppService()));
                    adapter.getWrapper().setPatient(getWrapper());
                    openForm(new FormInput(adapter), PatientVisitEntryForm.ID);
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
}
