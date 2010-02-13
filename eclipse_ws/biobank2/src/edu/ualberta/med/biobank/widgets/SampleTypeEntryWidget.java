package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.dialogs.SampleTypeDialog;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SampleTypeInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class SampleTypeEntryWidget extends BiobankWidget {

    private SampleTypeInfoTable sampleTypeTable;

    private Button addSampleTypeButton;

    private List<SampleTypeWrapper> selectedSampleTypes;

    private List<SampleTypeWrapper> conflictTypes;

    private List<SampleTypeWrapper> addedOrModifiedSampleTypes;

    private List<SampleTypeWrapper> deletedSampleTypes;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style the style of control to construct
     * @param SampleTypeCollection the sample storage already selected and to be
     *            displayed in the table viewer (can be null).
     * @param toolkit The toolkit is responsible for creating SWT controls
     *            adapted to work in Eclipse forms. If widget is not used in a
     *            form this parameter should be null.
     */
    public SampleTypeEntryWidget(Composite parent, int style,
        List<SampleTypeWrapper> sampleTypeCollection,
        List<SampleTypeWrapper> conflictTypes, String buttonLabel,
        FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");

        setLists(sampleTypeCollection, conflictTypes);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        sampleTypeTable = new SampleTypeInfoTable(parent, selectedSampleTypes);
        sampleTypeTable.adaptToToolkit(toolkit, true);
        GridData gd = (GridData) sampleTypeTable.getLayoutData();
        gd.heightHint = 230;

        addEditSupport();
        sampleTypeTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    SampleTypeEntryWidget.this.notifyListeners();
                }
            });

        addSampleTypeButton = toolkit.createButton(parent, buttonLabel,
            SWT.PUSH);
        addSampleTypeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addOrEditSampleType(true, new SampleTypeWrapper(SessionManager
                    .getAppService()), getRestrictedTypes());
            }
        });
    }

    private boolean addOrEditSampleType(boolean add,
        SampleTypeWrapper sampleType, Set<SampleTypeWrapper> restrictedTypes) {
        SampleTypeDialog dlg = new SampleTypeDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), sampleType);
        if (dlg.open() == Dialog.OK) {
            if (addEditOk(sampleType, restrictedTypes)) {
                if (add) {
                    // only add to the collection when adding and not editing
                    selectedSampleTypes.add(dlg.getSampleType());
                }
                sampleTypeTable.setCollection(selectedSampleTypes);
                addedOrModifiedSampleTypes.add(dlg.getSampleType());
                notifyListeners();
                return true;
            }
            BioBankPlugin.openAsyncError("Name Problem",
                "A type with the same name or short name already exists.");
        }
        return false;
    }

    // need sample types that have not yet been selected in sampleStorageTable
    private Set<SampleTypeWrapper> getRestrictedTypes() {
        Set<SampleTypeWrapper> restrictedTypes = new HashSet<SampleTypeWrapper>(
            conflictTypes);
        Collection<SampleTypeWrapper> currentSampleTypes = sampleTypeTable
            .getCollection();
        restrictedTypes.addAll(currentSampleTypes);
        return restrictedTypes;
    }

    private void addEditSupport() {
        sampleTypeTable.addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                SampleTypeWrapper pvss = sampleTypeTable.getSelection();
                Set<SampleTypeWrapper> restrictedTypes = getRestrictedTypes();
                restrictedTypes.remove(pvss);
                addOrEditSampleType(false, pvss, restrictedTypes);
            }
        });

        sampleTypeTable
            .addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    SampleTypeWrapper sampleType = sampleTypeTable
                        .getSelection();

                    if (!MessageDialog.openConfirm(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        "Delete Sample Type",
                        "Are you sure you want to delete sample type \""
                            + sampleType.getName() + "\"?")) {
                        return;
                    }

                    // equals method now compare toString() results if both
                    // ids are null.
                    selectedSampleTypes.remove(sampleType);

                    sampleTypeTable.setCollection(selectedSampleTypes);
                    deletedSampleTypes.add(sampleType);
                    notifyListeners();
                }
            });
    }

    private boolean addEditOk(SampleTypeWrapper type,
        Set<SampleTypeWrapper> restrictedTypes) {
        for (SampleTypeWrapper st : restrictedTypes) {
            if (st.getName().equals(type.getName())
                || st.getNameShort().equals(type.getNameShort())) {
                return false;
            }
        }
        return true;
    }

    public List<SampleTypeWrapper> getAddedOrModifiedSampleTypes() {
        return addedOrModifiedSampleTypes;
    }

    public List<SampleTypeWrapper> getDeletedSampleTypes() {
        return deletedSampleTypes;
    }

    @Override
    public boolean setFocus() {
        return addSampleTypeButton.setFocus();
    }

    public void setLists(List<SampleTypeWrapper> sampleTypeCollection,
        List<SampleTypeWrapper> conflictTypes) {
        this.conflictTypes = conflictTypes;
        if (conflictTypes == null) {
            conflictTypes = new ArrayList<SampleTypeWrapper>();
        }

        if (sampleTypeCollection == null) {
            selectedSampleTypes = new ArrayList<SampleTypeWrapper>();
        } else {
            selectedSampleTypes = new ArrayList<SampleTypeWrapper>(
                sampleTypeCollection);
        }
        Collections.sort(selectedSampleTypes);
        if (sampleTypeTable != null) {
            sampleTypeTable.setCollection(sampleTypeCollection);
        }
        addedOrModifiedSampleTypes = new ArrayList<SampleTypeWrapper>();
        deletedSampleTypes = new ArrayList<SampleTypeWrapper>();
    }
}
