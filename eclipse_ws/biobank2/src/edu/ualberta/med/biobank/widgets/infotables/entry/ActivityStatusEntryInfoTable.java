package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.dialogs.ActivityStatusDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.widgets.infotables.ActivityStatusInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current activity status collection and allows the user to add
 * additional activity status to the collection.
 */
public class ActivityStatusEntryInfoTable extends ActivityStatusInfoTable {

    private static BgcLogger logger = BgcLogger
        .getLogger(ActivityStatusEntryInfoTable.class.getName());

    List<ActivityStatusWrapper> selectedActivityStatus;

    private String addMessage;

    private String editMessage;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param SampleTypeCollection the activity status already selected and to
     *            be displayed in the table viewer (can be null).
     */
    public ActivityStatusEntryInfoTable(Composite parent,
        List<ActivityStatusWrapper> globalActivityStatus, String addMessage,
        String editMessage) {
        super(parent, null);
        setLists(globalActivityStatus);
        this.addMessage = addMessage;
        this.editMessage = editMessage;
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
        ActivityStatusWrapper activityStatus, String message) {
        ActivityStatusDialog dlg = new ActivityStatusDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            activityStatus, message);
        if (dlg.open() == Dialog.OK) {
            if (addEditOk(activityStatus)) {
                if (add) {
                    // only add to the collection when adding and not editing
                    selectedActivityStatus.add(activityStatus);
                }
                try {
                    activityStatus.persist();
                } catch (Exception e) {
                    BgcPlugin.openAsyncError("Save Failed", e);
                }
                reloadCollection(selectedActivityStatus);
                return true;
            } else {
                try {
                    activityStatus.reload();
                } catch (Exception e) {
                    BgcPlugin.openAsyncError("Refresh Failed", e);
                }
                reloadCollection(selectedActivityStatus);
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
                ActivityStatusWrapper type = getSelection();
                if (type != null) {
                    try {
                        if (!type.isNew() && type.isUsed()) {
                            BgcPlugin
                                .openError(
                                    "Activity Status Delete Error",
                                    "Cannot delete activity status \""
                                        + type.getName()
                                        + "\" since studies and/or patient visits are using it.");
                            return;
                        }

                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Activity Status",
                            "Are you sure you want to delete activity status \""
                                + type.getName() + "\"?")) {
                            return;
                        }

                        // equals method now compare toString() results if both
                        // ids are null.
                        selectedActivityStatus.remove(type);
                        type.delete();
                        setCollection(selectedActivityStatus);
                    } catch (final RemoteConnectFailureException exp) {
                        BgcPlugin
                            .openRemoteConnectErrorMessage(exp);
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
            for (ActivityStatusWrapper selectedAs : selectedActivityStatus)
                if (!selectedAs.getId().equals(type.getId())
                    && selectedAs.getName().equals(type.getName()))
                    throw new BiobankCheckException(
                        "That activity status has already been added.");
        } catch (BiobankException bce) {
            BgcPlugin.openAsyncError("Check error", bce);
            return false;
        }
        return true;
    }

    public void setLists(List<ActivityStatusWrapper> activityStatusCollection) {
        selectedActivityStatus = activityStatusCollection;
        reloadCollection(activityStatusCollection);
    }

    public void reload() {
        try {
            setLists(ActivityStatusWrapper
                .getAllActivityStatuses(SessionManager.getAppService()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("AppService unavailable", e);
        }
    }

    @SuppressWarnings("serial")
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
