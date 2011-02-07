package edu.ualberta.med.biobank.treeview.patient;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.PatientVisitViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class PatientVisitAdapter extends AdapterBase {

    /**
     * Aliquot selected in this patient visit
     */
    private AliquotWrapper selectedAliquot;

    public PatientVisitAdapter(AdapterBase parent,
        ProcessingEventWrapper patientVisitWrapper) {
        super(parent, patientVisitWrapper);
        setEditable(parent instanceof PatientAdapter || parent == null);
    }

    public ProcessingEventWrapper getWrapper() {
        return (ProcessingEventWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        ProcessingEventWrapper wrapper = getWrapper();
        Assert.isNotNull(wrapper, "patientVisit is null");
        String name = wrapper.getFormattedDateProcessed();
        Collection<AliquotWrapper> samples = wrapper.getAliquotCollection();
        int total = 0;
        if (samples != null) {
            total = samples.size();
        }
        return name + " [" + total + "]";
    }

    @Override
    public String getTooltipText() {
        ProcessingEventWrapper visit = getWrapper();
        if (visit != null) {
            PatientWrapper patient = visit.getPatient();
            if (patient != null) {
                StudyWrapper study = patient.getStudy();
                Assert.isNotNull(study, "study is null");
                return study.getNameShort() + " - " + patient.getPnumber()
                    + " - " + getTooltipText("Patient Visit");

            }
        }
        return getTooltipText("Patient Visit");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Visit");
        addViewMenu(menu, "Visit");
        addDeleteMenu(menu, "Visit");
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this visit?";
    }

    public void setSelectedAliquot(AliquotWrapper aliquot) {
        this.selectedAliquot = aliquot;
    }

    public AliquotWrapper getSelectedAliquot() {
        return selectedAliquot;
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return PatientVisitEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return PatientVisitViewForm.ID;
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

}
