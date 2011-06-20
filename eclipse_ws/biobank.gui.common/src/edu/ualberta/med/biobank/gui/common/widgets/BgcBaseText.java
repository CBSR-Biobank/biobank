package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BgcBaseText extends BgcBaseWidget {

    private Text text;
    private boolean alreadyFocused;

    public BgcBaseText(Composite parent, int style) {
        this(parent, style, null);
    }

    public BgcBaseText(Composite parent, int style, FormToolkit toolkit) {
        super(parent, SWT.NONE);
        this.alreadyFocused = false;
        if (toolkit == null) {
            this.text = new Text(this, style | SWT.BORDER);
        } else {
            this.text = toolkit.createText(this, "", style | SWT.BORDER); //$NON-NLS-1$
        }
        this.text.addFocusListener(getFocusListener());
        this.text.addMouseListener(getMouseListener());
        this.text.addModifyListener(getModifyListener());
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        this.setLayout(layout);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        layout();
    }

    private FocusListener getFocusListener() {
        return new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                text.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                alreadyFocused = false;
            }
        };
    }

    private MouseListener getMouseListener() {
        return new MouseListener() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {

            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseUp(MouseEvent e) {
                if (!alreadyFocused && text.getSelectionCount() == 0) {
                    text.selectAll();
                    alreadyFocused = true;
                } else
                    alreadyFocused = false;
            }

        };
    }

    private ModifyListener getModifyListener() {
        return new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                alreadyFocused = false;
            }
        };
    }

    @Override
    public void setBackground(Color color) {
        if (text == null)
            return;
        text.setBackground(color);
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public String getText() {
        return text.getText().trim();
    }

    public Text getTextBox() {
        return text;
    }

    public void setSelection(int start, int end) {
        text.setSelection(start, end);
    }

    public void addSelectionListener(SelectionListener s) {
        text.addSelectionListener(s);
    }

    public void removeSelectionListener(SelectionListener s) {
        text.removeSelectionListener(s);
    }

    public void addVerifyListener(VerifyListener v) {
        text.addVerifyListener(v);
    }

    public void removeVerifyListener(VerifyListener v) {
        text.removeVerifyListener(v);
    }

    @Override
    public void addKeyListener(KeyListener listener) {
        text.addKeyListener(listener);
    }

    @Override
    public void removeKeyListener(KeyListener listener) {
        text.removeKeyListener(listener);
    }

    @Override
    public void addListener(int eventType, Listener listener) {
        if (text == null)
            return;
        text.addListener(eventType, listener);
    }

    @Override
    public void removeListener(int eventType, Listener listener) {
        if (text == null)
            return;
        text.removeListener(eventType, listener);
    }

    public void addModifyListener(ModifyListener modifyListener) {
        text.addModifyListener(modifyListener);
    }

    public void removeModifyListener(ModifyListener modifyListener) {
        text.addModifyListener(modifyListener);
    }

    public void selectAll() {
        text.selectAll();
    }

    public String getLineDelimiter() {
        return text.getLineDelimiter();
    }

    public void setEditable(boolean b) {
        text.setEditable(b);
    }

    @Override
    public void setEnabled(boolean enabled) {
        text.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void setToolTipText(String string) {
        text.setToolTipText(string);
    }
}
