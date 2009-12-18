package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.PatientVisitViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class PatientVisitAdapter extends AdapterBase {

    /**
     * Sample selected in this patient visit
     */
    private SampleWrapper selectedSample;

    public PatientVisitAdapter(AdapterBase parent,
        PatientVisitWrapper patientVisitWrapper) {
        super(parent, patientVisitWrapper);
        setEditable(parent instanceof PatientAdapter);
    }

    public PatientVisitWrapper getWrapper() {
        return (PatientVisitWrapper) modelObject;
    }

    @Override
    public String getName() {
        PatientVisitWrapper patientVisitWrapper = getWrapper();
        Assert.isNotNull(patientVisitWrapper.getWrappedObject(),
            "patientVisit is null");
        return patientVisitWrapper.getFormattedDateProcessed();
    }

    @Override
    public String getTreeText() {
        Collection<SampleWrapper> samples = getWrapper().getSampleCollection();
        int total = 0;
        if (samples != null) {
            total = samples.size();
        }
        return getName() + " [" + total + "]";
    }

    @Override
    public String getTitle() {
        return getTitle("Patient Visit");
    }

    @Override
    public void performDoubleClick() {
        openForm(new FormInput(this), PatientVisitViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Visit", PatientVisitEntryForm.ID);
        addViewMenu(menu, "Visit", PatientVisitViewForm.ID);
    }

    public void setSelectedSample(SampleWrapper sample) {
        this.selectedSample = sample;
    }

    public SampleWrapper getSelectedSample() {
        return selectedSample;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
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
