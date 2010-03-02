package edu.ualberta.med.biobank.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandAdapter;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.utils.EMailDescriptor;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;

public class SendErrorMessageDialog extends BiobankDialog {

    private EMailDescriptor email;

    private int compositeHeight;

    private static final BiobankLogger logger = BiobankLogger
        .getLogger(SendErrorMessageDialog.class.getName());

    public SendErrorMessageDialog(Shell parentShell) {
        super(parentShell);
        initEmailDescriptor();
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Send Error EMail");
        setTitleImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_EMAIL_BANNER));
        setMessage(
            "Please describe steps to reproduce the problem. A log file of the application will be attached to the email.",
            IMessageProvider.INFORMATION);
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(contents, Text.class, SWT.NONE, "Title",
            new String[0], PojoObservables.observeValue(email, "title"),
            new NonEmptyStringValidator("Please enter a title"));

        Text descText = (Text) createBoundWidgetWithLabel(contents, Text.class,
            SWT.MULTI, "Description", new String[0], PojoObservables
                .observeValue(email, "description"),
            new NonEmptyStringValidator(
                "Please enter at least a very small comment"));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 200;
        descText.setLayoutData(gd);

        ExpandBar bar = new ExpandBar(contents, SWT.NONE);
        bar.setLayoutData(new GridData(GridData.FILL_BOTH));
        final Composite compositeWithBar = new Composite(bar, SWT.NONE);
        compositeWithBar.setLayout(new GridLayout(2, false));
        GridData compData = new GridData(GridData.FILL_BOTH);
        compositeWithBar.setLayoutData(compData);

        Text smtp = (Text) createBoundWidgetWithLabel(compositeWithBar,
            Text.class, SWT.NONE, "Smtp server", new String[0], PojoObservables
                .observeValue(email, "smtpServer"),
            new NonEmptyStringValidator("Please enter the smtp server name"));

        createBoundWidgetWithLabel(compositeWithBar, Text.class, SWT.NONE,
            "Port", new String[0], PojoObservables.observeValue(email,
                "serverPort"), new NonEmptyStringValidator(
                "Please enter the server port"));

        createBoundWidgetWithLabel(compositeWithBar, Text.class, SWT.NONE,
            "Username", new String[0], PojoObservables.observeValue(email,
                "serverUsername"), new NonEmptyStringValidator(
                "Please enter the server username"));

        createBoundWidgetWithLabel(compositeWithBar, Text.class, SWT.PASSWORD,
            "Password", new String[0], PojoObservables.observeValue(email,
                "serverPassword"), new NonEmptyStringValidator(
                "Please enter the server password"));

        createBoundWidgetWithLabel(compositeWithBar, Text.class, SWT.NONE,
            "Sender", new String[0], PojoObservables.observeValue(email,
                "senderEmail"), new NonEmptyStringValidator(
                "Please enter the sender email"));

        ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
        item.setText("Email configuration");
        compositeHeight = compositeWithBar
            .computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        item.setHeight(compositeHeight);
        item.setControl(compositeWithBar);
        item.setExpanded(true);

        bar.addExpandListener(new ExpandAdapter() {
            @Override
            public void itemCollapsed(ExpandEvent e) {
                compositeWithBar.layout(true, true);
                Point shellSize = getShell().getSize();
                getShell().setSize(shellSize.x, shellSize.y - compositeHeight);
            }

            @Override
            public void itemExpanded(ExpandEvent e) {
                compositeWithBar.layout(true, true);
                compositeWithBar.setVisible(true);
                Point shellSize = getShell().getSize();
                getShell().setSize(shellSize.x, shellSize.y + compositeHeight);
            }
        });
    }

    @Override
    protected void okPressed() {
        try {
            sendMail();
        } catch (AuthenticationFailedException afe) {
            BioBankPlugin.openAsyncError("Authentification Error",
                "Wrong authentification for " + email.getServerUsername());
            return;
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error in sending email", e);
            return;
        }
        try {
            registerMailInfo();
        } catch (StorageException e) {
            BioBankPlugin.openAsyncError("Error registering mailing info", e);
        }
        super.okPressed();
    }

    private void registerMailInfo() throws StorageException {
        ISecurePreferences node = getSecurePrefNode();
        node.put("SmtpServer", email.getSmtpServer(), false);
        node.put("serverPort", email.getServerPort(), false);
        node.put("senderEmail", email.getSenderEmail(), false);
        node.put("ServerUsername", email.getServerUsername(), false);
        node.put("ReceiverEmail", email.getReceiverEmail(), false);
        node.put("serverPassword", email.getServerPassword(), false); // FIXME
        // should set to true
    }

    private ISecurePreferences getSecurePrefNode() {
        ISecurePreferences securePreferences = SecurePreferencesFactory
            .getDefault();
        // securePreferences.
        ISecurePreferences node = securePreferences.node("MailErrorPref");
        return node;
    }

    private void initEmailDescriptor() {
        ISecurePreferences node = getSecurePrefNode();

        email = new EMailDescriptor();
        try {
            email.setSmtpServer(node.get("SmtpServer", "smtp.company.ca"));
            email.setServerPort(node.get("serverPort", "25"));
            email.setSenderEmail(node.get("senderEmail",
                "delphine.dard@gmail.com"));
            email.setReceiverEmail(node.get("ReceiverEmail",
                "degrisda@ualberta.ca"));
            email
                .setServerUsername(node.get("ServerUsername", "delphine.dard"));
            email.setServerPassword(node.get("serverPassword", ""));
        } catch (StorageException se) {
            logger.debug("Can't retrieve mail stored information", se);
        }
    }

    private void sendMail() throws AddressException, MessagingException,
        IOException {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", email.getSmtpServer());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", email.getServerPort());
        props.put("mail.smtp.socketFactory.port", email.getServerPort());
        props.put("mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        email.getServerUsername(), email.getServerPassword());
                }
            });
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        message.setSender(new InternetAddress(email.getSenderEmail()));
        message.setSubject(email.getTitle());
        // message.setContent(email.getDescription(), "text/plain");

        message.setRecipient(Message.RecipientType.TO, new InternetAddress(
            email.getReceiverEmail()));

        // create and fill the first message part
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(email.getDescription());

        // create the second message part
        MimeBodyPart mbp2 = new MimeBodyPart();
        // attach the file to the message
        // attach log file
        File logFile = Platform.getLogFileLocation().toFile();
        mbp2.attachFile(logFile.getPath());

        // create the Multipart and add its parts to it
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(mbp1);
        mp.addBodyPart(mbp2);

        // add the Multipart to the message
        message.setContent(mp);

        // set the Date: header
        message.setSentDate(new Date());

        Transport.send(message);
    }

}
