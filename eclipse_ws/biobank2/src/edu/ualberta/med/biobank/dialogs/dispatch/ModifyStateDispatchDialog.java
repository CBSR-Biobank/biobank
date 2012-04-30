package edu.ualberta.med.biobank.dialogs.dispatch;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class ModifyStateDispatchDialog extends BgcBaseDialog {

    private static final String TITLE_STATE = "Setting {0} state to specimens in current dispatch";
    private static final String TITLE_COMMENT_ONLY = "Modifying comment of specimens in current dispatch";
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

    private CommentValue commentValue = new CommentValue();

    public ModifyStateDispatchDialog(Shell parentShell, String oldComment,
        DispatchSpecimenState newState) {
        super(parentShell);
        commentValue.setValue(oldComment);
        if (newState == null) {
            currentTitle = TITLE_COMMENT_ONLY;
            message = "Set a comment";

        } else {
            currentTitle = MessageFormat.format(TITLE_STATE,
                newState.getLabel());
            message = "Set a comment to explain the state modification";
        }
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

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.MULTI,
            "Comment", null,
            commentValue, "value", 
            new NonEmptyStringValidator(
                "Comment should not be empty"));
    }

    public String getComment() {
        return commentValue.getValue();
    }

}
