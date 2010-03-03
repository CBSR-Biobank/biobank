package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.PatientVisitViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class PatientVisitAdapter extends AdapterBase {

    /**
     * Aliquot selected in this patient visit
     */
    private AliquotWrapper selectedAliquot;

    public PatientVisitAdapter(AdapterBase parent,
        PatientVisitWrapper patientVisitWrapper) {
        super(parent, patientVisitWrapper);
        setEditable(parent instanceof PatientAdapter);
    }

    public PatientVisitWrapper getWrapper() {
        return (PatientVisitWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        PatientVisitWrapper wrapper = getWrapper();
        Assert.isNotNull(wrapper, "patientVisit is null");
        String name = wrapper.getFormattedDateProcessed();
        if (wrapper.getShipment() != null) {
            name += " - " + wrapper.getShipment().getWaybill();
        }
        Collection<AliquotWrapper> samples = wrapper.getAliquotCollection();
        int total = 0;
        if (samples != null) {
            total = samples.size();
        }
        return name + " [" + total + "]";
    }

    @Override
    public String getTooltipText() {
        PatientVisitWrapper visit = getWrapper();
        PatientWrapper patient = visit.getPatient();
        if (patient != null) {
            StudyWrapper study = patient.getStudy();
            Assert.isNotNull(study, "study is null");
            SiteWrapper site = study.getSite();
            Assert.isNotNull(site, "site is null");
            return site.getName() + " - " + study.getName() + " - "
                + patient.getPnumber() + " - "
                + getTooltipText("Patient Visit");

        }
        return getTooltipText("Patient Visit");
    }

    @Override
    public void executeDoubleClick() {
        openForm(new FormInput(this), PatientVisitViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Visit", PatientVisitEntryForm.ID);
        addViewMenu(menu, "Visit", PatientVisitViewForm.ID);
    }

    public void setSelectedAliquot(AliquotWrapper aliquot) {
        this.selectedAliquot = aliquot;
    }

    public AliquotWrapper getSelectedAliquot() {
        return selectedAliquot;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
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

}
