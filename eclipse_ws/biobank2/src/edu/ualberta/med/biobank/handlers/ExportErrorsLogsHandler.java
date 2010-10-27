package edu.ualberta.med.biobank.handlers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;

public class ExportErrorsLogsHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String selected = openFileSelection();
        if (selected != null) {
            if (!selected.endsWith(".zip")) {
                selected += ".zip";
            }
            File f = new File(selected);
            File parentFolder = f.getParentFile();
            if (parentFolder.canWrite()) {
                createZip(selected);
            } else {
                BioBankPlugin.openAsyncError("Path problem",
                    "Cannot write in '" + parentFolder.getAbsolutePath() + "'");
                openFileSelection();
            }
        }
        return null;
    }

    private void createZip(final String zipFile) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                ZipOutputStream out = null;
                FileInputStream in = null;
                try {
                    out = new ZipOutputStream(new FileOutputStream(zipFile));

                    List<File> files = getFiles();

                    // Compress the files
                    for (File file : files) {
                        in = new FileInputStream(file);
                        // Add ZIP entry to output stream.
                        out.putNextEntry(new ZipEntry(file.getName()));
                        // Transfer bytes from the file to the ZIP file
                        int len;
                        byte[] buf = new byte[1024];
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        // Complete the entry
                        out.closeEntry();
                        in.close();
                    }
                } catch (FileNotFoundException e) {
                    openError(e);
                } catch (IOException e) {
                    openError(e);
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        // Complete the ZIP file
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        BioBankPlugin.openAsyncError("Error closing files", e);
                    }
                }
                exportComplete(zipFile);
            }
        });
    }

    protected List<File> getFiles() {
        List<File> files = new ArrayList<File>();
        // .log file
        files.add(Platform.getLogFileLocation().toFile());
        // activitylog files of the current day
        String activityLogsPath = BioBankPlugin.getActivityLogPath();
        File activityLogsFolder = new File(activityLogsPath);
        if (activityLogsFolder.exists() && activityLogsFolder.isDirectory()) {
            final Calendar todayDate = Calendar.getInstance();
            File[] activityLogsFiles = activityLogsFolder
                .listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        if (file.isFile() && file.getName().endsWith(".log")) {
                            Calendar modificationDate = Calendar.getInstance();
                            modificationDate.setTime(new Date(file
                                .lastModified()));
                            return todayDate.get(Calendar.DATE) == modificationDate
                                .get(Calendar.DATE)
                                && todayDate.get(Calendar.MONTH) == modificationDate
                                    .get(Calendar.MONTH)
                                && todayDate.get(Calendar.YEAR) == modificationDate
                                    .get(Calendar.YEAR);
                        }
                        return false;
                    }
                });
            files.addAll(Arrays.asList(activityLogsFiles));
        }
        return files;
    }

    protected void exportComplete(String zipFile) {
        BioBankPlugin.openInformation("Export complete",
            "Log informations have been successfully exported in file "
                + zipFile);

    }

    protected void openError(Exception e) {
        BioBankPlugin.openAsyncError("Problem while exporting", e);
    }

    private String openFileSelection() {
        FileDialog fd = new FileDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell());
        fd.setText("Select destination zip file");
        fd.setFilterExtensions(new String[] { "*.zip" });
        String selected = fd.open();
        return selected;
    }
}
