package edu.ualberta.med.biobank.utils;

import java.io.File;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class FilePromptUtil {
    private static final I18n i18n = I18nFactory.getI18n(FilePromptUtil.class);

    /**
     * Creates the given directory, if it does not exist, after prompting the
     * user for permission. Checks if the <code>File</code> is a directory and
     * is writable.
     * 
     * @param dir
     * @return <code>true</code> if the directory exists and is writable.
     */
    // TODO: accept a list of checks to apply? Order matters.
    @SuppressWarnings("nls")
    public static boolean isWritableDir(File dir) {
        if (!dir.exists()) {
            boolean createPath =
                BgcPlugin
                    .openConfirm(
                        // dialog title.
                        i18n.tr("Create Path"),
                        // dialog message.
                        i18n.tr(
                            "Path {0} does not exist. Would you like to create it?",
                            dir));

            if (!createPath) {
                return false;
            }

            if (!dir.mkdirs()) {
                BgcPlugin.openAsyncError(
                    // dialog title.
                    i18n.tr("Error Creating Path"),
                    // dialog message.
                    i18n.tr("An error occurred. Could not create path {0}.",
                        dir));
                return false;
            }
        }

        if (!dir.isDirectory()) {
            BgcPlugin.openAsyncError(
                // dialog title.
                i18n.tr("Error Creating Path"),
                // dialog message.
                i18n.tr("An error occurred. The path {0} is not a directory.",
                    dir));
            return false;
        }

        if (!dir.canWrite()) {
            BgcPlugin
                .openAsyncError(
                    // dialog title.
                    i18n.tr("Path Error"),
                    // dialog message.
                    i18n.tr("An error occurred. Unable to write to path {0}.",
                        dir));
            return false;
        }

        return true;
    }
}
