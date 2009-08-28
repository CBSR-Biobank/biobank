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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.dialogs.SampleTypeDialog;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.SampleTypeComparator;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.SampleTypeInfoTable;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class SampleTypeEntryWidget extends BiobankWidget {

    private SampleTypeInfoTable sampleTypeTable;

    private Button addSampleTypeButton;

    private List<SampleType> selectedSampleTypes;

    private Collection<SampleType> conflictTypes;

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
        Collection<SampleType> sampleTypeCollection,
        Collection<SampleType> conflictTypes, String buttonLabel,
        FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");

        this.conflictTypes = conflictTypes;

        if (sampleTypeCollection == null) {
            selectedSampleTypes = new ArrayList<SampleType>();
        } else {
            selectedSampleTypes = new ArrayList<SampleType>(
                sampleTypeCollection);
        }
        Collections.sort(selectedSampleTypes, new SampleTypeComparator());
        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sampleTypeTable = new SampleTypeInfoTable(parent, selectedSampleTypes);
        sampleTypeTable.adaptToToolkit(toolkit, true);
        GridData gd = (GridData) sampleTypeTable.getLayoutData();
        gd.heightHint = 230;

        addTableMenu();
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
                addOrEditSampleType(true, new SampleType(),
                    getRestrictedTypes());
            }
        });
    }

    private boolean addOrEditSampleType(boolean add, SampleType sampleType,
        Set<SampleType> restrictedTypes) {
        SampleTypeDialog dlg = new SampleTypeDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), sampleType);
        if (dlg.open() == Dialog.OK) {
            if (addEditOk(sampleType, restrictedTypes)) {
                if (add) {

                    // only add to the collection when adding and not editing
                    selectedSampleTypes.add(dlg.getSampleType());
                }
                sampleTypeTable.setCollection(selectedSampleTypes);
                return true;
            } else {
                BioBankPlugin.openAsyncError("Name Problem",
                    "A type with the same name or short name already exists.");
                return false;
            }
        }
        return false;
    }

    // need sample types that have not yet been selected in sampleStorageTable
    private Set<SampleType> getRestrictedTypes() {

        Set<SampleType> restrictedTypes = new HashSet<SampleType>(conflictTypes);
        Collection<SampleType> currentSampleTypes = sampleTypeTable
            .getCollection();

        for (SampleType ss : currentSampleTypes) {
            restrictedTypes.add(ss);
        }
        return restrictedTypes;
    }

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        sampleTypeTable.getTableViewer().getTable().setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Edit");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {

                IStructuredSelection stSelection = (IStructuredSelection) sampleTypeTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                SampleType pvss = ((SampleType) item.o);
                SampleType st = new SampleType();
                st.setId(pvss.getId());
                st.setName(pvss.getName());
                st.setNameShort(pvss.getNameShort());

                Set<SampleType> restrictedTypes = getRestrictedTypes();
                restrictedTypes.remove(pvss);
                if (addOrEditSampleType(false, st, restrictedTypes)) {
                    pvss.setName(st.getName());
                    pvss.setNameShort(st.getNameShort());
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection stSelection = (IStructuredSelection) sampleTypeTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                SampleType sampleType = (SampleType) item.o;

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Sample Type",
                    "Are you sure you want to delete sample type \""
                        + sampleType.getName() + "\"?");

                if (confirm) {
                    Collection<SampleType> stToDelete = new HashSet<SampleType>();
                    for (SampleType st : selectedSampleTypes) {
                        if (st.getName().equals(sampleType.getName()))
                            stToDelete.add(st);
                    }

                    for (SampleType st : stToDelete) {
                        selectedSampleTypes.remove(st);
                    }

                    sampleTypeTable.setCollection(selectedSampleTypes);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private boolean addEditOk(SampleType type, Set<SampleType> restrictedTypes) {
        for (SampleType st : restrictedTypes) {
            if (st.getName().equals(type.getName())
                || st.getNameShort().equals(type.getNameShort())) {
                return false;
            }
        }
        return true;
    }

    public Collection<SampleType> getTableSampleTypes() {
        return sampleTypeTable.getCollection();
    }

    @Override
    public boolean setFocus() {
        return addSampleTypeButton.setFocus();
    }
}
