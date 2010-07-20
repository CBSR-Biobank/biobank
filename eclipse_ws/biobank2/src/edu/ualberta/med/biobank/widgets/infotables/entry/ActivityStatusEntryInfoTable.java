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
import edu.ualberta.med.biobank.widgets.infotables.ActivityStatusMethodInfoTable;
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
public class ActivityStatusEntryInfoTable extends ActivityStatusMethodInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ActivityStatusEntryInfoTable.class.getName());

    private List<ActivityStatusWrapper> selectedActivityStatuss;

    private List<ActivityStatusWrapper> addedOrModifiedActivityStatuss;

    private List<ActivityStatusWrapper> deletedActivityStatuss;

    private String addMessage;

    private String editMessage;

    private SiteWrapper currentSite;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param SampleTypeCollection the sample storage already selected and to be
     *            displayed in the table viewer (can be null).
     */
    public ActivityStatusEntryInfoTable(Composite parent,
        List<ActivityStatusWrapper> globalActivityStatuss, String addMessage,
        String editMessage, SiteWrapper currentSite) {
        super(parent, null);
        setLists(globalActivityStatuss);
        this.addMessage = addMessage;
        this.editMessage = editMessage;
        this.currentSite = currentSite;
        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    /**
     * 
     * @param message The message to display in the SampleTypeDialog.
     */
    public void addActivityStatus() {
        ActivityStatusWrapper newST = new ActivityStatusWrapper(
            SessionManager.getAppService());
        addOrEditActivityStatus(true, newST, addMessage);
    }

    private boolean addOrEditActivityStatus(boolean add,
        ActivityStatusWrapper ActivityStatus, String message) {

        ActivityStatusDialog dlg = new ActivityStatusDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            ActivityStatus, message);

        if (dlg.open() == Dialog.OK) {
            if (addEditOk(ActivityStatus)) {
                if (add) {
                    // only add to the collection when adding and not editing
                    selectedActivityStatuss.add(ActivityStatus);
                }
                reloadCollection(selectedActivityStatuss);
                addedOrModifiedActivityStatuss.add(ActivityStatus);
                notifyListeners();
                return true;
            } else {
                ActivityStatusWrapper orig = dlg.getOrigActivityStatus();
                ActivityStatus.setName(orig.getName());
                reloadCollection(selectedActivityStatuss);
            }
        }
        return false;
    }

    private void addEditSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                addActivityStatus();
            }
        });

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                ActivityStatusWrapper type = getSelection();
                if (type != null)
                    addOrEditActivityStatus(false, type, editMessage);
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
                            .getShell(), "Delete Activity Status Method",
                            "Are you sure you want to delete activity status\""
                                + ActivityStatus.getName() + "\"?")) {
                            return;
                        }

                        // equals method now compare toString() results if both
                        // ids are null.
                        selectedActivityStatuss.remove(ActivityStatus);

                        setCollection(selectedActivityStatuss);
                        deletedActivityStatuss.add(ActivityStatus);
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

    private boolean addEditOk(ActivityStatusWrapper type) {
        try {
            for (ActivityStatusWrapper sm : selectedActivityStatuss)
                if ((sm.getId() != type.getId() || type.isNew())
                    && sm.getName().equals(type.getName()))
                    throw new BiobankCheckException(
                        "That shipping method has already been added.");
            for (ActivityStatusWrapper sm : addedOrModifiedActivityStatuss)
                if ((sm.getId() != type.getId() || type.isNew())
                    && sm.getName().equals(type.getName()))
                    throw new BiobankCheckException(
                        "That shipping method has already been added.");
            type.checkUnique();
        } catch (BiobankCheckException bce) {
            BioBankPlugin.openAsyncError("Check error", bce);
            return false;
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Check error", e);
            return false;
        }
        return true;
    }

    public List<ActivityStatusWrapper> getAddedOrModifiedActivityStatuss() {
        return addedOrModifiedActivityStatuss;
    }

    public List<ActivityStatusWrapper> getDeletedActivityStatuss() {
        return deletedActivityStatuss;
    }

    public void setLists(List<ActivityStatusWrapper> ActivityStatusCollection) {
        if (ActivityStatusCollection == null) {
            selectedActivityStatuss = new ArrayList<ActivityStatusWrapper>();
        } else {
            selectedActivityStatuss = new ArrayList<ActivityStatusWrapper>(
                ActivityStatusCollection);
        }
        reloadCollection(ActivityStatusCollection);
        addedOrModifiedActivityStatuss = new ArrayList<ActivityStatusWrapper>();
        deletedActivityStatuss = new ArrayList<ActivityStatusWrapper>();
    }

    public SiteWrapper getCurrentSite() {
        return currentSite;
    }

    public void reload() {
        try {
            setLists(new ArrayList<ActivityStatusWrapper>(
                ActivityStatusWrapper.getAllActivityStatuses(SessionManager
                    .getAppService())));
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("AppService unavailable", e);
        }
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                return super.compare(((ActivityStatusWrapper) e1).getName(),
                    ((ActivityStatusWrapper) e2).getName());
            }
        };
    }
}
