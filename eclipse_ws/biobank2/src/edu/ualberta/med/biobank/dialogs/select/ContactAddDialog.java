package edu.ualberta.med.biobank.dialogs.select;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class ContactAddDialog extends BgcBaseDialog {

    private ContactWrapper contactWrapper;
    private String currentTitle;

    public ContactAddDialog(Shell parent) {
        this(parent, new ContactWrapper(SessionManager.getAppService()));
    }

    public ContactAddDialog(Shell parent, ContactWrapper contactWrapper) {
        super(parent);
        Assert.isNotNull(contactWrapper);
        this.contactWrapper = contactWrapper;
        currentTitle = contactWrapper.getName() == null ? Messages.ContactAddDialog_title_add
            : Messages.ContactAddDialog_title_edit;
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        if (contactWrapper.getName() == null) {
            return Messages.ContactAddDialog_description_add;
        }
        return Messages.ContactAddDialog_description_edit;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Control c = createBoundWidgetWithLabel(contents, BgcBaseText.class,
            SWT.BORDER, Messages.ContactAddDialog_name_label, new String[0],
            contactWrapper, ContactPeer.NAME.getName(), null);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        c.setLayoutData(gd);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.ContactAddDialog_title_label, new String[0],
            contactWrapper, ContactPeer.TITLE.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.ContactAddDialog_email_label, new String[0],
            contactWrapper, ContactPeer.EMAIL_ADDRESS.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.ContactAddDialog_mobile_label, new String[0],
            contactWrapper, ContactPeer.MOBILE_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.ContactAddDialog_pager_label, new String[0],
            contactWrapper, ContactPeer.PAGER_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.ContactAddDialog_office_label, new String[0],
            contactWrapper, ContactPeer.OFFICE_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.ContactAddDialog_fax_label, new String[0], contactWrapper,
            ContactPeer.FAX_NUMBER.getName(), null);
    }

    public ContactWrapper getContactWrapper() {
        return contactWrapper;
    }

}