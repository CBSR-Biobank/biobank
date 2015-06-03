package edu.ualberta.med.biobank.forms.linkassign;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenAssignProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenLinkProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.AssignProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction.SpecimenAssignResInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenResInfo;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenLinkAndAssignPermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.ScanAssignDialog;
import edu.ualberta.med.biobank.dialogs.ScanLinkDialog;
import edu.ualberta.med.biobank.dialogs.scanmanually.ScanTubeManuallyFactory;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.helpers.ScanLinkHelper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.mvp.view.DialogView.Dialog;
import edu.ualberta.med.biobank.widgets.grids.IContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.PalletWidget;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionListener;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionSpecificBehaviour;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Allows the user to select specimens from a pallet grid and link and assign specimens.
 *
 * Specimens on the same pallet row can be assigned to different source specimens, aliquot specimen
 * types, collection events and patients.
 *
 * @author nelson
 *
 */
public class SpecimenLinkAndAssignForm
    extends AbstractPalletSpecimenAdminForm
    implements IContainerDisplayWidget {

    private static final I18n i18n = I18nFactory.getI18n(SpecimenLinkAndAssignForm.class);

    private static Logger log = LoggerFactory.getLogger(SpecimenLinkAndAssignForm.class.getName());

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenLinkEntryAndAssignForm";

    // TR: form titlekey
    @SuppressWarnings("nls")
    public static final String FORM_TITLE = i18n.tr("Specimen link and assign");

    @SuppressWarnings("nls")
    // TR: button label
    private static final String SCAN_LINK_BUTTON_LABEL = i18n.tr("Scan link");

    @SuppressWarnings("nls")
    // TR: button label
    private static final String SCAN_ASSIGN_BUTTON_LABEL = i18n.tr("Scan assign");

    @SuppressWarnings("nls")
    // TR: button label
    private static final String CLEAR_LINK_AND_ASSIGN_BUTTON_LABEL = i18n.tr("Clear link / assign");

    @SuppressWarnings("nls")
    // TR: button label
    private static final String CLEAR_PALLET_CONTAINER_BUTTON_LABEL =
        i18n.tr("Clear pallet container");

    @SuppressWarnings("nls")
    // TR: button label
    private static final String CLEAR_ALL_ON_SELECTEDLABEL = i18n.tr("Clear all on selected");

    private static final int LEFT_SECTION_WIDTH = 220;

    private static final int RIGHT_SECTION_WIDTH = 320;

    private static BgcLogger logger = BgcLogger.getLogger(SpecimenLinkAndAssignForm.class.getName());

    private ComboViewer palletDimensionsComboViewer;

    protected PalletWidget palletWidget;

    protected RowColPos currentGridDimensions =
        new RowColPos(RowColPos.ROWS_DEFAULT, RowColPos.COLS_DEFAULT);

    @SuppressWarnings("nls")
    private static final String PALLET_TYPES_BINDING = "palletType-binding";

    private ScanMode scanMode = ScanMode.NONE;

    protected Button scanLinkButton;

    protected Button scanAssignButton;

    protected Button clearCellsButton;

    protected Button clearPalletContainerButton;

    protected Button clearAllOnSelectedButton;

    private final IObservableValue allSpecimensLinkedOrAssigned =
        new WritableValue(Boolean.FALSE, Boolean.class);

    private final IObservableValue allSpecimensSameStudy =
        new WritableValue(Boolean.FALSE, Boolean.class);

    private final IObservableValue specimenPositionsValid =
        new WritableValue(Boolean.FALSE, Boolean.class);

    private ScanAssignSettings scanAssignSettings;

    private boolean multiSelectionEnabled = false;

    private final Map<String, AliquotedSpecimenResInfo> linkedSpecimensMap =
        new HashMap<String, AliquotedSpecimenResInfo>(0);

    private boolean havePermission = false;

    @Override
    protected void init() throws Exception {
        super.init();
        palletScanManagement = new PalletScanManagement(this, new ScanTubeManuallyFactory());
        scanAssignSettings = ScanAssignSettings.getInitialValues();
        setPartName(FORM_TITLE);
        setCanLaunchScan(false);
        createDataBinding();

        // make sure the user is allowed to access this feature
        havePermission = SessionManager.getAppService().isAllowed(
            new SpecimenLinkAndAssignPermission(
                SessionManager.getUser().getCurrentWorkingCenter().getId()));
    }

    private void createDataBinding() {
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @SuppressWarnings("nls")
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    return ValidationStatus.error(
                        // validation error message.
                        i18n.tr("Please link / assign all specimens"));
                }
                return Status.OK_STATUS;
            }
        });
        widgetCreator.bindValue(new WritableValue(Boolean.FALSE, Boolean.class),
            allSpecimensLinkedOrAssigned, uvs, uvs);

        uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @SuppressWarnings("nls")
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    return ValidationStatus.error(
                        // validation error message.
                        i18n.tr("Specimens linked to different studies"));
                }
                return Status.OK_STATUS;
            }
        });
        widgetCreator.bindValue(new WritableValue(Boolean.FALSE, Boolean.class),
            allSpecimensSameStudy, uvs, uvs);

        uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @SuppressWarnings("nls")
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    return ValidationStatus.error(
                        // validation error message.
                        i18n.tr("Specimen(s) assigned to invalid positions"));
                }
                return Status.OK_STATUS;
            }
        });
        widgetCreator.bindValue(new WritableValue(Boolean.FALSE, Boolean.class),
            specimenPositionsValid, uvs, uvs);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(FORM_TITLE);

        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        page.setLayout(gl);

        Composite mainComposite = new Composite(page, SWT.NONE);
        gl = new GridLayout(3, false);
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        mainComposite.setLayout(gl);
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, true);
        mainComposite.setLayoutData(gd);
        toolkit.adapt(mainComposite);

        if (!havePermission) {
            noPermission(mainComposite);
            return;
        }

        createLeftSection(mainComposite);
        createPalletComposite(mainComposite);
        createRightSection(mainComposite);
    }

    @SuppressWarnings("nls")
    private void noPermission(Composite parent) {
        Composite composite = toolkit.createComposite(parent);
        GridLayout gl = new GridLayout(1, false);
        gl.marginTop = 20;
        composite.setLayout(gl);
        GridData gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
        composite.setLayoutData(gd);
        toolkit.paintBordersFor(composite);

        Label label = toolkit.createLabel(composite,
            // TR: label in entry form
            i18n.tr("Access Denied. You do not have permission to use this feature."),
            SWT.WRAP | SWT.LEFT);
        gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
        label.setLayoutData(gd);
    }

    @SuppressWarnings("nls")
    private void createLeftSection(Composite parent) throws Exception {
        Composite leftComposite = toolkit.createComposite(parent);
        GridLayout gl = new GridLayout(1, false);
        leftComposite.setLayout(gl);
        toolkit.paintBordersFor(leftComposite);

        GridData gd = new GridData();
        gd.widthHint = LEFT_SECTION_WIDTH;
        gd.verticalAlignment = SWT.TOP;
        leftComposite.setLayoutData(gd);

        Composite dimensionsComposite = toolkit.createComposite(leftComposite);
        gl = new GridLayout(2, false);
        dimensionsComposite.setLayout(gl);
        toolkit.paintBordersFor(dimensionsComposite);

        gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        dimensionsComposite.setLayoutData(gd);

        palletDimensionsComboViewer = widgetCreator.createComboViewer(
            dimensionsComposite,
            i18n.tr("Pallet dimensions"),
            Arrays.asList(PalletDimensions.values()),
            getCurrentPlateDimensions(),
            // TR: validation error message
            i18n.tr("A pallet type should be selected"),
            true,
            PALLET_TYPES_BINDING,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    PalletDimensions plateDimensions = (PalletDimensions) selectedObject;
                    int rows = plateDimensions.getRows();
                    int cols = plateDimensions.getCols();
                    Capacity capacity = new Capacity(rows, cols);
                    adjustWidgetsForScannedPallet(capacity);
                    setFakeContainerType(rows, cols);
                    palletDimensionsComboViewer.getCombo().setFocus();
                }
            },
            new LabelProvider() {
                @Override
                public String getText(Object element) {
                    PalletDimensions plateDimensions = (PalletDimensions) element;
                    return plateDimensions.getDisplayLabel();
                }
            }
            );

        createScanButton(leftComposite);
        createHelpText(leftComposite);

        setCanLaunchScan(true);
        setFirstControl(palletDimensionsComboViewer.getCombo());
    }

    @SuppressWarnings("nls")
    private void createHelpText(Composite parent) {
        Composite composite = toolkit.createComposite(parent);
        GridLayout gl = new GridLayout(1, false);
        gl.marginTop = 20;
        composite.setLayout(gl);
        GridData gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
        composite.setLayoutData(gd);
        toolkit.paintBordersFor(composite);

        Label label = toolkit.createLabel(composite,
            // TR: label in entry form
            i18n.tr("Once a pallet has been decoded, use left click to select a cell."),
            SWT.WRAP | SWT.LEFT);
        gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
        label.setLayoutData(gd);

        label = toolkit.createLabel(composite,
            // TR: label in entry form
            i18n.tr("Use the Shift key and left click to extend the range of selected cells."),
            SWT.WRAP | SWT.LEFT);
        gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
        label.setLayoutData(gd);

        label = toolkit.createLabel(composite,
            // TR: label in entry form
            i18n.tr("Use the Ctrl key and left click to add a cell to the selected cells."),
            SWT.WRAP);
        gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
        label.setLayoutData(gd);

    }

    private void createRightSection(Composite parent) throws Exception {
        Composite rightComposite = toolkit.createComposite(parent, SWT.BORDER_DASH);
        GridLayout gl = new GridLayout(1, false);
        rightComposite.setLayout(gl);
        toolkit.paintBordersFor(rightComposite);
        GridData gd = new GridData();
        gd.widthHint = RIGHT_SECTION_WIDTH;
        gd.verticalAlignment = SWT.TOP;
        rightComposite.setLayoutData(gd);

        int buttonWidth = RIGHT_SECTION_WIDTH - 140;

        scanLinkButton = toolkit.createButton(rightComposite, SCAN_LINK_BUTTON_LABEL, SWT.PUSH);
        gd = new GridData();
        gd.widthHint = buttonWidth;
        scanLinkButton.setLayoutData(gd);
        scanLinkButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scanLinkSelection();
            }
        });
        scanLinkButton.setEnabled(false);

        scanAssignButton = toolkit.createButton(rightComposite, SCAN_ASSIGN_BUTTON_LABEL, SWT.PUSH);
        gd = new GridData();
        gd.widthHint = buttonWidth;
        scanAssignButton.setLayoutData(gd);
        scanAssignButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scanAssignSelection();
            }
        });
        scanAssignButton.setEnabled(false);

        clearCellsButton = toolkit.createButton(rightComposite, CLEAR_LINK_AND_ASSIGN_BUTTON_LABEL, SWT.PUSH);
        gd = new GridData();
        gd.widthHint = buttonWidth;
        clearCellsButton.setLayoutData(gd);
        clearCellsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearSelection();
            }
        });
        clearCellsButton.setEnabled(false);

        clearPalletContainerButton = toolkit.createButton(
            rightComposite, CLEAR_PALLET_CONTAINER_BUTTON_LABEL, SWT.PUSH);
        gd = new GridData();
        gd.widthHint = buttonWidth;
        clearPalletContainerButton.setLayoutData(gd);
        clearPalletContainerButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearPalletContainer();
            }
        });
        clearPalletContainerButton.setEnabled(false);

        clearAllOnSelectedButton = toolkit.createButton(rightComposite, CLEAR_ALL_ON_SELECTEDLABEL, SWT.PUSH);
        gd = new GridData();
        gd.widthHint = buttonWidth;
        clearAllOnSelectedButton.setLayoutData(gd);
        clearAllOnSelectedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearAllOnSelected();
            }
        });
        clearAllOnSelectedButton.setEnabled(false);

        createCancelConfirmWidget(rightComposite);
        setChildrenActionSectionEnabled(false);
    }

    protected void createPalletComposite(Composite composite) {
        Composite palletComposite = toolkit.createComposite(composite);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        palletComposite.setLayout(layout);

        GridData gd = new GridData(SWT.BEGINNING, SWT.FILL, true, true);
        palletComposite.setLayoutData(gd);
        palletWidget = createScanPalletWidget(
            palletComposite,
            currentGridDimensions.getRow(),
            currentGridDimensions.getCol()
            );
    }

    private void adjustWidgetsForScannedPallet(Capacity capacity) {
        int rows = capacity.getRowCapacity();
        int cols = capacity.getColCapacity();
        currentGridDimensions = new RowColPos(rows, cols);
        recreateScanPalletWidget(rows, cols);
        page.layout(true, true);
        book.reflow(true);
    }

    /**
     * Called when the user has selected some cells from the pallet visualization widget.
     *
     * This can only be called after the user has decoded an image.
     *
     * @param enable
     */
    private void setChildrenActionSectionEnabled(boolean enable) {
        scanLinkButton.setEnabled(enable);
        scanAssignButton.setEnabled(enable);
        clearCellsButton.setEnabled(enable);
        clearPalletContainerButton.setEnabled(enable);
        clearAllOnSelectedButton.setEnabled(enable);
    }

    protected void recreateScanPalletWidget(int rows, int cols) {
        Composite palletComposite = palletWidget.getParent();
        palletWidget.dispose();
        palletWidget = createScanPalletWidget(palletComposite, rows, cols);
    }

    protected PalletWidget createScanPalletWidget(Composite palletComposite, int rows, int cols) {
        PalletWidget palletWidget = new PalletWidget(
            palletComposite,
            UICellStatus.DEFAULT_PALLET_SCAN_ASSIGN_STATUS_LIST,
            rows,
            cols,
            this);

        // to prevent scrollbars, do not add a GridLayout or GridData to this widget

        toolkit.adapt(palletWidget);
        palletWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                manageDoubleClick(e);
            }
        });

        return palletWidget;
    }

    /**
     * Called to enable or disable the cell selection functionality in the pallet visualization
     * widget.
     *
     * @param enable
     */
    private void enableMultiSelection(boolean enable) {
        if (enable) {
            palletWidget.getMultiSelectionManager().enableMultiSelection(
                new MultiSelectionSpecificBehaviour() {
                    @Override
                    public void removeSelection(AbstractUIWell cell) {
                        //
                    }

                    @Override
                    public boolean isSelectable(AbstractUIWell cell) {
                        return true;
                    }
                });
            palletWidget.getMultiSelectionManager().addMultiSelectionListener(
                new MultiSelectionListener() {
                    @Override
                    public void selectionChanged(MultiSelectionEvent mse) {
                        setChildrenActionSectionEnabled(mse.selections > 0);
                    }
                });
        } else {
            palletWidget.getMultiSelectionManager().disableMultiSelection();
        }
    }

    /**
     * Used to check if the selected cells are valid for scan linking or assigning. I.e. not already
     * linked or assigned.
     *
     * @return true if all the selected cells have type {@link UICellStatus.NO_TYPE}.
     */
    @SuppressWarnings("nls")
    private boolean selectedCellsHaveNoType() {
        Collection<AbstractUIWell> selectedCells =
            palletWidget.getMultiSelectionManager().getSelectedCells();
        for (AbstractUIWell i : selectedCells) {
            SpecimenCell cell = (SpecimenCell) i;
            log.trace("selectedCellsHaveNoType: cell: {}, status: {}",
                cell.getLabel(), cell.getStatus());
            if (cell.getStatus() != UICellStatus.NO_TYPE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Used to check if all the cells in the pallet have been linked or assigned.
     *
     * @return true if all the selected cells have type other than {@link UICellStatus.NO_TYPE}.
     */
    private boolean allCellsHaveNoType() {
        for (AbstractUIWell i : palletWidget.getCells().values()) {
            SpecimenCell cell = (SpecimenCell) i;
            if (cell.getStatus() != UICellStatus.NO_TYPE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Used to check if all the cells in the pallet have been linked or assigned.
     *
     * @return true if all the selected cells have type other than {@link UICellStatus.NO_TYPE}.
     */
    private boolean allCellsHaveType() {
        for (AbstractUIWell i : palletWidget.getCells().values()) {
            SpecimenCell cell = (SpecimenCell) i;
            if (cell.getStatus() == UICellStatus.NO_TYPE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks that all the linked specimens belong to the same study.
     *
     * @return true if all specimens are to be linked to the same study. False otherwise.
     */
    private boolean allSpecimensInSameStudy() {
        Set<Integer> studyIds = new HashSet<Integer>();

        for (AbstractUIWell i : palletWidget.getCells().values()) {
            SpecimenCell cell = (SpecimenCell) i;
            SpecimenWrapper sourceSpecimen = cell.getSourceSpecimen();
            if (sourceSpecimen != null) {
                CollectionEventWrapper collectionEvent = sourceSpecimen.getCollectionEvent();
                studyIds.add(collectionEvent.getPatient().getStudy().getId());
            }
        }
        return (studyIds.size() == 1);
    }

    /**
     * Called when the user has selected some cells and wants to link them to patients, collection
     * event, source specimen, and specimen type.
     *
     * Note that the server will not be updated until the user presses the "Confirm" button.
     */
    @SuppressWarnings("nls")
    private void scanLinkSelection() {
        if (!selectedCellsHaveNoType()) {
            // some cells have already been linked, display error message
            BgcPlugin.openError(
                // TR: error dialog title
                i18n.tr("Selection problem"),
                // TR: error dialog message
                i18n.tr("You have selected cells that you've already linked. "
                    + "They cannot be scan linked again.\n"
                    + "\nPlease make another selection or press the Cancel button to reset the form."));
            return;
        }

        if (scanMode == ScanMode.ASSIGN) {
            // some specimens were Assigned, cannot switch to linking now, must first clear the
            // cells
            BgcPlugin.openError(
                // TR: error dialog title
                i18n.tr("Selection problem"),
                // TR: error dialog message
                i18n.tr("You previously assigned positions to specimens.\n\n"
                    + "If you want to link specimens you must clear the cells that were assigned."));
            return;
        }

        ScanLinkDialog dialog =
            new ScanLinkDialog(Display.getDefault().getActiveShell(), activityLogger);

        if (dialog.open() == Dialog.OK) {
            scanMode = ScanMode.LINK;

            palletWidgetLinkSpecimens(
                palletWidget.getMultiSelectionManager().getSelectedCells(),
                dialog.getSourceSpecimenSelected(),
                dialog.getAliquotsTypeSelection().getSpecimenType());
        }

        allSpecimensSameStudy.setValue(allSpecimensInSameStudy());

        if (allCellsHaveType()) {
            allSpecimensLinkedOrAssigned.setValue(true);
        }
    }

    private void palletWidgetLinkSpecimens(
        Collection<AbstractUIWell> selectedCells,
        Specimen sourceSecimen,
        SpecimenType aliquotSpecimenType) {
        for (AbstractUIWell i : selectedCells) {
            SpecimenCell cell = (SpecimenCell) i;
            cell.setSourceSpecimen(new SpecimenWrapper(
                SessionManager.getAppService(), sourceSecimen));
            cell.setSpecimenType(new SpecimenTypeWrapper(
                SessionManager.getAppService(), aliquotSpecimenType));
            cell.setTitle(
                cell.getSourceSpecimen().getCollectionEvent().getPatient().getPnumber());
            if (cell.getStatus() != UICellStatus.ERROR) {
                cell.setStatus(UICellStatus.TYPE);
            }
        }
        palletWidget.updateCells();
        palletWidget.redraw();
    }

    /**
     * Called when the user has selected some cells and wants to link them to patients, collection
     * event, source specimen, specimen type, and a position in a container.
     *
     * This is the same as {@link scanLinkSelection} but also allows to assign a container position.
     *
     * Note that the server will not be updated until the user presses the "Confirm" button.
     */
    @SuppressWarnings("nls")
    private void scanAssignSelection() {
        if (!selectedCellsHaveNoType()) {
            // some cells have already been linked, display error message
            BgcPlugin.openError(
                // TR: error dialog title
                i18n.tr("Selection problem"),
                // TR: error dialog message
                i18n.tr("You have selected cells that you've already linked.\n\n"
                    + "If you want to assign specimens you must reset the cells that were linked."));
            return;
        }

        if (scanMode == ScanMode.LINK) {
            // some specimens were Assigned, cannot switch to linking now, must first clear the
            // cells
            BgcPlugin.openError(
                // TR: error dialog title
                i18n.tr("Selection problem"),
                // TR: error dialog message
                i18n.tr("You previously linked specimens. You cannot now assign specimens."));
            return;
        }

        Capacity capacity = getContainerType().getCapacity();

        ScanAssignDialog dialog =
            new ScanAssignDialog(Display.getDefault().getActiveShell(),
                capacity.getRowCapacity(),
                capacity.getColCapacity(),
                scanAssignSettings.palletBarcode,
                scanAssignSettings.palletLabel,
                scanAssignSettings.palletType,
                activityLogger);

        if (dialog.open() == Dialog.OK) {
            scanMode = ScanMode.ASSIGN;

            if (scanAssignSettings.palletLabel.isEmpty()) {
                // only update the settings the first time the dialog is opened
                scanAssignSettings = new ScanAssignSettings(
                    dialog.getPalletBarcode(),
                    dialog.getPalletLabel(),
                    dialog.getPalletContainerType(),
                    dialog.getPalletContainer(),
                    dialog.isNewContainer());
            }

            palletWidgetLinkSpecimens(
                palletWidget.getMultiSelectionManager().getSelectedCells(),
                dialog.getSourceSpecimenSelected(),
                dialog.getAliquotsTypeSelection().getSpecimenType());

            IRunnableWithProgress op = new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) {
                    monitor.beginTask(
                        // progress monitor message
                        i18n.tr("Processing specimens..."),
                        IProgressMonitor.UNKNOWN);

                    try {
                        assignProcessDecodeResult();
                    } catch (RemoteConnectFailureException exp) {
                        BgcPlugin.openRemoteConnectErrorMessage(exp);
                        assignDecodeAndProcessError(null);
                    } catch (AccessDeniedException e) {
                        BgcPlugin.openAsyncError(
                            // dialog title
                            i18n.tr("Scan result error"),
                            e.getLocalizedMessage());
                        assignDecodeAndProcessError(e.getLocalizedMessage());
                    } catch (Exception ex) {
                        BgcPlugin.openAsyncError(
                            // dialog title
                            i18n.tr("Processing error"),
                            ex,
                            // dialog message
                            i18n.tr("Barcodes can still be entered with the handheld 2D scanner."));
                    }
                    monitor.done();
                }
            };

            try {
                Shell shell = Display.getDefault().getActiveShell();
                new ProgressMonitorDialog(shell).run(true, false, op);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        allSpecimensSameStudy.setValue(allSpecimensInSameStudy());

        if (allCellsHaveType()) {
            allSpecimensLinkedOrAssigned.setValue(true);
        }
    }

    /**
     * Goes through cells retrieved from scan, set status based on response from server.
     *
     * @throws Exception
     */
    @SuppressWarnings({ "nls", "unchecked" })
    public void assignProcessDecodeResult() throws Exception {
        CenterWrapper<?> currentWorkingCenter = SessionManager.getUser().getCurrentWorkingCenter();
        if (currentWorkingCenter == null) {
            throw new IllegalStateException("current working center is null");
        }

        Map<RowColPos, SpecimenCell> cellsMap = (Map<RowColPos, SpecimenCell>) palletWidget.getCells();
        Map<RowColPos, CellInfo> serverCells = new HashMap<RowColPos, CellInfo>(0);
        for (SpecimenCell cell : cellsMap.values()) {
            if (cell.getStatus() != UICellStatus.EMPTY) {
                RowColPos pos = new RowColPos(cell.getRow(), cell.getCol());
                cellsMap.put(pos, cell);
                CellInfo serverCell = cell.transformIntoServerCell();

                // set server cells status to null so that SpecimenAssignProcessAction performs
                // proper checks
                serverCell.setStatus(null);

                serverCells.put(pos, serverCell);
            }
        }

        AssignProcessInfo assignProcessInfo =
            new AssignProcessInfo(scanAssignSettings.palletContainer.getWrappedObject());

        ScanProcessResult res = (ScanProcessResult) SessionManager.getAppService().doAction(
            new SpecimenAssignProcessAction(
                assignProcessInfo,
                currentWorkingCenter.getId(),
                serverCells,
                Locale.getDefault()));

        // print result logs - but first filter out specimens that are to be linked and assigned
        List<String> filteredLogs = new ArrayList<String>();
        for (String log : res.getLogs()) {
            if (!log.contains("not found in the database")) {
                filteredLogs.add(log);
            }
        }
        appendLogs(filteredLogs);

        Map<String, SpecimenBriefInfo> specimenDataMap =
            AbstractPalletSpecimenAdminForm.getSpecimenData(
                currentWorkingCenter, new HashSet<SpecimenCell>(cellsMap.values()));

        boolean haveSpecimenPositionError = false;

        // for each cell, convert into a client side cell
        for (Entry<RowColPos, CellInfo> entry : res.getCells().entrySet()) {
            RowColPos pos = entry.getKey();
            SpecimenCell palletCell = cellsMap.get(entry.getKey());
            CellInfo serverCell = entry.getValue();
            if (palletCell == null) {
                // can happen if missing no tube in this cell
                palletCell = new SpecimenCell(
                    pos.getRow(),
                    pos.getCol(),
                    new DecodedWell(
                        serverCell.getRow(),
                        serverCell.getCol(),
                        serverCell.getValue()));
                cellsMap.put(pos, palletCell);
                log.debug("processScanResult: palletCell is null: pos ({}, {})",
                    pos.getRow(), pos.getCol());
            }

            // specimens that have not been linked yet will return a status of ERROR, it is OK
            // to add these.
            //
            // However, serverCell.getExpectedSpecimenId() returns non null if the spot is already
            // filled by a different specimen
            if ((serverCell.getStatus() != CellInfoStatus.ERROR)
                || (serverCell.getExpectedSpecimenId() != null)) {
                palletCell.mergeExpected(specimenDataMap.get(palletCell.getValue()), serverCell);
            }

            if (serverCell.getExpectedSpecimenId() != null) {
                haveSpecimenPositionError = true;
            }
        }
        updateCells(!haveSpecimenPositionError);
    }

    private void updateCells(final boolean validSpecimenPositions) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                palletWidget.updateCells();
                palletWidget.redraw();
                specimenPositionsValid.setValue(validSpecimenPositions);
            }
        });

    }

    public void assignDecodeAndProcessError(String errorMsg) {
        if (errorMsg != null && !errorMsg.isEmpty()) {
            appendLog(errorMsg);
        }
    }

    private void clearLinkAndAssignOnCells(Collection<? extends AbstractUIWell> cells) {
        for (AbstractUIWell i : cells) {
            SpecimenCell cell = (SpecimenCell) i;

            // log.info("pos: {}, status: {}", cell.getPositionStr(), cell.getStatus());

            cell.setSourceSpecimen(null);
            cell.setSpecimenType(null);
            cell.setTitle(StringUtil.EMPTY_STRING);

            UICellStatus cellStatus = cell.getStatus();

            if (cellStatus == UICellStatus.TYPE) {
                cell.setStatus(UICellStatus.NO_TYPE);
            } else if (cellStatus != UICellStatus.NO_TYPE) {
                cell.setStatus(UICellStatus.EMPTY);
            }
            cell.setExpectedSpecimen(null);
        }
        palletWidget.updateCells();
        palletWidget.redraw();

        allSpecimensSameStudy.setValue(false);
        allSpecimensLinkedOrAssigned.setValue(false);
    }

    /**
     * Called when the user presses the "Clear link / assign" button.
     *
     * The selected cells have any link and / or assign information cleared.
     */
    @SuppressWarnings("nls")
    private void clearSelection() {
        boolean userSelection = BgcPlugin.openConfirm(
            // confirmation dialog title
            i18n.tr("Clear cells"),
            // confirmation dialog message
            i18n.tr("Are you sure you want to clear the link / assing information on the selected cells?"));

        if (userSelection) {
            clearSelectionConfirmed();
        }
    }

    private void clearSelectionConfirmed() {
        clearLinkAndAssignOnCells(palletWidget.getMultiSelectionManager().getSelectedCells());

        if (allCellsHaveNoType()) {
            scanMode = ScanMode.NONE;
        }
        linkedSpecimensMap.clear();
    }

    @SuppressWarnings("nls")
    private void clearPalletContainer() {
        boolean userSelection = BgcPlugin.openConfirm(
            // confirmation dialog title
            i18n.tr("Clear pallet container"),
            // confirmation dialog message
            i18n.tr("Are you sure you want to clear the pallet container?\n"
                + "Doing so will clear any previously assigned specimens also."));

        if (userSelection) {
            clearLinkAndAssignOnCells(palletWidget.getCells().values());
            linkedSpecimensMap.clear();
            scanAssignSettings = ScanAssignSettings.getInitialValues();
        }
    }

    /**
     * Called when the user presses the "Clear all" button.
     *
     * The selected cells have any link and / or assign information cleared.
     */
    @SuppressWarnings("nls")
    private void clearAllOnSelected() {
        boolean userSelection = BgcPlugin.openConfirm(
            // confirmation dialog title
            i18n.tr("Clear cells"),
            // confirmation dialog message
            i18n.tr("Are you sure you want to clear everything on the selected cells (including decoded inventory IDs)?"));

        if (userSelection) {
            clearSelectionConfirmed();
            for (AbstractUIWell cell : palletWidget.getMultiSelectionManager().getSelectedCells()) {
                SpecimenCell specimenCell = (SpecimenCell) cell;
                specimenCell.setStatus(UICellStatus.EMPTY);
                specimenCell.setValue(StringUtil.EMPTY_STRING);
            }

            palletWidget.updateCells();
            palletWidget.redraw();
        }
    }

    /**
     * Displays the tooltip text when user hovers over a cell in the pallet grid.
     */
    @SuppressWarnings("nls")
    @Override
    public String getTooltipText(AbstractUIWell cell) {
        SpecimenCell palletCell = (SpecimenCell) cell;
        StringBuffer buf = new StringBuffer();
        String msg = palletCell.getValue();
        if (!msg.isEmpty()) {
            buf.append(i18n.tr("Invetory ID: ")).append(msg);
            String information = palletCell.getInformation();
            if ((information != null) && !information.isEmpty()) {
                buf.append(": ");
                buf.append(information);
            }
        }
        SpecimenTypeWrapper type = palletCell.getType();
        if (type != null) {
            buf.append("\n").append(i18n.tr("Specimen Type: ")).append(type.getName());
        }
        SpecimenWrapper sourceSpecimen = palletCell.getSourceSpecimen();
        if (sourceSpecimen != null) {
            CollectionEventWrapper collectionEvent = sourceSpecimen.getCollectionEvent();
            buf.append("\n").append(i18n.tr("Source Specimen: ")).append(sourceSpecimen.getInventoryId());
            buf.append("\n").append(i18n.tr("Visit number: ")).append(collectionEvent.getVisitNumber());
            buf.append("\n").append(i18n.tr("Patient: ")).append(collectionEvent.getPatient().getPnumber());

            if (scanMode == ScanMode.ASSIGN) {
                buf.append("\n").append(i18n.tr("Pallet: ")).append(scanAssignSettings.palletLabel);
            }
        }
        return buf.toString();
    }

    /**
     * Used to enter the decoded barcode's message.
     *
     * This can be done by using a hand held scanner.
     *
     * Sometimes, decoding of barcodes using the flatbed scanner some barcodes are missed. This
     * additional method of adding deocded barcode messages provides more flexibility.
     */
    protected void manageDoubleClick(MouseEvent e) {
        PalletWidget widget = (PalletWidget) e.widget;
        SpecimenCell cell = (SpecimenCell) widget.getObjectAtCoordinates(e.x, e.y);
        if (canDecodeTubesManually(cell)) {
            scanTubesManually(e);
        } else if (cell != null) {
            switch (cell.getStatus()) {
            case ERROR:
                // do something ?
                break;
            case MISSING:
                SessionManager.openViewForm(cell.getExpectedSpecimen());
                break;
            }
        }
    }

    /**
     * Called after the barcodes on the tubes (in a pallet) have been scanned and decoded.
     */
    @Override
    protected void afterScanAndProcess(final Integer rowToProcess) {
        Display.getDefault().asyncExec(new Runnable() {
            @SuppressWarnings("nls")
            @Override
            public void run() {
                log.debug("afterScanAndProcess: asyncExec");
                // Show result in grid
                palletWidget.setCells(getCells());
                if (!multiSelectionEnabled) {
                    multiSelectionEnabled = true;
                    enableMultiSelection(true);
                }
            }
        });
    }

    /**
     * Called when the form is reset, either by pressing the cancel or confirm buttons
     *
     * @param enalbe - in this implementation we don't care about this value.
     */
    @Override
    protected void enableFields(boolean enable) {
        scanMode = ScanMode.NONE;
        linkedSpecimensMap.clear();
        setChildrenActionSectionEnabled(false);
        multiSelectionEnabled = false;
        enableMultiSelection(false);
        palletWidget.setCells(getCells());
        palletScanManagement.onReset();
        scanAssignSettings = ScanAssignSettings.getInitialValues();
        // setScanHasBeenLaunched(false);
        cancelConfirmWidget.reset();
        currentGridDimensions = new RowColPos(RowColPos.ROWS_DEFAULT, RowColPos.COLS_DEFAULT);
        specimenPositionsValid.setValue(false);
    }

    @Override
    protected void refreshPalletDisplay() {
        // do nothing
    }

    /**
     * Fields are always valid for this form.
     *
     */
    @Override
    protected boolean fieldsValid() {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    protected Action<ProcessResult> getCellProcessAction(Integer centerId, CellInfo cell,
        Locale locale) {
        log.debug("getCellProcessAction:");
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    protected Action<ProcessResult> getPalletProcessAction(Integer centerId,
        Map<RowColPos, CellInfo> cells, Locale locale) {
        log.debug("getPalletProcessAction");
        return new SpecimenLinkProcessAction(centerId, null, cells, locale);
    }

    @SuppressWarnings("nls")
    @Override
    protected String getActivityTitle() {
        return i18n.tr("Specimen link and assign");
    }

    @Override
    public BgcLogger getErrorLogger() {
        return logger;
    }

    /**
     * Called on data binding state changes.
     */
    @SuppressWarnings("nls")
    @Override
    protected String getOkMessage() {
        if (allCellsHaveType()) {
            // TR: title area message
            return i18n.tr("Press the Confirm button to submit your changes");
        }

        // TR: title area message
        return i18n.tr("Select specimens to link or assign");
    }

    @Override
    public String getNextOpenedFormId() {
        return ID;
    }

    /**
     * Called when user presses Cancel or Confirm button.
     */
    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        log.debug("setValues");

        super.setValues();
        allSpecimensLinkedOrAssigned.setValue(false);
        allSpecimensSameStudy.setValue(true);
        form.layout(true, true);
    }

    /**
     * Called when the user presses the "Confirm" button.
     */
    @SuppressWarnings("nls")
    @Override
    protected void saveForm() throws Exception {
        log.debug("saveForm");

        switch (scanMode) {
        case LINK:
            saveLinkedSpecimens();
            break;

        case ASSIGN:
            saveAssignedSpecimens();
            break;

        default:
            throw new IllegalStateException("invalid mode: " + scanMode);
        }

        setFinished(false);
        SessionManager.log("save", null, "SpecimenLinkAndAssign");
    }

    @SuppressWarnings("nls")
    private void saveLinkedSpecimens() throws Exception {
        if (scanMode != ScanMode.LINK) {
            throw new IllegalStateException("invalid mode: " + scanMode);
        }
        linkSpecimens();
    }

    @SuppressWarnings("unchecked")
    private void linkSpecimens() throws Exception {
        Map<RowColPos, SpecimenCell> cells = (Map<RowColPos, SpecimenCell>) palletWidget.getCells();
        Map<String, SpecimenCell> cellsByInventoryId = new HashMap<String, SpecimenCell>(cells.size());
        List<AliquotedSpecimenInfo> spcInfoList = new ArrayList<AliquotedSpecimenInfo>();

        for (SpecimenCell cell : cells.values()) {
            if (SpecimenCell.hasValue(cell) && cell.getStatus() == UICellStatus.TYPE) {
                SpecimenWrapper sourceSpecimen = cell.getSourceSpecimen();
                SpecimenWrapper aliquotedSpecimen = cell.getSpecimen();
                AliquotedSpecimenInfo spcInfo = new AliquotedSpecimenInfo();
                spcInfo.activityStatus = ActivityStatus.ACTIVE;
                spcInfo.typeId = aliquotedSpecimen.getSpecimenType().getId();
                spcInfo.inventoryId = cell.getValue();
                spcInfo.parentSpecimenId = sourceSpecimen.getId();
                spcInfoList.add(spcInfo);
            }
            cellsByInventoryId.put(cell.getValue(), cell);
        }

        List<AliquotedSpecimenResInfo> linkedSpecimens =
            SessionManager.getAppService().doAction(
                new SpecimenLinkSaveAction(
                    SessionManager.getUser().getCurrentWorkingCenter().getId(), null, spcInfoList)
                ).getList();
        setLinkedSpecimens(linkedSpecimens);

        appendLogs(ScanLinkHelper.linkedSpecimensLogMessage(linkedSpecimens));
    }

    private void setLinkedSpecimens(List<AliquotedSpecimenResInfo> linkedSpecimens) {
        for (AliquotedSpecimenResInfo spcInfo : linkedSpecimens) {
            linkedSpecimensMap.put(spcInfo.inventoryId, spcInfo);
        }
    }

    @SuppressWarnings("nls")
    private void saveAssignedSpecimens() throws Exception {
        if (scanMode != ScanMode.ASSIGN) {
            throw new IllegalStateException("invalid mode: " + scanMode);
        }

        linkSpecimens();
        assignSpecimens();
    }

    @SuppressWarnings({ "unchecked", "nls" })
    private void assignSpecimens() throws ApplicationException {
        // TODO See SpecimenAssignEntryForm.saveMultipleSpecimens()
        Map<RowColPos, SpecimenCell> cells = (Map<RowColPos, SpecimenCell>) palletWidget.getCells();
        List<SpecimenInfo> specInfos = new ArrayList<SpecimenAssignSaveAction.SpecimenInfo>();
        for (Entry<RowColPos, SpecimenCell> entry : cells.entrySet()) {
            RowColPos rcp = entry.getKey();
            SpecimenCell cell = entry.getValue();
            if (cell != null) {
                log.info("cell: pos: {}, status: {}", cell.getRowColPos(), cell.getStatus());

                // only add cells with status of UICellStatus.TYPE
                //
                // if cell has status of UICellStatus.FILLED it has been previously assigned
                if (cell.getStatus() == UICellStatus.TYPE) {
                    AliquotedSpecimenResInfo spcInfo = linkedSpecimensMap.get(cell.getValue());

                    if (spcInfo == null) {
                        throw new IllegalStateException(
                            "inventory ID missing from linked specimen information: "
                                + cell.getValue());
                    }

                    SpecimenInfo specInfo = new SpecimenInfo();
                    specInfo.specimenId = spcInfo.id;
                    specInfo.position = rcp;
                    specInfos.add(specInfo);
                }
            }
        }

        ContainerWrapper palletContainer = scanAssignSettings.palletContainer;
        ContainerWrapper parentContainer = palletContainer.getParentContainer();

        if (parentContainer == null) {
            throw new IllegalStateException("pallet's parent container is null");
        }

        // need to update container's product barcode
        ContainerSaveAction csAction = new ContainerSaveAction();
        csAction.setBarcode(scanAssignSettings.palletBarcode);
        csAction.setTypeId(palletContainer.getContainerType().getId());
        csAction.setActivityStatus(palletContainer.getActivityStatus());
        csAction.setSiteId(SessionManager.getUser().getCurrentWorkingSite().getId());

        if (palletContainer.getId() != null) {
            csAction.setId(palletContainer.getId());
        }

        if (palletContainer.getParentContainer() == null) {
            csAction.setLabel(palletContainer.getLabel());
        } else {
            csAction.setParentId(parentContainer.getId());
            csAction.setPosition(palletContainer.getPositionAsRowCol());
        }

        Integer containerId = SessionManager.getAppService().doAction(csAction).getId();

        SpecimenAssignResInfo res = SessionManager.getAppService().doAction(
            new SpecimenAssignSaveAction(containerId, specInfos));

        if (scanAssignSettings.isNewPalletContainer) {
            if (res.parentContainerId != null) {
                appendLog(MessageFormat.format(
                    "ADDED: Pallet {0} of type {1} to position {2} of site {3}",
                    res.parentBarcode, res.parentTypeName,
                    res.parentLabel, res.siteName));
            } else {
                throw new RuntimeException(
                    // TR: exception message
                    i18n.tr("problem with parent container creation"));
            }
        }
    }

    private enum ScanMode {
        NONE,
        LINK,
        ASSIGN
    }

    private static class ScanAssignSettings {

        public final String palletBarcode;

        public final String palletLabel;

        public final ContainerTypeWrapper palletType;

        public final ContainerWrapper palletContainer;

        public final boolean isNewPalletContainer;

        public ScanAssignSettings(
            String palletBarcode,
            String palletLabel,
            ContainerTypeWrapper palletType,
            ContainerWrapper palletContainer,
            boolean isNewPalletContainer) {
            this.palletBarcode = palletBarcode;
            this.palletLabel = palletLabel;
            this.palletType = palletType;
            this.palletContainer = palletContainer;
            this.isNewPalletContainer = isNewPalletContainer;
        }

        public static ScanAssignSettings getInitialValues() {
            return new ScanAssignSettings(
                StringUtil.EMPTY_STRING, StringUtil.EMPTY_STRING, null, null, false);
        }

    }

}
