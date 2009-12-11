package edu.ualberta.med.biobank.helpers;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressConstants;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.cbsr.CbsrClinics;
import edu.ualberta.med.biobank.common.cbsr.CbsrContainerTypes;
import edu.ualberta.med.biobank.common.cbsr.CbsrContainers;
import edu.ualberta.med.biobank.common.cbsr.CbsrSite;
import edu.ualberta.med.biobank.common.cbsr.CbsrStudies;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Accessed via the "**Debug**" main menu item. Invoked by the
 * CbsrConfigurationHandler to populate the database with CBSR configuration and
 * sample objects.
 */
public class CbsrConfigJob {

    private static Logger LOGGER = Logger.getLogger(CbsrConfigJob.class
        .getName());

    protected WritableApplicationService appService;

    protected SiteWrapper cbsrSite;

    protected Random r = new Random();

    protected List<SampleTypeWrapper> sampleTypesList;

    protected List<ShippingCompanyWrapper> shippingCompaniesList;

    // methods that add objects to database, plus a message to display in job
    // dialog
    protected static Map<String, String> addMethodMap;
    static {
        Map<String, String> aMap = new LinkedHashMap<String, String>();

        // insert methods are listed here and order is important
        aMap.put("addSite", "Adding sites");
        aMap.put("addClinicsToSite", "Adding clinics");
        aMap.put("addStudiesToSite", "Adding studies");
        aMap.put("addContainerTypesInSite", "Adding container types");
        aMap.put("addContainers", "Adding containers");
        addMethodMap = Collections.unmodifiableMap(aMap);
    };

    public CbsrConfigJob() {
        this(addMethodMap);
    }

    /*
     * Sub classes can invoke this method with their now methodMap.
     */
    protected CbsrConfigJob(final Map<String, String> methodMap) {
        appService = SessionManager.getInstance().getSession().getAppService();

        try {
            sampleTypesList = SampleTypeWrapper.getGlobalSampleTypes(
                appService, false);
            shippingCompaniesList = ShippingCompanyWrapper
                .getShippingCompanies(appService);
        } catch (Exception e) {
            BioBankPlugin.openError("Init Examples",
                "Error encounted when adding init examples");
            return;
        }

        Job job = new Job("CBSR Configuration") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    int taskNber = methodMap.size() + 1;

                    monitor.beginTask("Adding new objects to database...",
                        taskNber);

                    monitor.subTask("Removing previous CBSR configuration...");
                    deleteConfiguration();
                    monitor.worked(1);

                    for (String methodName : methodMap.keySet()) {
                        monitor.subTask(methodMap.get(methodName) + "...");
                        Method method = CbsrConfigJob.this.getClass()
                            .getDeclaredMethod(methodName, new Class<?>[] {});
                        method.setAccessible(true);
                        method.invoke(CbsrConfigJob.this.getClass(),
                            new Object[] {});
                        monitor.worked(1);
                        method.setAccessible(false);
                        if (monitor.isCanceled())
                            throw new OperationCanceledException();
                    }
                } catch (Exception e) {
                    LOGGER.error("initialization error", e);
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }

                return Status.OK_STATUS;
            }
        };

        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(final IJobChangeEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        try {
                            SessionManager.getInstance().updateSites();
                            SessionManager.getInstance().getSession().rebuild();
                            SessionManager.getInstance().getSession()
                                .performExpand();
                            if (event.getResult().isOK()) {
                                if ((Boolean) event.getJob().getProperty(
                                    IProgressConstants.PROPERTY_IN_DIALOG))
                                    return;
                                BioBankPlugin.openMessage("Init Examples",
                                    "successfully added all init examples");
                            } else
                                BioBankPlugin
                                    .openError("Init Examples",
                                        "Error encounted when adding init examples");
                        } catch (Exception e) {
                            LOGGER.error("Init Examples error", e);
                        }
                    }
                });
            }
        });
        job.setUser(true);
        job.schedule();
    }

    protected void addSite() throws Exception {
        cbsrSite = CbsrSite.addSite(appService);
    }

    protected void addClinicsToSite() throws Exception {
        CbsrClinics.createClinics(cbsrSite);
    }

    protected void addStudiesToSite() throws Exception {
        CbsrStudies.createStudies(cbsrSite);
    }

    protected void addContainerTypesInSite() throws Exception {
        CbsrContainerTypes.createContainerTypes(cbsrSite);
    }

    protected void addContainers() throws Exception {
        CbsrContainers.createContainers(cbsrSite);
    }

    protected void deleteConfiguration() throws Exception {
        CbsrSite.deleteConfiguration(appService);
    }
}
