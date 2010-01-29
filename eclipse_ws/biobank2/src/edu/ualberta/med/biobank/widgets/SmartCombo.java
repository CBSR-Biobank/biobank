package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class SmartCombo {

    private String[] items;
    private Combo combo;

    public SmartCombo(Composite parent, String[] args) {
        combo = new Combo(parent, SWT.DROP_DOWN);
        combo.setItems(args);
        items = args;
        combo.select(0);
        combo.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (items != null) {
                    refineList(combo.getText());
                    combo.setListVisible(true);
                }
            }
        });
        combo.addMouseListener(new MouseListener() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDown(MouseEvent e) {
                resetList();
            }

            @Override
            public void mouseUp(MouseEvent e) {

            }
        });
    }

    protected void refineList(String text) {
        combo.remove(0, combo.getItemCount() - 1);
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (item.startsWith(text))
                combo.add(item);
        }
        combo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

    protected void resetList() {
        combo.remove(0, combo.getItemCount() - 1);
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            combo.add(item);
        }
        combo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

}
