package edu.ualberta.med.biobank.dialogs;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.peer.SourceSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudySourceSpecimenDialog extends PagedDialog {

    private SourceSpecimenWrapper origSourceSpecimen;

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private String currentTitle;

    private String message;

    private ComboViewer typeName;

    private Button volume;

    private SourceSpecimenWrapper internalSourceSpecimen;

    public StudySourceSpecimenDialog(Shell parent,
        SourceSpecimenWrapper origSourceSpecimen, NewListener newListener,
        List<SpecimenTypeWrapper> allSpecimenTypes) {
        super(parent, newListener, origSourceSpecimen.getSpecimenType() == null);
        Assert.isNotNull(origSourceSpecimen);
        Assert.isNotNull(allSpecimenTypes);
        this.origSourceSpecimen = origSourceSpecimen;
        this.allSpecimenTypes = allSpecimenTypes;
        internalSourceSpecimen = new SourceSpecimenWrapper(null);
        if (origSourceSpecimen.getSpecimenType() == null) {
            currentTitle = Messages.getString("SourceSpecimenDialog.add.title");
            message = Messages.getString("SourceSpecimenDialog.add.msg");
        } else {
            currentTitle = Messages
                .getString("SourceSpecimenDialog.edit.title");
            message = Messages.getString("SourceSpecimenDialog.edit.msg");
            internalSourceSpecimen = new SourceSpecimenWrapper(null);
        }
        internalSourceSpecimen.setStudy(origSourceSpecimen.getStudy());
        internalSourceSpecimen.setSpecimenType(origSourceSpecimen
            .getSpecimenType());
        internalSourceSpecimen.setNeedOriginalVolume(origSourceSpecimen
            .getNeedOriginalVolume());
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
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        typeName = getWidgetCreator().createComboViewer(contents,
            Messages.getString("SourceSpecimen.field.type.label"),
            allSpecimenTypes, internalSourceSpecimen.getSpecimenType(),
            Messages.getString("SourceSpecimen.field.type.validation.msg"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    internalSourceSpecimen
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                }
            }, new BiobankLabelProvider());

        volume = (Button) createBoundWidgetWithLabel(contents, Button.class,
            SWT.BORDER,
            Messages.getString("SourceSpecimen.field.originalVolume.label"),
            new String[0], internalSourceSpecimen,
            SourceSpecimenPeer.NEED_ORIGINAL_VOLUME.getName(), null);

    }

    @Override
    protected void okPressed() {
        copy(origSourceSpecimen);
        super.okPressed();
    }

    @Override
    protected ModelWrapper<SourceSpecimen> getNew() {
        return new SourceSpecimenWrapper(null);
    }

    @Override
    protected void resetFields() {
        try {
            internalSourceSpecimen.reset();
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Error", e);
        }
        typeName.getCombo().deselectAll();
        volume.setSelection(false);
    }

    @Override
    protected void copy(ModelWrapper<?> newModelObject) {
        ((SourceSpecimenWrapper) newModelObject)
            .setSpecimenType((internalSourceSpecimen).getSpecimenType());
        ((SourceSpecimenWrapper) newModelObject)
            .setNeedOriginalVolume((internalSourceSpecimen)
                .getNeedOriginalVolume());
    }
}
