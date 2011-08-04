package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.security.GroupTemplate;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.gui.common.widgets.utils.TableFilter;
import edu.ualberta.med.biobank.widgets.infotables.TemplateInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.UserInfoTable;

public abstract class TemplatesPage extends BgcDialogPage {

    private TemplateInfoTable templateInfoTable;

    public TemplatesPage(BgcDialogWithPages dialog) {
        super(dialog);
    }

    @Override
    public String getTitle() {
        return Messages.TemplatesPage_page_title;
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        new TableFilter<GroupTemplate>(content) {
            @Override
            protected boolean accept(GroupTemplate template, String text) {
                return contains(template.getName(), text);
            }

            @Override
            public List<GroupTemplate> getAllCollection() {
                return getCurrentAllTemplatesList();
            }

            @Override
            public void setFilteredList(List<GroupTemplate> filteredObjects) {
                templateInfoTable.reloadCollection(filteredObjects);
            }
        };

        templateInfoTable = new TemplateInfoTable(content, null) {
            @Override
            protected boolean deleteTemplate(GroupTemplate template) {
                boolean deleted = super.deleteTemplate(template);
                if (deleted)
                    getCurrentAllTemplatesList().remove(template);
                return deleted;
            }
        };
        List<GroupTemplate> tmpTemplates = new ArrayList<GroupTemplate>();
        for (int i = 0; i < UserInfoTable.ROWS_PER_PAGE + 1; i++) {
            GroupTemplate user = new GroupTemplate();
            user.setName("loading...");
            tmpTemplates.add(user);
        }
        templateInfoTable.setCollection(tmpTemplates);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final List<GroupTemplate> templates = getCurrentAllTemplatesList();
                    sleep(200); // FIXME for some reason, if the group list is
                    // already loaded and therefore is retrieved
                    // right away, the setCollection method is not
                    // working because the current thread is still
                    // alive (see setCollection implementation).
                    // With a small pause, it is ok.
                    getShell().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            templateInfoTable.setCollection(templates);
                        }
                    });
                } catch (final Exception ex) {
                    getShell().getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            BgcPlugin
                                .openAsyncError(
                                    Messages.UserManagementDialog_get_users_groups_error_title,
                                    ex);
                        }
                    });
                }
            }
        };
        t.start();
        setControl(content);
    }

    @Override
    public void runAddAction() {
        addTemplate();
    }

    protected void addTemplate() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                final GroupTemplate template = new GroupTemplate();
                TemplateEditDialog dlg = new TemplateEditDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    template, true);
                int res = dlg.open();
                if (res == Status.OK) {
                    BgcPlugin.openAsyncInformation("Template added",
                        MessageFormat.format(
                            "Successfully added new template {0}",
                            template.getName()));
                    getCurrentAllTemplatesList().add(template);
                    templateInfoTable.reloadCollection(
                        getCurrentAllTemplatesList(), template);
                }
            }
        });
    }

    protected abstract List<GroupTemplate> getCurrentAllTemplatesList();

}
