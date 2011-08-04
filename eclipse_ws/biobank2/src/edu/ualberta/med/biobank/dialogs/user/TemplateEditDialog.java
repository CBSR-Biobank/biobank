package edu.ualberta.med.biobank.dialogs.user;

import java.util.LinkedHashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.GroupTemplate;
import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.TableFilter;
import edu.ualberta.med.biobank.server.applicationservice.BiobankSecurityUtil;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectNode;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TemplateEditDialog extends BgcBaseDialog {

    private GroupTemplate originalTemplate, modifiedTemplate;
    private MultiSelectWidget centerFeaturesWidget;
    private Text featuresFilterText;
    private String currentTitle;
    private String titleAreaMessage;

    public TemplateEditDialog(Shell parent, GroupTemplate originalTemplate,
        boolean isNewTemplate) {
        super(parent);
        Assert.isNotNull(originalTemplate);
        this.originalTemplate = originalTemplate;
        this.modifiedTemplate = new GroupTemplate();
        this.modifiedTemplate.copy(originalTemplate);
        if (isNewTemplate) {
            currentTitle = "Add Template";
            titleAreaMessage = "Add a new group template";
        } else {
            currentTitle = "Edit Template";
            titleAreaMessage = "Modify an existing template's information.";
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return titleAreaMessage;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            "Name", null, modifiedTemplate,
            "name", new NonEmptyStringValidator( //$NON-NLS-1$
                "A valid name is required"));

        final LinkedHashMap<Integer, String> featuresMap = new LinkedHashMap<Integer, String>();
        for (ProtectionGroupPrivilege pgp : SessionManager.getAppService()
            .getSecurityCenterFeatures(SessionManager.getUser())) {
            featuresMap.put(
                pgp.getId().intValue(),
                pgp.getName().replace(
                    BiobankSecurityUtil.CENTER_FEATURE_START_NAME, "")); //$NON-NLS-1$
        }
        centerFeaturesWidget = new MultiSelectWidget(parent, SWT.NONE,
            "Center specific features available",
            "Center specific features enabled", 110);
        centerFeaturesWidget.setSelections(featuresMap,
            modifiedTemplate.getCenterFeaturesEnabled());
        centerFeaturesWidget.setFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                Object element) {
                if (featuresFilterText == null)
                    return true;
                MultiSelectNode node = (MultiSelectNode) element;
                return TableFilter.contains(node.getName(),
                    featuresFilterText.getText());
            }
        });
        Label label = new Label(parent, SWT.NONE);
        label.setText("Enter text to filter the features lists:");
        featuresFilterText = new Text(parent, SWT.BORDER);
        GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
        featuresFilterText.setLayoutData(gd);
        featuresFilterText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                centerFeaturesWidget.refreshLists();
            }
        });
    }

    @Override
    protected void okPressed() {
        modifiedTemplate.setCenterFeaturesEnabled(centerFeaturesWidget
            .getSelected());
        // FIXME save in DB
        // Group groupeResult = SessionManager.getAppService().persistGroup(
        // SessionManager.getUser(), modifiedGroup);
        originalTemplate.copy(modifiedTemplate);
        close();
    }

}