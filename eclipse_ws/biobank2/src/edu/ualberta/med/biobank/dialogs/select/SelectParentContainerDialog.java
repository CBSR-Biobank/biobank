package edu.ualberta.med.biobank.dialogs.select;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

/**
 * Allows the user to select a parent container when more than one is available
 */

public class SelectParentContainerDialog extends BgcBaseDialog {
    private Collection<ContainerWrapper> containers;
    private ComboViewer comboViewer;
    protected ContainerWrapper selectedContainer;

    public SelectParentContainerDialog(Shell parent,
        Collection<ContainerWrapper> containers) {
        super(parent);
        Assert.isNotNull(containers);
        this.containers = containers;
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.SelectParentContainerDialog_dialog_title;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.SelectParentContainerDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.SelectParentContainerDialog_main_title;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        comboViewer = getWidgetCreator().createComboViewer(contents,
            Messages.SelectParentContainerDialog_select_label, containers,
            null,
            Messages.SelectParentContainerDialog_select_validation_error_msg,
            null, new BiobankLabelProvider());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                ContainerWrapper container = (ContainerWrapper) element;
                StringBuffer text = new StringBuffer();
                text.append(container.getFullInfoLabel());
                ContainerWrapper parent = container.getParentContainer();
                boolean hasParents = parent != null;
                if (hasParents)
                    text.append(" (") //$NON-NLS-1$  
                        .append(
                            Messages.SelectParentContainerDialog_parents_list_label)
                        .append(": "); //$NON-NLS-1$  
                while (parent != null) {
                    text.append(parent.getFullInfoLabel());
                    parent = parent.getParentContainer();
                    if (parent != null)
                        text.append("; "); //$NON-NLS-1$
                }
                if (hasParents)
                    text.append(")"); //$NON-NLS-1$
                return text.toString();
            }
        });
        comboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    saveSelectedContainer();
                }
            });
    }

    private void saveSelectedContainer() {
        selectedContainer = (ContainerWrapper) ((IStructuredSelection) comboViewer
            .getSelection()).getFirstElement();
    }

    public ContainerWrapper getSelectedContainer() {
        return selectedContainer;
    }

}
