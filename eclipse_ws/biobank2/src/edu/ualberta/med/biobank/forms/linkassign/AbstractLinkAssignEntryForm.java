package edu.ualberta.med.biobank.forms.linkassign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction.ContainerData;
import edu.ualberta.med.biobank.common.action.containerType.SpecimenContainerTypesByCapacityAction;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectParentContainerDialog;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.PalletDisplay;
import edu.ualberta.med.biobank.widgets.grids.PalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Allows the user to perform a specimen link, which means that specimens are linked with patients.
 * 
 * @author Delphine
 * 
 */
public abstract class AbstractLinkAssignEntryForm extends AbstractPalletSpecimenAdminForm {

    private static final I18n i18n = I18nFactory.getI18n(AbstractLinkAssignEntryForm.class);

    enum Mode {
        SINGLE_NO_POSITION,
        SINGLE_POSITION,
        MULTIPLE;

        public boolean isSingleMode() {
            return this == SINGLE_NO_POSITION || this == SINGLE_POSITION;
        }
    }

    // composite containing common fields to single and multiple
    private Composite commonFieldsComposite;

    private Button radioSingle;
    private Button radioSinglePosition;
    private Button radioMultiple;

    // parents of either the specimen in single mode or the pallet/box in
    // multiple mode. First container, is the direct parent, second is the
    // parent parent, etc...
    protected List<ContainerWrapper> parentContainers;

    // the container that matches the label entered by the user
    protected ContainerWrapper container;

    // Single
    private Composite singleFieldsComposite;
    // when work only with one specimen
    protected SpecimenWrapper singleSpecimen;
    private Composite singleVisualisation;
    private Label thirdSingleParentLabel;
    private ContainerDisplayWidget thirdSingleParentWidget;
    private Label secondSingleParentLabel;
    private ContainerDisplayWidget secondSingleParentWidget;
    protected IObservableValue canSaveSingleSpecimen = new WritableValue(
        Boolean.TRUE, Boolean.class);
    private Binding canSaveSingleBinding;

    // Multiple
    private Composite multipleFieldsComposite;
    private Composite visualisationComposite;
    private Composite multipleVisualisation;
    protected Label freezerLabel;
    protected Label hotelLabel;
    protected Label palletLabel;
    protected ContainerDisplayWidget freezerWidget;
    protected ContainerDisplayWidget hotelWidget;
    protected PalletWidget palletWidget;

    protected RowColPos currentGridDimensions =
        new RowColPos(RowColPos.ROWS_DEFAULT, RowColPos.COLS_DEFAULT);

    // set to true when in scan link multiple and user enters barcodes using the
    // handheld scanner
    protected boolean scanMultipleWithHandheldInput = false;

    private final Set<ContainerType> palletContainerTypes;

    public AbstractLinkAssignEntryForm() {
        palletContainerTypes = new HashSet<ContainerType>();
    }

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        super.init();
        singleSpecimen = new SpecimenWrapper(SessionManager.getAppService());

