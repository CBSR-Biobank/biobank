package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Collection;
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

    public ActivityStatusEntryInfoTable(Composite parent, String addMessage,
        String editMessage, SiteWrapper currentSite)
        throws ApplicationException {
        super(parent, null);
        this.addMessage = addMessage;
        this.editMessage = editMessage;
        this.currentSite = currentSite;
        this.localActivityStatuses = getActivityStatusesFromServer();
        addEditSupport();
        refresh();
    }

    private List<ActivityStatusWrapper> getActivityStatusesFromServer()
        throws ApplicationException {
        List<ActivityStatusWrapper> activityStatusesList;
        Collection<ActivityStatusWrapper> globalActivityStatusColl = ActivityStatusWrapper
            .getAllActivityStatuses(SessionManager.getAppService());
        if (globalActivityStatusColl != null)
            activityStatusesList = new ArrayList<ActivityStatusWrapper>(
                globalActivityStatusColl);
        else
            activityStatusesList = new ArrayList<ActivityStatusWrapper>();
        return activityStatusesList;
    }

    public void reset() throws ApplicationException {
        this.loadTableData(getActivityStatusesFromServer());
    }

    public void save() throws Exception {
        List<ActivityStatusWrapper> serverActivityStatuses = getActivityStatusesFromServer();

        for (ActivityStatusWrapper aws : localActivityStatuses) {
            if (!serverActivityStatuses.contains(aws))
                aws.persist();
        }
        for (ActivityStatusWrapper aws : serverActivityStatuses) {
            if (!localActivityStatuses.contains(aws))
                aws.delete();
        }
    }

    // if add is false then we are editing
    private boolean addOrEditActivityStatus(boolean add,
        ActivityStatusWrapper selectActivityStatus, String message) {

        ActivityStatusWrapper oldActivityStatusWrapper = selectActivityStatus;

        ActivityStatusDialog dlg = new ActivityStatusDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(), add,
            message,
            oldActivityStatusWrapper != null ? oldActivityStatusWrapper
                .getName() : null);

        if (dlg.open() == Dialog.OK) {
            try {
                ActivityStatusWrapper newActivityStatusWrapper = dlg
                    .getNewActivityStatus();

                if (newActivityStatusWrapper == null)
                    return false;

                if (add) {
                    addActivityStatus(newActivityStatusWrapper);
                } else {
                    // FIXME editing activity statuses do not update the
                    // infotable correctly
                    removeActivityStatus(oldActivityStatusWrapper);
                    addActivityStatus(newActivityStatusWrapper);
                }
                refresh();
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
        addOrEditActivityStatus(true, null, addMessage);
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
                ActivityStatusWrapper ActivityStatus = getSelection();
                if (ActivityStatus != null) {
                    try {
                        if (!ActivityStatus.isNew()
                            && ActivityStatus.isActive()) {
                            BioBankPlugin
                                .openError(
                                    "Activity Status Delete Error",
                                    "Cannot delete activity status \""
                                        + ActivityStatus.getName()
                                        + "\" since created activity statuses are using it.");
                            return;
                        }

                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Activity Status",
                            "Are you sure you want to delete activity status\""
                                + ActivityStatus.getName() + "\"?")) {
                            return;
                        }
                        removeActivityStatus(ActivityStatus);

                        notifyListeners();
                    } catch (final RemoteConnectFailureException exp) {
                        BioBankPlugin.openRemoteConnectErrorMessage();
                    } catch (Exception e) {
                        logger.error("BioBankFormBase.createPartControl Error",
                            e);
                    }
                }
            }
        });
    }

    public void refresh() {
        loadTableData(this.localActivityStatuses);
    }

    private void removeActivityStatus(ActivityStatusWrapper asw) {
        for (ActivityStatusWrapper i : this.localActivityStatuses) {
            if (i.getName().equals(asw.getName())) {
                this.localActivityStatuses.remove(asw);
                break;
            }
        }
        this.refresh();
    }

    private void addActivityStatus(ActivityStatusWrapper asw)
        throws BiobankCheckException {
        for (ActivityStatusWrapper i : this.localActivityStatuses) {
            if (i.getName().equals(asw.getName())) {
                throw new BiobankCheckException(
                    "Activity status already added.");
            }
        }
        this.localActivityStatuses.add(asw);
        this.refresh();
    }

    private void loadTableData(
        List<ActivityStatusWrapper> activityStatusCollection) {

        reloadCollection(activityStatusCollection);
        this.localActivityStatuses = activityStatusCollection;

        this.update();
        this.redraw();
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
