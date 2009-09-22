package edu.ualberta.med.biobank.logs;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ActivityLogAppender extends AppenderSkeleton {

    private MessageConsole messageConsole;
    private MessageConsoleStream msg;
    private StringBuffer logBuffer;

    public ActivityLogAppender(String name) {
        setName(name);
        messageConsole = new MessageConsole(name, null);
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(
            new IConsole[] { messageConsole });
        msg = messageConsole.newMessageStream();
        setLayout(new PatternLayout("%d{ABSOLUTE} %m%n"));
        logBuffer = new StringBuffer();
    }

    @Override
    protected void append(LoggingEvent event) {
        String text = this.layout.format(event);
        msg.print(text);
        logBuffer.append(text);
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

    public StringBuffer getLogBuffer() {
        return logBuffer;
    }

}
