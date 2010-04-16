package edu.ualberta.med.biobank.dialogs;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.utils.EMailDescriptor;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;

public class SendErrorMessageDialog extends BiobankDialog {

    private EMailDescriptor email;

    // private int compositeHeight;

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

        // ExpandBar bar = new ExpandBar(contents, SWT.NONE);
        // bar.setLayoutData(new GridData(GridData.FILL_BOTH));
        // final Composite compositeWithBar = new Composite(bar, SWT.NONE);
        // compositeWithBar.setLayout(new GridLayout(2, false));
        // GridData compData = new GridData(GridData.FILL_BOTH);
        // compositeWithBar.setLayoutData(compData);
        //
        // createBoundWidgetWithLabel(compositeWithBar, Text.class, SWT.NONE,
        // "Smtp server", new String[0], PojoObservables.observeValue(email,
        // "smtpServer"), new NonEmptyStringValidator(
        // "Please enter the smtp server name"));
        //
        // createBoundWidgetWithLabel(compositeWithBar, Text.class, SWT.NONE,
        // "Port", new String[0], PojoObservables.observeValue(email,
        // "serverPort"), new NonEmptyStringValidator(
        // "Please enter the server port"));
        //
        // createBoundWidgetWithLabel(compositeWithBar, Text.class, SWT.NONE,
        // "Username", new String[0], PojoObservables.observeValue(email,
        // "serverUsername"), new NonEmptyStringValidator(
        // "Please enter the server username"));
        //
        // createBoundWidgetWithLabel(compositeWithBar, Text.class,
        // SWT.PASSWORD,
        // "Password", new String[0], PojoObservables.observeValue(email,
        // "serverPassword"), new NonEmptyStringValidator(
        // "Please enter the server password"));

        // ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
        // item.setText("Email configuration");
        // compositeHeight = compositeWithBar
        // .computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        // item.setHeight(compositeHeight);
        // item.setControl(compositeWithBar);
        // item.setExpanded(true);
        //
        // bar.addExpandListener(new ExpandAdapter() {
        // @Override
        // public void itemCollapsed(ExpandEvent e) {
        // compositeWithBar.layout(true, true);
        // Point shellSize = getShell().getSize();
        // getShell().setSize(shellSize.x, shellSize.y - compositeHeight);
        // }
        //
        // @Override
        // public void itemExpanded(ExpandEvent e) {
        // compositeWithBar.layout(true, true);
        // compositeWithBar.setVisible(true);
        // Point shellSize = getShell().getSize();
        // getShell().setSize(shellSize.x, shellSize.y + compositeHeight);
        // }
        // });
    }

    @Override
    protected void okPressed() {
        try {
            sendMail();
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error sending mail", e);
        }
        super.okPressed();
    }

    private void initEmailDescriptor() {
        IPreferenceStore node = BioBankPlugin.getDefault().getPreferenceStore();
        email = new EMailDescriptor();
        email.setSmtpServer(node
            .getString(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER));
        email.setServerPort(node
            .getInt(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PORT));
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
            @Override
            public void run(final IProgressMonitor monitor) {
                monitor.beginTask("Sending mail...", IProgressMonitor.UNKNOWN);
                try {
                    Properties props = new Properties();
                    props.setProperty("mail.transport.protocol", "smtp");
                    props.setProperty("mail.host", email.getSmtpServer());
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.port", email.getServerPort());
                    props.put("mail.smtp.socketFactory.port", email
                        .getServerPort());
                    props.put("mail.smtp.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.socketFactory.fallback", "false");

                    Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(email
                                    .getServerUsername(), email
                                    .getServerPassword());
                            }
                        });
                    session.setDebug(false);

                    MimeMessage message = new MimeMessage(session);
                    message.setSubject(email.getTitle());
                    message.setContent(email.getDescription(), "text/plain");

                    message.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(email.getReceiverEmail()));

                    Multipart mp = new MimeMultipart();

                    // create and fill the first message part
                    MimeBodyPart mbp1 = new MimeBodyPart();
                    String text = email.getDescription();
                    if (SessionManager.getInstance().isConnected()) {
                        text += "\n\n------\nCreated by user "
                            + SessionManager.getInstance().getSession()
                                .getUserName();
                    }
                    mbp1.setText(text);
                    mp.addBodyPart(mbp1);

                    // create the second message part
                    MimeBodyPart mbp2 = new MimeBodyPart();
                    // attach the file to the message
                    // attach log file
                    File logFile = Platform.getLogFileLocation().toFile();
                    mbp2.attachFile(logFile.getPath());
                    mp.addBodyPart(mbp2);

                    // add the Multipart to the message
                    message.setContent(mp);

                    // set the Date: header
                    message.setSentDate(new Date());

                    Transport.send(message);
                    monitor.done();
                } catch (AuthenticationFailedException afe) {
                    BioBankPlugin.openAsyncError("Authentification Error",
                        "Wrong authentification for "
                            + email.getServerUsername());
                    monitor.setCanceled(true);
                    return;
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError("Error in sending email", e);
                    monitor.setCanceled(true);
                    return;
                }
            }
        });
    }
}
