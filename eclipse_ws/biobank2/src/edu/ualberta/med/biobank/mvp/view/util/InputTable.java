package edu.ualberta.med.biobank.mvp.view.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Helps to create a table of inputs in a tabular format.
 * 
 * @author jferland
 * 
 */
public class InputTable extends Composite {
    public InputTable(Composite parent) {
        super(parent, SWT.NONE);

        setLayout(new GridLayout(2, false));
        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
    }

    public Label addLabel(String text) {
        Label label = new Label(addLabelContainer(), SWT.WRAP);
        label.setText(text + ":"); //$NON-NLS-1$
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
        return label;
    }

    public Text addText() {
        Text text = new Text(this, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        return text;
    }

    public Text addTextArea() {
        Text text = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP |
            SWT.V_SCROLL | SWT.H_SCROLL);

        GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, true);
        gridData.heightHint = 80;
        text.setLayoutData(gridData);

        return text;
    }

    /**
     * Create a container to maintain a constant left-column width. The label
     * will be put in this.
     * 
     * @return
     */
    private Composite addLabelContainer() {
        Composite box = new Composite(this, SWT.WRAP);

        box.setLayout(new GridLayout(1, false));

        GridData data = new GridData(SWT.LEFT, SWT.TOP, false, true);
        data.verticalIndent = 0;
        data.horizontalIndent = 0;
        data.widthHint = 130;
        box.setLayoutData(data);

        return box;
    }
}
