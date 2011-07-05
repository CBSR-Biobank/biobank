package edu.ualberta.med.biobank.forms.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;

/**
 * When Enter key is used, go to next selectable widget
 */
public class EnterKeyToNextFieldListener extends KeyAdapter {

    public static final EnterKeyToNextFieldListener INSTANCE = new EnterKeyToNextFieldListener();

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.character == SWT.CR) {
            ((Control) e.widget).traverse(SWT.TRAVERSE_TAB_NEXT);
        }
    }

}
