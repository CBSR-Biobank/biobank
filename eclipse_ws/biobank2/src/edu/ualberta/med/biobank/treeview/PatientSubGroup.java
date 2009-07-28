package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Patient;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PatientSubGroup extends Node {

    private List<Integer> patientIDs;
    private static final int maxSize = 100;

    public PatientSubGroup(PatientGroup parent, int id) {
        super(parent, id, "Group " + (id + 1), true);
        patientIDs = new ArrayList<Integer>();
    }

    public boolean full() {
        if (patientIDs != null)
            return (patientIDs.size() >= maxSize);
        else
            return false;
    }

    public boolean addID(Integer id) {
        if (!full()) {
            patientIDs.add(id);
            return true;
        }
        return false;
    }

    public boolean hasId(Integer i) {
        return patientIDs.contains(i);
    }

    @Override
    public void performDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        /*
         * MenuItem mi = new MenuItem(menu, SWT.PUSH);
         * mi.setText("Add Patient"); mi.addSelectionListener(new
         * SelectionListener() { public void widgetSelected(SelectionEvent
         * event) { PatientAdapter adapter = new PatientAdapter(
         * PatientSubGroup.this, new Patient()); openForm(new
         * FormInput(adapter), PatientEntryForm.ID); }
         * 
         * public void widgetDefaultSelected(SelectionEvent e) { } });
         */
    }

    private Patient retrievePatient(int patient) {
        WritableApplicationService appService = ((PatientGroup) getParent())
            .getAppService();
        List<Patient> result;
        Patient searchPatient = new Patient();
        searchPatient.setId(patient);
        try {
            result = appService.search(Patient.class, searchPatient);
            Assert.isTrue(result.size() == 1);
            return result.get(0);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void loadChildren(boolean updateNode) {
        PatientGroup parentGroup = (PatientGroup) getParent();
        Assert.isNotNull(parentGroup, "null group");
        try {

            for (Integer patient : patientIDs) {
                PatientAdapter node = (PatientAdapter) getChild(patient
                    .intValue());

                if (node == null) {
                    node = new PatientAdapter(this, retrievePatient(patient
                        .intValue()));
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().getTreeViewer().update(node,
                        null);
                }
            }

        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading patient sub group children"
                    + parentGroup.getName(), e);
        }
    }

    @Override
    public Node accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getTitle() {
        return null;
    }
}
