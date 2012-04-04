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
        currentTitle = contactWrapper.getName() == null ? "Add Contact"
            : "Edit Contact";
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        if (contactWrapper.getName() == null) {
            return "Add a contact person to this clinic";
        }
        return "Edit contact person for this clinic";
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
            SWT.BORDER, "Name", new String[0],
            contactWrapper, ContactPeer.NAME.getName(), null);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        c.setLayoutData(gd);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            "Title", new String[0],
            contactWrapper, ContactPeer.TITLE.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            "Email", new String[0],
            contactWrapper, ContactPeer.EMAIL_ADDRESS.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            "Mobile #", new String[0],
            contactWrapper, ContactPeer.MOBILE_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            "Pager #", new String[0],
            contactWrapper, ContactPeer.PAGER_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            "Office #", new String[0],
            contactWrapper, ContactPeer.OFFICE_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            "Fax #", new String[0], contactWrapper,
            ContactPeer.FAX_NUMBER.getName(), null);
    }

    public ContactWrapper getContactWrapper() {
        return contactWrapper;
    }

}