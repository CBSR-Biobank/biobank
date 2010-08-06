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
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.ReportTreeWidget;

public class ReportsView extends ViewPart {

    public static BiobankLogger logger = BiobankLogger
        .getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";

    public static ReportsView currentInstance;

    private CTabFolder top;

    private CTabItem aliquotTab;

    private ReportTreeWidget aliquotTree;
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

        // Aliquots
        aliquotTab = new CTabItem(top, SWT.NONE);
        aliquotTab.setText("Aliquots");
        Composite aliquotBody = new Composite(top, SWT.NONE);
        aliquotBody.setLayout(treeLayout);
        aliquotBody.setLayoutData(treeGd);
        aliquotTab.setControl(aliquotBody);
        aliquotTree = new ReportTreeWidget(aliquotBody);
        AbstractReportTreeNode aliquotRoot = new AbstractReportTreeNode("");
        aliquotTree.setLayoutData(treeGd);

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

        initializeNewReports(aliquotRoot, clinicRoot, patientRoot,
            sampleTypeRoot, containerRoot);

        aliquotTree.setInput(aliquotRoot);
        aliquotTree.expandAll();
        clinicTree.setInput(clinicRoot);
        clinicTree.expandAll();
        patientTree.setInput(patientRoot);
        patientTree.expandAll();
        sampleTypeTree.setInput(sampleTypeRoot);
        sampleTypeTree.expandAll();
        containerTree.setInput(containerRoot);
        containerTree.expandAll();
        /*
         * List<Class<?>> advancedObjs = SearchUtils.getSearchableObjs(); for
         * (Class<?> obj : advancedObjs) { AdvancedReportTreeNode child = new
         * AdvancedReportTreeNode(obj .getSimpleName().replace("Wrapper", ""),
         * QueryTree.constructTree(new HQLField("", obj.getSimpleName(), obj)));
         * advanced.addChild(child); child.setParent(advanced); }
         * 
         * AbstractReportTreeNode custom = new AbstractReportTreeNode("Custom");
         * custom.setParent(advanced); advanced.addChild(custom);
         * 
         * File dir = new File(Platform.getInstanceLocation().getURL().getPath()
         * + "/saved_reports"); File[] files = dir.listFiles(); if (files !=
         * null) try { for (int i = 0; i < files.length; i++) { if
         * (files[i].getName().contains(".xml")) { String name =
         * files[i].getName().replace(".xml", ""); AdvancedReportTreeNode
         * customNode = new AdvancedReportTreeNode( name,
         * QueryTreeNode.getTreeFromFile(files[i]));
         * customNode.setParent(custom); custom.addChild(customNode); } } }
         * catch (Exception e) {
         * BioBankPlugin.openAsyncError("Error loading saved reports", e); }
         * 
         * root.addChild(standard); standard.setParent(root); // FIXME: Advanced
         * reports temporarily disabled // root.addChild(advanced);
         * advanced.setParent(root);
         */
    }

    private void initializeNewReports(AbstractReportTreeNode aliquots,
        AbstractReportTreeNode clinics, AbstractReportTreeNode patients,
        AbstractReportTreeNode sampleTypes, AbstractReportTreeNode containers) {
        String[] names = BiobankReport.getReportNames();
        for (int i = 0; i < names.length; i++) {
            try {
                ReportTreeNode child = new ReportTreeNode(
                    BiobankReport.getReportByName(names[i]));
                addInTree(aliquots, clinics, patients, sampleTypes, containers,
                    child);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addInTree(AbstractReportTreeNode aliquots,
        AbstractReportTreeNode clinics, AbstractReportTreeNode patients,
        AbstractReportTreeNode sampleTypes, AbstractReportTreeNode containers,
        ReportTreeNode child) throws Exception {
        if (child.getLabel().contains("Aliquot")) {
            aliquots.addChild(child);
            child.setParent(aliquots);
        } else if (child.getLabel().contains("Sample Type")) {
            sampleTypes.addChild(child);
            child.setParent(sampleTypes);
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
            throw new Exception("Unable to place report node.");
    }

    @Override
    public void setFocus() {

    }

}
