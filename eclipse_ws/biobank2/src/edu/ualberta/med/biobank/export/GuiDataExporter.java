package edu.ualberta.med.biobank.export;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public abstract class GuiDataExporter implements DataExporter {
    private static final I18n i18n = I18nFactory
        .getI18n(GuiDataExporter.class);

    private final String name;

    @SuppressWarnings("nls")
    public static final String LOG_TYPE = "data";

    public GuiDataExporter(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @SuppressWarnings("nls")
    @Override
    public void canExport(Data data) throws DataExportException {
        if (data.getRows() == null || data.getRows().isEmpty()) {
            throw new DataExportException(
                // exception message
                i18n.tr("no results to export"));
        }
    }

    @Override
    public final void export(final Data data, final ITableLabelProvider labelProvider)
        throws DataExportException {
        Shell shell = Display.getDefault().getActiveShell();
        IRunnableContext context = new ProgressMonitorDialog(shell);
        try {
            context.run(true, true, new IRunnableWithProgress() {
                @SuppressWarnings("nls")
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask(
                        // TR: progress monitor title
                        i18n.tr("Exporting data..."),
                        IProgressMonitor.UNKNOWN);
                    try {
                        export(data, labelProvider, monitor);
                    } catch (Exception e) {
                        BgcPlugin.openAsyncError(
                            // TR: error dialog title
                            i18n.tr("Error Exporting Data"), e);
                        return;
                    }
                }
            });
        } catch (InvocationTargetException e) {
            throw new DataExportException(e);
        } catch (InterruptedException e) {
            throw new DataExportException(e);
        }
    }

    protected abstract void export(Data data,
        ITableLabelProvider labelProvider, IProgressMonitor monitor)
        throws DataExportException;

    /**
     * Open a dialog to select a file path from.
     * 
     * @param data
     * @param exts
     * @return selected/ entered path
     */
    @SuppressWarnings("nls")
    public static String getPath(Data data, final String[] exts) {
        final Holder<String> path = new Holder<String>(null);

        if (exts != null) {
            final Holder<String> defaultFilename =
                new Holder<String>(i18n.trc(
                    "default filename for output, without suffix", "output"));

            if (data.getTitle() != null) {
                defaultFilename.setValue(data.getTitle().replaceAll("[^\\w]",
                    "_"));
            }

            defaultFilename.setValue(defaultFilename.getValue() + "_"
                + DateFormatter.formatAsDate(new Date()).replaceAll("_{2,}",
                    "_"));

            Display display = Display.getDefault();
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    if (shell != null) {
                        FileDialog fd = new FileDialog(shell, SWT.SAVE);
                        fd.setOverwrite(true);
                        // TR: export file dialog title
                        fd.setText(i18n.tr("Export as"));
                        fd.setFilterExtensions(exts);
                        fd.setFileName(defaultFilename.getValue());

                        path.setValue(fd.open());
                    }
                }
            });
        }

        return path.getValue();
    }
}
