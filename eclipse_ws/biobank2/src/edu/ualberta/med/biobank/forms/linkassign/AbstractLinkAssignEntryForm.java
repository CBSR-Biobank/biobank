package edu.ualberta.med.biobank.forms.linkassign;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
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

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectParentContainerDialog;
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletDisplay;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;

public abstract class AbstractLinkAssignEntryForm extends
    AbstractPalletSpecimenAdminForm {

    enum Mode {
        SINGLE_NO_POSITION, SINGLE_POSITION, MULTIPLE;

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

    @Override
    protected void init() throws Exception {
        super.init();
        singleSpecimen = new SpecimenWrapper(appService);
        canSaveSingleBinding = widgetCreator.addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            canSaveSingleSpecimen,
            "Cannot save specimen because of previous errors");
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

    protected void setFirstControl(@SuppressWarnings("unused") Mode mode) {

    }

    protected boolean showSinglePosition() {
        return false;
    }

    protected void defaultInitialisation() {

    }

    protected void setNeedSinglePosition(
        @SuppressWarnings("unused") boolean position) {

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
        radioSingle = toolkit.createButton(buttonsComposite, Messages
            .getString("AbstractLinkAssignEntryForm.choice.radio.single"), //$NON-NLS-1$
            SWT.RADIO);
        // used only for linking (but faster and easier to add it in this class)
        radioSinglePosition = toolkit
            .createButton(
                buttonsComposite,
                Messages
                    .getString("AbstractLinkAssignEntryForm.choice.radio.single.position"), //$NON-NLS-1$
                SWT.RADIO);
        radioMultiple = toolkit.createButton(buttonsComposite, Messages
            .getString("AbstractLinkAssignEntryForm.choice.radio.multiple"), //$NON-NLS-1$
            SWT.RADIO);

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
    }

    protected Composite getFocusedComposite(
        @SuppressWarnings("unused") boolean single) {
        return null;
    }

    protected abstract void createSingleFields(Composite parent);

    protected abstract void createMultipleFields(Composite parent)
        throws Exception;

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
        freezerLabel = toolkit.createLabel(freezerComposite, "Freezer"); //$NON-NLS-1$
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
        hotelLabel = toolkit.createLabel(hotelComposite, "Hotel"); //$NON-NLS-1$
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
        palletLabel = toolkit.createLabel(palletComposite, "Pallet"); //$NON-NLS-1$
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

        createScanTubeAloneButton(multipleVisualisation);
    }

    /**
     * Multiple assign
     */
    protected void manageDoubleClick(MouseEvent e) {
        if (isScanTubeAloneMode()) {
            scanTubeAlone(e);
        } else {
            PalletCell cell = (PalletCell) ((ScanPalletWidget) e.widget)
                .getObjectAtCoordinates(e.x, e.y);
            if (cell != null) {
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
    }

    protected void createSingleVisualisation(Composite parent) {
        singleVisualisation = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        singleVisualisation.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        singleVisualisation.setLayoutData(gd);

        thirdSingleParentLabel = toolkit.createLabel(singleVisualisation, ""); //$NON-NLS-1$
        secondSingleParentLabel = toolkit.createLabel(singleVisualisation, ""); //$NON-NLS-1$

        ContainerTypeWrapper thirdSingleParentType = null;
        ContainerTypeWrapper secondSingleParentType = null;
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
        fakeScanRandom = toolkit.createButton(comp, "Get random scan values", //$NON-NLS-1$
            SWT.RADIO);
        fakeScanRandom.setSelection(true);
        toolkit.createButton(comp,
            "Get random and already linked specimens", SWT.RADIO); //$NON-NLS-1$
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
    protected void onReset() throws Exception {
        super.onReset();
        singleSpecimen.initObjectWith(new SpecimenWrapper(appService));
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
     * Search possible parents from the position text. Is used both by single
     * and multiple assign.
     * 
     * @param positionText the position to use for initialisation
     * @param isContainerPosition if true, the position is a full container
     *            position, if false, it is a full specimen position
     */
    protected void initContainersFromPosition(BiobankText positionText,
        boolean isContainerPosition, ContainerTypeWrapper type) {
        parentContainers = null;
        try {
            parentContainers = null;
            List<ContainerWrapper> foundContainers = ContainerWrapper
                .getPossibleContainersFromPosition(appService,
                    SessionManager.getUser(), positionText.getText(),
                    isContainerPosition, type);
            if (foundContainers.size() == 1) {
                initParentContainers(foundContainers.get(0));
            } else if (foundContainers.size() > 1) {
                SelectParentContainerDialog dlg = new SelectParentContainerDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), foundContainers);
                dlg.open();
                if (dlg.getSelectedContainer() == null) {
                    StringBuffer sb = new StringBuffer();
                    for (ContainerWrapper cont : foundContainers) {
                        sb.append(cont.getFullInfoLabel());
                    }
                    BiobankGuiCommonPlugin
                        .openError(
                            Messages
                                .getString("SpecimenAssign.single.checkParent.error.toomany.title"), //$NON-NLS-1$
                            Messages
                                .getString(
                                    "SpecimenAssign.single.checkParent.error.toomany.msg", //$NON-NLS-1$
                                    sb.toString()));
                    focusControl(positionText);
                } else
                    initParentContainers(dlg.getSelectedContainer());
            }
        } catch (BiobankException be) {
            BiobankGuiCommonPlugin
                .openError(
                    Messages
                        .getString("SpecimenAssign.container.init.position.error.title"), //$NON-NLS-1$
                    be);
            appendLog(Messages.getString(
                "SpecimenAssign.single.activitylog.checkParent.error", //$NON-NLS-1$
                be.getMessage()));
            focusControl(positionText);
        } catch (Exception ex) {
            BiobankGuiCommonPlugin
                .openError(
                    Messages
                        .getString("SpecimenAssign.container.init.position.error.title"), //$NON-NLS-1$
                    ex);
            focusControl(positionText);
        }
    }

    /**
     * Initialise parents
     */
    private void initParentContainers(ContainerWrapper bottomContainer) {
        parentContainers = new ArrayList<ContainerWrapper>();
        ContainerWrapper parent = bottomContainer;
        while (parent != null) {
            parentContainers.add(parent);
            parent = parent.getParentContainer();
        }
        StringBuffer parentMsg = new StringBuffer();
        for (int i = parentContainers.size() - 1; i >= 0; i--) {
            parent = parentContainers.get(i);
            String label = parent.getPositionString();
            if (label == null)
                label = parent.getLabel();
            parentMsg.append(label);
            if (i != 0)
                parentMsg.append("|"); //$NON-NLS-1$
        }
        appendLog(Messages.getString(
            "SpecimenAssign.activitylog.containers.init", //$NON-NLS-1$
            parentMsg.toString()));
    }

    /**
     * Single assign. Check can really add to the position
     */
    protected void checkPositionAndSpecimen(final BiobankText inventoryIdField,
        final BiobankText positionField) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                try {
                    appendLog("----"); //$NON-NLS-1$
                    String positionString = positionField.getText();
                    if (parentContainers == null
                        || parentContainers.size() == 0) {
                        displaySinglePositions(false);
                        return;
                    }
                    appendLog(Messages
                        .getString(
                            "SpecimenAssign.single.activitylog.checkingPosition", positionString)); //$NON-NLS-1$
                    singleSpecimen.setSpecimenPositionFromString(
                        positionString, parentContainers.get(0));
                    if (singleSpecimen.isPositionFree(parentContainers.get(0))) {
                        singleSpecimen.setParent(parentContainers.get(0));
                        displaySinglePositions(true);
                        canSaveSingleSpecimen.setValue(true);
                        cancelConfirmWidget.setFocus();
                    } else {
                        BiobankGuiCommonPlugin.openError(
                            Messages
                                .getString("SpecimenAssign.single.position.error.msg"), //$NON-NLS-1$
                            Messages
                                .getString(
                                    "SpecimenAssign.single.checkStatus.error", positionString, //$NON-NLS-1$
                                    parentContainers.get(0).getLabel()));
                        appendLog(Messages
                            .getString(
                                "SpecimenAssign.single.activitylog.checkPosition.error", //$NON-NLS-1$
                                positionString, parentContainers.get(0)
                                    .getLabel()));
                        focusControl(positionField);
                        return;
                    }
                    setDirty(true);
                } catch (RemoteConnectFailureException exp) {
                    BiobankGuiCommonPlugin.openRemoteConnectErrorMessage(exp);
                } catch (BiobankCheckException bce) {
                    BiobankGuiCommonPlugin.openError(
                        "Error while checking position", bce); //$NON-NLS-1$
                    appendLog("ERROR: " + bce.getMessage()); //$NON-NLS-1$
                    focusControl(inventoryIdField);
                } catch (Exception e) {
                    BiobankGuiCommonPlugin.openError(
                        "Error while checking position", e); //$NON-NLS-1$
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
        if (isSingleMode)
            widgetCreator.addBinding(canSaveSingleBinding);
        else
            widgetCreator.removeBinding(canSaveSingleBinding);
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

}
