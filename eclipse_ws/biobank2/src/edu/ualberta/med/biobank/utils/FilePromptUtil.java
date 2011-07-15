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
                Messages.FilePromptUtil_create_path_title, MessageFormat
                    .format(Messages.FilePromptUtil_create_path_msg, dir));

            if (!createPath) {
                return false;
            }

            if (!dir.mkdirs()) {
                BgcPlugin.openAsyncError(
                    Messages.FilePromptUtil_create_path_error_title,
                    MessageFormat.format(
                        Messages.FilePromptUtil_create_pathe_error_msg, dir));
                return false;
            }
        }

        if (!dir.isDirectory()) {
            BgcPlugin.openAsyncError(
                Messages.FilePromptUtil_create_path_error_title, MessageFormat
                    .format(Messages.FilePromptUtil_path_directory_error_msg,
                        dir));
            return false;
        }

        if (!dir.canWrite()) {
            BgcPlugin.openAsyncError(Messages.FilePromptUtil_path_error_title,
                MessageFormat.format(
                    Messages.FilePromptUtil_path_write_error_msg, dir));
            return false;
        }

        return true;
    }
}
