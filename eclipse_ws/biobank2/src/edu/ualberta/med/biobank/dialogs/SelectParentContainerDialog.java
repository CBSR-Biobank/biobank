package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

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
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = "Selecting new parent container";
        shell.setText(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(contents, SWT.NONE);
        label.setText("Available Parents: ");
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.LEFT;
        gd.verticalAlignment = SWT.TOP;

        label.setLayoutData(gd);
        createCombo(contents);

        return contents;
    }

    private void createCombo(Composite parent) {
        comboViewer = new ComboViewer(parent);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((ContainerWrapper) element).getLabel() + " ("
                    + ((ContainerWrapper) element).getContainerType().getName()
                    + ")";
            }
        });
        comboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    saveSelectedContainer();
                }
            });
        comboViewer.setComparator(new ViewerComparator());
        comboViewer.setInput(containers);
        comboViewer.getCombo().select(0);
        saveSelectedContainer();
    }

    private void saveSelectedContainer() {
        selectedContainer = (ContainerWrapper) ((IStructuredSelection) comboViewer
            .getSelection()).getFirstElement();
    }

    public ContainerWrapper getSelectedContainer() {
        return selectedContainer;
    }

}
