package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.dialogs.PvSourceVesselDialog;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.PvSourceVesselInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.PvSoruceVesselEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class PvSourceVesselEntryWidget extends BiobankWidget {

    private PvSourceVesselInfoTable pvSourceVesselTable;

    private Button addPvSourceVesselButton;

    private List<SourceVesselWrapper> allSourceVessels;

    private List<PvSourceVesselWrapper> selectedPvSourceVessels;

    private List<PvSourceVesselWrapper> addedPvSourceVessels;

    private List<PvSourceVesselWrapper> removedPvSourceVessels;

    private IObservableValue sourceVesselsAdded = new WritableValue(
        Boolean.FALSE, Boolean.class);

    private PatientVisitWrapper patientVisit;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style the style of control to construct
     * @param pvSourceVesselCollection the sample storage already selected and
     *            to be displayed in the table viewer (can be null).
     * @param toolkit The toolkit is responsible for creating SWT controls
     *            adapted to work in Eclipse forms. If widget is not used in a
     *            form this parameter should be null.
     */
    public PvSourceVesselEntryWidget(Composite parent, int style,
        PatientVisitWrapper visit, FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");

        getSourceVessels();
        this.patientVisit = visit;
        setLists();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        pvSourceVesselTable = new PvSoruceVesselEntryInfoTable(parent, null);
        updateCollection();
        pvSourceVesselTable.adaptToToolkit(toolkit, true);
        addEditSupport();
        pvSourceVesselTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    PvSourceVesselEntryWidget.this.notifyListeners();
                }
            });

        addPvSourceVesselButton = toolkit.createButton(parent,
            "Add Source Vessel", SWT.PUSH);
        addPvSourceVesselButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addPvSourceVessel();
            }
        });
    }

    public void addPvSourceVessel() {
        PvSourceVesselWrapper sourceVessel = new PvSourceVesselWrapper(
            SessionManager.getAppService());
        sourceVessel.setPatientVisit(patientVisit);
        addOrEditPvSourceVessel(true, sourceVessel,
            getNonDuplicateSourceVessels());
    }

    private void setLists() {
        List<PvSourceVesselWrapper> pvSourceVesselCollection = patientVisit
            .getPvSourceVesselCollection();
        if (pvSourceVesselCollection == null) {
            selectedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();
        } else {
            selectedPvSourceVessels = pvSourceVesselCollection;
        }
        addedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();
        removedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();
    }

    public void addBinding(WidgetCreator dbc) {
        final ControlDecoration controlDecoration = createDecorator(
            addPvSourceVesselButton,
            "Source vessels should be selected for this visit");
        WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    controlDecoration.show();
                    return ValidationStatus
                        .error("Source vessels should be added");
                } else {
                    controlDecoration.hide();
                    return Status.OK_STATUS;
                }
            }
        });
        dbc.bindValue(wv, sourceVesselsAdded, uvs, uvs);
    }

    private void addOrEditPvSourceVessel(boolean add,
        PvSourceVesselWrapper pvSourceVessel,
        Set<SourceVesselWrapper> availSourceVessels) {
        PvSourceVesselDialog dlg = new PvSourceVesselDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            pvSourceVessel, availSourceVessels);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedPvSourceVessels.add(dlg.getPvSourceVessel());
                addedPvSourceVessels.add(dlg.getPvSourceVessel());
            }
            updateCollection();
            notifyListeners();
        }
    }

    // need sample types that have not yet been selected in sampleStorageTable
    private Set<SourceVesselWrapper> getNonDuplicateSourceVessels() {
        Set<SourceVesselWrapper> nonDupSourceVessels = new HashSet<SourceVesselWrapper>();
        Collection<PvSourceVesselWrapper> currentSourceVessels = pvSourceVesselTable
            .getCollection();
        for (SourceVesselWrapper ss : allSourceVessels) {
            if (!currentSourceVessels.contains(ss)) {
                nonDupSourceVessels.add(ss);
            }
        }
        return nonDupSourceVessels;
    }

    private void addEditSupport() {
        pvSourceVesselTable.addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                addPvSourceVessel();
            }
        });

        pvSourceVesselTable
            .addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    PvSourceVesselWrapper svss = pvSourceVesselTable
                        .getSelection();
                    Set<SourceVesselWrapper> allowedSourceVessels = getNonDuplicateSourceVessels();
                    allowedSourceVessels.add(svss.getSourceVessel());
                    addOrEditPvSourceVessel(false, svss, allowedSourceVessels);
                }
            });
        pvSourceVesselTable
            .addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    PvSourceVesselWrapper svss = pvSourceVesselTable
                        .getSelection();

                    if (!MessageDialog.openConfirm(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        "Delete Aliquot Storage",
                        "Are you sure you want to delete source vessel \""
                            + svss.getSourceVessel().getName() + "\"?")) {
                        return;
                    }

                    selectedPvSourceVessels.remove(svss);
                    addedPvSourceVessels.remove(svss);
                    removedPvSourceVessels.add(svss);

                    updateCollection();
                    notifyListeners();
                }
            });
    }

    private void updateCollection() {
        pvSourceVesselTable.reloadCollection(selectedPvSourceVessels);
        sourceVesselsAdded.setValue(selectedPvSourceVessels.size() > 0);
    }

    private void getSourceVessels() {
        try {
            allSourceVessels = SourceVesselWrapper
                .getAllSourceVessels(SessionManager.getAppService());
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Error retrieving source vessels", e);
        }
    }

    public Collection<PvSourceVesselWrapper> getPvSourceVessels() {
        return pvSourceVesselTable.getCollection();
    }

    public List<PvSourceVesselWrapper> getAddedPvSourceVessels() {
        return addedPvSourceVessels;
    }

    public List<PvSourceVesselWrapper> getRemovedPvSourceVessels() {
        return removedPvSourceVessels;
    }

    public void setSelectedPvSourceVessels(
        List<PvSourceVesselWrapper> selectedPvSourceVessels) {
        this.selectedPvSourceVessels = selectedPvSourceVessels;
        pvSourceVesselTable.setCollection(selectedPvSourceVessels);
    }

    @Override
    public boolean setFocus() {
        return addPvSourceVesselButton.setFocus();
    }
}
