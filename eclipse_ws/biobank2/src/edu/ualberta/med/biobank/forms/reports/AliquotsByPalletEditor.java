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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AliquotsByPalletEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotsByPalletEditor";

    private BiobankText palletLabel;
    private TopContainerListWidget topContainers;
    private IObservableValue listStatus = new WritableValue(Boolean.FALSE,
        Boolean.class);

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        params.add(palletLabel.getText());
        report.setParams(params);
        report.setContainerList(ReportsEditor
            .containerIdsToString(topContainers.getSelectedContainerIds()));
    }

    @Override
    protected void createOptionSection(Composite parameterSection) {
        palletLabel = createCustomText("Container Label", parameterSection);
        topContainers = new TopContainerListWidget(parameterSection, toolkit);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), listStatus, "Top Container List Empty");
        topContainers.addSelectionChangedListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                listStatus.setValue(topContainers.getEnabled());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
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
                    validate(widget.getText());
                }
            }
        });
        widget.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB) {
                    validate(widget.getText());
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
    }

    protected void validate(String label) {
        try {
            List<ContainerWrapper> validContainers = new ArrayList<ContainerWrapper>();
            List<ContainerWrapper> containers = ContainerWrapper
                .getContainersByLabel(SessionManager.getAppService(), label);
            for (ContainerWrapper c : containers)
                if (c.getContainerType().getSpecimenTypeCollection().size() > 0)
                    validContainers.add(c);
            if (label.equals("") || validContainers.size() > 0)
                filterList(label);
            else {
                throw new ApplicationException();
            }
        } catch (ApplicationException e) {
            BiobankGuiCommonPlugin.openAsyncError("Invalid label",
                "No bottom-level container labelled " + label);
        }
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Location", "Inventory ID", "Patient", "Type" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add("Container Label");
        paramNames.add("Top Containers");
        return paramNames;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(palletLabel.getText());
        params.add(topContainers.getSelectedContainerNames());
        return params;
    }
}
