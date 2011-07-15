package edu.ualberta.med.biobank.views;

import org.eclipse.osgi.util.NLS;
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

    public static BgcLogger logger = BgcLogger.getLogger(ReportsView.class
        .getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView"; //$NON-NLS-1$

    public static ReportsView currentInstance;

    private CTabFolder top;

    private CTabItem specimenTab;

    private ReportTreeWidget specimenTree;
    private ReportTreeWidget clinicTree;
    private ReportTreeWidget patientTree;
    private ReportTreeWidget specimenTypeTree;
    private ReportTreeWidget containerTree;

    private CTabItem clinicTab;

    private CTabItem patientTab;

    private CTabItem specimenTypeTab;

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
        specimenTab.setText(Messages.ReportsView_specimens_tab_label);
        Composite specimenBody = new Composite(top, SWT.NONE);
        specimenBody.setLayout(treeLayout);
        specimenBody.setLayoutData(treeGd);
        specimenTab.setControl(specimenBody);
        specimenTree = new ReportTreeWidget(specimenBody);
        AbstractReportTreeNode specimenRoot = new AbstractReportTreeNode(""); //$NON-NLS-1$
        specimenTree.setLayoutData(treeGd);

        top.setSelection(specimenTab);

        // Clinics
        clinicTab = new CTabItem(top, SWT.NONE);
        clinicTab.setText(Messages.ReportsView_clinics_tab_label);
        Composite clinicBody = new Composite(top, SWT.NONE);
        clinicBody.setLayout(treeLayout);
        clinicBody.setLayoutData(treeGd);
        clinicTab.setControl(clinicBody);
        clinicTree = new ReportTreeWidget(clinicBody);
        AbstractReportTreeNode clinicRoot = new AbstractReportTreeNode(""); //$NON-NLS-1$
        clinicTree.setLayoutData(treeGd);

        // Patients
        patientTab = new CTabItem(top, SWT.NONE);
        patientTab.setText(Messages.ReportsView_patients_tab_label);
        Composite patientBody = new Composite(top, SWT.NONE);
        patientBody.setLayout(treeLayout);
        patientBody.setLayoutData(treeGd);
        patientTab.setControl(patientBody);
        patientTree = new ReportTreeWidget(patientBody);
        AbstractReportTreeNode patientRoot = new AbstractReportTreeNode(""); //$NON-NLS-1$
        patientTree.setLayoutData(treeGd);

        // Specimen Types
        specimenTypeTab = new CTabItem(top, SWT.NONE);
        specimenTypeTab.setText(Messages.ReportsView_specTypes_tab_label);
        Composite specimenTypeBody = new Composite(top, SWT.NONE);
        specimenTypeBody.setLayout(treeLayout);
        specimenTypeBody.setLayoutData(treeGd);
        specimenTypeTab.setControl(specimenTypeBody);
        specimenTypeTree = new ReportTreeWidget(specimenTypeBody);
        AbstractReportTreeNode specimenTypeRoot = new AbstractReportTreeNode(""); //$NON-NLS-1$
        specimenTypeTree.setLayoutData(treeGd);

        // Containers
        containerTab = new CTabItem(top, SWT.NONE);
        containerTab.setText(Messages.ReportsView_containers_tab_label);
        Composite containerBody = new Composite(top, SWT.NONE);
        containerBody.setLayout(treeLayout);
        containerBody.setLayoutData(treeGd);
        containerTab.setControl(containerBody);
        containerTree = new ReportTreeWidget(containerBody);
        AbstractReportTreeNode containerRoot = new AbstractReportTreeNode(""); //$NON-NLS-1$
        containerTree.setLayoutData(treeGd);

        initializeNewReports(specimenRoot, clinicRoot, patientRoot,
            specimenTypeRoot, containerRoot);

        specimenTree.setInput(specimenRoot);
        specimenTree.expandAll();
        clinicTree.setInput(clinicRoot);
        clinicTree.expandAll();
        patientTree.setInput(patientRoot);
        patientTree.expandAll();
        specimenTypeTree.setInput(specimenTypeRoot);
        specimenTypeTree.expandAll();
        containerTree.setInput(containerRoot);
        containerTree.expandAll();

    }

    private void initializeNewReports(AbstractReportTreeNode specimens,
        AbstractReportTreeNode clinics, AbstractReportTreeNode patients,
        AbstractReportTreeNode specimenTypes, AbstractReportTreeNode containers) {
        String[] names = BiobankReport.getReportNames();
        for (int i = 0; i < names.length; i++) {
            try {
                ReportTreeNode child = new ReportTreeNode(
                    BiobankReport.getReportByName(names[i]));
                addInTree(specimens, clinics, patients, specimenTypes,
                    containers, child);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addInTree(AbstractReportTreeNode specimens,
        AbstractReportTreeNode clinics, AbstractReportTreeNode patients,
        AbstractReportTreeNode specimenTypes,
        AbstractReportTreeNode containers, ReportTreeNode child)
        throws Exception {
        switch (child.getReport().getType()) {
        case SPECIMEN_TYPE:
            specimenTypes.addChild(child);
            child.setParent(specimenTypes);
            break;
        case SPECIMEN:
            specimens.addChild(child);
            child.setParent(specimens);
            break;
        case PATIENT:
            patients.addChild(child);
            child.setParent(patients);
            break;
        case CLINIC:
            clinics.addChild(child);
            child.setParent(clinics);
            break;
        case CONTAINER:
            containers.addChild(child);
            child.setParent(containers);
            break;
        default:
            throw new Exception(NLS.bind("Unable to place report node: {0}", //$NON-NLS-1$
                child.getLabel()));
        }
    }

    @Override
    public void setFocus() {

    }

}
