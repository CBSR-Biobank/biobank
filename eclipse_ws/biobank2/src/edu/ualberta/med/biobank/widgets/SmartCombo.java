package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
        combo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (items != null) {
                    refineList(combo.getText());
                    combo.setListVisible(true);
                }
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
    }
}
