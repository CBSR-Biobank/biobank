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

import edu.ualberta.med.biobank.forms.linkassign.SpecimenAssignEntryForm;
import edu.ualberta.med.biobank.forms.linkassign.SpecimenLinkEntryForm;
import edu.ualberta.med.biobank.rcp.perspective.LinkAssignPerspective;

/**
 * Display in ConsoleView and possibly printed.
 * 
 * @see LinkAssignPerspective
 * @see SpecimenLinkEntryForm
 * @see SpecimenAssignEntryForm
 */
public class ActivityLogAppender extends AppenderSkeleton {

    private final MessageConsole messageConsole;
    private final MessageConsoleStream consoleStream;
    private final List<LogInfo> logsList;
    @SuppressWarnings("nls")
    private static final char[] SYSTEM_LINE_SEPARATOR = System.getProperty(
        "line.separator").toCharArray();
    @SuppressWarnings("nls")
    public static final PatternLayout layout = new PatternLayout(
        "%d{HH:mm:ss} %m%n");

    public ActivityLogAppender(String name) {
        setName(name);
        messageConsole = new MessageConsole(name, null);
        ConsolePlugin.getDefault().getConsoleManager()
            .addConsoles(new IConsole[] { messageConsole });
        consoleStream = messageConsole.newMessageStream();
        setLayout(layout);
        logsList = new ArrayList<LogInfo>();
    }

    @Override
    protected void append(LoggingEvent event) {
        String text = layout.format(event);
        consoleStream.print(text);
        boolean shouldRemoveLineSeparator = false;
        for (int i = 0; i < SYSTEM_LINE_SEPARATOR.length; i++) {
            char c = SYSTEM_LINE_SEPARATOR[i];
            int positionFromEnd = SYSTEM_LINE_SEPARATOR.length - i;
            if (text.length() >= positionFromEnd) {
                char charAtPosition = text.charAt(text.length()
                    - positionFromEnd);
                if (charAtPosition == c) {
                    shouldRemoveLineSeparator = true;
                } else {
                    shouldRemoveLineSeparator = false;
                    break;
                }
            }
        }
        if (shouldRemoveLineSeparator) {
            text = text.substring(0, text.length()
                - SYSTEM_LINE_SEPARATOR.length);
        }
        logsList.add(new LogInfo(text));
    }

    @Override
    public void close() {
        ConsolePlugin.getDefault().getConsoleManager()
            .removeConsoles(new IConsole[] { messageConsole });
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    public List<LogInfo> getLogsList() {
        return logsList;
    }

}
