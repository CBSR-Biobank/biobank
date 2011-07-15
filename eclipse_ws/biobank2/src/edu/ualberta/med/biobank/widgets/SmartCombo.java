package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public class SmartCombo extends Composite {

    List<SelectionListener> listeners;
    private String[] items;
    private Combo combo;
    boolean ignore;
    boolean traversed;
    char keyPress;

    public SmartCombo(Composite parent, int style) {
        super(parent, style);
        listeners = new ArrayList<SelectionListener>();
        GridLayout gl = new GridLayout();
        gl.marginWidth = 0;
        this.setLayout(gl);
        combo = new Combo(this, SWT.DROP_DOWN);
        items = new String[0];
        ignore = false;
        traversed = false;

        combo.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (Character.isLetter(e.character) || e.keyCode == SWT.DEL
                    || e.keyCode == SWT.BS || e.keyCode == SWT.Selection) {
                    ignore = false;
                    traversed = false;
                } else {
                    ignore = true;
                    traversed = true;
                }
                // in case the user hits multiple keys, only consider the last
                // pressed
                keyPress = e.character;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!ignore && keyPress == e.character) {
                    refineList(combo.getText());
                    String newText = closestMatch();
                    int caretPosition = combo.getSelection().x;
                    // combo might need to be expanded
                    combo.setVisibleItemCount(combo.getItemCount());
                    combo.setListVisible(true);
                    if (newText.length() > 0) {
                        if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
                            combo.setText(newText.substring(0, caretPosition));
                            combo.setSelection(new Point(caretPosition,
                                caretPosition));
                        } else {
                            combo.setText(newText);
                            combo.setSelection(new Point(caretPosition, newText
                                .length()));
                        }
                    }
                }
                if (e.keyCode == SWT.Selection && !valid()) {
                    reset();
                } else if (e.keyCode == SWT.Selection) {
                    combo.setListVisible(false);
                    Event event = new Event();
                    event.widget = SmartCombo.this;
                    notifyListeners(new SelectionEvent(event));
                }
                // ignore = false;
                // traversed = false;
            }
        });

        combo.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!valid()) {
                    reset();
                }
            }
        });

        combo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }

            @Override
            public void widgetSelected(SelectionEvent e) {

                // ignore traversal
                if (!traversed) {
                    // System.out.println("Selected");
                    ignore = true;
                    if (combo.getSelectionIndex() == -1)
                        combo.select(0);
                    ignore = false;
                    Event event = new Event();
                    event.widget = SmartCombo.this;
                    notifyListeners(new SelectionEvent(event));
                } else {
                    Event event = new Event();
                    event.widget = SmartCombo.this;
                    notifyListeners(new SelectionEvent(event));
                }
            }
        });

    }

    protected void reset() {
        refineList(""); //$NON-NLS-1$
        combo.setVisibleItemCount(combo.getItemCount());
        combo.select(0);
        Event event = new Event();
        event.widget = SmartCombo.this;
        notifyListeners(new SelectionEvent(event));
    }

    protected boolean valid() {
        String text = combo.getText();
        for (int i = 0; i < items.length; i++) {
            if (items[i].compareTo(text) == 0)
                return true;
        }
        return false;
    }

    protected void refineList(String text) {
        combo.remove(0, combo.getItemCount() - 1);
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (item.startsWith(text))
                combo.add(item);
        }
    }

    protected String closestMatch() {
        if (combo.getItemCount() > 0)
            return combo.getItem(0);
        else
            return ""; //$NON-NLS-1$
    }

    public void setInput(String[] items) {
        this.items = items;
        reset();
    }

    public void setLayoutData(GridData combodata) {
        combo.setLayoutData(combodata);
    }

    public String getSelection() {
        return combo.getText();
    }

    public void addSelectionListener(SelectionListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(SelectionEvent event) {
        for (SelectionListener listener : listeners) {
            listener.widgetSelected(event);
        }
    }
}
