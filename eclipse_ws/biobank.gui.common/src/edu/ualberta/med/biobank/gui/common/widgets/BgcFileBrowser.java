package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class BgcFileBrowser extends BgcBaseWidget {

    private BgcBaseText textfield;

    private Button browse;

    private FileDialog fileDialog;

    private String[] filterExtensions;

    protected ListenerList fileSelectionListeners = new ListenerList();

    public BgcFileBrowser(Composite parent, String label, int style,
        String[] extensions) {
        super(parent, style);
        this.filterExtensions = extensions;

        setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        setLayout(new GridLayout(3, false));
        Label l = new Label(this, SWT.NONE);
        l.setText(label + ":"); //$NON-NLS-1$
        textfield = new BgcBaseText(this, SWT.NONE);
        textfield.setEditable(false);
        textfield.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));

        browse = new Button(this, style);
        browse.setText(Messages.BgcFileBrowser_browse);
        browse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fileDialog = new FileDialog(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), SWT.OPEN);
                fileDialog.setText(Messages.BgcFileBrowser_open);
                fileDialog.setFilterExtensions(filterExtensions);
                final String path = fileDialog.open();
                if (path != null) {
                    textfield.setText(path);
                    Object[] listeners = fileSelectionListeners.getListeners();
                    for (int i = 0; i < listeners.length; ++i) {
                        final IBgcFileBrowserListener l = (IBgcFileBrowserListener) listeners[i];
                        SafeRunnable.run(new SafeRunnable() {
                            @Override
                            public void run() {
                                l.fileSelected(path);
                            }
                        });
                    }
                }
            }
        });
    }

    public String getFilePath() {
        return textfield.getText();
    }

    public void reset() {
        textfield.setText(""); //$NON-NLS-1$
    }

    public FileDialog getFileDialog() {
        return fileDialog;
    }

    public void addFileSelectedListener(IBgcFileBrowserListener listener) {
        fileSelectionListeners.add(listener);
    }

}
