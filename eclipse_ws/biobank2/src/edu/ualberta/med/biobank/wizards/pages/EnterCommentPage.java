package edu.ualberta.med.biobank.wizards.pages;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class EnterCommentPage extends BiobankWizardPage {
    public static final String PAGE_NAME = EnterCommentPage.class
        .getCanonicalName();
    private static final String COMMENT_REQUIRED = "Please enter a comment.";
    private String comment;

    public EnterCommentPage() {
        super(PAGE_NAME, "Enter a comment to explain the modification", null);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        getWidgetCreator().createBoundWidgetWithLabel(content,
            BgcBaseText.class, SWT.BORDER | SWT.MULTI, "Comment", null,
            PojoObservables.observeValue(this, "comment"),
            new NonEmptyStringValidator(COMMENT_REQUIRED));

        setControl(content);
    }
}