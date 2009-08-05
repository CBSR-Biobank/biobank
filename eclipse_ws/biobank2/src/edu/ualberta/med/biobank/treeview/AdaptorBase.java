package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.AddCabinetSampleEntryForm;
import edu.ualberta.med.biobank.forms.AddPalletSamplesEntryForm;
import edu.ualberta.med.biobank.forms.AssignSamplesLocationEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in
 * the tree are adaptors for classes in the ORM model.
 */
public abstract class AdaptorBase {

    protected IDeltaListener listener = NullDeltaListener.getSoleInstance();

    private Integer id;

    private String name;

    protected AdaptorBase parent;

    protected boolean hasChildren;

    protected List<AdaptorBase> children;

    public AdaptorBase(AdaptorBase parent) {
        this.parent = parent;
        children = new ArrayList<AdaptorBase>();
        if (parent != null) {
            addListener(parent.listener);
        }
    }

    public AdaptorBase(AdaptorBase parent, int id, String name) {
        this(parent);
        setId(id);
        setName(name);
    }

    public AdaptorBase(AdaptorBase parent, int id, String name,
        boolean hasChildren) {
        this(parent, id, name);
        setHasChildren(hasChildren);
    }

    public void setParent(AdaptorBase parent) {
        this.parent = parent;
    }

    public AdaptorBase getParent() {
        return parent;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getTitle();

    protected String getTitle(String string) {
        String name = getName();
        if (name == null) {
            return "New " + string;
        }
        return string + " " + name;
    }

    public List<AdaptorBase> getItems() {
        return children;
    }

    public List<AdaptorBase> getChildren() {
        return children;
    }

    public AdaptorBase getChild(int id) {
        return getChild(id, false);
    }

    public AdaptorBase getChild(int id, boolean reloadChildren) {
        if (reloadChildren) {
            loadChildren(false);
        }
        if (children.size() == 0)
            return null;

        for (AdaptorBase child : children) {
            if (child.getId() == id)
                return child;
        }
        return null;
    }

    public void addChild(AdaptorBase child) {
        hasChildren = true;
        AdaptorBase existingNode = contains(child);
        if (existingNode != null) {
            // don't add - assume our model is up to date
            return;
        }

        AdaptorBase namedChild = getChildByName(child.getName());
        if (namedChild != null) {
            // may have inserted a new object into database
            // replace current object with new one
            int index = children.indexOf(namedChild);
            children.remove(index);
        }

        child.setParent(this);
        children.add(child);
        child.addListener(listener);
        fireAdd(child);
    }

    public void insertAfter(AdaptorBase existingNode, AdaptorBase newNode) {
        int pos = children.indexOf(existingNode);
        Assert.isTrue(pos >= 0, "existing node not found: "
            + existingNode.getName());
        newNode.setParent(this);
        children.add(pos + 1, newNode);
        newNode.addListener(listener);
        fireAdd(newNode);
    }

    public void removeChild(AdaptorBase item) {
        if (children.size() == 0)
            return;

        AdaptorBase itemToRemove = null;

        for (AdaptorBase child : children) {
            if ((child.getId() == item.getId())
                && child.getName().equals(item.getName()))
                itemToRemove = child;
        }

        if (itemToRemove != null) {
            children.remove(itemToRemove);
            fireRemove(itemToRemove);
        }
    }

    public void removeByName(String name) {
        if (children.size() == 0)
            return;

        AdaptorBase itemToRemove = null;

        for (AdaptorBase child : children) {
            if (child.getName().equals(name))
                itemToRemove = child;
        }

        if (itemToRemove != null) {
            children.remove(itemToRemove);
            fireRemove(itemToRemove);
        }
    }

    public AdaptorBase contains(AdaptorBase item) {
        if (children.size() == 0)
            return null;

        for (AdaptorBase child : children) {
            if ((child.getId() == item.getId())
                && child.getName().equals(item.getName()))
                return child;
        }
        return null;
    }

    public AdaptorBase getChildByName(String name) {
        if (children.size() == 0)
            return null;

        for (AdaptorBase child : children) {
            if (child.getName() != null && child.getName().equals(name))
                return child;
        }
        return null;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public WritableApplicationService getAppService() {
        Assert.isNotNull(parent, "parent is null");
        return parent.getAppService();
    }

    public void addListener(IDeltaListener listener) {
        this.listener = listener;
    }

    public void removeListener(IDeltaListener listener) {
        if (this.listener.equals(listener)) {
            this.listener = NullDeltaListener.getSoleInstance();
        }
    }

    protected void fireAdd(Object added) {
        listener.add(new DeltaEvent(added));
    }

    protected void fireRemove(Object removed) {
        listener.remove(new DeltaEvent(removed));
    }

    public abstract void performDoubleClick();

    public void performExpand() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                loadChildren(true);
                SessionManager.getInstance().getTreeViewer().expandToLevel(
                    AdaptorBase.this, 1);
            }
        });
    }

    public abstract void popupMenu(TreeViewer tv, Tree tree, Menu menu);

    public abstract void loadChildren(boolean updateNode);

    public static void closeEditor(FormInput input) {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        IEditorPart part = page.findEditor(input);
        if (part != null) {
            page.closeEditor(part, true);
        }
    }

    public static void openForm(FormInput input, String id) {
        closeEditor(input);
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(input, id, true);
        } catch (PartInitException e) {
            // handle error
            e.printStackTrace();
        }
    }

    public void closeScannersEditors() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();

        IEditorReference[] editors = page.getEditorReferences();
        for (IEditorReference editorRef : editors) {
            IEditorPart editor = editorRef.getEditor(false);
            if (editor != null
                && (editor instanceof AddPalletSamplesEntryForm || editor instanceof AssignSamplesLocationEntryForm)) {
                page.closeEditor(editor, true);
            }
        }
    }

    protected void closeCabinetsEditors() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();

        IEditorReference[] editors = page.getEditorReferences();
        for (IEditorReference editorRef : editors) {
            IEditorPart editor = editorRef.getEditor(false);
            if (editor != null && editor instanceof AddCabinetSampleEntryForm) {
                page.closeEditor(editor, true);
            }
        }
    }

    public AdaptorBase getParentFromClass(Class<?> parentClass) {
        AdaptorBase node = this;
        while (node != null) {
            if (node.getClass().equals(parentClass)) {
                return node;
            } else {
                node = node.getParent();
            }
        }
        return null;
    }

    public abstract AdaptorBase accept(NodeSearchVisitor visitor);

    public String getTreeText() {
        return getName();
    }
}
