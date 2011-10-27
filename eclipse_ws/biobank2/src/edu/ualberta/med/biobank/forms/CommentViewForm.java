package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class CommentViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.CommentViewForm"; //$NON-NLS-1$

    private CommentWrapper comment;

    private BgcBaseText user;

    private BgcBaseText date;

    private BgcBaseText message;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof CommentAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        if (adapter.getId() != null) {
            comment = (CommentWrapper) ((AdapterBase) adapter).getModelObject();
        }

        setPartName(NLS.bind(Messages.CommentViewForm_title,
            comment.getCreatedAt()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.CommentViewForm_title,
            comment.getCreatedAt()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createCommentSection();
        setValues();
    }

    private void createCommentSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        user = createReadOnlyLabelledField(client, SWT.NONE, Messages.CommentViewForm_0);
        date = createReadOnlyLabelledField(client, SWT.NONE, Messages.CommentViewForm_1);
        message = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.CommentViewForm_2);

    }

    private void setValues() {
        setTextValue(user, comment.getUser().getLogin());
        setTextValue(date,
            DateFormatter.formatAsDateTime(comment.getCreatedAt()));
        setTextValue(message, comment.getMessage());
    }

    @Override
    public void reload() throws Exception {
        setValues();
        setPartName(NLS.bind(Messages.CommentViewForm_title,
            comment.getCreatedAt()));
        form.setText(NLS.bind(Messages.CommentViewForm_title,
            comment.getCreatedAt()));
    }
}
