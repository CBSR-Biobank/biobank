package edu.ualberta.med.biobank.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.utils.EMailDescriptor;

public class SendErrorMessageDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(SendErrorMessageDialog.class);

    @SuppressWarnings("nls")
    private static final String CONTENT_TYPE = "text/plain";

    @SuppressWarnings("nls")
    private static final String MAIL_SMTP_SOCKET_FACTORY_FALLBACK_KEY =
        "mail.smtp.socketFactory.fallback";

    @SuppressWarnings("nls")
    private static final String MAIL_SMTP_SOCKET_FACTORY_CLASS_KEY =
        "mail.smtp.socketFactory.class";

    @SuppressWarnings("nls")
    private static final String MAIL_SMTP_SOCKET_FACTORY_PORT_KEY =
        "mail.smtp.socketFactory.port";

    @SuppressWarnings("nls")
    private static final String MAIL_SMTP_PORT_KEY = "mail.smtp.port";

    @SuppressWarnings("nls")
    private static final String MAIL_SMTP_AUTH_KEY = "mail.smtp.auth";

    @SuppressWarnings("nls")
    private static final String MAIL_SMTP_HOST_KEY = "mail.smtp.host";

    @SuppressWarnings("nls")
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    private EMailDescriptor email;

    private final List<AttachmentComposite> attachments;

    public SendErrorMessageDialog(Shell parentShell) {
        super(parentShell);
        initEmailDescriptor();
        attachments = new ArrayList<AttachmentComposite>();
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // TR: dialog title area message
        return i18n
            .tr("Please describe steps to reproduce the problem. The application error log will be automatically attached.");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // TR: dialog title area title
        return i18n.tr("Send Error Email");
    }

    @Override
    protected Image getTitleAreaImage() {
        return BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_EMAIL_BANNER);
    }

    @Override
    protected int getTitleAreaMessageType() {
        return IMessageProvider.INFORMATION;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) {
        final Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.NONE,
            // TR: label
            i18n.trc("email", "Title"),
            new String[0], email,
            "title",
            new NonEmptyStringValidator(
                // TR: validation error message
                i18n.tr("Please enter a title")));

        BgcBaseText descText = (BgcBaseText) createBoundWidgetWithLabel(
            contents, BgcBaseText.class, SWT.MULTI,
            // TR: label
            i18n.trc("email", "Description"),
            new String[0],
            email, "description", new NonEmptyStringValidator(
                // TR: validation error message
                i18n.tr("Please enter at least a very small comment")));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 200;
        descText.setLayoutData(gd);

        Label attLabel = widgetCreator.createLabel(contents,
            i18n.trc("email", "Attachments"));
        attLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        final Composite attachmentsComposite =
            new Composite(contents, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        attachmentsComposite.setLayout(layout);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        attachmentsComposite.setLayoutData(gd);

        Button addButton = new Button(contents, SWT.PUSH);
        addButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ADD));
        // TR: button tooltip
        addButton.setToolTipText(i18n.trc("email", "Add attachment"));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AttachmentComposite attachmentComposite =
                    new AttachmentComposite(
                        attachmentsComposite, contents);
                contents.layout(true, true);
                Point shellSize = getShell().getSize();
                getShell().setSize(shellSize.x,
                    shellSize.y + attachmentComposite.getHeight());
                attachments.add(attachmentComposite);
            }
        });
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        // TR: dialog shell title
        return i18n.tr("Send Error Email");
    }

    // private int createAttachmentChooser(Composite parent,
    // final Composite globalComposite) {
    // final Composite attachmentLine = new Composite(parent, SWT.NONE);
    // GridLayout layout = new GridLayout(3, false);
    // layout.horizontalSpacing = 0;
    // layout.marginWidth = 0;
    // layout.verticalSpacing = 0;
    // attachmentLine.setLayout(layout);
    // GridData gd = new GridData();
    // gd.horizontalAlignment = SWT.FILL;
    // gd.grabExcessHorizontalSpace = true;
    // attachmentLine.setLayoutData(gd);
    //
    // final BiobankText attachmentText = (BiobankText) widgetCreator
    // .createWidget(attachmentLine, BiobankText.class, SWT.READ_ONLY,
    // null);
    // final Button button = new Button(attachmentLine, SWT.PUSH);
    // button.setText("Browse");
    // button.addSelectionListener(new SelectionAdapter() {
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // FileDialog fd = new FileDialog(button.getShell(), SWT.OPEN);
    // fd.setText("Select attachment");
    // String selected = fd.open();
    // if (selected != null) {
    // attachments.add(selected);
    // attachmentText.setText(selected);
    // }
    // }
    // });
    // Button removeButton = new Button(attachmentLine, SWT.PUSH);
    // removeButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
    // .get(BgcPlugin.IMG_DELETE));
    // removeButton.setToolTipText("Remove this attachment");
    // removeButton.addSelectionListener(new SelectionAdapter() {
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // int height = attachmentLine.computeSize(SWT.DEFAULT,
    // SWT.DEFAULT).y;
    // attachments.remove(attachmentText.getText());
    // attachmentText.setText(StringUtil.EMPTY_STRING);
    // attachmentLine.dispose();
    // globalComposite.layout(true, true);
    // Point shellSize = getShell().getSize();
    // getShell().setSize(shellSize.x, shellSize.y - height);
    // }
    // });
    // return attachmentLine.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
    // }

    @SuppressWarnings("nls")
    @Override
    protected void okPressed() {
        try {
            sendMail();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Error sending mail"),
                e);
        }
        super.okPressed();
    }

    private void initEmailDescriptor() {
        IPreferenceStore node = BiobankPlugin.getDefault().getPreferenceStore();
        email = new EMailDescriptor();
        email.setSmtpServer(node
            .getString(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER));
        email.setServerPort(node
            .getString(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PORT));
        email.setReceiverEmail(node
            .getString(PreferenceConstants.ISSUE_TRACKER_EMAIL));
        email.setServerUsername(node
            .getString(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_USER));
        email.setServerPassword(node
            .getString(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PASSWORD));
    }

    private void sendMail() throws Exception {
        IRunnableContext context = new ProgressMonitorDialog(Display
            .getDefault().getActiveShell());
        context.run(true, false, new IRunnableWithProgress() {
            @SuppressWarnings("nls")
            @Override
            public void run(final IProgressMonitor monitor) {
                // TR: progress dialog message
                monitor.beginTask(i18n.tr("Sending mail..."),
                    IProgressMonitor.UNKNOWN);
                try {
                    Properties props = new Properties();
                    props.put(MAIL_SMTP_HOST_KEY, email.getSmtpServer());
                    props.put(MAIL_SMTP_AUTH_KEY, Boolean.TRUE.toString());
                    // props.put("mail.debug", "true");
                    props.put(MAIL_SMTP_PORT_KEY, email.getServerPort());
                    props.put(MAIL_SMTP_SOCKET_FACTORY_PORT_KEY,
                        email.getServerPort());
                    props.put(MAIL_SMTP_SOCKET_FACTORY_CLASS_KEY, SSL_FACTORY);
                    props.put(MAIL_SMTP_SOCKET_FACTORY_FALLBACK_KEY,
                        Boolean.FALSE.toString());

                    Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(email
                                    .getServerUsername(), email
                                    .getServerPassword());
                            }
                        });
                    // session.setDebug(true);
                    Transport.send(getEmailMessage(session));
                    monitor.done();
                } catch (AuthenticationFailedException afe) {
                    BgcPlugin
                        .openAsyncError(
                            // TR: dialog title
                            i18n.tr("Authentification Error"),
                            // TR: dialog message
                            i18n.tr("Wrong authentification for {0}",
                                email.getServerUsername()));
                    monitor.setCanceled(true);
                    return;
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        // TR: dialog title
                        i18n.tr("Error sending mail"), e);
                    monitor.setCanceled(true);
                    return;
                }
            }
        });
    }

    @SuppressWarnings("nls")
    private Message getEmailMessage(Session session) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setSubject(email.getTitle());
        message.setContent(email.getDescription(), CONTENT_TYPE);

        message.setRecipient(Message.RecipientType.TO, new InternetAddress(
            email.getReceiverEmail()));

        Multipart mp = new MimeMultipart();

        // create and fill the first message part
        MimeBodyPart mbp1 = new MimeBodyPart();
        String text = email.getDescription() + "\n\n------";
        if (SessionManager.getInstance().isConnected()) {
            text += "\nCreated by user "
                + SessionManager.getInstance().getSession().getUser()
                    .getLogin();
        }

        text += "\nSent from BioBank Java Client, version "
            + BiobankPlugin.getDefault().getBundle().getVersion();

        mbp1.setText(text);
        mp.addBodyPart(mbp1);

        // add log file
        File logFile = Platform.getLogFileLocation().toFile();
        addAttachment(mp, logFile.getPath());

        // add user attachments
        for (AttachmentComposite attachment : attachments) {
            addAttachment(mp, attachment.getFile());
        }

        // add the Multipart to the message
        message.setContent(mp);

        // set the Date: header
        message.setSentDate(new Date());
        return message;
    }

    private void addAttachment(Multipart mp, String file) throws IOException,
        MessagingException {
        if (file != null && !file.isEmpty()) {
            // create the second message part
            MimeBodyPart mbp2 = new MimeBodyPart();
            // attach the file to the message
            mbp2.attachFile(file);
            mp.addBodyPart(mbp2);
        }
    }

    @SuppressWarnings("nls")
    private class AttachmentComposite extends BgcBaseWidget {

        private final BgcBaseText attachmentText;

        private final Button browseButton;

        private final Button removeButton;

        private String file;

        public AttachmentComposite(Composite parent,
            final Composite globalComposite) {
            super(parent, SWT.NONE);
            GridLayout layout = new GridLayout(3, false);
            layout.horizontalSpacing = 0;
            layout.marginWidth = 0;
            layout.verticalSpacing = 0;
            setLayout(layout);
            GridData gd = new GridData();
            gd.horizontalAlignment = SWT.FILL;
            gd.grabExcessHorizontalSpace = true;
            setLayoutData(gd);

            attachmentText = (BgcBaseText) widgetCreator.createWidget(this,
                BgcBaseText.class, SWT.READ_ONLY, null);
            browseButton = new Button(this, SWT.PUSH);
            // TR: button
            browseButton.setText(i18n.trc("file", "Browse"));
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    FileDialog fd = new FileDialog(browseButton.getShell(),
                        SWT.OPEN);
                    // TR: dialog title
                    fd.setText(i18n.tr("Select attachment"));
                    file = fd.open();
                    if (file != null) {
                        attachmentText.setText(file);
                    }
                }
            });
            removeButton = new Button(this, SWT.PUSH);
            removeButton.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_DELETE));
            // TR: tooltip
            removeButton
                .setToolTipText(i18n.tr("Remove this attachment"));
            removeButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    int height = AttachmentComposite.this.computeSize(
                        SWT.DEFAULT, SWT.DEFAULT).y;
                    attachmentText.setText(StringUtil.EMPTY_STRING);
                    AttachmentComposite.this.setVisible(false);
                    GridData gd = (GridData) AttachmentComposite.this
                        .getLayoutData();
                    gd.exclude = true;
                    attachments.remove(AttachmentComposite.this);
                    globalComposite.layout(true, true);
                    Point shellSize = getShell().getSize();
                    getShell().setSize(shellSize.x, shellSize.y - height);
                    AttachmentComposite.this.dispose();
                }
            });
        }

        public String getFile() {
            return file;
        }

        public int getHeight() {
            return computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        }
    }
}
