package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.ActivityStatusDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.ActivityStatusInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class ActivityStatusEntryInfoTable extends ActivityStatusInfoTable {

    private List<ActivityStatusWrapper> localActivityStatuses = new ArrayList<ActivityStatusWrapper>();

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ActivityStatusEntryInfoTable.class.getName());

    private String addMessage;
    private String editMessage;
    private SiteWrapper currentSite;

    List<ActivityStatusWrapper> addedOrModifiedStatuses = new ArrayList<ActivityStatusWrapper>();
    List<ActivityStatusWrapper> deletedStatuses = new ArrayList<ActivityStatusWrapper>();

    public ActivityStatusEntryInfoTable(Composite parent, String addMessage,
        String editMessage, SiteWrapper currentSite)
        throws ApplicationException {
        super(parent, null);
        this.addMessage = addMessage;
        this.editMessage = editMessage;
        this.currentSite = currentSite;
        this.localActivityStatuses = getActivityStatusesFromServer();
        addEditSupport();
        setCollection(localActivityStatuses);
    }

    private List<ActivityStatusWrapper> getActivityStatusesFromServer()
        throws ApplicationException {
        List<ActivityStatusWrapper> activityStatusesList = ActivityStatusWrapper
            .getAllActivityStatuses(SessionManager.getAppService());
        if (activityStatusesList == null) {
            activityStatusesList = new ArrayList<ActivityStatusWrapper>();
        }
        return activityStatusesList;
    }

    public void reset() throws ApplicationException {
        localActivityStatuses = getActivityStatusesFromServer();
        reloadCollection(localActivityStatuses);
    }

    public void save() throws Exception {
        ActivityStatusWrapper.persistActivityStatuses(addedOrModifiedStatuses,
            deletedStatuses);
    }

    // if add is false then we are editing
    private boolean addOrEditActivityStatus(boolean add,
        ActivityStatusWrapper selectActivityStatus, String message) {

        ActivityStatusDialog dlg = new ActivityStatusDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            selectActivityStatus, message);

        if (dlg.open() == Dialog.OK) {
            try {
                ActivityStatusWrapper statusCopy = dlg.getActivityStatusCopy();
                String newName = statusCopy.getName();
                for (ActivityStatusWrapper activity : localActivityStatuses) {
                    if (activity.getName().equals(newName)) {
                        throw new BiobankCheckException(
                            "Activity status with name " + newName
                                + " already exists.");
                    }
                }
                selectActivityStatus.setName(statusCopy.getName());
                if (add) {
                    localActivityStatuses.add(selectActivityStatus);
                }
                addedOrModifiedStatuses.add(selectActivityStatus);
                reloadCollection(localActivityStatuses);
                notifyListeners();
                return true;

            } catch (BiobankCheckException e) {
                BioBankPlugin.openAsyncError("Error: addOrEditActivityStatus",
                    e);
            }
        }
        return false;
    }

    public void addActivityStatusExternal() {
        addOrEditActivityStatus(true,
            new ActivityStatusWrapper(SessionManager.getAppService()),
            addMessage);
    }

    private void addEditSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                addActivityStatusExternal();
            }
        });

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                ActivityStatusWrapper selected = getSelection();
                if (selected != null)
                    addOrEditActivityStatus(false, selected, editMessage);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                ActivityStatusWrapper activityStatus = getSelection();
                if (activityStatus != null) {
                    try {
                        if (!activityStatus.isNew()
                            && activityStatus.isActive()) {
                            BioBankPlugin
                                .openError(
                                    "Activity Status Delete Error",
                                    "Cannot delete activity status \""
                                        + activityStatus.getName()
                                        + "\" since created activity statuses are using it.");
                            return;
                        }

                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Activity Status",
                            "Are you sure you want to delete activity status\""
                                + activityStatus.getName() + "\"?")) {
                            return;
                        }
                        localActivityStatuses.remove(activityStatus);
                        deletedStatuses.add(activityStatus);
                        reloadCollection(localActivityStatuses);
                        notifyListeners();
                    } catch (final RemoteConnectFailureException exp) {
                        BioBankPlugin.openRemoteConnectErrorMessage(exp);
                    } catch (Exception e) {
                        logger.error("BioBankFormBase.createPartControl Error",
                            e);
                    }
                }
            }
        });
    }

    public SiteWrapper getCurrentSite() {
        return currentSite;
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
