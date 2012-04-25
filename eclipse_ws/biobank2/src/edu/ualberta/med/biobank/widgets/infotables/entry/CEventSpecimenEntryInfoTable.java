package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventUpdatePermission;
import edu.ualberta.med.biobank.dialogs.CEventSourceSpecimenDialog;
import edu.ualberta.med.biobank.dialogs.PagedDialog.NewListener;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CEventSpecimenEntryInfoTable extends NewSpecimenEntryInfoTable {
    public static final I18n i18n = I18nFactory
        .getI18n(AliquotedSpecimenEntryInfoTable.class);

    protected IObservableValue specimensAdded = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private boolean isEditable;
    private boolean isDeletable;

    @SuppressWarnings("nls")
    public CEventSpecimenEntryInfoTable(Composite parent,
        List<SpecimenInfo> sourceSpecimens, CollectionEvent cevent,
        ColumnsShown columnsShowns) {
        super(parent, sourceSpecimens, columnsShowns);
        try {
            if (cevent.getId() != null) {
                this.isEditable =
                    SessionManager.getAppService().isAllowed(
                        new CollectionEventUpdatePermission(cevent.getId()));
                this.isDeletable =
                    SessionManager.getAppService().isAllowed(
                        new CollectionEventUpdatePermission(cevent.getId()));
            } else {
                // not real deletes or edits
                this.isEditable = true;
                this.isDeletable = true;
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                i18n.tr("Permission Error"),
                i18n.tr("Unable to retrieve user permissions"));
        }
    }

    @Override
    public void reload(List<SpecimenInfo> specimens) {
        super.reload(specimens);
        specimensAdded.setValue(currentSpecimens.size() > 0);
    }

    public void addOrEditSpecimen(boolean add, CommentedSpecimenInfo si,
        Set<SourceSpecimen> studySourceTypes,
        List<SpecimenType> allSpecimenTypes, final CollectionEvent cEvent,
        final Date defaultTimeDrawn) {
        NewListener newListener = null;
        List<String> inventoryIdExcludeList = new ArrayList<String>();
        for (SpecimenInfo sp : currentSpecimens) {
            inventoryIdExcludeList.add(sp.specimen.getInventoryId());
        }
        if (add) {
            newListener = new NewListener() {
                @Override
                public void newAdded(Object mw) {
                    CommentedSpecimenInfo spec = (CommentedSpecimenInfo) mw;
                    spec.specimen.setCollectionEvent(cEvent);
                    spec.specimen.setOriginalCollectionEvent(cEvent);
                    spec.specimen.setCurrentCenter(SessionManager.getUser()
                        .getCurrentWorkingCenter().getWrappedObject());
                    currentSpecimens.add(spec);
                    specimensAdded.setValue(true);
                    reloadCollection(currentSpecimens);
                    notifyListeners();
                }
            };
        } else {
            inventoryIdExcludeList.remove(si.specimen.getInventoryId());
        }
        CEventSourceSpecimenDialog dlg =
            new CEventSourceSpecimenDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), si == null ? null
                : si, studySourceTypes,
                allSpecimenTypes,
                inventoryIdExcludeList, newListener, defaultTimeDrawn);
        int res = dlg.open();
        if (!add && res == Dialog.OK) {

            reloadCollection(currentSpecimens);
            notifyListeners();
        }
    }

    public void addEditSupport(final Set<SourceSpecimen> studySourceTypes,
        final List<SpecimenType> allSpecimenTypes) {
        if (isEditable) {
            addEditItemListener(new IInfoTableEditItemListener<SpecimenInfo>() {
                @Override
                public void editItem(InfoTableEvent<SpecimenInfo> event) {
                    CommentedSpecimenInfo si =
                        (CommentedSpecimenInfo) getSelection();
                    if (si != null)
                        addOrEditSpecimen(false, si, studySourceTypes,
                            allSpecimenTypes, null, null);
                }
            });
        }
        if (isDeletable) {
            addDeleteItemListener(new IInfoTableDeleteItemListener<SpecimenInfo>() {
                @SuppressWarnings("nls")
                @Override
                public void deleteItem(InfoTableEvent<SpecimenInfo> event) {
                    SpecimenInfo si = getSelection();
                    if (si != null) {
                        if (!MessageDialog
                            .openConfirm(
                                PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getShell(),
                                // dialog title.
                                i18n.tr("Delete specimen"),
                                // dialog message.
                                i18n.tr(
                                    "Are you sure you want to delete specimen \"{0}\"?",
                                    si.specimen.getInventoryId()))) {
                            return;
                        }
                        currentSpecimens.remove(si);
                        setList(currentSpecimens);
                        if (currentSpecimens.size() == 0) {
                            specimensAdded.setValue(false);
                        }
                        notifyListeners();
                    }
                }
            });
        }
    }
}
