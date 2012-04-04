package edu.ualberta.med.biobank.views;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.common.reports.AbstractReportTreeNode;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.trees.ReportTreeWidget;

public class ReportsView extends ViewPart {

    public static BgcLogger logger = BgcLogger.getLogger(ReportsView.class
        .getName());

    public static final String ID =
        "edu.ualberta.med.biobank.views.ReportsView"; 

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

    private boolean allowed = false;

    public ReportsView() {
        currentInstance = this;
        try {
            if (SessionManager.getInstance().isConnected())
                this.allowed =
                    SessionManager.getAppService().isAllowed(
                        new ReportsPermission());
        } catch (Exception e) {
            BgcPlugin.openAccessDeniedErrorMessage(e);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new CTabFolder(parent, SWT.BORDER);

        GridLayout treeLayout = new GridLayout();
        GridData treeGd =
            new GridData(GridData.FILL, GridData.FILL, true, true);

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

        // Specimen Types
        specimenTypeTab = new CTabItem(top, SWT.NONE);
        specimenTypeTab.setText("Specimen Types");
        Composite specimenTypeBody = new Composite(top, SWT.NONE);
        specimenTypeBody.setLayout(treeLayout);
        specimenTypeBody.setLayoutData(treeGd);
        specimenTypeTab.setControl(specimenTypeBody);
        specimenTypeTree = new ReportTreeWidget(specimenTypeBody);
        AbstractReportTreeNode specimenTypeRoot =
            new AbstractReportTreeNode(""); 
        specimenTypeTree.setLayoutData(treeGd);

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
            specimenTypeRoot, containerRoot);

        if (allowed) {
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
            throw new Exception(NLS.bind("Unable to place report node: {0}", 
                child.getLabel()));
        }
    }

    @Override
    public void setFocus() {

    }

}
