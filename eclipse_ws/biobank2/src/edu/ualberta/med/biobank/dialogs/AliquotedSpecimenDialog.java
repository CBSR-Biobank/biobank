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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.AliquotedSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class AliquotedSpecimenDialog extends BiobankDialog {

    private AliquotedSpecimenWrapper origAliquotedSpecimen;

    private AliquotedSpecimenWrapper newAliquotedSpecimen;

    private ComboViewer specimenTypeComboViewer;

    private String currentTitle;

    private Collection<SpecimenTypeWrapper> availableSpecimenTypes;

    public AliquotedSpecimenDialog(Shell parent,
        AliquotedSpecimenWrapper origAliquotedSpecimen,
        Collection<SpecimenTypeWrapper> specimenTypes) {
        super(parent);
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
            currentTitle = Messages
                .getString("AliquotedSpecimenDialog.add.title");

            try {
                this.newAliquotedSpecimen
                    .setActivityStatus(ActivityStatusWrapper
                        .getActiveActivityStatus(origAliquotedSpecimen
                            .getAppService()));
            } catch (Exception e) {
                BiobankPlugin.openAsyncError("Database Error",
                    "Error while retrieving activity status");
            }
        } else {
            currentTitle = Messages
                .getString("AliquotedSpecimenDialog.edit.title");
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.getString("AliquotedSpecimenDialog.msg");
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected Image getTitleAreaImage() {
        // FIXME should use another icon
        return BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_COMPUTER_KEY);
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
            contents, Messages.getString("AliquotedSpecimen.field.type.label"),
            availableSpecimenTypes, newAliquotedSpecimen.getSpecimenType(),
            Messages.getString("AliquotedSpecimen.field.type.validation.msg"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    newAliquotedSpecimen
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                }
            });
        specimenTypeComboViewer.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SpecimenTypeWrapper) element).getName();
            }
        });

        getWidgetCreator().createComboViewer(
            contents,
            Messages.getString("label.activity"),
            ActivityStatusWrapper.getAllActivityStatuses(SessionManager
                .getAppService()), newAliquotedSpecimen.getActivityStatus(),
            Messages.getString("validation.activity"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    try {
                        newAliquotedSpecimen
                            .setActivityStatus((ActivityStatusWrapper) selectedObject);
                    } catch (Exception e) {
                        BiobankPlugin.openAsyncError(
                            "Error setting activity status", e);
                    }
                }
            });

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.BORDER,
            Messages.getString("AliquotedSpecimen.field.volume.label"),
            new String[0], newAliquotedSpecimen,
            AliquotedSpecimenPeer.VOLUME.getName(), new DoubleNumberValidator(
                Messages.getString("AliquotedSpecimen.field.validation.msg"),
                false));

        createBoundWidgetWithLabel(
            contents,
            BiobankText.class,
            SWT.BORDER,
            Messages.getString("AliquotedSpecimen.field.quantity.label"),
            new String[0],
            newAliquotedSpecimen,
            AliquotedSpecimenPeer.QUANTITY.getName(),
            new IntegerNumberValidator(Messages
                .getString("AliquotedSpecimen.field.quantity.validation.msg"),
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
        origAliquotedSpecimen.setSpecimenType(newAliquotedSpecimen
            .getSpecimenType());
        origAliquotedSpecimen.setVolume(newAliquotedSpecimen.getVolume());
        origAliquotedSpecimen.setQuantity(newAliquotedSpecimen.getQuantity());
        origAliquotedSpecimen.setActivityStatus(newAliquotedSpecimen
            .getActivityStatus());
        super.okPressed();
    }

}
