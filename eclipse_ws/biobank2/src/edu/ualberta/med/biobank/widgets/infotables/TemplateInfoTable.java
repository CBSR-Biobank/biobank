package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.security.GroupTemplate;
import edu.ualberta.med.biobank.dialogs.user.TemplateEditDialog;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class TemplateInfoTable extends InfoTableWidget<GroupTemplate> {
    public static final int ROWS_PER_PAGE = 12;
    private static final String[] HEADINGS = new String[] { "Name" };
    private static final String LOADING_ROW = "loading...";

    public TemplateInfoTable(Composite parent, List<GroupTemplate> collection) {
        super(parent, collection, HEADINGS, ROWS_PER_PAGE, GroupTemplate.class);

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                editTemplate((GroupTemplate) getSelection());
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                deleteTemplate((GroupTemplate) getSelection());
            }
        });
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof GroupTemplate && o2 instanceof GroupTemplate) {
                    GroupTemplate u1 = (GroupTemplate) o1;
                    GroupTemplate u2 = (GroupTemplate) o2;

                    int cmp = u1.getName().compareToIgnoreCase(u2.getName());
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                return 0;
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null) {
            return null;
        }

        GroupTemplate template = (GroupTemplate) o;
        return StringUtils.join(Arrays.asList(template.getName()), "\t"); //$NON-NLS-1$
    }

    @Override
    protected IBaseLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                GroupTemplate template = (GroupTemplate) ((BiobankCollectionModel) element).o;
                if (template == null) {
                    if (columnIndex == 0) {
                        return LOADING_ROW;
                    }
                    return ""; //$NON-NLS-1$
                }

                switch (columnIndex) {
                case 0:
                    return template.getName();
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    protected void editTemplate(GroupTemplate template) {
        TemplateEditDialog dlg = new TemplateEditDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(), template,
            false);
        int res = dlg.open();
        if (res == Dialog.OK) {
            reloadCollection(getCollection(), template);
            notifyListeners();
        }
    }

    protected boolean deleteTemplate(GroupTemplate template) {
        // FIXME
        // try {
        // String loginName = user.getLogin();
        // String message;
        //
        // if (SessionManager.getUser().equals(user)) {
        // message = CONFIRM_SUICIDE_MESSAGE;
        // } else {
        // message = MessageFormat.format(CONFIRM_DELETE_MESSAGE,
        // new Object[] { loginName });
        // }
        //
        // if (BgcPlugin.openConfirm(CONFIRM_DELETE_TITLE, message)) {
        // SessionManager.getAppService().deleteUser(
        // SessionManager.getUser(), loginName);
        //
        // // remove the user from the collection
        // getCollection().remove(user);
        //
        // reloadCollection(getCollection(), null);
        // notifyListeners();
        // return true;
        // }
        // } catch (ApplicationException e) {
        // BgcPlugin.openAsyncError(USER_DELETE_ERROR, e);
        // }
        return false;
    }
}