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

    private Text textWidget;
    private boolean alreadyFocused;

    public BgcBaseText(Composite parent, int style) {
        this(parent, style, null);
    }

    @SuppressWarnings("nls")
    public BgcBaseText(Composite parent, int style, FormToolkit toolkit) {
        super(parent, SWT.NONE);
        this.alreadyFocused = false;
        if (toolkit == null) {
            this.textWidget = new Text(this, style | SWT.BORDER);
        } else {
            this.textWidget = toolkit.createText(this, "", style | SWT.BORDER);
        }
        this.textWidget.addFocusListener(getFocusListener());
        this.textWidget.addMouseListener(getMouseListener());
        this.textWidget.addModifyListener(getModifyListener());
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        this.setLayout(layout);
        textWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        layout();
    }

    private FocusListener getFocusListener() {
        return new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                textWidget.selectAll();
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
                if (!alreadyFocused && textWidget.getSelectionCount() == 0) {
                    textWidget.selectAll();
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
        if (textWidget == null)
            return;
        textWidget.setBackground(color);
    }

    public void setText(String text) {
        this.textWidget.setText(text);
    }

    public String getText() {
        return textWidget.getText().trim();
    }

    public Text getTextWidget() {
        return textWidget;
    }

    public void setSelection(int start, int end) {
        textWidget.setSelection(start, end);
    }

    public void addSelectionListener(SelectionListener s) {
        textWidget.addSelectionListener(s);
    }

    public void removeSelectionListener(SelectionListener s) {
        textWidget.removeSelectionListener(s);
    }

    public void addVerifyListener(VerifyListener v) {
        textWidget.addVerifyListener(v);
    }

    public void removeVerifyListener(VerifyListener v) {
        textWidget.removeVerifyListener(v);
    }

    @Override
    public void addKeyListener(KeyListener listener) {
        textWidget.addKeyListener(listener);
    }

    @Override
    public void removeKeyListener(KeyListener listener) {
        textWidget.removeKeyListener(listener);
    }

    @Override
    public void addListener(int eventType, Listener listener) {
        if (textWidget == null)
            return;
        textWidget.addListener(eventType, listener);
    }

    @Override
    public void removeListener(int eventType, Listener listener) {
        if (textWidget == null)
            return;
        textWidget.removeListener(eventType, listener);
    }

    public void addModifyListener(ModifyListener modifyListener) {
        textWidget.addModifyListener(modifyListener);
    }

    public void removeModifyListener(ModifyListener modifyListener) {
        textWidget.addModifyListener(modifyListener);
    }

    public void selectAll() {
        textWidget.selectAll();
    }

    public String getLineDelimiter() {
        return textWidget.getLineDelimiter();
    }

    public void setEditable(boolean b) {
        textWidget.setEditable(b);
    }

    public void setTextLimit(int limit) {
        textWidget.setTextLimit(limit);
    }

    @Override
    public void setEnabled(boolean enabled) {
        textWidget.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void setToolTipText(String string) {
        textWidget.setToolTipText(string);
    }
}
