package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.dialogs.PagedDialog.NewListener;
import edu.ualberta.med.biobank.dialogs.StudyAliquotedSpecimenDialog;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.AliquotedSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;

/**
 * Displays the current aliquoted specimen collection and allows the user to add
 * additional aliquoted specimen to the collection.
 */
public class AliquotedSpecimenEntryInfoTable extends AliquotedSpecimenInfoTable {

    private List<AliquotedSpecimenWrapper> selectedAliquotedSpecimen;

    private List<AliquotedSpecimenWrapper> addedOrModifiedAliquotedSpecimens;

    private List<AliquotedSpecimenWrapper> deletedAliquotedSpecimens;

    private boolean isDeletable;

    private boolean isEditable;

    private Set<SpecimenTypeWrapper> availableSpecimenTypes =
        new HashSet<SpecimenTypeWrapper>();

    private StudyAliquotedSpecimenDialog dlg;

    public AliquotedSpecimenEntryInfoTable(Composite parent,
        List<AliquotedSpecimenWrapper> aliquotedSpecimens, boolean isEditable,
        boolean isDeletable) {
        super(parent, null);
        selectedAliquotedSpecimen = aliquotedSpecimens;
        if (selectedAliquotedSpecimen == null) {
            selectedAliquotedSpecimen =
                new ArrayList<AliquotedSpecimenWrapper>();
        }
        setList(selectedAliquotedSpecimen);
        addedOrModifiedAliquotedSpecimens =
            new ArrayList<AliquotedSpecimenWrapper>();
        deletedAliquotedSpecimens = new ArrayList<AliquotedSpecimenWrapper>();

        this.isEditable = isEditable;
        this.isDeletable = isDeletable;

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addAliquotedSpecimen() {
        AliquotedSpecimenWrapper asw = new AliquotedSpecimenWrapper(
            SessionManager.getAppService());
        // DO NOT set the study on asw - if it was done then it would have to be
        // unset if the user presses the Cancel button
        addOrEditAliquotedSpecimen(true, asw);
    }

    private void addOrEditAliquotedSpecimen(boolean add,
        final AliquotedSpecimenWrapper aliquotedSpecimen) {
        List<SpecimenTypeWrapper> dialogSpecimenTypes;
        if (!add) {
            dialogSpecimenTypes =
                Arrays.asList(aliquotedSpecimen.getSpecimenType());
        } else
            dialogSpecimenTypes =
                new ArrayList<SpecimenTypeWrapper>(getAvailableSpecimenTypes());
        NewListener newListener = null;
        if (add) {
            // only add to the collection when adding and not editing
            newListener = new NewListener() {
                @Override
                public void newAdded(Object spec) {
                    AliquotedSpecimenWrapper added =
                        ((AliquotedSpecimenWrapper) spec);
                    availableSpecimenTypes.remove(added.getSpecimenType());
                    selectedAliquotedSpecimen.add(added);
                    dlg.setSpecimenTypes(availableSpecimenTypes);
                    addedOrModifiedAliquotedSpecimens.add(added);
                    reloadCollection(selectedAliquotedSpecimen);
                    notifyListeners();
                }
            };
        }
        dlg = new StudyAliquotedSpecimenDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            aliquotedSpecimen, newListener, dialogSpecimenTypes);

        int res = dlg.open();
        if (!add && res == Dialog.OK) {
            reloadCollection(selectedAliquotedSpecimen);
            notifyListeners();
        }
    }

    private Collection<SpecimenTypeWrapper> getAvailableSpecimenTypes() {
        return availableSpecimenTypes;
    }

    public void setAvailableSpecimenTypes(List<SourceSpecimenWrapper> types) {
        availableSpecimenTypes = new HashSet<SpecimenTypeWrapper>();
        for (SourceSpecimenWrapper ssw : types) {
            availableSpecimenTypes.addAll(ssw.getSpecimenType()
                .getChildSpecimenTypeCollection(false));
        }
        for (AliquotedSpecimenWrapper ss : selectedAliquotedSpecimen)
            availableSpecimenTypes.remove(ss.getSpecimenType());
    }

    private void addEditSupport() {
        if (isEditable) {
            addAddItemListener(new IInfoTableAddItemListener<AliquotedSpecimenWrapper>() {
                @Override
                public void addItem(
                    InfoTableEvent<AliquotedSpecimenWrapper> event) {
                    addAliquotedSpecimen();
                }
            });
        }
        if (isEditable) {
            addEditItemListener(new IInfoTableEditItemListener<AliquotedSpecimenWrapper>() {
                @Override
                public void editItem(
                    InfoTableEvent<AliquotedSpecimenWrapper> event) {
                    AliquotedSpecimenWrapper aliquotedSpecimen = getSelection();
                    if (aliquotedSpecimen != null)
                        addOrEditAliquotedSpecimen(false, aliquotedSpecimen);
                }
            });
        }
        if (isDeletable) {
            addDeleteItemListener(new IInfoTableDeleteItemListener<AliquotedSpecimenWrapper>() {
                @Override
                public void deleteItem(
                    InfoTableEvent<AliquotedSpecimenWrapper> event) {
                    AliquotedSpecimenWrapper aliquotedSpecimen = getSelection();
                    if (aliquotedSpecimen != null) {
                        if (!MessageDialog
                            .openConfirm(
                                PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getShell(),
                                Messages.AliquotedSpecimenEntryInfoTable_delete_title,
                                NLS.bind(
                                    Messages.AliquotedSpecimenEntryInfoTable_delete_question,
                                    aliquotedSpecimen.getSpecimenType()
                                        .getName()))) {
                            return;
                        }
                        availableSpecimenTypes.add(aliquotedSpecimen
                            .getSpecimenType());
                        selectedAliquotedSpecimen.remove(aliquotedSpecimen);
                        setList(selectedAliquotedSpecimen);
                        deletedAliquotedSpecimens.add(aliquotedSpecimen);
                        notifyListeners();
                    }
                }
            });
        }
    }

    public List<AliquotedSpecimenWrapper> getAddedOrModifiedAliquotedSpecimens() {
        return addedOrModifiedAliquotedSpecimens;
    }

    public List<AliquotedSpecimenWrapper> getDeletedAliquotedSpecimens() {
        return deletedAliquotedSpecimens;
    }

    public void reload(List<AliquotedSpecimenWrapper> aliquotedSpecimens,
        boolean isEditable, boolean isDeletable) {
        this.isEditable = isEditable;
        this.isDeletable = isDeletable;

        selectedAliquotedSpecimen = aliquotedSpecimens;
        if (selectedAliquotedSpecimen == null) {
            selectedAliquotedSpecimen =
                new ArrayList<AliquotedSpecimenWrapper>();
        }
        reloadCollection(selectedAliquotedSpecimen);
        addedOrModifiedAliquotedSpecimens =
            new ArrayList<AliquotedSpecimenWrapper>();
        deletedAliquotedSpecimens = new ArrayList<AliquotedSpecimenWrapper>();
    }

    @Override
    public BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject(e1);
                    TableRowData i2 = getCollectionModelObject(e2);
                    return super.compare(i1.typeName, i2.typeName);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
