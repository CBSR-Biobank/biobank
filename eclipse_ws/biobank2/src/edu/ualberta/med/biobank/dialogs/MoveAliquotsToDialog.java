package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Allows the user to choose a container to which aliquots will be moved
 */
public class MoveAliquotsToDialog extends BiobankDialog {

    private ContainerWrapper oldContainer;

    private IObservableValue newLabel = new WritableValue("", String.class);

    private AdapterTreeWidget adaptersTree;

    private RootNode rootNode;

    public MoveAliquotsToDialog(Shell parent, ContainerWrapper oldContainer) {
        super(parent);
        Assert.isNotNull(oldContainer);
        this.oldContainer = oldContainer;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = "Move aliquots from one container to another";
        shell.setText(title);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Move aliquots from one container " + oldContainer.getLabel()
            + " to another");
        setMessage("Select the new container that can hold the aliquots.");
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        adaptersTree = new AdapterTreeWidget(parent, false);
        adaptersTree.getTreeViewer().getTree().setMenu(null);
        adaptersTree
            .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        rootNode = new RootNode();
        adaptersTree.getTreeViewer().setInput(rootNode);
        rootNode.setTreeViewer(adaptersTree.getTreeViewer());

        adaptersTree.setBackground(Display.getCurrent().getSystemColor(
            SWT.COLOR_BLUE));

        Button button = new Button(parent, SWT.PUSH);
        button.setText("display");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO faire avec toute la liste de sample types et non pas un
                // seul
                List<SampleTypeWrapper> typesFromOlContainer = oldContainer
                    .getContainerType().getSampleTypeCollection();
                try {
                    List<ContainerWrapper> conts = ContainerWrapper
                        .getEmptyContainersHoldingSampleType(SessionManager
                            .getAppService(), SessionManager.getInstance()
                            .getCurrentSite(), typesFromOlContainer.get(0));

                    List<AdapterBase> parentAdapterList = new ArrayList<AdapterBase>();
                    for (ContainerWrapper cont : conts) {
                        ContainerWrapper parent = cont.getParent();
                        if (parent == null) {

                            new ContainerAdapterCustom(rootNode, cont);
                        } else {
                            ContainerAdapterCustom parentAdapter = new ContainerAdapterCustom(
                                null, parent);
                            int existingParentIndex = parentAdapterList
                                .indexOf(parentAdapter);
                            if (existingParentIndex == -1) {
                                parentAdapterList.add(parentAdapter);
                            } else {
                                parentAdapter = (ContainerAdapterCustom) parentAdapterList
                                    .get(existingParentIndex);
                            }
                            ContainerAdapterCustom child = new ContainerAdapterCustom(
                                parentAdapter, cont);
                            parentAdapter.addChild(child);
                        }
                    }
                    List<AdapterBase> tmpAdapterList = new ArrayList<AdapterBase>(
                        parentAdapterList);
                    parentAdapterList.clear();
                    while (tmpAdapterList.size() > 0) {
                        for (AdapterBase contAdapt : tmpAdapterList) {
                            ContainerWrapper parent = ((ContainerAdapter) contAdapt)
                                .getContainer().getParent();
                            AdapterBase parentAdapter = rootNode;
                            if (parent != null) {
                                parentAdapter = new ContainerAdapterCustom(
                                    null, parent);
                                int existingParentIndex = parentAdapterList
                                    .indexOf(parentAdapter);
                                if (existingParentIndex == -1) {
                                    parentAdapterList.add(parentAdapter);
                                } else {
                                    parentAdapter = parentAdapterList
                                        .get(existingParentIndex);
                                }

                            }
                            contAdapt.setParent(parentAdapter);
                            parentAdapter.addChild(contAdapt);
                        }
                        tmpAdapterList = new ArrayList<AdapterBase>(
                            parentAdapterList);
                        parentAdapterList.clear();
                    }
                    adaptersTree.getTreeViewer().expandToLevel(3);
                } catch (ApplicationException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Button button2 = new Button(parent, SWT.PUSH);
        button2.setText("expand");
        button2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                adaptersTree.getTreeViewer().expandToLevel(3);
            }
        });

    }

    public String getNewLabel() {
        return newLabel.getValue().toString().toUpperCase();
    }

    public class ContainerAdapterCustom extends ContainerAdapter {

        public ContainerAdapterCustom(AdapterBase parent, ContainerWrapper top) {
            super(parent, top);
        }

        @Override
        protected AdapterBase createChildNode() {
            return new ContainerAdapterCustom(this, null);
        }

        @Override
        protected AdapterBase createChildNode(ModelWrapper<?> child) {
            Assert.isTrue(child instanceof ContainerWrapper);
            return new ContainerAdapterCustom(this, (ContainerWrapper) child);
        }

        @Override
        protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
            throws Exception {
            Assert.isNotNull(modelObject, "site null");
            ((ContainerWrapper) modelObject).reload();
            return getContainer().getChildren().values();
        }

        @Override
        protected int getWrapperChildCount() {
            return getContainer().getChildCount();
        }

        @Override
        public String getEntryFormId() {
            return null;
        }

        @Override
        public String getViewFormId() {
            return null;
        }

    }

}
