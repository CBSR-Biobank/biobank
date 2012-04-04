package edu.ualberta.med.biobank.utils;

import java.io.File;
import java.text.MessageFormat;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class FilePromptUtil {

    /**
     * Creates the given directory, if it does not exist, after prompting the
     * user for permission. Checks if the <code>File</code> is a directory and
     * is writable.
     * 
     * @param dir
     * @return <code>true</code> if the directory exists and is writable.
     */
    // TODO: accept a list of checks to apply? Order matters.
    public static boolean isWritableDir(File dir) {
        if (!dir.exists()) {
            boolean createPath = BgcPlugin.openConfirm(
                "Create Path", MessageFormat
                    .format("Path {0} does not exist. Would you like to create it?", dir));

            if (!createPath) {
                return false;
            }

            if (!dir.mkdirs()) {
                BgcPlugin.openAsyncError(
                    "Error Creating Path",
                    MessageFormat.format(
                        "An error occurred. Could not create path {0}.", dir));
                return false;
            }
        }

        if (!dir.isDirectory()) {
            BgcPlugin.openAsyncError(
                "Error Creating Path", MessageFormat
                    .format("An error occurred. The path {0} is not a directory.",
                        dir));
            return false;
        }

        if (!dir.canWrite()) {
            BgcPlugin.openAsyncError("Path Error",
                MessageFormat.format(
                    "An error occurred. Unable to write to path {0}.", dir));
            return false;
        }

        return true;
    }
}
