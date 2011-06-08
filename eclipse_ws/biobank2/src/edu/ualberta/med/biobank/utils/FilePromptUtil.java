package edu.ualberta.med.biobank.utils;

import java.io.File;
import java.text.MessageFormat;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;

public class FilePromptUtil {
    private static final String CREATE_PATH_TITLE = "Create Path";
    private static final String CREATE_PATH_MESSAGE = "Path {0} does not exist. Would you like to create it?";
    private static final String CREATE_PATH_ERROR_TITLE = "Error Creating Path";
    private static final String CREATE_PATH_ERROR_MESSAGE = "An error occured. Could not create path {0}.";
    private static final String PATH_ERROR_TITLE = "Path Error";
    private static final String PATH_CANNOT_WRITE_MESSAGE = "An error occured. Unable to write to path {0}.";
    private static final String PATH_NOT_DIRECTORY_MESSAGE = "An error occured. The path {0} is not a directory.";

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
            boolean createPath = BiobankGuiCommonPlugin.openConfirm(CREATE_PATH_TITLE,
                MessageFormat.format(CREATE_PATH_MESSAGE, dir));

            if (!createPath) {
                return false;
            }

            if (!dir.mkdirs()) {
                BiobankGuiCommonPlugin.openAsyncError(CREATE_PATH_ERROR_TITLE,
                    MessageFormat.format(CREATE_PATH_ERROR_MESSAGE, dir));
                return false;
            }
        }

        if (!dir.isDirectory()) {
            BiobankGuiCommonPlugin.openAsyncError(CREATE_PATH_ERROR_TITLE,
                MessageFormat.format(PATH_NOT_DIRECTORY_MESSAGE, dir));
            return false;
        }

        if (!dir.canWrite()) {
            BiobankGuiCommonPlugin.openAsyncError(PATH_ERROR_TITLE,
                MessageFormat.format(PATH_CANNOT_WRITE_MESSAGE, dir));
            return false;
        }

        return true;
    }
}
