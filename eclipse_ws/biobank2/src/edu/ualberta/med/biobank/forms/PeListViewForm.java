package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridLayout;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventBriefInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetBriefInfoAction;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.widgets.infotables.PeListInfoTable;

public class PeListViewForm extends BiobankViewForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.PvListViewForm"; //$NON-NLS-1$

    private PeListInfoTable processingEvents;

    private List<ProcessingEventWrapper> pes;
    private List<ProcessingEventBriefInfo> peInfos;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter == null, "adapter should be null"); //$NON-NLS-1$
        FormInput input = (FormInput) getEditorInput();
        pes =
            (List<ProcessingEventWrapper>) input.getAdapter(ArrayList.class);
        Assert.isNotNull(pes, "aliquots are null"); //$NON-NLS-1$

        peInfos = new ArrayList<ProcessingEventBriefInfo>();
        for (ProcessingEventWrapper pe : pes) {
            peInfos.add(SessionManager.getAppService().doAction(
                new ProcessingEventGetBriefInfoAction(pe.getId())));
        }

        setPartName(Messages.PeListViewForm_title);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.PeListViewForm_title);
        page.setLayout(new GridLayout(1, false));
        form.setImage(BiobankPlugin.getDefault().getImage(
            new ProcessingEventAdapter(null, null)));

        processingEvents = new PeListInfoTable(page, peInfos);
        processingEvents.adaptToToolkit(toolkit, true);
        processingEvents.addClickListener(collectionDoubleClickListener);
        processingEvents.createDefaultEditItem();
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }

}