        // creates the binding, and enables it by default. However, it should not be enabled
        // until it is needed.
        canSaveSingleBinding = widgetCreator.addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            canSaveSingleSpecimen,
            // TR: validation error message
            i18n.tr("Please fill in the fields and hit enter or tab or resolve previous errors"));
        widgetCreator.removeBinding(canSaveSingleBinding);
    }

    protected abstract String getFormTitle();

    protected abstract boolean isSingleMode();

    protected abstract void setMode(Mode mode);

    @Override
    protected void createFormContent() throws Exception {
        form.setText(getFormTitle());
        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        page.setLayout(gl);

        Composite mainComposite = new Composite(page, SWT.NONE);
        gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        mainComposite.setLayout(gl);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.TOP;
        mainComposite.setLayoutData(gd);

        createLeftSection(mainComposite);

        createVisualisationSection(mainComposite);

        toolkit.adapt(mainComposite);

        Mode mode = initialisationMode();
        setFirstControl(mode);
        radioSingle.setSelection(mode == Mode.SINGLE_NO_POSITION);
        radioSinglePosition.setSelection(mode == Mode.SINGLE_POSITION);
        radioMultiple.setSelection(mode == Mode.MULTIPLE);
        showModeComposite(mode);
        defaultInitialisation();
        widgetCreator.showWidget(radioSinglePosition, showSinglePosition());
        showOnlyPallet(true);
        form.layout(true, true);
    }

    @SuppressWarnings("unused")
    protected void setFirstControl(Mode mode) {
        // default does nothing
    }

    protected boolean showSinglePosition() {
        return false;
    }

    protected void defaultInitialisation() {
        // default does nothing
    }

    @SuppressWarnings("unused")
    protected void setNeedSinglePosition(boolean position) {
        // default does nothing
    }

    protected abstract Mode initialisationMode();

    private void createLeftSection(Composite parent) throws Exception {
        Composite leftComposite = toolkit.createComposite(parent);
        GridLayout gl = new GridLayout(1, false);
        leftComposite.setLayout(gl);
        toolkit.paintBordersFor(leftComposite);
        GridData gd = new GridData();
        gd.widthHint = getLeftSectionWidth();
        gd.verticalAlignment = SWT.TOP;
        leftComposite.setLayoutData(gd);

        commonFieldsComposite = toolkit.createComposite(leftComposite);
        gl = new GridLayout(3, false);
        gl.horizontalSpacing = 10;
        commonFieldsComposite.setLayout(gl);
        gd = new GridData();
        gd.widthHint = getLeftSectionWidth();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commonFieldsComposite.setLayoutData(gd);
        toolkit.paintBordersFor(commonFieldsComposite);

        createCommonFields(commonFieldsComposite);
        createSingleMultipleSection(leftComposite);
        createCancelConfirmWidget(leftComposite);
    }

    protected int getLeftSectionWidth() {
        return 520;
    }

    protected abstract void createCommonFields(Composite commonFieldsComposite);

    @SuppressWarnings("nls")
    private void createSingleMultipleSection(Composite leftComposite)
        throws Exception {
        Composite singleMultipleComposite = toolkit
            .createComposite(leftComposite);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        layout.marginWidth = 0;
        singleMultipleComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        singleMultipleComposite.setLayoutData(gd);
        toolkit.paintBordersFor(singleMultipleComposite);

        Composite buttonsComposite = toolkit
            .createComposite(singleMultipleComposite);
        layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        buttonsComposite.setLayout(layout);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 2;
        buttonsComposite.setLayoutData(gd);
        toolkit.paintBordersFor(buttonsComposite);
        // radio button to choose single or multiple
        radioSingle = toolkit.createButton(buttonsComposite,
            // TR: radio button label
            i18n.trc("scan link assign", "Single"),
            SWT.RADIO);

        // used only for linking (but faster and easier to add it in this class)
        radioSinglePosition = toolkit.createButton(buttonsComposite,
            // TR: radio button label
            i18n.trc("scan link assign", "Single with position"), SWT.RADIO);
        radioMultiple = toolkit.createButton(buttonsComposite,
            // TR: radio button label
            i18n.trc("scan link assign", "Multiple"), SWT.RADIO);

        singleFieldsComposite = toolkit.createComposite(leftComposite);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        singleFieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(singleFieldsComposite);
        gd = new GridData();
        gd.widthHint = getLeftSectionWidth();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        singleFieldsComposite.setLayoutData(gd);
        createSingleFields(singleFieldsComposite);

        multipleFieldsComposite = toolkit.createComposite(leftComposite);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        layout.marginWidth = 0;
        multipleFieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(multipleFieldsComposite);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleFieldsComposite.setLayoutData(gd);
        createMultipleFields(multipleFieldsComposite);

        radioSingle.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioSingle.getSelection()) {
                    showModeComposite(Mode.SINGLE_NO_POSITION);
                }
            }

        });
        radioSinglePosition.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioSinglePosition.getSelection()) {
                    showModeComposite(Mode.SINGLE_POSITION);
                }
            }

        });
        radioMultiple.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioMultiple.getSelection()) {
                    showModeComposite(Mode.MULTIPLE);
                }
            }
        });
    }

    /**
     * Show either single or multiple selection fields
     */
    protected void showModeComposite(Mode mode) {
        setMode(mode);
        boolean single = mode.isSingleMode();
        widgetCreator.showWidget(singleFieldsComposite, single);
        widgetCreator.showWidget(singleVisualisation, single);
        widgetCreator.showWidget(multipleFieldsComposite, !single);
        widgetCreator.showWidget(multipleVisualisation, !single);
        setNeedSinglePosition(mode == Mode.SINGLE_POSITION);
        setBindings(single);
        showVisualisation(!single);
        Composite focusComposite = getFocusedComposite(single);
        if (focusComposite != null) {
            focusComposite.setFocus();
        }
        page.layout(true, true);
        book.reflow(true);
    }

    @SuppressWarnings("unused")
    protected Composite getFocusedComposite(boolean single) {
        return null;
    }

    protected abstract void createSingleFields(Composite parent);

    protected abstract void createMultipleFields(Composite parent)
        throws Exception;

    @SuppressWarnings("nls")
    protected void createMultipleVisualisation(Composite parent) {
        multipleVisualisation = toolkit.createComposite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        multipleVisualisation.setLayout(layout);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
        multipleVisualisation.setLayoutData(gd);

        Composite freezerComposite = toolkit.createComposite(multipleVisualisation, SWT.NONE);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        freezerComposite.setLayout(layout);

        gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gd.horizontalSpan = 2;
        freezerComposite.setLayoutData(gd);

        freezerLabel = toolkit.createLabel(freezerComposite,
            // TR: label
            i18n.tr("Freezer"));
        freezerLabel.setLayoutData(new GridData());
        freezerWidget = new ContainerDisplayWidget(freezerComposite, "freezerWidget");
        toolkit.adapt(freezerWidget);
        freezerWidget.setDisplaySize(PalletDisplay.PALLET_WIDTH, 100);

        Composite hotelComposite = toolkit.createComposite(multipleVisualisation, SWT.NONE);
        layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        hotelComposite.setLayout(layout);

        gd = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        hotelComposite.setLayoutData(gd);

        hotelLabel = toolkit.createLabel(hotelComposite,
            // TR: label
            i18n.tr("Hotel"));
        hotelWidget = new ContainerDisplayWidget(hotelComposite, "hotelWidget");
        toolkit.adapt(hotelWidget);
        hotelWidget.setDisplaySize(100, PalletDisplay.PALLET_HEIGHT_AND_LEGEND);

        Composite palletComposite = toolkit.createComposite(multipleVisualisation, SWT.NONE);
        layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        palletComposite.setLayout(layout);

        gd = new GridData(SWT.BEGINNING, SWT.FILL, true, true);
        palletComposite.setLayoutData(gd);

        palletLabel = toolkit.createLabel(palletComposite,
            // TR: label
            i18n.tr("Pallet"));
        palletWidget = createScanPalletWidget(
            palletComposite,
            currentGridDimensions.getRow(),
            currentGridDimensions.getCol());
        showOnlyPallet(true);
    }

    protected PalletWidget createScanPalletWidget(Composite palletComposite, int rows, int cols) {
        PalletWidget palletWidget = new PalletWidget(
            palletComposite,
            UICellStatus.DEFAULT_PALLET_SCAN_ASSIGN_STATUS_LIST,
            rows,
            cols,
            true);

        toolkit.adapt(palletWidget);
        palletWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                manageDoubleClick(e);
            }
        });

        showOnlyPallet(true);
        return palletWidget;
    }

    protected void recreateScanPalletWidget(int rows, int cols) {
        Composite palletComposite = palletWidget.getParent();
        palletWidget.dispose();
        palletWidget = createScanPalletWidget(palletComposite, rows, cols);
    }

    /**
     * Multiple assign
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

    @SuppressWarnings("nls")
    protected void createSingleVisualisation(Composite parent) {
        singleVisualisation = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        singleVisualisation.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.grabExcessHorizontalSpace = true;
        singleVisualisation.setLayoutData(gd);

        thirdSingleParentLabel =
            toolkit.createLabel(singleVisualisation, StringUtil.EMPTY_STRING);
        secondSingleParentLabel =
            toolkit.createLabel(singleVisualisation, StringUtil.EMPTY_STRING);

        ContainerType thirdSingleParentType = null;
        ContainerType secondSingleParentType = null;
        thirdSingleParentWidget = new ContainerDisplayWidget(
            singleVisualisation,
            null,
            "thirdSingleParentWidget",
            null,
            thirdSingleParentType,
            true);
        toolkit.adapt(thirdSingleParentWidget);
        GridData gdDrawer = new GridData();
        gdDrawer.verticalAlignment = SWT.TOP;
        thirdSingleParentWidget.setLayoutData(gdDrawer);

        secondSingleParentWidget = new ContainerDisplayWidget(
            singleVisualisation,
            null,
            "secondSingleParentWidget",
            null,
            secondSingleParentType,
            true);
        toolkit.adapt(secondSingleParentWidget);

        displaySinglePositions(false);
    }

    /**
     * Containers visualisation
     */
    private void createVisualisationSection(Composite parent) {
        visualisationComposite = toolkit.createComposite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        visualisationComposite.setLayout(layout);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        visualisationComposite.setLayoutData(gd);

        createMultipleVisualisation(visualisationComposite);
        createSingleVisualisation(visualisationComposite);
    }

    @SuppressWarnings("nls")
    protected void showVisualisation(boolean show) {
        if (visualisationComposite == null) {
            throw new IllegalStateException("visualisationComposite is null");
        }

        widgetCreator.showWidget(visualisationComposite, show);
    }

    @Override
    protected void enableFields(boolean enable) {
        commonFieldsComposite.setEnabled(enable);
        radioSingle.setEnabled(enable);
        radioSinglePosition.setEnabled(enable);
        radioMultiple.setEnabled(enable);
    }

    @Override
    public void setValues() throws Exception {
        super.setValues();
        singleSpecimen.initObjectWith(new SpecimenWrapper(SessionManager.getAppService()));
        singleSpecimen.reset();
        setDirty(false);
        reset(true);
        setFocus();
    }

    /**
     * @param resetAll When true visual widgets are also reset.
     */
    protected void reset(boolean resetAll) {
        container = null;
        cancelConfirmWidget.reset();
        setScanHasBeenLaunched(isSingleMode());
        currentGridDimensions = new RowColPos(RowColPos.ROWS_DEFAULT, RowColPos.COLS_DEFAULT);
    }

    /**
     * single assign. Display containers
     */
    protected void displaySinglePositions(boolean show) {
        if (isSingleMode()) {
            if (secondSingleParentWidget != null) {
                widgetCreator.showWidget(secondSingleParentWidget, show);
                widgetCreator.showWidget(secondSingleParentLabel, show);
            }
            if (thirdSingleParentWidget != null) {
                widgetCreator.showWidget(thirdSingleParentLabel, show);
                widgetCreator.showWidget(thirdSingleParentWidget, show);
            }
            if (show) {
                if (parentContainers != null) {
                    ContainerWrapper thirdParent = null;
                    ContainerWrapper secondParent = null;
                    ContainerWrapper firstParent = null;

                    if (parentContainers.size() >= 3)
                        thirdParent = parentContainers.get(2);
                    if (parentContainers.size() >= 2)
                        secondParent = parentContainers.get(1);
                    if (parentContainers.size() >= 1)
                        firstParent = parentContainers.get(0);
                    boolean hasThirdParent = (thirdParent != null);

                    if (hasThirdParent) {
                        Capacity capacity = thirdParent.getContainerType().getWrappedObject().getCapacity();
                        thirdSingleParentWidget.setStorageSize(capacity.getRowCapacity(), capacity.getColCapacity());
                        thirdSingleParentWidget.setSelection(secondParent.getPositionAsRowCol());
                        thirdSingleParentLabel.setText(thirdParent.getLabel());
                    }

                    widgetCreator.showWidget(thirdSingleParentWidget, hasThirdParent);
                    widgetCreator.showWidget(thirdSingleParentLabel, hasThirdParent);

                    boolean hasSecondParent = secondParent != null;
                    widgetCreator.showWidget(secondSingleParentWidget, hasSecondParent);
                    widgetCreator.showWidget(secondSingleParentLabel, hasSecondParent);
                    if (hasSecondParent) {
                        Capacity capacity = secondParent.getContainerType().getWrappedObject().getCapacity();
                        secondSingleParentWidget.setStorageSize(capacity.getRowCapacity(), capacity.getColCapacity());
                        secondSingleParentWidget.setSelection(firstParent.getPositionAsRowCol());
                        secondSingleParentLabel.setText(secondParent.getLabel());
                    }
                }
            }
            showVisualisation(show);
            page.layout(true, true);
            book.reflow(true);
        }
    }

    /**
     * Search possible parents from the position text. Is used both by single and multiple assign.
     * 
     * @param positionText the position to use for initialisation
     * @param type
     */
    @SuppressWarnings("nls")
    protected void initContainersFromPosition(BgcBaseText positionText, ContainerTypeWrapper type) {
        parentContainers = new ArrayList<ContainerWrapper>();
        try {
            Site site = SessionManager.getUser().getCurrentWorkingSite().getWrappedObject();

            ContainerType rawType = null;
            if (type != null) {
                rawType = type.getWrappedObject();
            }

            container = null;
            ContainerData containerData = SessionManager.getAppService().doAction(
                new ContainerGetContainerOrParentsByLabelAction(positionText.getText(),
                    site, rawType));

            List<Container> possibleParents = containerData.getPossibleParentContainers();

            if (containerData.getContainer() != null) {
                container = new ContainerWrapper(SessionManager.getAppService(),
                    containerData.getContainer());
            } else if (possibleParents.isEmpty()) {
                BgcPlugin.openAsyncError(
                    // TR: dialog title
                    i18n.tr("Container label error"),
                    // TR: dialog message
                    i18n.tr("Unable to find a container with label {0}", positionText.getText()));
            } else if (possibleParents.size() == 1) {
                Container parent = possibleParents.get(0);
                parentContainers.add(new ContainerWrapper(SessionManager.getAppService(),
                    parent));
                appendLog(i18n.tr("Parent container: {0}", parent.getLabel()));
            } else {
                SelectParentContainerDialog dlg = new SelectParentContainerDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), possibleParents);
                dlg.open();
                if (dlg.getSelectedContainer() == null) {
                    Set<String> labelData = new HashSet<String>();
                    for (Container cont : possibleParents) {
                        labelData.add(ContainerWrapper.getFullInfoLabel(cont));
                    }
                    BgcPlugin.openError(
                        // TR: dialog title
                        i18n.tr("Container problem"),
                        // TR: dialog message
                        i18n.tr("More than one container found matching {0}", StringUtils.join(labelData, ", ")));
                    focusControl(positionText);
                } else {
                    parentContainers.add(new ContainerWrapper(SessionManager.getAppService(),
                        dlg.getSelectedContainer()));
                }
            }
            updateAvailableSpecimenTypes();
        } catch (Exception ex) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Init container from position"), ex);
            focusControl(positionText);
        }
    }

    protected abstract void updateAvailableSpecimenTypes();

    /**
     * Single assign. Check can really add to the position
     */
    protected void checkPositionAndSpecimen(final BgcBaseText inventoryIdField,
        final BgcBaseText positionField) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @SuppressWarnings("nls")
            @Override
            public void run() {
                try {
                    appendLog("----");
                    String positionString = positionField.getText();
                    if ((parentContainers == null) || parentContainers.isEmpty()) {
                        displaySinglePositions(false);
                        return;
                    }

                    appendLog(NLS.bind("Checking position {0}", positionString));
                    ContainerWrapper container = parentContainers.get(0);
                    RowColPos position = container.getContainerType()
                        .getRowColFromPositionString(
                            positionString.replace(container.getLabel(),
                                StringUtil.EMPTY_STRING));

                    if (position == null) {
                        BgcPlugin.openError(
                            // TR: dialog title
                            i18n.tr("Position error"),
                            // TR: dialog message
                            i18n.tr(
                                "Position {0} is invalid: no specimen position found that matches this label",
                                positionString));
                        appendLog(NLS.bind(
                            "ERROR: Position {0} is invalid: no specimen position found that matches this label",
                            positionString));
                        return;
                    }

                    List<SpecimenTypeWrapper> specimenTypeCollection =
                        container.getContainerType().getSpecimenTypeCollection();

                    if (specimenTypeCollection.isEmpty()) {
                        BgcPlugin.openError(
                            // TR: dialog title
                            i18n.tr("Container error"),
                            // TR: dialog message
                            i18n.tr("Container cannot hold specimens: {0}",
                                positionString));
                        appendLog(NLS.bind(
                            "ERROR: Container cannot hold specimens: {0}",
                            positionString));
                        focusControl(positionField);
                        return;
                    } else if ((singleSpecimen.getSpecimenType() != null)
                        && !specimenTypeCollection.contains(singleSpecimen.getSpecimenType())) {
                        BgcPlugin.openError(
                            // TR: dialog title
                            i18n.tr("Container error"),
                            // TR: dialog message
                            i18n.tr("Container {0} cannot hold specimens of type \"{1}\"",
                                positionString, singleSpecimen.getSpecimenType()));
                        appendLog(NLS.bind(
                            "ERROR: Container {0} cannot hold specimens of type \"{1}\"",
                            positionString, singleSpecimen.getSpecimenType()));
                        focusControl(positionField);
                        return;
                    }

                    if (container.isPositionFree(position)) {
                        singleSpecimen.setParent(container, position);
                        displaySinglePositions(true);
                        canSaveSingleSpecimen.setValue(true);
                        cancelConfirmWidget.setFocus();
                    } else {
                        BgcPlugin.openError(
                            // TR: dialog title
                            i18n.tr("Position not free"),
                            // TR: dialog message
                            i18n.tr("Position {0} already in use in container {1}",
                                positionString, parentContainers.get(0).getLabel()));
                        appendLog(NLS.bind(
                            "ERROR: Position {0} already in use in container {1}",
                            positionString, parentContainers.get(0).getLabel()));
                        focusControl(positionField);
                        return;
                    }
                    setDirty(true);
                } catch (RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                } catch (BiobankCheckException bce) {
                    BgcPlugin.openError(
                        // TR: dialog title
                        i18n.tr("Error while checking position"),
                        bce);
                    appendLog("ERROR: "
                        + bce.getMessage());
                    focusControl(inventoryIdField);
                } catch (Exception e) {
                    BgcPlugin.openError(
                        // TR: dialog title
                        i18n.tr("Error while checking position"),
                        e);
                    focusControl(positionField);
                }
            }
        });
    }

    /**
     * assign multiple
     */
    protected void showOnlyPallet(boolean onlyPallet) {
        widgetCreator.showWidget(freezerLabel, !onlyPallet);
        widgetCreator.showWidget(freezerWidget, !onlyPallet);
        widgetCreator.showWidget(hotelLabel, !onlyPallet);
        widgetCreator.showWidget(hotelWidget, !onlyPallet);
        page.layout(true, true);
    }

    @Override
    protected void setBindings(boolean isSingleMode) {
        super.setBindings(isSingleMode);
        widgetCreator.removeBinding(canSaveSingleBinding);
        if (isSingleMode) {
            widgetCreator.addBinding(canSaveSingleBinding);
        }
        canSaveSingleSpecimen.setValue(true);
    }

    /**
     * assign multiple
     */
    protected void showOnlyPallet(final boolean show, boolean async) {
        if (async) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    showOnlyPallet(show);
                }
            });
        } else {
            showOnlyPallet(show);
        }
    }

    @Override
    protected void refreshPalletDisplay() {
        palletWidget.redraw();
    }

    protected void initPalletContainerTypes() throws ApplicationException {
        palletContainerTypes.addAll(getPalletContainerTypes());
    }

    @SuppressWarnings("nls")
    protected void checkPalletContainerTypes() {
        if (!isSingleMode() && palletContainerTypes.isEmpty()) {
            throw new IllegalStateException("no pallets defined at this site");
        }
    }

    protected boolean isPalletContainerTypesInvalid() {
        return palletContainerTypes.isEmpty();
    }

    /**
     * Returns the leaf container types, ones that only hold specimens, that match the plate
     * dimensions define in {@link PalletDimensions}.
     * 
     * @note A site may not have any container types defined that match any elements of
     *       PlateDimensions.
     */
    public static Set<ContainerType> getPalletContainerTypes() throws ApplicationException {
        Site site = SessionManager.getUser().getCurrentWorkingSite().getWrappedObject();

        Set<Capacity> capacities = new HashSet<Capacity>();
        for (PalletDimensions plateDimensions : PalletDimensions.values()) {
            Capacity capacity = new Capacity();
            capacity.setRowCapacity(plateDimensions.getRows());
            capacity.setColCapacity(plateDimensions.getCols());
            capacities.add(capacity);
        }

        List<ContainerType> ctypesList = SessionManager.getAppService().doAction(
            new SpecimenContainerTypesByCapacityAction(site, capacities)).getList();
        return new HashSet<ContainerType>(ctypesList);
    }

    public PalletDimensions getCurrentPlateDimensions() {
        ContainerType containerType = getContainerType();
        return PalletScanManagement.capacityToPlateDimensions(containerType.getCapacity());

    }
}
