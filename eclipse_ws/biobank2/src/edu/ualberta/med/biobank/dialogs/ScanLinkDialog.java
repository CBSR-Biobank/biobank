package edu.ualberta.med.biobank.dialogs;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenLinkAndAssignForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.PersistedDialog;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.helpers.ScanLinkHelper;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.widgets.specimenlink.ILinkFormPatientManagementParent;
import edu.ualberta.med.biobank.widgets.specimenlink.LinkFormPatientManagement;
import edu.ualberta.med.biobank.widgets.specimentypeselection.ISpecimenTypeSelectionChangedListener;
import edu.ualberta.med.biobank.widgets.specimentypeselection.SpecimenTypeSelectionEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * This dialog is used when the user is going to scan link specimens.
 * 
 * At the moment is is invoked by {@link SpecimenLinkAndAssignForm} when the user presses the
 * "Scan link" button.
 * 
 * @author nelson
 * 
 */
public class ScanLinkDialog extends PersistedDialog
    implements ILinkFormPatientManagementParent, ISpecimenTypeSelectionChangedListener {

    private static final I18n i18n = I18nFactory.getI18n(ScanLinkDialog.class);

    private static Logger log = LoggerFactory.getLogger(ScanLinkDialog.class);

    @SuppressWarnings("nls")
    private static final String SCAN_LINK_DIALOG_SETTINGS =
        ScanLinkDialog.class.getSimpleName() + "_SETTINGS";

    @SuppressWarnings("nls")
    private static final String TITLE = i18n.tr("Scan link");

    @SuppressWarnings("nls")
    private static final String TITLE_AREA_MESSAGE_SELECT_PATIENT =
        i18n.tr("Select a patient number");

    private final LinkFormPatientManagement linkFormPatientManagement;

    private final IObservableValue sourceSelected = new WritableValue(Boolean.FALSE, Boolean.class);

    private final IObservableValue aliquotsSelected = new WritableValue(Boolean.FALSE, Boolean.class);

    private Set<SpecimenType> sourceChildTypes = new HashSet<SpecimenType>();

    private ComboViewer cvSource;

    private ComboViewer cvAliquots;

    private Specimen sourceSpecimenSelected;

    private AliquotedSpecimen aliquotedSpecimenSelected;

    public ScanLinkDialog(Shell parentShell, org.apache.log4j.Logger activityLogger) {
        super(parentShell);
        linkFormPatientManagement = new LinkFormPatientManagement(widgetCreator, this, activityLogger);
    }

    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();
        IDialogSettings section = settings.getSection(SCAN_LINK_DIALOG_SETTINGS);
        if (section == null) {
            section = settings.addNewSection(SCAN_LINK_DIALOG_SETTINGS);
        }
        return section;
    }

    @Override
    protected String getTitleAreaMessage() {
        return TITLE_AREA_MESSAGE_SELECT_PATIENT;
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        final Composite contents = new Composite(parent, SWT.BORDER_DASH);

        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        contents.setLayout(layout);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        contents.setLayoutData(gd);
        createControls(contents);

        parent.pack(true);
        Point size = getShell().computeSize(SWT.DEFAULT, 400);
        getShell().setMinimumSize(size);
    }

    private void createControls(Composite parent) {
        final Composite contents = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10; // for databinding icon to display correctly
        contents.setLayout(layout);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        contents.setLayoutData(gd);

        createControlWidgets(contents);
    }

    /**
     * Classes extending this dialog can override this module to create it's additional widgets.
     * 
     * @param contents The composite parent to bind widgets to.
     */
    protected void createControlWidgets(Composite contents) {
        linkFormPatientManagement.createPatientNumberText(contents);
        linkFormPatientManagement.createEventsWidgets(contents);
        createSpecimenTypeSelection(contents);
    }

    @SuppressWarnings("nls")
    private void createSpecimenTypeSelection(Composite parent) {
        Label sourceLabel = widgetCreator.createLabel(parent, i18n.tr("Source specimen type"));
        cvSource = widgetCreator.createComboViewer(
            parent, sourceLabel, null, null,
            // TR: validation error message
            i18n.tr("Please select a source specimen type"), false,
            null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    sourceSelected.setValue(selectedObject != null);

                    Specimen spc = (Specimen) selectedObject;
                    sourceChildTypes = spc.getSpecimenType().getChildSpecimenTypes();
                    cvAliquots.refresh();
                }
            },
            new LabelProvider() {
                @Override
                public String getText(Object element) {
                    Specimen spc = (Specimen) element;
                    StringBuffer label = new StringBuffer();
                    label.append(spc.getSpecimenType().getNameShort());
                    label.append(" (");
                    label.append(spc.getInventoryId());
                    label.append(")");
                    return label.toString();
                }
            });

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.horizontalSpan = 2;
        cvSource.getCombo().setLayoutData(gridData);

        Label aliquotLabel = widgetCreator.createLabel(parent, i18n.tr("Aliquot specimen type"));
        cvAliquots = widgetCreator.createComboViewer(
            parent, aliquotLabel, null, null,
            // TR: validation error message
            i18n.tr("Please select an aliquot specimen type"), false,
            null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    aliquotsSelected.setValue(selectedObject != null);
                    AliquotedSpecimen spc = (AliquotedSpecimen) selectedObject;
                    sourceChildTypes = spc.getSpecimenType().getChildSpecimenTypes();
                    setMessage(i18n.tr("Press OK to select these values"), IMessageProvider.NONE);
                }
            },
            new LabelProvider() {
                @Override
                public String getText(Object element) {
                    AliquotedSpecimen aqSpecimen = (AliquotedSpecimen) element;

                    DecimalFormat dm = new DecimalFormat("0.000#");
                    dm.setRoundingMode(RoundingMode.HALF_UP);

                    StringBuffer label = new StringBuffer();
                    label.append(aqSpecimen.getSpecimenType().getName());
                    label.append(" (");
                    label.append(dm.format(aqSpecimen.getVolume()));
                    label.append(")");
                    return label.toString();
                }
            });

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.horizontalSpan = 2;
        cvAliquots.getCombo().setLayoutData(gridData);

        cvAliquots.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                boolean filterBySource = false;

                Specimen sourceSpecimen = getSourceSelection();
                if (sourceSpecimen != null) {
                    SpecimenType sourceType = sourceSpecimen.getSpecimenType();
                    if (sourceType != null) {
                        String name = sourceType.getName();
                        if ((name != null) && name.equals(SpecimenTypeWrapper.UNKNOWN_IMPORT_NAME)) {
                            filterBySource = true;
                        }
                    }
                }

                return (filterBySource
                || sourceChildTypes.contains(((AliquotedSpecimen) element).getSpecimenType()));
            }
        });
    }

    private Specimen getSourceSelection() {
        return (Specimen) ((StructuredSelection) cvSource.getSelection()).getFirstElement();
    }

    @Override
    public Button createButton(Composite parent, String text, int style) {
        Button button = new Button(parent, style);
        button.setText(text);
        return button;
    }

    @Override
    public boolean isFinished() {
        // do nothing
        return false;
    }

    @SuppressWarnings("nls")
    @Override
    public void focusLost() {
        setMessage(i18n.tr("Select source and aliquot specimen types"), IMessageProvider.NONE);
    }

    @Override
    public void textModified() {
        // log.info("textModified");
        // do nothing
    }

    @Override
    public void collectionEventSelectionChanged() {
        setSpecimenTypeCobos();
    }

    @SuppressWarnings("nls")
    private void setSpecimenTypeCobos() {
        List<AliquotedSpecimen> studyAliquotedTypes = new ArrayList<AliquotedSpecimen>();
        List<SpecimenType> authorizedTypesInContainers = new ArrayList<SpecimenType>();

        try {
            authorizedTypesInContainers.addAll(
                ScanLinkHelper.getSpecimenTypeForPalletScannable());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Failed to retrieve specimen types.");
        }

        studyAliquotedTypes =
            linkFormPatientManagement.getStudyAliquotedTypes(authorizedTypesInContainers);
        List<Specimen> availableSourceSpecimens =
            linkFormPatientManagement.getParentSpecimenForPEventAndCEvent();

        if (!authorizedTypesInContainers.isEmpty()) {
            // availableSourceSpecimen should be parents of the authorised types!
            List<Specimen> filteredSpecs = new ArrayList<Specimen>();
            for (Specimen spec : availableSourceSpecimens)
                if (!Collections.disjoint(authorizedTypesInContainers,
                    spec.getSpecimenType().getChildSpecimenTypes())) {
                    filteredSpecs.add(spec);
                }
            availableSourceSpecimens = filteredSpecs;
        }

        // specimenTypesWidget.setSelections(availableSourceSpecimens, studyAliquotedTypes);
        cvSource.setInput(availableSourceSpecimens);
        cvAliquots.setInput(studyAliquotedTypes);
    }

    @Override
    public void selectionChanged(SpecimenTypeSelectionEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void okPressed() {
        sourceSpecimenSelected =
            (Specimen) ((StructuredSelection) cvSource.getSelection()).getFirstElement();
        aliquotedSpecimenSelected =
            (AliquotedSpecimen) ((StructuredSelection) cvAliquots.getSelection()).getFirstElement();
        super.okPressed();
    }

    public Specimen getSourceSpecimenSelected() {
        return sourceSpecimenSelected;
    }

    public AliquotedSpecimen getAliquotsTypeSelection() {
        return aliquotedSpecimenSelected;
    }

}
