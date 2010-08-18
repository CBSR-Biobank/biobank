package edu.ualberta.med.biobank.helpers;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
import edu.ualberta.med.biobank.client.config.calgary.CalgaryClinics;
import edu.ualberta.med.biobank.client.config.calgary.CalgaryContainerTypes;
import edu.ualberta.med.biobank.client.config.calgary.CalgarySite;
import edu.ualberta.med.biobank.client.config.calgary.CalgaryStudies;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Accessed via the "**Debug**" main menu item. Invoked by the
 * CalgaryConfigurationHandler to populate the database with Calgary
 * configuration and sample objects.
 */
public class CalgaryConfigJob {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(CalgaryConfigJob.class.getName());

    protected WritableApplicationService appService;

    protected SiteWrapper calgarySite;

    protected CalgaryClinics configClinics;

    protected Random r = new Random();

    protected List<SampleTypeWrapper> sampleTypesList;

    protected List<ShippingMethodWrapper> shippingCompaniesList;

    // methods that add objects to database, plus a message to display in job
    // dialog
    protected static Set<String> defaultSubTasks;
    static {
        Set<String> aSet = new LinkedHashSet<String>();

        // insert methods are listed here and order is important
        aSet.add("Removing previous Calgary configuration");
        aSet.add("Adding sites");
        aSet.add("Adding clinics");
        aSet.add("Adding studies");
        aSet.add("Adding container types");
        defaultSubTasks = Collections.unmodifiableSet(aSet);
    };

    public CalgaryConfigJob() {
        this(defaultSubTasks);
    }

    /*
     * Sub classes can invoke this method with their now methodMap.
     */
    protected CalgaryConfigJob(final Set<String> subTasks) {
        appService = SessionManager.getInstance().getSession().getAppService();

        try {
            sampleTypesList = SampleTypeWrapper.getAllSampleTypes(
                appService, false);
            shippingCompaniesList = ShippingMethodWrapper
                .getShippingMethods(appService);
        } catch (Exception e) {
            BioBankPlugin.openError("Init Examples",
                "Error encounted when adding init examples");
            return;
        }

        Job job = new Job("Calgary Configuration") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask("Adding new objects to database...",
                        subTasks.size());

                    int subTaskCount = 0;
                    for (String stepName : subTasks) {
                        monitor.subTask(stepName + "...");
                        performSubTask(subTaskCount);
                        monitor.worked(1);

                        if (monitor.isCanceled()) {
                            throw new OperationCanceledException();
                        }

                        ++subTaskCount;
                    }
                } catch (Exception e) {
                    logger.error("initialization error", e);
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
                    @Override
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
                            logger.error("Init Examples error", e);
                        }
                    }
                });
            }
        });
        job.setUser(true);
        job.schedule();
    }

    protected void performSubTask(int subTaskNumber) throws Exception {
        switch (subTaskNumber) {
        case 0:
            CalgarySite.deleteConfiguration(appService);
            break;
        case 1:
            calgarySite = CalgarySite.addSite(appService);
            break;
        case 2:
            configClinics = new CalgaryClinics(calgarySite);
            break;
        case 3:
            new CalgaryStudies(calgarySite);
            break;
        case 4:
            new CalgaryContainerTypes(calgarySite);
            break;
        default:
            throw new Exception("sub task number " + subTaskNumber
                + " is invalid");
        }
    }
}
