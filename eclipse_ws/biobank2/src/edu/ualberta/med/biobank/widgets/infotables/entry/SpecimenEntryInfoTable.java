package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
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
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.SpecimenDialog;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;

public class SpecimenEntryInfoTable extends SpecimenInfoTable {

    private List<SpecimenWrapper> addedSpecimens = new ArrayList<SpecimenWrapper>();
    private List<SpecimenWrapper> removedSpecimens = new ArrayList<SpecimenWrapper>();

    private List<SpecimenWrapper> currentSpecimens;

    private IObservableValue specimensAdded = new WritableValue(Boolean.FALSE,
        Boolean.class);

    public SpecimenEntryInfoTable(Composite parent,
        List<SpecimenWrapper> specs, ColumnsShown columnsShowns) {
        super(parent, specs, columnsShowns, 10);
        currentSpecimens = new ArrayList<SpecimenWrapper>(specs);
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((SpecimenWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((SpecimenWrapper) e2);
                    return super.compare(i1.inventoryId, i2.inventoryId);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

    /**
     * called from the dialog to add new specimen into the table
     */
    public void addSpecimen(SpecimenWrapper specimen) {
        currentSpecimens.add(specimen);
        addedSpecimens.add(specimen);
        specimensAdded.setValue(true);
        reloadCollection(currentSpecimens);
        notifyListeners();
    }

    public void addOrEditSpecimen(boolean add, SpecimenWrapper specimen,
        List<SourceSpecimenWrapper> studySourceTypes,
        List<SpecimenTypeWrapper> allSpecimenTypes,
        CollectionEventWrapper cEvent) {
        SpecimenDialog dlg = new SpecimenDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), specimen, studySourceTypes,
            allSpecimenTypes, this);
        if (add) {
            dlg.setPatientVisit(cEvent);
        }
        int res = dlg.open();
        if (!add && res == Dialog.OK) {
            reloadCollection(currentSpecimens);
            notifyListeners();
        }
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(SpecimenWrapper.class, null)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    SpecimenWrapper sw = getSelection();
                    addOrEditSpecimen(true, sw, null, null, null);
                }
            });
        }
        if (SessionManager.canUpdate(SpecimenWrapper.class, null)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    SpecimenWrapper sw = getSelection();
                    if (sw != null)
                        // FIXME
                        addOrEditSpecimen(false, sw, null, null, null);
                }
            });
        }
        if (SessionManager.canDelete(SpecimenWrapper.class, null)) {
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

    public void reload(List<SpecimenWrapper> specimens) {
        currentSpecimens = specimens;
        if (currentSpecimens == null) {
            currentSpecimens = new ArrayList<SpecimenWrapper>();
        }
        reloadCollection(currentSpecimens);
        addedSpecimens = new ArrayList<SpecimenWrapper>();
        removedSpecimens = new ArrayList<SpecimenWrapper>();

        specimensAdded.setValue(currentSpecimens.size() > 0);
    }

    public List<SpecimenWrapper> getAddedPvSourceVessels() {
        return addedSpecimens;
    }

    public List<SpecimenWrapper> getRemovedPvSourceVessels() {
        return removedSpecimens;
    }

}
