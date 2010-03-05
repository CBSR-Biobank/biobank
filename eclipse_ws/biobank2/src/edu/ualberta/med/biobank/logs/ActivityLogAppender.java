package edu.ualberta.med.biobank.logs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm;
import edu.ualberta.med.biobank.forms.ScanAssignEntryForm;
import edu.ualberta.med.biobank.forms.ScanLinkEntryForm;
import edu.ualberta.med.biobank.rcp.AliquotManagementPerspective;

/**
 * Display in ConsoleView and possibly printed.
 * 
 * @see AliquotManagementPerspective
 * @see ScanLinkEntryForm
 * @see ScanAssignEntryForm
 * @see CabinetLinkAssignEntryForm
 */
public class ActivityLogAppender extends AppenderSkeleton {

    private MessageConsole messageConsole;
    private MessageConsoleStream consoleStream;
    private List<LogInfo> logsList;

    public ActivityLogAppender(String name) {
        setName(name);
        messageConsole = new MessageConsole(name, null);
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(
            new IConsole[] { messageConsole });
        consoleStream = messageConsole.newMessageStream();
        setLayout(new PatternLayout("%d{ABSOLUTE} %m%n"));
        logsList = new ArrayList<LogInfo>();
    }

    @Override
    protected void append(LoggingEvent event) {
        String text = this.layout.format(event);
        consoleStream.print(text);
        logsList.add(new LogInfo(text.substring(0, text.lastIndexOf("\n"))));
    }

    @Override
    public void close() {
        ConsolePlugin.getDefault().getConsoleManager().removeConsoles(
            new IConsole[] { messageConsole });
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    public List<LogInfo> getLogsList() {
        return logsList;
    }

}
