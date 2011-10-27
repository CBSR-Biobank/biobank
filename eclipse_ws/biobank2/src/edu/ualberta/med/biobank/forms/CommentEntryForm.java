package edu.ualberta.med.biobank.forms;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class CommentEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CommentEntryForm"; //$NON-NLS-1$

    private CommentWrapper comment;

    private BgcEntryFormWidgetListener listener = new BgcEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private Control user;

    private static final String MSG_NEW_COMMENT_OK = Messages.CommentEntryForm_0;
    private static final String MSG_COMMENT_OK = Messages.CommentEntryForm_1;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof CommentAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        if (adapter.getId() != null) {
            comment = (CommentWrapper) ((AdapterBase) adapter).getModelObject();
        }

        // FIXME log edit action?
        // SessionManager.logEdit(patient);
        String tabName;
        if (comment == null) {
            tabName = Messages.CommentEntryForm_new_title;
        } else {
            tabName = NLS.bind(Messages.CommentEntryForm_edit_title,
                comment.getCreatedAt());
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.CommentEntryForm_main_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createCommentSection();

        if (comment == null) {
            setDirty(true);
        }
    }

    private void createCommentSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        user = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.CommentEntryForm_2);
        ((BgcBaseText) user).setText(SessionManager.getUser().getLogin());

        setFirstControl(user);
        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.CommentViewForm_1)
            .setText(
                DateFormatter.formatAsDateTime(comment == null ? new Date()
                    : comment.getCreatedAt()));
        createLabelledWidget(client, BgcBaseText.class, SWT.MULTI,
            Messages.CommentEntryForm_4);

    }

    @Override
    protected String getOkMessage() {
        if (comment == null) {
            return MSG_NEW_COMMENT_OK;
        }
        return MSG_COMMENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
    }

    @Override
    protected void doAfterSave() throws Exception {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }

    @Override
    protected void onReset() throws Exception {
    }

    @Override
    protected boolean openViewAfterSaving() {
        // already done by showSearchedObjectsInTree called in doAfterSave
        return false;
    }
}
