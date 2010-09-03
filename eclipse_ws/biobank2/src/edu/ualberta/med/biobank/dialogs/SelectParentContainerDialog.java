package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

/**
 * Allows the user to move a container and its contents to a new location
 */

public class SelectParentContainerDialog extends BiobankDialog {
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
        return "Select Parent Container";
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Select the appropriate parent container";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Multiple Parents are Possible";
    }

    @Override
    protected Image getTitleAreaImage() {
        return BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_COMPUTER_KEY);
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        comboViewer = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Select parent", containers, null,
                "A source vessel should be selected");
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                ContainerWrapper container = (ContainerWrapper) element;
                return container.getLabel() + " ("
                    + container.getContainerType().getNameShort() + ")";
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
