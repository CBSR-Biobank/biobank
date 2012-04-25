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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.peer.SourceSpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudySourceSpecimenDialog extends PagedDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(StudySourceSpecimenDialog.class);

    private final SourceSpecimenWrapper defaultSourceSpecimen;

    private List<SpecimenTypeWrapper> specimenTypes;

    private String currentTitle;

    private String message;

    private ComboViewer typeName;

    private Button volume;

    private final SourceSpecimenWrapper userSourceSpecimen;

    @SuppressWarnings("nls")
    public StudySourceSpecimenDialog(Shell parent,
        boolean defaultNeedOrigVolume, SpecimenTypeWrapper defaultSpecimenType,
        List<SpecimenTypeWrapper> specimenTypes, NewListener newListener) {
        super(parent, newListener, defaultSpecimenType == null);
        Assert.isNotNull(specimenTypes);

        defaultSourceSpecimen = new SourceSpecimenWrapper(null);
        defaultSourceSpecimen.setNeedOriginalVolume(defaultNeedOrigVolume);
        defaultSourceSpecimen.setSpecimenType(defaultSpecimenType);

        userSourceSpecimen = new SourceSpecimenWrapper(null);
        userSourceSpecimen.setNeedOriginalVolume(defaultNeedOrigVolume);
        userSourceSpecimen.setSpecimenType(defaultSpecimenType);

        if (defaultSpecimenType == null) {
            // TR: dialog title
            currentTitle = i18n.tr("Add source specimen types");
            // TR: dialog title area message
            message = i18n.tr("Add a source specimen type to this study");
        } else {
            // TR: dialog title
            currentTitle = i18n.tr("Edit source specimen types");
            // TR: dialog title area message
            message = i18n.tr("Edit a source specimen type of this study");
        }
        this.specimenTypes = specimenTypes;
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

    public void setSpecimenTypes(List<SpecimenTypeWrapper> specimenTypes) {
        this.specimenTypes = specimenTypes;
        typeName.setInput(specimenTypes);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        typeName = getWidgetCreator().createComboViewer(contents,
            SpecimenType.NAME.singular().toString(),
            specimenTypes, userSourceSpecimen.getSpecimenType(),
            // TR: validation error message
            i18n.tr("A specimen type should be selected"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    userSourceSpecimen
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                }
            }, new BiobankLabelProvider());

        volume = (Button) createBoundWidgetWithLabel(contents, Button.class,
            SWT.BORDER,
            SourceSpecimen.PropertyName.NEED_ORIGINAL_VOLUME.toString(),
            new String[0], userSourceSpecimen,
            SourceSpecimenPeer.NEED_ORIGINAL_VOLUME.getName(), null);

    }

    @Override
    protected ModelWrapper<SourceSpecimen> getNew() {
        return new SourceSpecimenWrapper(null);
    }

    @Override
    protected void resetFields() {
        userSourceSpecimen.setNeedOriginalVolume(defaultSourceSpecimen
            .getNeedOriginalVolume());
        userSourceSpecimen.setSpecimenType(defaultSourceSpecimen
            .getSpecimenType());
        typeName.getCombo().deselectAll();
        volume.setSelection(false);
    }

    @Override
    protected void copy(Object newModelObject) {
        SourceSpecimenWrapper sourceSpecimen =
            (SourceSpecimenWrapper) newModelObject;
        sourceSpecimen.setNeedOriginalVolume(userSourceSpecimen
            .getNeedOriginalVolume());
        sourceSpecimen.setSpecimenType(userSourceSpecimen.getSpecimenType());
    }

    public boolean getNeedOriginalVolume() {
        return userSourceSpecimen.getNeedOriginalVolume();
    }

    public SpecimenTypeWrapper getSpecimenType() {
        return userSourceSpecimen.getSpecimenType();
    }
}
