package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class SmartCombo extends Combo {

    private String[] items;

    public SmartCombo(Composite parent, String[] args) {
        super(parent, SWT.DROP_DOWN);
        super.setItems(args);
        items = args;
        select(0);
        this.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (items != null) {
                    refineList(getText());
                    setListVisible(true);
                }
            }
        });
    }

    protected void refineList(String text) {
        this.remove(0, getItemCount() - 1);
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (item.startsWith(text))
                add(item);
        }
    }
}
