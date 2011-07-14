package edu.ualberta.med.biobank.widgets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;

public class FileBrowser extends BgcBaseWidget {
    private BgcBaseText textfield;
    private Button browse;
    private String text;

    public FileBrowser(Composite parent, String label, int style) {
        super(parent, style);
        setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        setLayout(new GridLayout(3, false));
        Label l = new Label(this, SWT.NONE);
        l.setText(label + ":");
        textfield = new BgcBaseText(this, SWT.NONE);
        textfield.setEditable(false);
        textfield.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        browse = new Button(this, style);
        browse.setText(Messages.FileBrowser_browse_label);
        browse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fd = new FileDialog(FileBrowser.this.getShell(),
                    SWT.OPEN);
                fd.setText(Messages.FileBrowser_open_label);
                String[] filterExt = { "*.csv" }; //$NON-NLS-1$
                fd.setFilterExtensions(filterExt);
                String path = fd.open();
                if (path != null) {
                    textfield.setText(path);
                    File file = new File(path);
                    loadFile(file);
                }
            }
        });
        // browse.setLayoutData(data);
    }

    public void loadFile(File file) {
        if (file.isFile()) {
            StringBuffer contents = new StringBuffer();
            FileReader fileReader;
            try {
                fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);
                while (br.ready())
                    contents.append(br.readLine()).append("\n"); //$NON-NLS-1$
                br.close();
            } catch (Exception e1) {
                BgcPlugin.openError(Messages.FileBrowser_io_error_title, Messages.FileBrowser_io_error_msg, e1);
            }
            setText(contents.toString());
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        loadFile(new File(textfield.getText()));
        return text;
    }

    public String getFilePath() {
        return textfield.getText();
    }

    public void reset() {
        textfield.setText("");
    }

}
