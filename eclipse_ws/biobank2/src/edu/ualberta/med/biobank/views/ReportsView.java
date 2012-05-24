package edu.ualberta.med.biobank.views;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.part.ViewPart;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.common.reports.AbstractReportTreeNode;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.LoginPermissionSessionState;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.widgets.trees.ReportTreeWidget;

public class ReportsView extends ViewPart {
    private static final I18n i18n = I18nFactory.getI18n(ReportsView.class);

    public static BgcLogger logger = BgcLogger.getLogger(ReportsView.class
        .getName());

    @SuppressWarnings("nls")
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

    private Boolean allowed = false;

    private AbstractReportTreeNode specimenRoot;

    private AbstractReportTreeNode clinicRoot;

    private AbstractReportTreeNode patientRoot;

    private AbstractReportTreeNode specimenTypeRoot;

    private AbstractReportTreeNode containerRoot;

    public ReportsView() {
        currentInstance = this;
        try {
            if (SessionManager.getInstance().isConnected()) allowed =
                SessionManager.getAppService().isAllowed(
                    new ReportsPermission());
        } catch (Exception e) {
            BgcPlugin.openAccessDeniedErrorMessage(e);
        }
        BgcPlugin.getLoginStateSourceProvider()
            .addSourceProviderListener(new ISourceProviderListener() {

                @SuppressWarnings("rawtypes")
                @Override
                public void sourceChanged(int sourcePriority,
                    Map sourceValuesByName) {
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    try {
                        if (sourceName
                            .equals(LoginPermissionSessionState.LOGIN_STATE_SOURCE_NAME)) {
                            if (sourceValue.equals(false)) {
                                allowed = false;
                            }
                            else {
                                allowed =
                                    SessionManager.getAppService().isAllowed(
                                        new ReportsPermission());
                            }
                        }
                        reload();
                    } catch (Exception e) {
                        BgcPlugin.openAccessDeniedErrorMessage(e);
                    }
                }
            });
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new CTabFolder(parent, SWT.BORDER);

        GridLayout treeLayout = new GridLayout();
        GridData treeGd =
            new GridData(GridData.FILL, GridData.FILL, true, true);

        // Specimens
        specimenTab = new CTabItem(top, SWT.NONE);
        specimenTab.setText(Specimen.NAME.plural().toString());
        Composite specimenBody = new Composite(top, SWT.NONE);
        specimenBody.setLayout(treeLayout);
        specimenBody.setLayoutData(treeGd);
        specimenTab.setControl(specimenBody);
        specimenTree = new ReportTreeWidget(specimenBody);
        specimenRoot = new AbstractReportTreeNode(StringUtil.EMPTY_STRING);
        specimenTree.setLayoutData(treeGd);

        top.setSelection(specimenTab);

        // Clinics
        clinicTab = new CTabItem(top, SWT.NONE);
        clinicTab.setText(Clinic.NAME.plural().toString());
        Composite clinicBody = new Composite(top, SWT.NONE);
        clinicBody.setLayout(treeLayout);
        clinicBody.setLayoutData(treeGd);
        clinicTab.setControl(clinicBody);
        clinicTree = new ReportTreeWidget(clinicBody);
        clinicRoot = new AbstractReportTreeNode(StringUtil.EMPTY_STRING);
        clinicTree.setLayoutData(treeGd);

        // Patients
        patientTab = new CTabItem(top, SWT.NONE);
        patientTab.setText(Patient.NAME.plural().toString());
        Composite patientBody = new Composite(top, SWT.NONE);
        patientBody.setLayout(treeLayout);
        patientBody.setLayoutData(treeGd);
        patientTab.setControl(patientBody);
        patientTree = new ReportTreeWidget(patientBody);
        patientRoot = new AbstractReportTreeNode(StringUtil.EMPTY_STRING);
        patientTree.setLayoutData(treeGd);

        // Specimen Types
        specimenTypeTab = new CTabItem(top, SWT.NONE);
        specimenTypeTab.setText(SpecimenType.NAME.plural().toString());
        Composite specimenTypeBody = new Composite(top, SWT.NONE);
        specimenTypeBody.setLayout(treeLayout);
        specimenTypeBody.setLayoutData(treeGd);
        specimenTypeTab.setControl(specimenTypeBody);
        specimenTypeTree = new ReportTreeWidget(specimenTypeBody);
        specimenTypeRoot =
            new AbstractReportTreeNode(StringUtil.EMPTY_STRING);
        specimenTypeTree.setLayoutData(treeGd);

        // Containers
        containerTab = new CTabItem(top, SWT.NONE);
        containerTab.setText(Container.NAME.plural().toString());
        Composite containerBody = new Composite(top, SWT.NONE);
        containerBody.setLayout(treeLayout);
        containerBody.setLayoutData(treeGd);
        containerTab.setControl(containerBody);
        containerTree = new ReportTreeWidget(containerBody);
        containerRoot = new AbstractReportTreeNode(StringUtil.EMPTY_STRING);
        containerTree.setLayoutData(treeGd);

        initializeNewReports(specimenRoot, clinicRoot, patientRoot,
            specimenTypeRoot, containerRoot);

        setInputs();

    }

    public void setInputs() {
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

    @SuppressWarnings("nls")
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
            throw new Exception(
                // exception message.
                i18n.tr("Unable to place report node: {0}", child.getLabel()));
        }
    }

    public void reload() {
        specimenTree.setInput(null);
        clinicTree.setInput(null);
        patientTree.setInput(null);
        specimenTypeTree.setInput(null);
        containerTree.setInput(null);
        setInputs();
    }

    @Override
    public void setFocus() {

    }

}
