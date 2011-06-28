package edu.ualberta.med.biobank.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.common.reports.AbstractReportTreeNode;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.widgets.trees.ReportTreeWidget;

public class ReportsView extends ViewPart {

    public static BgcLogger logger = BgcLogger
        .getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";

    public static ReportsView currentInstance;

    private CTabFolder top;

    private CTabItem specimenTab;

    private ReportTreeWidget specimenTree;
    private ReportTreeWidget clinicTree;
    private ReportTreeWidget patientTree;
    private ReportTreeWidget sampleTypeTree;
    private ReportTreeWidget containerTree;

    private CTabItem clinicTab;

    private CTabItem patientTab;

    private CTabItem sampleTypeTab;

    private CTabItem containerTab;

    public ReportsView() {
        currentInstance = this;
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new CTabFolder(parent, SWT.BORDER);

        GridLayout treeLayout = new GridLayout();
        GridData treeGd = new GridData(GridData.FILL, GridData.FILL, true, true);

        // Specimens
        specimenTab = new CTabItem(top, SWT.NONE);
        specimenTab.setText("Specimens");
        Composite specimenBody = new Composite(top, SWT.NONE);
        specimenBody.setLayout(treeLayout);
        specimenBody.setLayoutData(treeGd);
        specimenTab.setControl(specimenBody);
        specimenTree = new ReportTreeWidget(specimenBody);
        AbstractReportTreeNode specimenRoot = new AbstractReportTreeNode("");
        specimenTree.setLayoutData(treeGd);

        top.setSelection(specimenTab);

        // Clinics
        clinicTab = new CTabItem(top, SWT.NONE);
        clinicTab.setText("Clinics");
        Composite clinicBody = new Composite(top, SWT.NONE);
        clinicBody.setLayout(treeLayout);
        clinicBody.setLayoutData(treeGd);
        clinicTab.setControl(clinicBody);
        clinicTree = new ReportTreeWidget(clinicBody);
        AbstractReportTreeNode clinicRoot = new AbstractReportTreeNode("");
        clinicTree.setLayoutData(treeGd);

        // Patients
        patientTab = new CTabItem(top, SWT.NONE);
        patientTab.setText("Patients");
        Composite patientBody = new Composite(top, SWT.NONE);
        patientBody.setLayout(treeLayout);
        patientBody.setLayoutData(treeGd);
        patientTab.setControl(patientBody);
        patientTree = new ReportTreeWidget(patientBody);
        AbstractReportTreeNode patientRoot = new AbstractReportTreeNode("");
        patientTree.setLayoutData(treeGd);

        // Sample Types
        sampleTypeTab = new CTabItem(top, SWT.NONE);
        sampleTypeTab.setText("Sample Types");
        Composite sampleTypeBody = new Composite(top, SWT.NONE);
        sampleTypeBody.setLayout(treeLayout);
        sampleTypeBody.setLayoutData(treeGd);
        sampleTypeTab.setControl(sampleTypeBody);
        sampleTypeTree = new ReportTreeWidget(sampleTypeBody);
        AbstractReportTreeNode sampleTypeRoot = new AbstractReportTreeNode("");
        sampleTypeTree.setLayoutData(treeGd);

        // Containers
        containerTab = new CTabItem(top, SWT.NONE);
        containerTab.setText("Containers");
        Composite containerBody = new Composite(top, SWT.NONE);
        containerBody.setLayout(treeLayout);
        containerBody.setLayoutData(treeGd);
        containerTab.setControl(containerBody);
        containerTree = new ReportTreeWidget(containerBody);
        AbstractReportTreeNode containerRoot = new AbstractReportTreeNode("");
        containerTree.setLayoutData(treeGd);

        initializeNewReports(specimenRoot, clinicRoot, patientRoot,
            sampleTypeRoot, containerRoot);

        specimenTree.setInput(specimenRoot);
        specimenTree.expandAll();
        clinicTree.setInput(clinicRoot);
        clinicTree.expandAll();
        patientTree.setInput(patientRoot);
        patientTree.expandAll();
        sampleTypeTree.setInput(sampleTypeRoot);
        sampleTypeTree.expandAll();
        containerTree.setInput(containerRoot);
        containerTree.expandAll();

    }

    private void initializeNewReports(AbstractReportTreeNode specimens,
        AbstractReportTreeNode clinics, AbstractReportTreeNode patients,
        AbstractReportTreeNode sampleTypes, AbstractReportTreeNode containers) {
        String[] names = BiobankReport.getReportNames();
        for (int i = 0; i < names.length; i++) {
            try {
                ReportTreeNode child = new ReportTreeNode(
                    BiobankReport.getReportByName(names[i]));
                addInTree(specimens, clinics, patients, sampleTypes,
                    containers, child);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addInTree(AbstractReportTreeNode specimens,
        AbstractReportTreeNode clinics, AbstractReportTreeNode patients,
        AbstractReportTreeNode sampleTypes, AbstractReportTreeNode containers,
        ReportTreeNode child) throws Exception {
        if (child.getLabel().contains("Specimen Type")
            || child.getLabel().contains("Invoicing")) {
            sampleTypes.addChild(child);
            child.setParent(sampleTypes);
        } else if (child.getLabel().contains("Specimen")) {
            specimens.addChild(child);
            child.setParent(specimens);
        } else if (child.getLabel().contains("Patient")) {
            patients.addChild(child);
            child.setParent(patients);
        } else if (child.getLabel().contains("Clinic")) {
            clinics.addChild(child);
            child.setParent(clinics);
        } else if (child.getLabel().contains("Container")) {
            containers.addChild(child);
            child.setParent(containers);
        } else
            throw new Exception("Unable to place report node: "
                + child.getLabel());
    }

    @Override
    public void setFocus() {

    }

}
