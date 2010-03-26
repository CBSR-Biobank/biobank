package edu.ualberta.med.biobank.treeview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.advanced.HQLField;
import edu.ualberta.med.biobank.common.reports.advanced.QueryTreeNode;
import edu.ualberta.med.biobank.common.reports.advanced.SearchUtils;

public class QueryTree extends TreeViewer {

    private HashMap<String, String> extraSelectClauses;

    public QueryTree(Composite parent, int style, QueryTreeNode node) {
        super(parent, style);

        extraSelectClauses = new HashMap<String, String>();

        this.setContentProvider(new ITreeContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {

            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public Object[] getElements(Object inputElement) {
                return ((QueryTreeNode) inputElement).getChildren().toArray();
            }

            @Override
            public boolean hasChildren(Object element) {
                return !((QueryTreeNode) element).isLeaf();
            }

            @Override
            public Object getParent(Object element) {
                return ((QueryTreeNode) element).getParent();
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                return ((QueryTreeNode) parentElement).getChildren().toArray();
            }
        });
        this.setLabelProvider(new ILabelProvider() {

            @Override
            public Image getImage(Object element) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getText(Object element) {
                return ((QueryTreeNode) element).getLabel();
            }

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
        });
        this.setInput(node);

        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Menu menu = QueryTree.this.getTree().getMenu();
                for (MenuItem menuItem : menu.getItems()) {
                    menuItem.dispose();
                }

                Object element = ((StructuredSelection) QueryTree.this
                    .getSelection()).getFirstElement();
                final QueryTreeNode node = (QueryTreeNode) element;
                if (node != null && node.getParent() != null) {
                    MenuItem mi = new MenuItem(menu, SWT.NONE);
                    mi.setText("OR");
                    mi.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            QueryTreeNode newOperator = new QueryTreeNode(
                                new HQLField(node.getNodeInfo().getPath(),
                                    "OR", String.class));
                            QueryTreeNode parent = node.getParent();
                            parent.removeChild(node);
                            parent.addChild(newOperator);
                            newOperator.setParent(parent);
                            newOperator.addChild(node);
                            node.setParent(newOperator);
                            QueryTreeNode copy = node.clone();
                            newOperator.addChild(copy);
                            copy.setParent(newOperator);
                            QueryTree.this.refresh(true);
                        }
                    });
                    if (node.getNodeInfo().getType() == String.class) {
                        MenuItem mi2 = new MenuItem(menu, SWT.NONE);
                        mi2.setText("Remove Node");
                        mi2.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                QueryTreeNode parent = node.getParent();
                                List<QueryTreeNode> children = node
                                    .getChildren();
                                QueryTreeNode child = children.get(0);
                                child.setParent(parent);
                                node.removeChild(child);
                                parent.addChild(child);
                                parent.removeChild(node);
                                QueryTree.this.refresh(true);
                            }
                        });
                    }
                    if (node.getNodeInfo().getFname().contains("Collection")
                        && !((node.getParent().getLabel().compareTo("All") == 0) || (node
                            .getParent().getLabel().compareTo("None") == 0))) {
                        MenuItem mi3 = new MenuItem(menu, SWT.NONE);
                        mi3.setText("All");
                        mi3.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                QueryTreeNode newOperator = new QueryTreeNode(
                                    new HQLField(node.getNodeInfo().getPath(),
                                        "All", String.class));
                                QueryTreeNode parent = node.getParent();
                                parent.removeChild(node);
                                parent.addChild(newOperator);
                                newOperator.setParent(parent);
                                newOperator.addChild(node);
                                node.setParent(newOperator);
                                QueryTree.this.refresh(true);
                            }
                        });
                        MenuItem mi4 = new MenuItem(menu, SWT.NONE);
                        mi4.setText("None");
                        mi4.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                QueryTreeNode newOperator = new QueryTreeNode(
                                    new HQLField(node.getNodeInfo().getPath(),
                                        "None", String.class));
                                QueryTreeNode parent = node.getParent();
                                parent.removeChild(node);
                                parent.addChild(newOperator);
                                newOperator.setParent(parent);
                                newOperator.addChild(node);
                                node.setParent(newOperator);
                                QueryTree.this.refresh(true);
                            }
                        });
                    }
                }
            }
        });
        this.getTree().setMenu(menu);

    }

    public static String getHQLExpression(String fname, String operator,
        Object value) {
        if (value instanceof String) {
            if (operator.compareTo("contains") == 0)
                return fname + " like '%" + value + "%'";
            else if (operator.compareTo("doesn't contain") == 0)
                return fname + " not like '%" + value + "%'";
            else if (operator.compareTo("starts with") == 0)
                return fname + " like '" + value + "%'";
            else if (operator.compareTo("doesn't start with") == 0)
                return fname + " not like '" + value + "%'";
            else if (operator.compareTo("ends with") == 0)
                return fname + " like '%" + value + "'";
            else if (operator.compareTo("doesn't end with") == 0)
                return fname + " not like '%" + value + "'";
            return fname + operator + "'" + value + "'";
        } else if (value instanceof Date) {
            return fname + operator + "'"
                + DateFormatter.formatAsDateTime((Date) value) + "'";
        }
        return fname + operator + value;

    }

    public static QueryTreeNode constructTree(HQLField root) {
        QueryTreeNode dummy = new QueryTreeNode(new HQLField("", "", root
            .getType()));
        QueryTreeNode rootNode = new QueryTreeNode(root);
        expand(rootNode);
        rootNode.setParent(dummy);
        dummy.addChild(rootNode);
        return dummy;
    }

    public static void expand(QueryTreeNode node) {
        Boolean collection = false;
        if (node.getNodeInfo().getFname().contains("Collection"))
            collection = true;

        List<HQLField> fields = SearchUtils.getSimpleFields(node.getNodeInfo()
            .getType(), node.getNodeInfo().getPath(), collection);
        for (HQLField field : fields)
            node.addField(field);
        List<HQLField> children = SearchUtils.getComplexFields(node
            .getNodeInfo().getType(), node.getNodeInfo().getPath(), collection);
        for (HQLField child : children) {
            QueryTreeNode nodeChild = new QueryTreeNode(child);
            nodeChild.setParent(node);
            expand(nodeChild);
            node.addChild(nodeChild);
        }
    }

    public String compileQuery() {
        HashSet<String> fromClauses = new HashSet<String>();
        List<String> whereClauses = new ArrayList<String>();

        QueryTreeNode root = (QueryTreeNode) this.getInput();
        Class<?> type = root.getNodeInfo().getType();

        addClausesForNode(root, whereClauses);
        generateSubClauses(root, whereClauses, fromClauses);

        HashMap<String, String> basicSelects = SearchUtils.getColumnInfo(root
            .getNodeInfo().getType());
        basicSelects.putAll(extraSelectClauses);
        extraSelectClauses = basicSelects;

        String selectClause = "select ";
        for (String key : extraSelectClauses.keySet()) {
            selectClause += extraSelectClauses.get(key) + ", ";
        }
        selectClause = selectClause.substring(0, selectClause.length() - 2)
            + " from " + type.getName() + " "
            + type.getSimpleName().toLowerCase();

        String hqlString = selectClause + compileFromClause(fromClauses);
        String where = compileWhereClause(whereClauses);
        if (where != null)
            hqlString = hqlString + " where " + where;

        System.out.println(hqlString);
        return hqlString;
    }

    private String compileFromClause(HashSet<String> fromClauses) {
        String fromClause = "";
        for (String clause : fromClauses) {
            fromClause = " join " + clause + fromClause;
        }
        return fromClause;
    }

    private String compileWhereClause(List<String> whereClauses) {
        String whereClause = "";
        for (String clause : whereClauses) {
            whereClause += clause + " and ";
        }
        if (whereClause.length() > 0)
            return whereClause.substring(0, whereClause.length() - 4);
        else
            return null;
    }

    private boolean generateSubClauses(QueryTreeNode parent,
        List<String> whereClauses, HashSet<String> fromClauses) {
        boolean addedFields = false;
        boolean addedChildren = false;
        List<QueryTreeNode> children = parent.getChildren();
        for (QueryTreeNode child : children) {
            if ((child.getLabel().compareTo("All") == 0)
                || (child.getLabel().compareTo("None") == 0)) {
                QueryTreeNode childCollection = child.getChildren().get(0);
                String collectionName = childCollection.getNodeInfo().getPath()
                    + childCollection.getNodeInfo().getFname();
                String name = childCollection.getNodeInfo().getPath().replace(
                    '.', '_')
                    + childCollection.getNodeInfo().getFname();
                String newSelect;
                if (child.getLabel().compareTo("All") == 0)
                    newSelect = collectionName
                        + ".size = (select count(*) from "
                        + childCollection.getNodeInfo().getType().getName()
                        + " " + name;
                else
                    newSelect = "0 = (select count(*) from "
                        + childCollection.getNodeInfo().getType().getName()
                        + " " + name;
                List<String> collectionWhereClauses = new ArrayList<String>();
                HashSet<String> collectionFromClauses = new HashSet<String>();
                Boolean addedSubFields = addClausesForNode(childCollection,
                    collectionWhereClauses);
                Boolean addedSubChildren = generateSubClauses(childCollection,
                    collectionWhereClauses, collectionFromClauses);
                String hqlString = newSelect
                    + compileFromClause(collectionFromClauses);
                collectionWhereClauses.add(name + " in elements("
                    + collectionName + ")");
                String where = compileWhereClause(collectionWhereClauses);
                hqlString = hqlString + " where " + where;
                whereClauses.add(hqlString + ")");
                addedFields = addedFields || addedSubFields;
                addedChildren = addedChildren || addedSubChildren;
            } else if (child.getLabel().compareTo("OR") == 0) {
                List<QueryTreeNode> subChildren = child.getChildren();
                List<String> leftList = new ArrayList<String>();
                List<String> rightList = new ArrayList<String>();
                boolean addedLFields = addClausesForNode(subChildren.get(0),
                    leftList);
                boolean addedLChildren = generateSubClauses(subChildren.get(0),
                    leftList, fromClauses);
                boolean addedRFields = addClausesForNode(subChildren.get(1),
                    rightList);
                boolean addedRChildren = generateSubClauses(subChildren.get(1),
                    rightList, fromClauses);
                if (leftList.size() > 0) {
                    if (rightList.size() > 0)
                        whereClauses.add("((" + compileWhereClause(leftList)
                            + ") OR (" + compileWhereClause(rightList) + "))");
                    else
                        whereClauses.addAll(leftList);
                } else if (rightList.size() > 0)
                    whereClauses.addAll(rightList);
                addedFields = addedFields || addedLFields || addedRFields;
                addedChildren = addedChildren || addedLChildren
                    || addedRChildren;
            } else {
                Boolean addedSubFields = addClausesForNode(child, whereClauses);
                Boolean addedSubChildren = generateSubClauses(child,
                    whereClauses, fromClauses);
                if ((addedSubFields || addedSubChildren)
                    && child.getNodeInfo().getFname().contains("Collection")) {
                    fromClauses.add(child.getNodeInfo().getPath()
                        + child.getNodeInfo().getFname() + " as "
                        + child.getNodeInfo().getPath().replace(".", "_")
                        + child.getNodeInfo().getFname());
                }
                addedFields = addedFields || addedSubFields;
                addedChildren = addedChildren || addedSubChildren;
            }
        }
        return (addedFields || addedChildren);
    }

    private boolean addClausesForNode(QueryTreeNode node,
        List<String> clauseList) {
        List<HQLField> fields = node.getFieldData();
        HQLField field;
        boolean addedClause = false;
        for (int i = 0; i < fields.size(); i++) {
            field = fields.get(i);
            if (field.getType() == String.class) {
                if (field.getValue() != null
                    && ((String) field.getValue()).compareTo("") != 0) {
                    addClause(field, clauseList);
                    addedClause = true;
                }
            } else {
                if (field.getValue() != null) {
                    addClause(field, clauseList);
                    addedClause = true;
                }
            }
        }
        return addedClause;
    }

    public void addClause(HQLField field, List<String> clauseList) {
        clauseList.add(getHQLExpression(field.getPath() + field.getFname(),
            field.getOperator(), field.getValue()));
        if (field.getDisplay())
            extraSelectClauses.put(field.getFname().substring(0, 1)
                .toUpperCase()
                + field.getFname().substring(1), field.getPath()
                + field.getFname());
    }

    public void saveTree(String path, String name) {
        try {
            ((QueryTreeNode) getInput()).saveTree(path, name);
        } catch (IOException e) {
            BioBankPlugin.openAsyncError("Save Failed", e);
        }
    }

    public HashMap<String, String> getSelectClauses() {
        return extraSelectClauses;
    }
}
