package edu.ualberta.med.biobank.treeview;

import java.util.Arrays;
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
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientGroup extends Node {

    public PatientGroup(StudyAdapter parent, int id) {
        super(parent, id, "Patients", true);
    }

    @Override
    public void performDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Patient");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                PatientAdapter adapter = new PatientAdapter(PatientGroup.this,
                    new Patient());
                openForm(new FormInput(adapter), PatientEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private List<Integer> getPatientIDs(int studyID) throws Exception {
        WritableApplicationService appService = ((StudyAdapter) getParent())
            .getAppService();

        HQLCriteria c = new HQLCriteria("select patients.id"
            + " from edu.ualberta.med.biobank.model.Patient as patients"
            + " inner join patients.study as study" + " where study.id=?");
        c.setParameters(Arrays.asList(new Object[] { studyID }));

        return (appService.query(c));
    }

    @Override
    public void loadChildren(boolean updateNode) {
        Study parentStudy = ((StudyAdapter) getParent()).getStudy();
        Assert.isNotNull(parentStudy, "null study");
        try {
            // read from database again
            parentStudy = (Study) ModelUtils.getObjectWithId(getAppService(),
                Study.class, parentStudy.getId());
            ((StudyAdapter) getParent()).setStudy(parentStudy);

            List<Integer> patientIDs = getPatientIDs(parentStudy.getId());
            List<Node> nodes = getChildren();
            Boolean found = false;

            for (Integer patient : patientIDs) {
                for (Node node : nodes) {
                    if (node.getChild(patient) != null) {
                        found = true;
                        break;
                    }
                }
                Boolean inserted = false;
                if (!found) {
                    for (Node node : nodes) {
                        if (!((PatientSubGroup) node).full()) {
                            ((PatientSubGroup) node).addID(patient);
                            inserted = true;
                            break;
                        }
                    }

                }
                if (!inserted) {
                    PatientSubGroup newNode = new PatientSubGroup(this, nodes
                        .size());
                    newNode.addID(patient);
                    addChild(newNode);
                }

            }
            if (updateNode) {
                for (Node node : nodes) {
                    SessionManager.getInstance().getTreeViewer().update(node,
                        null);
                }
            }

        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading patient group children for study "
                    + parentStudy.getName(), e);
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
