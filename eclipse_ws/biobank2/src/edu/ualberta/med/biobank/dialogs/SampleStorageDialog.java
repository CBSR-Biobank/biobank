package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class SampleStorageDialog extends BiobankDialog {

    private static final String TITLE = "Sample Storage";

    private AliquotedSpecimenWrapper origSampleStorage;

    private AliquotedSpecimenWrapper sampleStorage;

    private ComboViewer sampleTypeComboViewer;

    private String currentTitle;

    private Collection<SpecimenTypeWrapper> availableSampleTypes;

    public SampleStorageDialog(Shell parent,
        AliquotedSpecimenWrapper sampleStorage,
        Collection<SpecimenTypeWrapper> sampleTypes) {
        super(parent);
        Assert.isNotNull(sampleStorage);
        Assert.isNotNull(sampleTypes);
        this.availableSampleTypes = sampleTypes;
        this.origSampleStorage = sampleStorage;
        this.sampleStorage = new AliquotedSpecimenWrapper(null);
        this.sampleStorage.setSpecimenType(sampleStorage.getSpecimenType());
        this.sampleStorage.setVolume(sampleStorage.getVolume());
        this.sampleStorage.setQuantity(sampleStorage.getQuantity());
        this.sampleStorage.setActivityStatus(sampleStorage.getActivityStatus());
        if (origSampleStorage.getSpecimenType() == null) {
            currentTitle = "Add " + TITLE;

            try {
                this.sampleStorage.setActivityStatus(ActivityStatusWrapper
                    .getActiveActivityStatus(sampleStorage.getAppService()));
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Database Error",
                    "Error while retrieving activity status");
            }
        } else {
            currentTitle = "Edit " + TITLE;
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Select the sample types used by this study";
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected Image getTitleAreaImage() {
        // FIXME should use another icon
        return BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_COMPUTER_KEY);
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sampleTypeComboViewer = getWidgetCreator().createComboViewer(contents,
            "Sample Type", availableSampleTypes, sampleStorage.getSpecimenType(),
            "A sample type should be selected", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    sampleStorage
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                }
            });
        sampleTypeComboViewer.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SpecimenTypeWrapper) element).getName();
            }
        });

        getWidgetCreator().createComboViewer(
            contents,
            "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(SessionManager
                .getAppService()), sampleStorage.getActivityStatus(),
            "A sample type should be selected", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    try {
                        sampleStorage
                            .setActivityStatus((ActivityStatusWrapper) selectedObject);
                    } catch (Exception e) {
                        BioBankPlugin.openAsyncError(
                            "Error setting activity status", e);
                    }
                }
            });

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.BORDER,
            "Volume (ml)", new String[0], sampleStorage, "volume",
            new DoubleNumberValidator("Volume should be a real number", false));

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.BORDER,
            "Quantity", new String[0], sampleStorage, "quantity",
            new IntegerNumberValidator("Quantity should be a whole number",
                false));
    }

    @Override
    protected Point getInitialSize() {
        final Point size = super.getInitialSize();
        size.y += convertHeightInCharsToPixels(2);
        return size;
    }

    @Override
    protected void okPressed() {
        origSampleStorage.setSpecimenType(sampleStorage.getSpecimenType());
        origSampleStorage.setVolume(sampleStorage.getVolume());
        origSampleStorage.setQuantity(sampleStorage.getQuantity());
        origSampleStorage.setActivityStatus(sampleStorage.getActivityStatus());
        super.okPressed();
    }

}
