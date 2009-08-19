package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.ModelUtils;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientAdapter extends AdapterBase {

    private Patient patient;

    public PatientAdapter(AdapterBase parent, Patient patient) {
        super(parent);
        this.patient = patient;
        setHasChildren(true);
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public Integer getId() {
        Assert.isNotNull(patient, "patient is null");
        return patient.getId();
    }

    @Override
    public String getName() {
        Assert.isNotNull(patient, "storage type is null");
        return patient.getNumber();
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
                openForm(new FormInput(adapter), PatientVisitEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        try {
            // read from database again
            patient = ModelUtils.getObjectWithId(getAppService(),
                Patient.class, patient.getId());

            Collection<PatientVisit> visits = patient
                .getPatientVisitCollection();

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
                    + patient.getNumber(), e);
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }
}
