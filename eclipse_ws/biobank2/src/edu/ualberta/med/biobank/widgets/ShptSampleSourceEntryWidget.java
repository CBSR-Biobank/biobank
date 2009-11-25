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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.dialogs.ShptSampleSourceDialog;
import edu.ualberta.med.biobank.forms.FormUtils;
import edu.ualberta.med.biobank.widgets.infotables.ShptSampleSourceInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class ShptSampleSourceEntryWidget extends BiobankWidget {

    private ShptSampleSourceInfoTable shptSampleSourceTable;

    private Button addShptSampleSourceButton;

    private List<SampleSourceWrapper> allSampleSources;

    private List<ShptSampleSourceWrapper> selectedShptSampleSources;

    private ShipmentWrapper shipment;

    private IObservableValue sampleSourcesAdded = new WritableValue(
        Boolean.FALSE, Boolean.class);

    private PatientWrapper currentPatient;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style the style of control to construct
     * @param shptSampleSourceCollection the sample storage already selected and
     *            to be displayed in the table viewer (can be null).
     * @param toolkit The toolkit is responsible for creating SWT controls
     *            adapted to work in Eclipse forms. If widget is not used in a
     *            form this parameter should be null.
     */
    public ShptSampleSourceEntryWidget(Composite parent, int style,
        List<ShptSampleSourceWrapper> shptSampleSourceCollection,
        ShipmentWrapper shipment, PatientWrapper currentPatient,
        FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        getSampleSources();
        this.shipment = shipment;
        this.currentPatient = currentPatient;
        if (shptSampleSourceCollection == null) {
            selectedShptSampleSources = new ArrayList<ShptSampleSourceWrapper>();
        } else {
            selectedShptSampleSources = shptSampleSourceCollection;
        }

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        shptSampleSourceTable = new ShptSampleSourceInfoTable(parent, null);
        updateCollection();
        shptSampleSourceTable.adaptToToolkit(toolkit, true);
        addTableMenu();
        shptSampleSourceTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    ShptSampleSourceEntryWidget.this.notifyListeners();
                }
            });

        addShptSampleSourceButton = toolkit.createButton(parent,
            "Add Sample Source", SWT.PUSH);
        addShptSampleSourceButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ShptSampleSourceWrapper sampleSource = new ShptSampleSourceWrapper(
                    SessionManager.getAppService());
                sampleSource
                    .setShipment(ShptSampleSourceEntryWidget.this.shipment);
                List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
                patients.add(ShptSampleSourceEntryWidget.this.currentPatient);
                sampleSource.setPatientCollection(patients);
                addOrEditShptSampleSource(true, sampleSource,
                    getNonDuplicateSampleSources());
            }
        });
    }

    public void addBinding(WidgetCreator dbc) {
        final ControlDecoration controlDecoration = FormUtils.createDecorator(
            addShptSampleSourceButton,
            "Sample sources should be selected for this visit");
        WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    controlDecoration.show();
                    return ValidationStatus
                        .error("Sample sources should be selected");
                } else {
                    controlDecoration.hide();
                    return Status.OK_STATUS;
                }
            }
        });
        dbc.bindValue(wv, sampleSourcesAdded, uvs, uvs);
    }

    private void addOrEditShptSampleSource(boolean add,
        ShptSampleSourceWrapper shptSampleSource,
        Set<SampleSourceWrapper> availSampleSources) {
        ShptSampleSourceDialog dlg = new ShptSampleSourceDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            shptSampleSource, availSampleSources);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedShptSampleSources.add(dlg.getShptSampleSource());
            }
            updateCollection();
            notifyListeners();
        }
    }

    // need sample types that have not yet been selected in sampleStorageTable
    private Set<SampleSourceWrapper> getNonDuplicateSampleSources() {
        Set<SampleSourceWrapper> nonDupSampleSources = new HashSet<SampleSourceWrapper>();
        Collection<ShptSampleSourceWrapper> currentSampleSources = shptSampleSourceTable
            .getCollection();
        for (SampleSourceWrapper ss : allSampleSources) {
            if (!currentSampleSources.contains(ss)) {
                nonDupSampleSources.add(ss);
            }
        }
        return nonDupSampleSources;
    }

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        shptSampleSourceTable.getTableViewer().getTable().setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Edit");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                ShptSampleSourceWrapper svss = shptSampleSourceTable
                    .getSelection();
                Set<SampleSourceWrapper> allowedSampleSources = getNonDuplicateSampleSources();
                allowedSampleSources.add(svss.getSampleSource());
                addOrEditShptSampleSource(false, svss, allowedSampleSources);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                ShptSampleSourceWrapper svss = shptSampleSourceTable
                    .getSelection();

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Sample Storage",
                    "Are you sure you want to delete sample source \""
                        + svss.getSampleSource().getName() + "\"");

                if (confirm) {
                    Collection<ShptSampleSourceWrapper> ssToDelete = new HashSet<ShptSampleSourceWrapper>();
                    for (ShptSampleSourceWrapper ss : selectedShptSampleSources) {
                        if (ss.getSampleSource().getId().equals(
                            svss.getSampleSource().getId())) {
                            ssToDelete.add(ss);
                        }
                    }
                    selectedShptSampleSources.removeAll(ssToDelete);
                    updateCollection();
                    notifyListeners();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void updateCollection() {
        shptSampleSourceTable.setCollection(selectedShptSampleSources);
        sampleSourcesAdded.setValue(selectedShptSampleSources.size() > 0);
    }

    private void getSampleSources() {
        try {
            allSampleSources = SampleSourceWrapper
                .getAllSampleSources(SessionManager.getAppService());
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Error retrieving sample sources", e);
        }
    }

    public Collection<ShptSampleSourceWrapper> getShptSampleSources() {
        return shptSampleSourceTable.getCollection();
    }

    public void setSelectedShptSampleSources(
        List<ShptSampleSourceWrapper> selectedShptSampleSources) {
        this.selectedShptSampleSources = selectedShptSampleSources;
        shptSampleSourceTable.setCollection(selectedShptSampleSources);
    }

    @Override
    public boolean setFocus() {
        return addShptSampleSourceButton.setFocus();
    }
}
