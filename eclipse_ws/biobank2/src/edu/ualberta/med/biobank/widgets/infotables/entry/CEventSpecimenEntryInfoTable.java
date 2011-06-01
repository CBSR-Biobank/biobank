package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.dialogs.CEventSourceSpecimenDialog;
import edu.ualberta.med.biobank.dialogs.PagedDialog.NewListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;

public class CEventSpecimenEntryInfoTable extends SpecimenEntryInfoTable {

    protected IObservableValue specimensAdded = new WritableValue(
        Boolean.FALSE, Boolean.class);

    public CEventSpecimenEntryInfoTable(Composite parent,
        List<SpecimenWrapper> specs, ColumnsShown columnsShowns) {
        super(parent, specs, columnsShowns);
    }

    @Override
    public void reload(List<SpecimenWrapper> specimens) {
        super.reload(specimens);
        specimensAdded.setValue(currentSpecimens.size() > 0);
    }

    public void addOrEditSpecimen(boolean add, SpecimenWrapper specimen,
        List<SourceSpecimenWrapper> studySourceTypes,
        List<SpecimenTypeWrapper> allSpecimenTypes,
        final CollectionEventWrapper cEvent, final Date defaultTimeDrawn) {
        NewListener newListener = null;
        List<SpecimenWrapper> excludeList = new ArrayList<SpecimenWrapper>(
            currentSpecimens);
        if (add) {
            newListener = new NewListener() {
                @Override
                public void newAdded(ModelWrapper<?> spec) {
                    ((SpecimenBaseWrapper) spec).setCollectionEvent(cEvent);
                    ((SpecimenBaseWrapper) spec)
                        .setOriginalCollectionEvent(cEvent);
                    ((SpecimenBaseWrapper) spec)
                        .setCurrentCenter(SessionManager.getUser()
                            .getCurrentWorkingCenter());
                    currentSpecimens.add((SpecimenWrapper) spec);
                    addedSpecimens.add((SpecimenWrapper) spec);
                    specimensAdded.setValue(true);
                    reloadCollection(currentSpecimens);
                    notifyListeners();
                }
            };
        } else {
            excludeList.remove(specimen);
        }
        CEventSourceSpecimenDialog dlg = new CEventSourceSpecimenDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            specimen, studySourceTypes, allSpecimenTypes, excludeList,
            newListener, defaultTimeDrawn);
        int res = dlg.open();
        if (!add && res == Dialog.OK) {
            reloadCollection(currentSpecimens);
            notifyListeners();
        }
    }

    public void addEditSupport(
        final List<SourceSpecimenWrapper> studySourceTypes,
        final List<SpecimenTypeWrapper> allSpecimenTypes) {
        if (SessionManager.canCreate(SpecimenWrapper.class)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addOrEditSpecimen(true, null, studySourceTypes,
                        allSpecimenTypes, null, null);
                }
            });
        }
        if (SessionManager.canUpdate(SpecimenWrapper.class)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    SpecimenWrapper sw = getSelection();
                    if (sw != null)
                        addOrEditSpecimen(false, sw, studySourceTypes,
                            allSpecimenTypes, null, null);
                }
            });
        }
        if (SessionManager.canDelete(SpecimenWrapper.class)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    SpecimenWrapper sw = getSelection();
                    if (sw != null) {
                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), Messages
                            .getString("SpecimenEntryInfoTable.delete.title"),
                            Messages.getString(
                                "SpecimenEntryInfoTable.delete.question",
                                sw.getInventoryId()))) {
                            return;
                        }

                        currentSpecimens.remove(sw);
                        setCollection(currentSpecimens);
                        if (currentSpecimens.size() == 0) {
                            specimensAdded.setValue(false);
                        }
                        addedSpecimens.remove(sw);
                        removedSpecimens.add(sw);
                        notifyListeners();
                    }
                }
            });
        }
    }

}
