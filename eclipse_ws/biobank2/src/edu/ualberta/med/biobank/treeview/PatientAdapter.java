package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PatientVisitComparator;

public class PatientAdapter extends AdapterBase {

    public PatientAdapter(AdapterBase parent, PatientWrapper patientWrapper) {
        super(parent, patientWrapper, PatientWrapper.class);
        setHasChildren(true);
    }

    public PatientAdapter(AdapterBase parent, Patient patient) {
        super(parent, null, null);
        setWrappedObject(new PatientWrapper(getAppService(), patient),
            PatientWrapper.class);
        setHasChildren(true);
    }

    public PatientWrapper getWrapper() {
        return (PatientWrapper) getWrappedObject();
    }

    @Override
    protected Integer getWrappedObjectId() {
        return getWrapper().getId();
    }

    @Override
    public Integer getId() {
        PatientWrapper patientWrapper = getWrapper();
        Assert.isNotNull(patientWrapper.getWrappedObject(), "patient is null");
        return patientWrapper.getId();
    }

    @Override
    public String getName() {
        PatientWrapper patientWrapper = getWrapper();
        Assert.isNotNull(patientWrapper.getWrappedObject(), "patient is null");
        return patientWrapper.getNumber();
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
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Edit Patient");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(PatientAdapter.this),
                    PatientEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View Patient");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(PatientAdapter.this), PatientViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Patient Visit");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                PatientVisitAdapter adapter = new PatientVisitAdapter(
                    PatientAdapter.this, new PatientVisit());
                adapter.getWrapper().setPatientWrapper(getWrapper());
                openForm(new FormInput(adapter), PatientVisitEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        try {
            PatientWrapper patientWrapper = getWrapper();
            // read from database again
            patientWrapper.reload();

            List<PatientVisit> visits = new ArrayList<PatientVisit>(
                patientWrapper.getPatientVisitCollection());
            Collections.sort(visits, new PatientVisitComparator());

            for (PatientVisit visit : visits) {
                PatientVisitAdapter node = (PatientVisitAdapter) getChild(visit
                    .getId());

                if (node == null) {
                    node = new PatientVisitAdapter(this, visit);
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().getTreeViewer().update(node,
                        null);
                }
            }
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading children of patient "
                    + getWrapper().getNumber(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    protected boolean integrityCheck() {
        return true;
    }
}
