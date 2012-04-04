package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.peer.AliquotedSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudyAliquotedSpecimenDialog extends PagedDialog {

    private AliquotedSpecimenWrapper origAliquotedSpecimen;

    private AliquotedSpecimenWrapper newAliquotedSpecimen;

    private ComboViewer specimenTypeComboViewer;

    private String currentTitle;

    private Collection<SpecimenTypeWrapper> availableSpecimenTypes;

    private BgcBaseText quantity;

    private BgcBaseText volume;

    private ComboViewer activityStatus;

    public StudyAliquotedSpecimenDialog(Shell parent,
        AliquotedSpecimenWrapper origAliquotedSpecimen,
        NewListener newListener, Collection<SpecimenTypeWrapper> specimenTypes) {
        super(parent, newListener,
            origAliquotedSpecimen.getSpecimenType() == null);
        Assert.isNotNull(origAliquotedSpecimen);
        Assert.isNotNull(specimenTypes);
        this.availableSpecimenTypes = specimenTypes;
        this.origAliquotedSpecimen = origAliquotedSpecimen;
        this.newAliquotedSpecimen = new AliquotedSpecimenWrapper(null);
        this.newAliquotedSpecimen.setSpecimenType(origAliquotedSpecimen
            .getSpecimenType());
        this.newAliquotedSpecimen.setVolume(origAliquotedSpecimen.getVolume());
        this.newAliquotedSpecimen.setQuantity(origAliquotedSpecimen
            .getQuantity());
        this.newAliquotedSpecimen.setActivityStatus(origAliquotedSpecimen
            .getActivityStatus());
        if (origAliquotedSpecimen.getSpecimenType() == null) {
            currentTitle = "Add aliquoted specimen";

            try {
                this.newAliquotedSpecimen
                    .setActivityStatus(ActivityStatus.ACTIVE);
            } catch (Exception e) {
                BgcPlugin
                    .openAsyncError(
                        "Database Error",
                        "Error while retrieving activity status");
            }
        } else {
            currentTitle = "Edit aliquoted specimen";
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        if (availableSpecimenTypes.size() > 0)
            return "Select the aliquoted specimen used by this study";
        return "No more aliquoted specimen type can be derived from the study source specimen types.";
    }

    @Override
    protected int getTitleAreaMessageType() {
        if (availableSpecimenTypes.size() > 0)
            return IMessageProvider.NONE;
        return IMessageProvider.INFORMATION;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        specimenTypeComboViewer = getWidgetCreator().createComboViewer(
            contents, "Specimen type",
            availableSpecimenTypes, newAliquotedSpecimen.getSpecimenType(),
            "A specimen type should be selected",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    newAliquotedSpecimen
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                }
            }, new BiobankLabelProvider());
        specimenTypeComboViewer.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SpecimenTypeWrapper) element).getName();
            }
        });

        activityStatus =
            getWidgetCreator().createComboViewer(
                contents,
                "Activity status",
                ActivityStatus.valuesList(),
                newAliquotedSpecimen.getActivityStatus(),
                "An activity status should be selected",
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        try {
                            newAliquotedSpecimen
                                .setActivityStatus((ActivityStatus) selectedObject);
                        } catch (Exception e) {
                            BgcPlugin
                                .openAsyncError(
                                    "Error setting activity status",
                                    e);
                        }
                    }
                }, new BiobankLabelProvider());

        volume = (BgcBaseText) createBoundWidgetWithLabel(contents,
            BgcBaseText.class, SWT.BORDER,
            "Volume (ml)", new String[0],
            newAliquotedSpecimen, AliquotedSpecimenPeer.VOLUME.getName(),
            new DoubleNumberValidator(
                "Volume should be a real number",
                false));

        quantity = (BgcBaseText) createBoundWidgetWithLabel(contents,
            BgcBaseText.class, SWT.BORDER,
            "Quantity",
            new String[0], newAliquotedSpecimen,
            AliquotedSpecimenPeer.QUANTITY.getName(),
            new IntegerNumberValidator(
                "Quantity should be a whole number",
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
        copy(origAliquotedSpecimen);
        super.okPressed();
    }

    @Override
    protected void copy(Object newModelObject) {
        ((AliquotedSpecimenWrapper) newModelObject)
            .setSpecimenType(newAliquotedSpecimen.getSpecimenType());
        ((AliquotedSpecimenWrapper) newModelObject)
            .setVolume(newAliquotedSpecimen.getVolume());
        ((AliquotedSpecimenWrapper) newModelObject)
            .setQuantity(newAliquotedSpecimen.getQuantity());
        ((AliquotedSpecimenWrapper) newModelObject)
            .setActivityStatus(newAliquotedSpecimen.getActivityStatus());
    }

    @Override
    protected ModelWrapper<?> getNew() {
        return new AliquotedSpecimenWrapper(null);
    }

    @Override
    protected void resetFields() {
        try {
            newAliquotedSpecimen.reset();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                "Error", e);
        }
        specimenTypeComboViewer.getCombo().deselectAll();
        quantity.setText("");
        volume.setText("");
        activityStatus.getCombo().deselectAll();
    }

    public void setSpecimenTypes(
        Set<SpecimenTypeWrapper> availableSpecimenTypes) {
        this.availableSpecimenTypes = availableSpecimenTypes;
        this.specimenTypeComboViewer.setInput(availableSpecimenTypes);
    }
}
