package edu.ualberta.med.biobank.dialogs.dispatch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Comment;

public class AddDispatchCommentDialog extends BgcBaseDialog
{
    private static final I18n i18n = I18nFactory.getI18n(AddDispatchCommentDialog.class);

    @SuppressWarnings("nls")
    private static final String TITLE_COMMENT_ONLY = i18n.tr("Add a comment for the dispatch");

    private String currentTitle;
    private String message;

    private static class CommentValue {
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final CommentValue commentValue = new CommentValue();

    @SuppressWarnings("nls")
    public AddDispatchCommentDialog(Shell parentShell, DispatchWrapper dispatchWrapper)
    {
        super(parentShell);
        currentTitle = TITLE_COMMENT_ONLY;
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
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.MULTI, Comment.NAME.singular().toString(), null, commentValue, "value", new NonEmptyStringValidator(i18n.tr("Comment should not be empty")));
    }

    public String getComment() {
        return commentValue.getValue();
    }
}