package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.scanprocess.SpecimenHierarchy;
import edu.ualberta.med.biobank.common.scanprocess.data.LinkProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.LinkFormPatientManagement.CEventComboCallback;
import edu.ualberta.med.biobank.forms.LinkFormPatientManagement.PatientTextCallback;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.AliquotedSpecimenSelectionWidget;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionListener;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class GenericLinkEntryForm extends AbstractPalletSpecimenAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.GenericLinkEntryForm"; //$NON-NLS-1$

    private static final String INVENTORY_ID_BINDING = "inventoryId-binding";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(GenericLinkEntryForm.class.getName());

    private LinkFormPatientManagement linkFormPatientManagement;

    private Composite singleLinkComposite;

    private Composite multipleLinkComposite;

    private boolean singleMode;

    // list of source specimen / type widget for multiple linking
    private List<AliquotedSpecimenSelectionWidget> specimenTypesWidgets;

    private ScanPalletWidget palletWidget;

    // when link only one specimen
    private SpecimenWrapper singleSpecimen;

    // source specimen / type relation when only one specimen
    private AliquotedSpecimenSelectionWidget singleTypesWidget;

    private ScrolledComposite multipleContainerDrawingScroll;

    @Override
    protected void init() throws Exception {
        super.init();
        setPartName(Messages.getString("GenericLinkEntryForm.tab.title")); //$NON-NLS-1$
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);
        singleSpecimen = new SpecimenWrapper(appService);
    }

    @Override
    protected String getActivityTitle() {
        return Messages.getString("GenericLinkEntryForm.activity.title"); //$NON-NLS-1$
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected String getOkMessage() {
        return Messages.getString("GenericLinkEntryForm.description.ok"); //$NON-NLS-1$
    }

    @Override
    public String getNextOpenedFormID() {
        // FIXME if checkbox to open assign form, should be assign form instead
        return ID;
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("GenericLinkEntryForm.form.title")); //$NON-NLS-1$
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

        createPalletSection(mainComposite);

        createCancelConfirmWidget();

        toolkit.adapt(mainComposite);
    }

    private void createLeftSection(Composite parent) {
        Composite leftComposite = toolkit.createComposite(parent);
        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        leftComposite.setLayout(gl);
        toolkit.paintBordersFor(leftComposite);
        GridData gd = new GridData();
        // TODO fix width ? Can be smaller ?
        gd.widthHint = 600;
        gd.verticalAlignment = SWT.TOP;
        leftComposite.setLayoutData(gd);

        Composite commonFieldsComposite = toolkit
            .createComposite(leftComposite);
        gl = new GridLayout(2, false);
        gl.horizontalSpacing = 10;
        commonFieldsComposite.setLayout(gl);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commonFieldsComposite.setLayoutData(gd);
        toolkit.paintBordersFor(commonFieldsComposite);

        // Patient number
        linkFormPatientManagement
            .createPatientNumberText(commonFieldsComposite);
        linkFormPatientManagement
            .setPatientTextCallback(new PatientTextCallback() {
                @Override
                public void focusLost() {
                    setTypeCombos();
                }

                @Override
                public void textModified() {
                }
            });
        // Collection events list
        linkFormPatientManagement.createEventsWidgets(commonFieldsComposite);
        linkFormPatientManagement
            .setCEventComboCallback(new CEventComboCallback() {
                @Override
                public void selectionChanged() {
                    setTypeCombos();
                }
            });

        createLinkingSection(leftComposite);
    }

    private void createLinkingSection(Composite leftComposite) {
        Composite linkComposite = toolkit.createComposite(leftComposite);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        linkComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        linkComposite.setLayoutData(gd);
        toolkit.paintBordersFor(linkComposite);

        // radio button to choose single or multiple
        final Button radioSingle = toolkit.createButton(linkComposite,
            Messages.getString("GenericLinkEntryForm.choice.radio.single"), //$NON-NLS-1$
            SWT.RADIO);
        final Button radioMultiple = toolkit.createButton(linkComposite,
            Messages.getString("GenericLinkEntryForm.choice.radio.multiple"), //$NON-NLS-1$
            SWT.RADIO);

        // stack for single or multiple
        final Composite stackComposite = toolkit.createComposite(linkComposite);
        final StackLayout stackLayout = new StackLayout();
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        stackComposite.setLayoutData(gd);
        stackComposite.setLayout(stackLayout);

        createSingleLinkComposite(stackComposite);
        createMultipleLink(stackComposite);

        radioSingle.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioSingle.getSelection()) {
                    setStackTopComposite(stackLayout, true);
                }
            }

        });
        radioMultiple.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioMultiple.getSelection()) {
                    setStackTopComposite(stackLayout, false);
                }
            }
        });

        radioSingle.setSelection(true);
        setStackTopComposite(stackLayout, true);
    }

    private void setStackTopComposite(final StackLayout stackLayout,
        boolean single) {
        singleMode = single;
        if (single) {
            stackLayout.topControl = singleLinkComposite;
        } else {
            stackLayout.topControl = multipleLinkComposite;
        }
        setBindings(single);
        if (multipleContainerDrawingScroll != null)
            widgetCreator.showWidget(multipleContainerDrawingScroll, !single);
        page.layout(true, true);
    }

    private void createMultipleLink(Composite parent) {
        multipleLinkComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 10;
        layout.marginWidth = 0;
        multipleLinkComposite.setLayout(layout);
        toolkit.paintBordersFor(multipleLinkComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleLinkComposite.setLayoutData(gd);

        Composite additionalFields = toolkit
            .createComposite(multipleLinkComposite);
        layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        layout.marginWidth = 0;
        additionalFields.setLayout(layout);
        toolkit.paintBordersFor(additionalFields);
        gd = new GridData();
        gd.horizontalSpan = 4;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        additionalFields.setLayoutData(gd);
        createPlateToScanField(additionalFields);

        toolkit.createLabel(multipleLinkComposite, ""); //$NON-NLS-1$
        toolkit.createLabel(multipleLinkComposite,
            Messages.getString("GenericLinkEntryForm.source.column.title")); //$NON-NLS-1$
        toolkit.createLabel(multipleLinkComposite,
            Messages.getString("GenericLinkEntryForm.result.column.title")); //$NON-NLS-1$
        toolkit.createLabel(multipleLinkComposite, ""); //$NON-NLS-1$

        specimenTypesWidgets = new ArrayList<AliquotedSpecimenSelectionWidget>();
        AliquotedSpecimenSelectionWidget precedent = null;
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            final AliquotedSpecimenSelectionWidget typeWidget = new AliquotedSpecimenSelectionWidget(
                multipleLinkComposite,
                ContainerLabelingSchemeWrapper.SBS_ROW_LABELLING_PATTERN
                    .charAt(i), widgetCreator, true);
            final int indexRow = i;
            typeWidget
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        updateRowType(typeWidget, indexRow);
                        if (palletWidget.isEverythingTyped()) {
                            setDirty(true);
                        }
                    }

                });
            typeWidget.addBindings();
            specimenTypesWidgets.add(typeWidget);
            if (precedent != null) {
                precedent.setNextWidget(typeWidget);
            }
            precedent = typeWidget;
            typeWidget.setEnabled(true);
        }
    }

    /**
     * Pallet visualisation
     */
    private void createPalletSection(Composite parent) {
        multipleContainerDrawingScroll = new ScrolledComposite(parent,
            SWT.H_SCROLL);
        multipleContainerDrawingScroll.setExpandHorizontal(true);
        multipleContainerDrawingScroll.setExpandVertical(true);
        multipleContainerDrawingScroll.setLayout(new FillLayout());
        GridData scrollData = new GridData();
        scrollData.horizontalAlignment = SWT.FILL;
        scrollData.grabExcessHorizontalSpace = true;
        multipleContainerDrawingScroll.setLayoutData(scrollData);
        Composite client = toolkit
            .createComposite(multipleContainerDrawingScroll);
        GridLayout layout = new GridLayout(2, false);
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        client.setLayoutData(gd);
        multipleContainerDrawingScroll.setContent(client);

        palletWidget = new ScanPalletWidget(client,
            UICellStatus.DEFAULT_PALLET_SCAN_LINK_STATUS_LIST);
        toolkit.adapt(palletWidget);
        palletWidget.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true,
            false));

        palletWidget.getMultiSelectionManager().addMultiSelectionListener(
            new MultiSelectionListener() {
                @Override
                public void selectionChanged(MultiSelectionEvent mse) {
                    // customSelectionWidget.setNumber(mse.selections);
                }
            });
        palletWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                scanTubeAlone(e);
            }
        });
        // palletWidget.loadProfile(profilesCombo.getCombo().getText());

        createScanTubeAloneButton(client);

        multipleContainerDrawingScroll.setMinSize(client.computeSize(
            SWT.DEFAULT, SWT.DEFAULT));
        widgetCreator.hideWidget(multipleContainerDrawingScroll);
    }

    /**
     * update types of specimens of one given row
     */
    @SuppressWarnings("unchecked")
    private void updateRowType(AliquotedSpecimenSelectionWidget typeWidget,
        int indexRow) {
        if (typeWidget.needToSave()) {
            SpecimenHierarchy selection = typeWidget.getSelection();
            if (selection != null) {
                Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) palletWidget
                    .getCells();
                if (cells != null) {
                    for (RowColPos rcp : cells.keySet()) {
                        if (rcp.row == indexRow) {
                            PalletCell cell = cells.get(rcp);
                            if (PalletCell.hasValue(cell)) {
                                setTypeToCell(cell, selection);
                            }
                        }
                    }
                    palletWidget.redraw();
                }
            }
        }
    }

    private void setTypeToCell(PalletCell cell, SpecimenHierarchy selection) {
        cell.setSourceSpecimen(selection.getParentSpecimen());
        cell.setSpecimenType(selection.getAliquotedSpecimenType());
        cell.setStatus(UICellStatus.TYPE);
    }

    private void createSingleLinkComposite(Composite parent) {
        singleLinkComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        singleLinkComposite.setLayout(layout);
        toolkit.paintBordersFor(singleLinkComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        singleLinkComposite.setLayoutData(gd);

        // inventoryID
        BiobankText inventoryIdText = (BiobankText) createBoundWidgetWithLabel(
            singleLinkComposite,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("GenericLinkEntryForm.inventoryId.label"), //$NON-NLS-1$
            new String[0],
            singleSpecimen,
            SpecimenPeer.INVENTORY_ID.getName(),
            new NonEmptyStringValidator(Messages
                .getString("GenericLinkEntryForm.inventoryId.validator.msg")), //$NON-NLS-1$
            INVENTORY_ID_BINDING);
        inventoryIdText.addKeyListener(textFieldKeyListener);

        // widget to select the source and the type
        singleTypesWidget = new AliquotedSpecimenSelectionWidget(
            singleLinkComposite, null, widgetCreator, false);
        singleTypesWidget.addBindings();

        widgetCreator.createLabel(singleLinkComposite,
            Messages.getString("GenericLinkEntryForm.checkbox.assign")); //$NON-NLS-1$
        toolkit.createButton(singleLinkComposite, "", SWT.CHECK); //$NON-NLS-1$
    }

    @Override
    protected void setBindings(boolean isSingleMode) {
        super.setBindings(isSingleMode);
        if (isSingleMode) {
            widgetCreator.addBinding(INVENTORY_ID_BINDING);
            singleTypesWidget.addBindings();
        } else {
            widgetCreator.removeBinding(INVENTORY_ID_BINDING);
            singleTypesWidget.removeBindings();
        }
    }

    /**
     * Get types only defined in the patient's study. Then set these types to
     * the types combos
     */
    private void setTypeCombos() {
        List<SpecimenTypeWrapper> studiesAliquotedTypes = linkFormPatientManagement
            .getStudyAliquotedTypes(null, null);
        List<SpecimenWrapper> availableSourceSpecimens = linkFormPatientManagement
            .getParentSpecimenForPEventAndCEvent();
        if (isFirstSuccessfulScan())
            // for multiple
            for (int row = 0; row < specimenTypesWidgets.size(); row++) {
                AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets
                    .get(row);
                widget.setSourceSpecimens(availableSourceSpecimens);
                widget.setResultTypes(studiesAliquotedTypes);
            }
        // for single
        singleTypesWidget.setSourceSpecimens(availableSourceSpecimens);
        singleTypesWidget.setResultTypes(studiesAliquotedTypes);
    }

    @Override
    protected void disableFields() {

    }

    @Override
    protected boolean fieldsValid() {
        return isPlateValid() && linkFormPatientManagement.fieldsValid();
    }

    @Override
    protected ProcessData getProcessData() {
        return new LinkProcessData();
    }

    @Override
    protected void doBeforeSave() throws Exception {
        // can't acces the combos in another thread, so do it now
        if (singleMode) {
            SpecimenHierarchy selection = singleTypesWidget.getSelection();
            singleSpecimen.setParentSpecimen(selection.getParentSpecimen());
            singleSpecimen
                .setSpecimenType(selection.getAliquotedSpecimenType());
            singleSpecimen.setCollectionEvent(linkFormPatientManagement
                .getSelectedCollectionEvent());
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if (singleMode)
            saveSingleSpecimen();
    }

    private void saveSingleSpecimen() throws Exception {
        singleSpecimen.setCreatedAt(new Date());
        singleSpecimen.setQuantityFromType();
        singleSpecimen.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        singleSpecimen.setCurrentCenter(SessionManager.getUser()
            .getCurrentWorkingCenter());

        OriginInfoWrapper originInfo = new OriginInfoWrapper(
            SessionManager.getAppService());
        originInfo
            .setCenter(SessionManager.getUser().getCurrentWorkingCenter());
        originInfo.persist();

        singleSpecimen.setOriginInfo(originInfo);
        singleSpecimen.persist();
        String posStr = singleSpecimen.getPositionString(true, false);
        if (posStr == null) {
            posStr = Messages
                .getString("GenericLinkEntryForm.position.label.none"); //$NON-NLS-1$
        }
        // LINKED\: specimen {0} of type\: {1} to source\: {2} ({3}) -
        // Patient\: {4} - Visit\: {5} - Center\: {6} \n
        appendLog(Messages.getString(
            "GenericLinkEntryForm.activitylog.specimen.linked", singleSpecimen //$NON-NLS-1$
                .getInventoryId(), singleSpecimen.getSpecimenType().getName(),
            singleSpecimen.getParentSpecimen().getInventoryId(), singleSpecimen
                .getParentSpecimen().getSpecimenType().getNameShort(),
            linkFormPatientManagement.getCurrentPatient().getPnumber(),
            singleSpecimen.getCollectionEvent().getVisitNumber(),
            singleSpecimen.getCurrentCenter().getNameShort()));
        setFinished(false);
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        singleSpecimen.reset(); // reset internal values
        linkFormPatientManagement.reset(true);
        singleTypesWidget.deselectAll();
        setDirty(false);
        setFocus();
    }

}
