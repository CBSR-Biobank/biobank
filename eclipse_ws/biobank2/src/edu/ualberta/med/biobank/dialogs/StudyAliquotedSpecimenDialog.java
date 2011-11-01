package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.AliquotedSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
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
            currentTitle = Messages.StudyAliquotedSpecimenDialog_add_title;

            try {
                this.newAliquotedSpecimen
                    .setActivityStatus(ActivityStatusWrapper
                        .getActiveActivityStatus(origAliquotedSpecimen
                            .getAppService()));
            } catch (Exception e) {
                BgcPlugin
                    .openAsyncError(
                        Messages.StudyAliquotedSpecimenDialog_activityStatus_retrieve_error_title,
                        Messages.StudyAliquotedSpecimenDialog_activityStatus_retrieve_error_msg);
            }
        } else {
            currentTitle = Messages.StudyAliquotedSpecimenDialog_edit_title;
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        if (availableSpecimenTypes.size() > 0)
            return Messages.StudyAliquotedSpecimenDialog_msg;
        else
            return Messages.StudyAliquotedSpecimenDialog_available_nomore_msg;
    }

    @Override
    protected int getTitleAreaMessageType() {
        if (availableSpecimenTypes.size() > 0)
            return IMessageProvider.NONE;
        else
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
            contents, Messages.StudyAliquotedSpecimenDialog_field_type_label,
            availableSpecimenTypes, newAliquotedSpecimen.getSpecimenType(),
            Messages.StudyAliquotedSpecimenDialog_field_type_validation_msg,
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

        activityStatus = getWidgetCreator().createComboViewer(
            contents,
            Messages.StudyAliquotedSpecimenDialog_label_activity,
            ActivityStatusWrapper.getAllActivityStatuses(SessionManager
                .getAppService()), newAliquotedSpecimen.getActivityStatus(),
            Messages.StudyAliquotedSpecimenDialog_validation_activity,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    try {
                        newAliquotedSpecimen
                            .setActivityStatus((ActivityStatusWrapper) selectedObject);
                    } catch (Exception e) {
                        BgcPlugin
                            .openAsyncError(
                                Messages.StudyAliquotedSpecimenDialog_activityStatus_error_title,
                                e);
                    }
                }
            }, new BiobankLabelProvider());

        volume = (BgcBaseText) createBoundWidgetWithLabel(contents,
            BgcBaseText.class, SWT.BORDER,
            Messages.StudyAliquotedSpecimenDialog_volume_label, new String[0],
            newAliquotedSpecimen, AliquotedSpecimenPeer.VOLUME.getName(),
            new DoubleNumberValidator(
                Messages.StudyAliquotedSpecimenDialog_volume_validation_msg,
                false));

        quantity = (BgcBaseText) createBoundWidgetWithLabel(contents,
            BgcBaseText.class, SWT.BORDER,
            Messages.StudyAliquotedSpecimenDialog_quantity_label,
            new String[0], newAliquotedSpecimen,
            AliquotedSpecimenPeer.QUANTITY.getName(),
            new IntegerNumberValidator(
                Messages.StudyAliquotedSpecimenDialog_quantity_validation_msg,
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
                Messages.StudyAliquotedSpecimenDialog_error_title, e);
        }
        specimenTypeComboViewer.getCombo().deselectAll();
        quantity.setText(""); //$NON-NLS-1$
        volume.setText(""); //$NON-NLS-1$
        activityStatus.getCombo().deselectAll();
    }
}
