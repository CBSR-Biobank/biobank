package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
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
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class ContainerEmptyLocationsEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.ContainerEmptyLocationsEditor";

    private BiobankText containerLabel;
    private TopContainerListWidget topContainers;
    private IObservableValue listStatus = new WritableValue(Boolean.FALSE,
        Boolean.class);

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100 };
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(containerLabel.getText());
        params.add(topContainers.getSelectedContainers());
        return params;
    }

    @Override
    protected void createOptionSection(Composite parameterSection) {
        containerLabel = createCustomText("Container Label", parameterSection);
        widgetCreator.createLabel(parameterSection, "Top Containers");
        topContainers = new TopContainerListWidget(parameterSection, SWT.NONE);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), listStatus, "Top Container List Empty");
        topContainers
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    listStatus.setValue(topContainers.getEnabled());
                }
            });
        topContainers.adaptToToolkit(toolkit, true);
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
                }
            }
        });
        widget.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB) {
                    filterList(widget.getText());
                }
            }

        });
        widget.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                topContainers.setEnabled(false);
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
        return new String[] { "Location", "Pallet Type" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add("Container Label");
        paramNames.add("Top Containers");
        return paramNames;
    }

}
