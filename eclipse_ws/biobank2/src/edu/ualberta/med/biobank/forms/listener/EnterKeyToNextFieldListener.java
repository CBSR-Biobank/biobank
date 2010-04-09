package edu.ualberta.med.biobank.forms.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

/**
 * When Enter key is used, go to next selectable widget
 */
public class EnterKeyToNextFieldListener implements KeyListener {

    public static final EnterKeyToNextFieldListener INSTANCE = new EnterKeyToNextFieldListener();

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.keyCode == 13) {
            ((Control) e.widget).traverse(SWT.TRAVERSE_TAB_NEXT);
        }
    }

}
