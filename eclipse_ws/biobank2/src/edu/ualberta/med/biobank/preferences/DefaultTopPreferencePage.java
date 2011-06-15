package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Not used at the moment. Only here for an example.
 * 
 * Similar to Eclipse Preferences for XML.
 * 
 */
public class DefaultTopPreferencePage extends PreferencePage implements
    IWorkbenchPreferencePage {
    private Composite createComposite(Composite parent, int numColumns) {
        noDefaultAndApplyButton();

        Composite composite = new Composite(parent, 0);

        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;
        composite.setLayout(layout);

        GridData data = new GridData(4);
        data.horizontalIndent = 0;
        data.verticalAlignment = 4;
        data.horizontalAlignment = 4;
        composite.setLayoutData(data);

        return composite;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = createScrolledComposite(parent);

        String description = Messages.DefaultTopPreferencePage_description;
        Text text = new Text(composite, 8);

        text.setBackground(composite.getBackground());
        text.setText(description);

        setSize(composite);
        return composite;
    }

    private Composite createScrolledComposite(Composite parent) {
        ScrolledComposite sc1 = new ScrolledComposite(parent, 768);
        sc1.setLayoutData(new GridData(1808));
        Composite composite = createComposite(sc1, 1);
        sc1.setContent(composite);

        setSize(composite);
        return composite;
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    private void setSize(Composite composite) {
        if (composite != null) {
            applyDialogFont(composite);
            Point minSize = composite.computeSize(-1, -1);
            composite.setSize(minSize);

            if (composite.getParent() instanceof ScrolledComposite) {
                ScrolledComposite sc1 = (ScrolledComposite) composite
                    .getParent();
                sc1.setMinSize(minSize);
                sc1.setExpandHorizontal(true);
                sc1.setExpandVertical(true);
            }
        }
    }
}