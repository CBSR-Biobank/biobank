package edu.ualberta.med.biobank.server.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

    private static final String FILE_PREFIX_DEFAULT = "biobanklogappender";

    public static void writeMsgToTmpFile(Throwable t) {
        writeMsgToTmpFile(FILE_PREFIX_DEFAULT, t);
    }

    /**
     * Writes fatal errors to a log file on the system's current directory.
     * 
     * @param t
     */
    public static void writeMsgToTmpFile(String fileprefix, Throwable t) {
        FileWriter writer = null;
        try {
            File f = new File(fileprefix + System.currentTimeMillis() + ".log");
            writer = new FileWriter(f);
            writer.write(getErrorAndStack(t));
            writer.flush();

        } catch (Exception e) {
        } finally {
            try {
                writer.close();
            } catch (Exception e1) {
            }
        }
    }

    public static String getErrorAndStack(Throwable t) {
        if (t == null) {
            return null;
        }
        return t.getMessage() + System.getProperty("line.separator")
            + getStackTrace(t).toString();
    }

    public static StringBuffer getStackTrace(Throwable t) {
        StringWriter stringWriter = new java.io.StringWriter();
        t.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.getBuffer();
    }
}
