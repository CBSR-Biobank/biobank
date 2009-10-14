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
    }

    public PatientVisitWrapper getWrapper() {
        return (PatientVisitWrapper) modelObject;
    }

    @Override
    public String getName() {
        PatientVisitWrapper patientVisitWrapper = getWrapper();
        Assert.isNotNull(patientVisitWrapper.getWrappedObject(),
            "patientVisit is null");
        return patientVisitWrapper.getFormattedDateDrawn();
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
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Edit Visit");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(PatientVisitAdapter.this),
                    PatientVisitEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View Visit");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(PatientVisitAdapter.this),
                    PatientVisitViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

    public void setSelectedSample(SampleWrapper sample) {
        this.selectedSample = sample;
    }

    public SampleWrapper getSelectedSample() {
        return selectedSample;
    }

}
