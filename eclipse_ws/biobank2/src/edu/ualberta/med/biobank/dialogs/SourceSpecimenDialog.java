package edu.ualberta.med.biobank.dialogs;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.peer.SourceSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class SourceSpecimenDialog extends BiobankDialog {

    private SourceSpecimenWrapper origSourceSpecimen;

    private SourceSpecimenWrapper newSourceSpecimen;

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private String currentTitle;

    private String message;

    public SourceSpecimenDialog(Shell parent,
        SourceSpecimenWrapper origSourceSpecimen,
        List<SpecimenTypeWrapper> allSpecimenTypes) {
        super(parent);
        Assert.isNotNull(origSourceSpecimen);
        Assert.isNotNull(allSpecimenTypes);
        this.origSourceSpecimen = origSourceSpecimen;
        this.newSourceSpecimen = new SourceSpecimenWrapper(null);
        this.newSourceSpecimen.setStudy(origSourceSpecimen.getStudy());
        this.newSourceSpecimen.setSpecimenType(origSourceSpecimen
            .getSpecimenType());
        this.newSourceSpecimen.setNeedTimeDrawn(origSourceSpecimen
            .getNeedTimeDrawn());
        this.newSourceSpecimen.setNeedOriginalVolume(origSourceSpecimen
            .getNeedOriginalVolume());
        this.allSpecimenTypes = allSpecimenTypes;
        if (origSourceSpecimen.getSpecimenType() == null) {
            currentTitle = Messages.getString("SourceSpecimenDialog.add.title");
            message = Messages.getString("SourceSpecimenDialog.add.msg");
        } else {
            currentTitle = Messages
                .getString("SourceSpecimenDialog.edit.title");
            message = Messages.getString("SourceSpecimenDialog.edit.msg");
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return message;
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
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        getWidgetCreator().createComboViewer(contents,
            Messages.getString("SourceSpecimen.field.type.label"),
            allSpecimenTypes, newSourceSpecimen.getSpecimenType(),
            Messages.getString("SourceSpecimen.field.type.validation.msg"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    newSourceSpecimen
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(contents, Button.class, SWT.BORDER,
            Messages.getString("SourceSpecimen.field.timeDrawn.label"),
            new String[0], newSourceSpecimen,
            SourceSpecimenPeer.NEED_TIME_DRAWN.getName(), null);

        createBoundWidgetWithLabel(contents, Button.class, SWT.BORDER,
            Messages.getString("SourceSpecimen.field.originalVolume.label"),
            new String[0], newSourceSpecimen,
            SourceSpecimenPeer.NEED_ORIGINAL_VOLUME.getName(), null);
    }

    @Override
    protected void okPressed() {
        origSourceSpecimen.setSpecimenType(newSourceSpecimen.getSpecimenType());
        origSourceSpecimen.setNeedTimeDrawn(newSourceSpecimen
            .getNeedTimeDrawn());
        origSourceSpecimen.setNeedOriginalVolume(newSourceSpecimen
            .getNeedOriginalVolume());
        super.okPressed();
    }

}
