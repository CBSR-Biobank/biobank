package edu.ualberta.med.biobank.forms.linkassign;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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
import edu.ualberta.med.biobank.common.action.container.ContainerGetParentsByChildLabelAction;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectParentContainerDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletDisplay;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;

public abstract class AbstractLinkAssignEntryForm
extends AbstractPalletSpecimenAdminForm {
    private static final I18n i18n = I18nFactory
        .getI18n(AbstractLinkAssignEntryForm.class);

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
    private ScrolledComposite visualisationScroll;
    private Composite visualisationComposite;
    private Button fakeScanRandom;
    protected boolean isFakeScanRandom;
    private Composite multipleVisualisation;
    protected Label freezerLabel;
    protected Label hotelLabel;
    protected Label palletLabel;
    protected ContainerDisplayWidget freezerWidget;
    protected ContainerDisplayWidget hotelWidget;
    protected ScanPalletWidget palletWidget;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        super.init();
        singleSpecimen = new SpecimenWrapper(SessionManager.getAppService());
        canSaveSingleBinding = widgetCreator.addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            canSaveSingleSpecimen,
            // TR: validation error message
            i18n.tr("Please fill in the fields and hit enter or tab or resolve previous errors"));
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
        radioSingle = toolkit
            .createButton(buttonsComposite,
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
        if (focusComposite != null)
            focusComposite.setFocus();
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
        multipleVisualisation = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        multipleVisualisation.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        multipleVisualisation.setLayoutData(gd);

        Composite freezerComposite = toolkit
            .createComposite(multipleVisualisation);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        freezerComposite.setLayout(layout);
        GridData gdFreezer = new GridData();
        gdFreezer.horizontalSpan = 3;
        gdFreezer.horizontalAlignment = SWT.RIGHT;
        freezerComposite.setLayoutData(gdFreezer);
        freezerLabel = toolkit.createLabel(freezerComposite,
            // TR: label
            i18n.tr("Freezer"));
        freezerLabel.setLayoutData(new GridData());
        freezerWidget = new ContainerDisplayWidget(freezerComposite);
        freezerWidget.initDisplayFromType(true);
        toolkit.adapt(freezerWidget);
        freezerWidget.setDisplaySize(ScanPalletDisplay.PALLET_WIDTH, 100);

        Composite hotelComposite = toolkit
            .createComposite(multipleVisualisation);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        hotelComposite.setLayout(layout);
        hotelComposite.setLayoutData(new GridData());
        hotelLabel = toolkit.createLabel(hotelComposite,
            // TR: label
            i18n.tr("Hotel"));
        hotelWidget = new ContainerDisplayWidget(hotelComposite);
        hotelWidget.initDisplayFromType(true);
        toolkit.adapt(hotelWidget);
        hotelWidget.setDisplaySize(100,
            ScanPalletDisplay.PALLET_HEIGHT_AND_LEGEND);

        Composite palletComposite = toolkit
            .createComposite(multipleVisualisation);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        palletComposite.setLayout(layout);
        palletComposite.setLayoutData(new GridData());
        palletLabel = toolkit.createLabel(palletComposite,
            // TR: label
            i18n.tr("Pallet"));
        palletWidget = new ScanPalletWidget(palletComposite,
            UICellStatus.DEFAULT_PALLET_SCAN_ASSIGN_STATUS_LIST);
        toolkit.adapt(palletWidget);
        palletWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                manageDoubleClick(e);
            }
        });
        showOnlyPallet(true);
    }

    protected void recreateScanPalletWidget(int rows, int cols) {
        Composite palletComposite = palletWidget.getParent();
        palletWidget.dispose();
        palletWidget = new ScanPalletWidget(palletComposite,
            UICellStatus.DEFAULT_PALLET_SCAN_ASSIGN_STATUS_LIST, rows, cols);
        toolkit.adapt(palletWidget);
    }

    /**
     * Multiple assign
     */
    protected void manageDoubleClick(MouseEvent e) {
        PalletWell cell = (PalletWell) ((ScanPalletWidget) e.widget)
            .getObjectAtCoordinates(e.x, e.y);
        if (canScanTubesManually(cell)) {
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

    protected void createSingleVisualisation(Composite parent) {
        singleVisualisation = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        singleVisualisation.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        singleVisualisation.setLayoutData(gd);

        thirdSingleParentLabel =
            toolkit.createLabel(singleVisualisation, StringUtil.EMPTY_STRING);
        secondSingleParentLabel =
            toolkit.createLabel(singleVisualisation, StringUtil.EMPTY_STRING);

        ContainerType thirdSingleParentType = null;
        ContainerType secondSingleParentType = null;
        thirdSingleParentWidget = new ContainerDisplayWidget(
            singleVisualisation);
        thirdSingleParentWidget.setContainerType(thirdSingleParentType, true);
        toolkit.adapt(thirdSingleParentWidget);
        GridData gdDrawer = new GridData();
        gdDrawer.verticalAlignment = SWT.TOP;
        thirdSingleParentWidget.setLayoutData(gdDrawer);

        secondSingleParentWidget = new ContainerDisplayWidget(
            singleVisualisation);
        secondSingleParentWidget.setContainerType(secondSingleParentType, true);
        toolkit.adapt(secondSingleParentWidget);

        displaySinglePositions(false);
    }

    @SuppressWarnings("nls")
    @Override
    /**
     * when creating the scan button in debug mode, add options to create random values
     */
    protected void createFakeOptions(Composite fieldsComposite) {
        GridData gd;
        Composite comp = toolkit.createComposite(fieldsComposite);
        comp.setLayout(new GridLayout());
        gd = new GridData();
        gd.horizontalSpan = 3;
        comp.setLayoutData(gd);
        fakeScanRandom = toolkit.createButton(comp, "Get random scan values",
            SWT.RADIO);
        fakeScanRandom.setSelection(true);
        toolkit.createButton(comp,
            "Get random and already linked specimens", SWT.RADIO);
    }

    /**
     * Containers visualisation
     */
    private void createVisualisationSection(Composite parent) {
        visualisationScroll = new ScrolledComposite(parent, SWT.H_SCROLL);
        visualisationScroll.setExpandHorizontal(true);
        visualisationScroll.setExpandVertical(true);
        visualisationScroll.setLayout(new FillLayout());
        GridData scrollData = new GridData();
        scrollData.horizontalAlignment = SWT.FILL;
        scrollData.grabExcessHorizontalSpace = true;
        visualisationScroll.setLayoutData(scrollData);
        visualisationComposite = toolkit.createComposite(visualisationScroll);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginLeft = 20;
        visualisationComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        visualisationComposite.setLayoutData(gd);
        visualisationScroll.setContent(visualisationComposite);

        createMultipleVisualisation(visualisationComposite);
        createSingleVisualisation(visualisationComposite);
    }

    protected void showVisualisation(boolean show) {
        if (visualisationScroll != null) {
            widgetCreator.showWidget(visualisationScroll, show);
            visualisationScroll.setMinSize(visualisationComposite.computeSize(
                SWT.DEFAULT, SWT.DEFAULT));
        }
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
        singleSpecimen.initObjectWith(new SpecimenWrapper(SessionManager
            .getAppService()));
        singleSpecimen.reset();
        setDirty(false);
        reset(true);
        setFocus();
    }

    protected void reset(boolean resetAll) {
        cancelConfirmWidget.reset();
        removeRescanMode();
        setScanHasBeenLaunched(isSingleMode());
        if (resetAll) {
            resetPlateToScan();
        }
    }

    @Override
    /**
     * Multiple linking: do this before multiple scan is made
     */
    protected void beforeScanThreadStart() {
        isFakeScanRandom = fakeScanRandom != null
            && fakeScanRandom.getSelection();
    }

    @Override
    /**
     * Multiple linking: do this before scan of one tube is really made
     */
    protected void beforeScanTubeAlone() {
        isFakeScanRandom = fakeScanRandom != null
            && fakeScanRandom.getSelection();
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
                    boolean hasThirdParent = thirdParent != null;
                    widgetCreator.showWidget(thirdSingleParentWidget,
                        hasThirdParent);
                    widgetCreator.showWidget(thirdSingleParentLabel,
                        hasThirdParent);
                    if (hasThirdParent) {
                        thirdSingleParentWidget.setContainerType(thirdParent
                            .getContainerType());
                        thirdSingleParentWidget.setSelection(secondParent
                            .getPositionAsRowCol());
                        thirdSingleParentLabel.setText(thirdParent.getLabel());
                    }
                    boolean hasSecondParent = secondParent != null;
                    widgetCreator.showWidget(secondSingleParentWidget,
                        hasSecondParent);
                    widgetCreator.showWidget(secondSingleParentLabel,
                        hasSecondParent);
                    if (hasSecondParent) {
                        secondSingleParentWidget.setContainer(secondParent);
                        secondSingleParentWidget.setSelection(firstParent
                            .getPositionAsRowCol());
                        secondSingleParentLabel
                        .setText(secondParent.getLabel());
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
     * @param isContainerPosition if true, the position is a full container position, if false, it
     *            is a full specimen position
     */
    @SuppressWarnings({ "nls" })
    protected void initContainersFromPosition(BgcBaseText positionText,
        ContainerTypeWrapper type) {
        parentContainers = new ArrayList<ContainerWrapper>();
        try {
            List<Container> foundContainers;

            if (type == null) {
                foundContainers = SessionManager.getAppService().doAction(
                    new ContainerGetParentsByChildLabelAction(positionText.getText(),
                        SessionManager.getUser().getCurrentWorkingSite().getWrappedObject()))
                        .getList();
            } else {
                foundContainers = SessionManager.getAppService().doAction(
                    new ContainerGetParentsByChildLabelAction(positionText.getText(),
                        type.getWrappedObject()))
                        .getList();
            }
            if (foundContainers.isEmpty())
                BgcPlugin.openAsyncError(
                    i18n.tr("Unable to find a container with label ", positionText.getText()));
            else if (foundContainers.size() == 1) {
                parentContainers.add(new ContainerWrapper(SessionManager
                    .getAppService(), foundContainers.get(0)));
            } else {
                SelectParentContainerDialog dlg = new SelectParentContainerDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow() .getShell(),
                    foundContainers);
                dlg.open();
                if (dlg.getSelectedContainer() == null) {
                    StringBuffer sb = new StringBuffer();
                    for (Container cont : foundContainers) {
                        sb.append(ContainerWrapper.getFullInfoLabel(cont));
                    }
                    BgcPlugin.openError(
                        // TR: dialog title
                        i18n.tr("Container problem"), //$NON-NLS-1$
                        // TR: dialog message
                        i18n.tr("More than one container found matching {0}", //$NON-NLS-1$
                            sb.toString()));
                    focusControl(positionText);
                } else {
                    parentContainers.add(new ContainerWrapper(
                        SessionManager.getAppService(), dlg
                        .getSelectedContainer()));
                }
            }
            updateAvailableSpecimenTypes();
        } catch (Exception ex) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Init container from position"), ex); //$NON-NLS-1$
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
                    if (parentContainers == null
                        || parentContainers.size() == 0) {
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
                        appendLog(NLS
                            .bind(
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
                            i18n.tr(
                                "Container {0} cannot hold specimens of type \"{1}\"",
                                positionString,
                                singleSpecimen.getSpecimenType()));
                        appendLog(NLS
                            .bind(
                                "ERROR: Container {0} cannot hold specimens of type \"{1}\"",
                                positionString,
                                singleSpecimen.getSpecimenType()));
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
                            i18n.tr(
                                "Position {0} already in use in container {1}",
                                positionString, parentContainers.get(0)
                                .getLabel()));
                        appendLog(NLS
                            .bind(
                                "ERROR: Position {0} already in use in container {1}",
                                positionString, parentContainers.get(0)
                                .getLabel()));
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
        if (isSingleMode)
            widgetCreator.addBinding(canSaveSingleBinding);
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

}
