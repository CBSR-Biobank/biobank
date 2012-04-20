package edu.ualberta.med.biobank.dialogs.select;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.i18n.ContactI18n;

public class ContactAddDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(ContactAddDialog.class);

    private final ContactWrapper contactWrapper;
    private final String currentTitle;

    public ContactAddDialog(Shell parent) {
        this(parent, new ContactWrapper(SessionManager.getAppService()));
    }

    @SuppressWarnings("nls")
    public ContactAddDialog(Shell parent, ContactWrapper contactWrapper) {
        super(parent);
        Assert.isNotNull(contactWrapper);
        this.contactWrapper = contactWrapper;
        currentTitle = contactWrapper.getName() == null
            // add contact dialog title
            ? i18n.tr("Add Contact")
            // edit contact dialog title
            : i18n.tr("Edit Contact");
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        if (contactWrapper.getName() == null) {
            // add contact dialog title area message
            return i18n.tr("Add a contact person to this clinic");
        }
        // edit contact dialog title area message
        return i18n.tr("Edit contact person for this clinic");
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
            SWT.BORDER,
            ContactI18n.Property.NAME.toString(),
            new String[0],
            contactWrapper, ContactPeer.NAME.getName(), null);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        c.setLayoutData(gd);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            ContactI18n.Property.TITLE.toString(),
            new String[0],
            contactWrapper, ContactPeer.TITLE.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            ContactI18n.Property.EMAIL_ADDRESS.toString(),
            new String[0],
            contactWrapper, ContactPeer.EMAIL_ADDRESS.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            ContactI18n.Property.MOBILE_NUMBER.toString(),
            new String[0],
            contactWrapper, ContactPeer.MOBILE_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            ContactI18n.Property.PAGER_NUMBER.toString(),
            new String[0],
            contactWrapper, ContactPeer.PAGER_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            ContactI18n.Property.OFFICE_NUMBER.toString(),
            new String[0],
            contactWrapper, ContactPeer.OFFICE_NUMBER.getName(), null);

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            ContactI18n.Property.FAX_NUMBER.toString(),
            new String[0], contactWrapper,
            ContactPeer.FAX_NUMBER.getName(), null);
    }

    public ContactWrapper getContactWrapper() {
        return contactWrapper;
    }

}