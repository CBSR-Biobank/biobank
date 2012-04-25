package edu.ualberta.med.biobank.wizards.pages;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Comment;

public class EnterCommentPage extends BgcWizardPage {
    private static final I18n i18n = I18nFactory
        .getI18n(EnterCommentPage.class);

    public static final String PAGE_NAME = EnterCommentPage.class
        .getCanonicalName();
    @SuppressWarnings("nls")
    private static final String COMMENT_REQUIRED = i18n
        .tr("Please enter a comment.");
    private String comment;

    @SuppressWarnings("nls")
    public EnterCommentPage() {
        super(PAGE_NAME,
            i18n.tr("Enter a comment to explain the modification"), null);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        getWidgetCreator().createBoundWidgetWithLabel(content,
            BgcBaseText.class, SWT.BORDER | SWT.MULTI,
            Comment.NAME.singular().toString(), null,
            PojoObservables.observeValue(this, "comment"),
            new NonEmptyStringValidator(COMMENT_REQUIRED));

        setControl(content);
    }
}