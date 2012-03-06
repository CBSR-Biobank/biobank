package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

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
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CEventSpecimenEntryInfoTable extends NewSpecimenEntryInfoTable {

    protected IObservableValue specimensAdded = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private boolean isEditable;
    private boolean isDeletable;

    public CEventSpecimenEntryInfoTable(Composite parent,
        List<SpecimenInfo> specs, CollectionEvent cevent,
        ColumnsShown columnsShowns) {
        super(parent, specs, columnsShowns);
        try {
            if (cevent.getId() != null) {
                this.isEditable =
                    SessionManager.getAppService().isAllowed(
                        new CollectionEventUpdatePermission(cevent.getId()));
                this.isDeletable =
                    SessionManager.getAppService().isAllowed(
                        new CollectionEventUpdatePermission(cevent.getId()));
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Permission Error",
                "Unable to retrieve user permissions");
        }
    }

    @Override
    public void reload(List<SpecimenInfo> specimens) {
        super.reload(specimens);
        specimensAdded.setValue(currentSpecimens.size() > 0);
    }

    public void addOrEditSpecimen(boolean add, SpecimenInfo si,
        List<SourceSpecimen> studySourceTypes,
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
                    Specimen spec = (Specimen) mw;
                    spec.setCollectionEvent(cEvent);
                    spec.setOriginalCollectionEvent(cEvent);
                    spec.setCurrentCenter(SessionManager.getUser()
                        .getCurrentWorkingCenter().getWrappedObject());
                    SpecimenInfo info = new SpecimenInfo();
                    info.specimen = spec;
                    currentSpecimens.add(info);
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
                : si.specimen, studySourceTypes, allSpecimenTypes,
                inventoryIdExcludeList, newListener, defaultTimeDrawn);
        int res = dlg.open();
        if (!add && res == Dialog.OK) {
            reloadCollection(currentSpecimens);
            notifyListeners();
        }
    }

    public void addEditSupport(final List<SourceSpecimen> studySourceTypes,
        final List<SpecimenType> allSpecimenTypes) {
        if (isEditable) {
            addEditItemListener(new IInfoTableEditItemListener<SpecimenInfo>() {
                @Override
                public void editItem(InfoTableEvent<SpecimenInfo> event) {
                    SpecimenInfo si = getSelection();
                    if (si != null)
                        addOrEditSpecimen(false, si, studySourceTypes,
                            allSpecimenTypes, null, null);
                }
            });
        }
        if (isDeletable) {
            addDeleteItemListener(new IInfoTableDeleteItemListener<SpecimenInfo>() {
                @Override
                public void deleteItem(InfoTableEvent<SpecimenInfo> event) {
                    SpecimenInfo si = getSelection();
                    if (si != null) {
                        if (!MessageDialog
                            .openConfirm(
                                PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getShell(),
                                Messages.SpecimenEntryInfoTable_delete_title,
                                NLS.bind(
                                    Messages.SpecimenEntryInfoTable_delete_question,
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
