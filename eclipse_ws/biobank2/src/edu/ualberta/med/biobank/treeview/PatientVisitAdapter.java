package edu.ualberta.med.biobank.treeview;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.PatientVisitViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;

public class PatientVisitAdapter extends AdapterBase {

    private PatientVisit patientVisit;

    /**
     * Sample selected in this patient visit
     */
    private Sample selectedSample;

    public PatientVisitAdapter(AdapterBase parent, PatientVisit patientVisit) {
        super(parent);
        this.patientVisit = patientVisit;
    }

    public PatientVisit getPatientVisit() {
        return patientVisit;
    }

    @Override
    public Integer getId() {
        Assert.isNotNull(patientVisit, "patientVisit is null");
        return patientVisit.getId();
    }

    @Override
    public String getName() {
        Assert.isNotNull(patientVisit, "patientVisit is null");
        Date date = patientVisit.getDateDrawn();
        // Assert.isNotNull(date, "patient visid drawn date is null");
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(
                BioBankPlugin.DATE_TIME_FORMAT);
            return sdf.format(date);
        }
        return null;
    }

    @Override
    public String getTreeText() {
        Collection<Sample> samples = patientVisit.getSampleCollection();
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
        return visitor.visit(this);
    }

    public void setPatientVisit(PatientVisit patientVisit) {
        this.patientVisit = patientVisit;
    }

    public void setSelectedSample(Sample sample) {
        this.selectedSample = sample;
    }

    public Sample getSelectedSample() {
        return selectedSample;
    }

}
