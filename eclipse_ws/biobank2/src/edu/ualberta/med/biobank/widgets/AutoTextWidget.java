package edu.ualberta.med.biobank.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class AutoTextWidget extends BiobankWidget {

    private Text text;
    private List<? extends ModelWrapper<?>> elements;
    private Class<?> type;
    boolean modified;
    private int selectionIndex;

    public AutoTextWidget(Composite parent, int style,
        List<? extends ModelWrapper<?>> elements, Class<?> type) {
        super(parent, style);
        GridLayout gl = new GridLayout();
        gl.marginWidth = 0;
        this.setLayout(gl);
        text = new Text(this, SWT.BORDER);
        this.elements = elements;
        this.type = type;
        this.selectionIndex = 0;
        modified = false;

        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (modified == false) {
                    String newText = closestMatch(text.getText());
                    AutoTextWidget.this.selectionIndex = text.getSelection().x;
                    modified = true;
                    if (newText.length() > 0)
                        text.insert(newText.substring(selectionIndex));
                    modified = false;
                }
                text.setSelection(selectionIndex, text.getCharCount());
            }
        });

        text.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS)
                    modified = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS)
                    modified = false;
            }
        });

        text.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                text.clearSelection();
            }
        });
    }

    protected String closestMatch(String text) {
        for (ModelWrapper<?> s : elements) {
            if (type.cast(s).toString().startsWith(text))
                return type.cast(s).toString();
        }
        return "";
    }

    public Object getText() {
        return text.getText();
    }

    public void setLayoutData(GridData data) {
        text.setLayoutData(data);
    }
}
