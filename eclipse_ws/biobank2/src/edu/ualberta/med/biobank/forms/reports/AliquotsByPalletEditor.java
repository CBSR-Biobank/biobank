package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;

public class AliquotsByPalletEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotsByPalletEditor";

    private BiobankText palletLabel;
    private TopContainerListWidget topContainers;
    private IObservableValue comboStatus = new WritableValue(Boolean.FALSE,
        Boolean.class);

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100, 100 };
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(palletLabel.getText());
        params.add(topContainers.getSelectedContainers());
        return params;
    }

    @Override
    protected void createOptionSection(Composite parameterSection) {
        palletLabel = createCustomText("Pallet Label", parameterSection);
        widgetCreator.createLabel(parameterSection, "Top Containers");
        topContainers = new TopContainerListWidget(parameterSection, SWT.NONE);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), comboStatus, "Pallet not found", IStatus.ERROR);
        topContainers.setEnabled(false);
    }

    protected BiobankText createCustomText(String labelText, Composite parent) {
        final BiobankText widget = (BiobankText) widgetCreator
            .createLabelledWidget(parent, BiobankText.class, SWT.NONE,
                labelText, "");
        widget.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    filterList(widget.getText());
                    comboStatus.setValue(topContainers.getEnabled());
                }
            }
        });
        widget.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB) {
                    filterList(widget.getText());
                    comboStatus.setValue(topContainers.getEnabled());
                }
            }

        });
        widget.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                topContainers.setEnabled(false);
                comboStatus.setValue(false);
            }
        });
        return widget;
    }

    protected void filterList(String text) {
        topContainers.filterBy(text);
        page.layout(true, true);
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Location", "Inventory ID", "Patient", "Type" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add("Pallet Label");
        paramNames.add("Top Containers");
        return paramNames;
    }

}
