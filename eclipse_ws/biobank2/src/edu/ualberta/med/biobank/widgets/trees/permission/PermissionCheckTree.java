package edu.ualberta.med.biobank.widgets.trees.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;

public class PermissionCheckTree extends Composite {

    private CheckboxTreeViewer treeviewer;

    public PermissionCheckTree(Composite parent, List<BbRightWrapper> allRights) {
        super(parent, SWT.NONE);
        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        this.setLayout(gl);

        treeviewer = new CheckboxTreeViewer(this);
        treeviewer.getTree().setLayoutData(
            new GridData(SWT.FILL, SWT.FILL, true, true));

        treeviewer.setLabelProvider(new PermissionLabelProvider());
        treeviewer.setContentProvider(new PermissionContentProvider());
        treeviewer.setComparator(new ViewerComparator());

        treeviewer.setInput(buildContent(allRights));

        treeviewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                PermissionNode node = (PermissionNode) event.getElement();
                boolean checked = event.getChecked();
                treeviewer.setParentsGrayed(node, false);
                treeviewer.setSubtreeChecked(node, checked);
                if (node.getParent() != null) {
                    int siblingsCheckedCount = 0;
                    for (PermissionNode child : node.getParent().getChildren()) {
                        if (treeviewer.getChecked(child))
                            siblingsCheckedCount++;
                    }
                    if (event.getChecked()) {
                        if (siblingsCheckedCount == node.getParent()
                            .getChildren().size()) {
                            treeviewer.setParentsGrayed(node.getParent(), false);
                            // treeviewer.setChecked(node.getParent(), true);
                        } else
                            treeviewer.setParentsGrayed(node.getParent(), true);
                    } else if (treeviewer.getChecked(node.getParent())) {
                        if (siblingsCheckedCount > 0)
                            treeviewer.setParentsGrayed(node.getParent(), true);
                        else {
                            treeviewer.setParentsGrayed(node.getParent(), false);
                            treeviewer.setChecked(node.getParent(), false);
                        }
                    }
                }
            }
        });
    }

    private List<PermissionNode> buildContent(List<BbRightWrapper> allRights) {
        final List<PermissionNode> nodes = new ArrayList<PermissionNode>();
        PermissionNode rootNode = new PermissionNode() {
            @Override
            public String getText() {
                return "All rights";
            }

            @Override
            public PermissionNode getParent() {
                return null;
            }

            @Override
            public List<PermissionNode> getChildren() {
                return nodes;
            }
        };
        for (BbRightWrapper r : allRights) {
            nodes.add(new RightNode(rootNode, r));
        }
        return Arrays.asList(rootNode);
    }

    public class PermissionLabelProvider implements ILabelProvider {

        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
        }

        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof PermissionNode)
                return ((PermissionNode) element).getText();
            return "Problem with display"; //$NON-NLS-1$
        }
    }

    public class PermissionContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof Object[]) {
                return (Object[]) inputElement;
            }
            if (inputElement instanceof Collection) {
                return ((Collection) inputElement).toArray();
            }
            return new Object[0];
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof PermissionNode)
                return ((PermissionNode) parentElement).getChildren().toArray();
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof PermissionNode)
                return ((PermissionNode) element).getParent();
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof PermissionNode)
                return getChildren(element).length > 0;
            return false;
        }

    }
}
