package edu.ualberta.med.biobank.views;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.client.reports.advanced.HQLField;
import edu.ualberta.med.biobank.client.reports.advanced.QueryTreeNode;
import edu.ualberta.med.biobank.client.reports.advanced.SearchUtils;
import edu.ualberta.med.biobank.common.reports.AbstractReportTreeNode;
import edu.ualberta.med.biobank.common.reports.AdvancedReportTreeNode;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.QueryTree;
import edu.ualberta.med.biobank.widgets.ReportTreeWidget;

public class ReportsView extends AbstractViewWithTree<AbstractReportTreeNode> {

    public static BiobankLogger logger = BiobankLogger
        .getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";

    public static ReportsView currentInstance;

    private Composite top;

    private ReportTreeWidget reportTree;

    public ReportsView() {
        currentInstance = this;
    }

    @Override
    public TreeViewer getTreeViewer() {
        return reportTree.getTreeViewer();
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new Composite(parent, SWT.BORDER);
        top.setLayout(new GridLayout());
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        reportTree = new ReportTreeWidget(top);

        GridData gd = new GridData();
        gd.verticalAlignment = SWT.FILL;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        reportTree.setLayoutData(gd);

        AbstractReportTreeNode root = new AbstractReportTreeNode("");
        AbstractReportTreeNode standard = new AbstractReportTreeNode("Standard");
        AbstractReportTreeNode advanced = new AbstractReportTreeNode("Advanced");

        // create standard's subnodes
        AbstractReportTreeNode aliquots = new AbstractReportTreeNode("Aliquots");
        AbstractReportTreeNode clinics = new AbstractReportTreeNode("Clinics");
        AbstractReportTreeNode patientVisits = new AbstractReportTreeNode(
            "PatientVisits");
        AbstractReportTreeNode patients = new AbstractReportTreeNode("Patients");
        AbstractReportTreeNode misc = new AbstractReportTreeNode("Sample Types");

        standard.addChild(aliquots);
        standard.addChild(clinics);
        standard.addChild(patientVisits);
        standard.addChild(patients);
        standard.addChild(misc);
        aliquots.setParent(standard);
        clinics.setParent(standard);
        patientVisits.setParent(standard);
        patients.setParent(standard);
        misc.setParent(standard);

        initializeNewReports(aliquots, clinics, patientVisits, patients, misc);
        List<Class<?>> advancedObjs = SearchUtils.getSearchableObjs();
        for (Class<?> obj : advancedObjs) {
            AdvancedReportTreeNode child = new AdvancedReportTreeNode(obj
                .getSimpleName().replace("Wrapper", ""),
                QueryTree.constructTree(new HQLField("", obj.getSimpleName(),
                    obj)));
            advanced.addChild(child);
            child.setParent(advanced);
        }

        AbstractReportTreeNode custom = new AbstractReportTreeNode("Custom");
        custom.setParent(advanced);
        advanced.addChild(custom);

        File dir = new File(Platform.getInstanceLocation().getURL().getPath()
            + "/saved_reports");
        File[] files = dir.listFiles();
        if (files != null)
            try {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().contains(".xml")) {
                        String name = files[i].getName().replace(".xml", "");
                        AdvancedReportTreeNode customNode = new AdvancedReportTreeNode(
                            name, QueryTreeNode.getTreeFromFile(files[i]));
                        customNode.setParent(custom);
                        custom.addChild(customNode);
                    }
                }
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Error loading saved reports", e);
            }

        root.addChild(standard);
        standard.setParent(root);
        // FIXME: Advanced reports temporarily disabled
        // root.addChild(advanced);
        advanced.setParent(root);
        getTreeViewer().setInput(root);
        getTreeViewer().expandAll();
    }

    private void initializeNewReports(AbstractReportTreeNode aliquots,
        AbstractReportTreeNode clinics, AbstractReportTreeNode patientVisits,
        AbstractReportTreeNode patients, AbstractReportTreeNode misc) {
        String[] names = BiobankReport.getReportNames();
        for (int i = 0; i < names.length; i++) {
            try {
                ReportTreeNode child = new ReportTreeNode(
                    BiobankReport.getReportByName(names[i]));
                addInTree(aliquots, clinics, patientVisits, patients, misc,
                    child);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addInTree(AbstractReportTreeNode aliquots,
        AbstractReportTreeNode clinics, AbstractReportTreeNode patientVisits,
        AbstractReportTreeNode patients, AbstractReportTreeNode misc,
        ReportTreeNode child) throws Exception {
        if (child.getLabel().contains("Aliquot")) {
            aliquots.addChild(child);
            child.setParent(aliquots);
        } else if (child.getLabel().contains("Sample Type")) {
            misc.addChild(child);
            child.setParent(misc);
        } else if (child.getLabel().contains("Patient Visit")) {
            patientVisits.addChild(child);
            child.setParent(patientVisits);
        } else if (child.getLabel().contains("Patient")) {
            patients.addChild(child);
            child.setParent(patients);
        } else if (child.getLabel().contains("Clinic")) {
            clinics.addChild(child);
            child.setParent(clinics);
        } else
            throw new Exception("Unable to place report node.");
    }

    @Override
    public void setFocus() {

    }

    @Override
    public AdapterBase searchNode(ModelWrapper<?> wrapper) {
        return null;
    }

}
