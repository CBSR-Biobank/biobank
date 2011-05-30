package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.peer.SourceSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.widgets.infotables.entry.AliquotedSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class StudySourceSpecimenDialog extends PagedDialog {

    private SourceSpecimenWrapper origSourceSpecimen;

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private String currentTitle;

    private String message;

    private ComboViewer typeName;

    private Button volume;

    private SourceSpecimenWrapper internalSourceSpecimen;

    private AliquotedSpecimenEntryInfoTable aliquotedSpecimenEntryTable;

    private List<AliquotedSpecimenWrapper> addedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();
    private List<AliquotedSpecimenWrapper> removedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();

    private Section aliquotedSection;

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
        internalSourceSpecimen.getWrappedObject()
            .setAliquotedSpecimenCollection(
                origSourceSpecimen.getWrappedObject()
                    .getAliquotedSpecimenCollection());
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

        typeName = getWidgetCreator().createComboViewer(contents,
            Messages.getString("SourceSpecimen.field.type.label"),
            allSpecimenTypes, internalSourceSpecimen.getSpecimenType(),
            Messages.getString("SourceSpecimen.field.type.validation.msg"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    SpecimenTypeWrapper stw = (SpecimenTypeWrapper) selectedObject;
                    internalSourceSpecimen.setSpecimenType(stw);
                    boolean children = stw
                        .getChildSpecimenTypeCollection(false).size() > 0;
                    aliquotedSection.setEnabled(children);
                    if (children)
                        setMessage(getTitleAreaMessage(),
                            getTitleAreaMessageType());
                    else
                        setMessage(
                            "This type cannot be derived (see Specimen types configuration or contact administrator).",
                            IMessageProvider.WARNING);
                }
            });
        typeName.getCombo().setEnabled(origSourceSpecimen.isNew());

        volume = (Button) createBoundWidgetWithLabel(contents, Button.class,
            SWT.BORDER,
            Messages.getString("SourceSpecimen.field.originalVolume.label"),
            new String[0], internalSourceSpecimen,
            SourceSpecimenPeer.NEED_ORIGINAL_VOLUME.getName(), null);

        aliquotedSection = new Section(contents, Section.TWISTIE
            | Section.TITLE_BAR | Section.EXPANDED);
        aliquotedSection.setText("Aliquoted specimen");
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;
        aliquotedSection.setLayoutData(gd);

        aliquotedSpecimenEntryTable = new AliquotedSpecimenEntryInfoTable(
            aliquotedSection, internalSourceSpecimen);
        aliquotedSection.setClient(aliquotedSpecimenEntryTable);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;

        widgetCreator
            .addSectionToolbar(
                aliquotedSection,
                "Add an aliquoted specimen type derived from this type.", new SelectionAdapter() { //$NON-NLS-1$
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        aliquotedSpecimenEntryTable.addAliquotedSpecimen();
                    }
                }, null, null);
    }

    @Override
    protected void okPressed() {
        copy(origSourceSpecimen);
        super.okPressed();
    }

    @Override
    protected void cancelPressed() {
        try {
            origSourceSpecimen.reload();
        } catch (Exception e) {
            new RuntimeException(e);
        }
        super.cancelPressed();
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
            BiobankPlugin.openAsyncError("Error", e);
        }
        typeName.getCombo().deselectAll();
        volume.setSelection(false);
    }

    @Override
    protected void copy(ModelWrapper<?> newModelObject) {
        SourceSpecimenWrapper ssw = ((SourceSpecimenWrapper) newModelObject);
        ssw.setSpecimenType((internalSourceSpecimen).getSpecimenType());
        ssw.setNeedOriginalVolume((internalSourceSpecimen)
            .getNeedOriginalVolume());
        for (AliquotedSpecimenWrapper asw : aliquotedSpecimenEntryTable
            .getAddedOrModifiedAliquotedSpecimens()) {
            asw.setSourceSpecimen(ssw);
            addedAliquotedSpecimen.add(asw);
        }
        ssw.addToAliquotedSpecimenCollection(addedAliquotedSpecimen);
        removedAliquotedSpecimen.addAll(aliquotedSpecimenEntryTable
            .getDeletedAliquotedSpecimens());
        ssw.removeFromAliquotedSpecimenCollection(removedAliquotedSpecimen);
    }

    public List<AliquotedSpecimenWrapper> getAddedAliquotedSpecimen() {
        return addedAliquotedSpecimen;
    }

    public List<AliquotedSpecimenWrapper> getRemovedAliquotedSpecimen() {
        return removedAliquotedSpecimen;
    }

}
