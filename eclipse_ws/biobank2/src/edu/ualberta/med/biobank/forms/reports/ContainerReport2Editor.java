package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerReport2Editor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.ContainerEmptyLocationsEditor"; //$NON-NLS-1$

    private BgcBaseText containerLabel;
    private TopContainerListWidget topContainers;
    private IObservableValue listStatus = new WritableValue(Boolean.FALSE,
        Boolean.class);

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        params.add(containerLabel.getText());
        report.setContainerList(ReportsEditor
            .containerIdsToString(topContainers.getSelectedContainerIds()));
        report.setParams(params);
    }

    @Override
    protected void createOptionSection(Composite parameterSection) {
        containerLabel = createCustomText(
            Messages.ContainerEmptyLocationsEditor_container_label_label,
            parameterSection);
        topContainers = new TopContainerListWidget(parameterSection, toolkit);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), listStatus,
            Messages.ContainerEmptyLocationsEditor_top_cont_validation_msg);
        topContainers.addSelectionChangedListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                listStatus.setValue(topContainers.getEnabled());
            }
        });
    }

    protected BgcBaseText createCustomText(String labelText, Composite parent) {
        final BgcBaseText widget = (BgcBaseText) widgetCreator
            .createLabelledWidget(parent, BgcBaseText.class, SWT.NONE,
                labelText, ""); //$NON-NLS-1$
        widget.addKeyListener(new KeyAdapter() {
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

    protected void validate(String label) {
        try {
            if (label.equals("") //$NON-NLS-1$
                || ContainerWrapper.getContainersByLabel(
                    SessionManager.getAppService(), label).size() > 0)
                filterList(label);
            else {
                throw new ApplicationException();
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                Messages.ContainerEmptyLocationsEditor_label_error_title, NLS
                    .bind(
                        Messages.ContainerEmptyLocationsEditor_label_error_msg,
                        label));
        }

    }

    protected void filterList(String text) {
        topContainers.filterBy(text);
        page.layout(true, true);
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] {
            Messages.ContainerEmptyLocationsEditor_location_label,
            Messages.ContainerEmptyLocationsEditor_type_label };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames
            .add(Messages.ContainerEmptyLocationsEditor_container_label_label);
        paramNames.add(Messages.ContainerEmptyLocationsEditor_top_label);
        return paramNames;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(containerLabel.getText());
        params.add(topContainers.getSelectedContainerNames());
        return params;
    }

    @Override
    protected void onReset() throws Exception {
        containerLabel.setText(""); //$NON-NLS-1$
        topContainers.reset();
        validate(""); //$NON-NLS-1$
        super.onReset();
    }
}
