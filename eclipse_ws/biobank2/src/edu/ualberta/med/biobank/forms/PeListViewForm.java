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
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.treeview.AdapterBase;
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
                new ProcessingEventGetBriefInfoAction(pe.getWrappedObject())));
        }

        setPartName(ProcessingEvent.NAME.plural().toString());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(ProcessingEvent.NAME.plural().toString());
        page.setLayout(new GridLayout(1, false));
        form.setImage(BiobankPlugin.getDefault().getImage(
            new ProcessingEventAdapter(null, null)));

        processingEvents = new PeListInfoTable(page, peInfos);
        processingEvents.adaptToToolkit(toolkit, true);
        processingEvents
            .addClickListener(new IInfoTableDoubleClickItemListener<ProcessingEventBriefInfo>() {

                @Override
                public void doubleClick(
                    InfoTableEvent<ProcessingEventBriefInfo> event) {
                    ProcessingEvent pe =
                        ((ProcessingEventBriefInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).pevent;
                    AdapterBase.openForm(
                        new FormInput(
                            new ProcessingEventAdapter(null,
                                new ProcessingEventWrapper(SessionManager
                                    .getAppService(), pe))),
                        ProcessingEventViewForm.ID);
                }
            });
        processingEvents
            .addEditItemListener(new IInfoTableEditItemListener<ProcessingEventBriefInfo>() {

                @Override
                public void editItem(
                    InfoTableEvent<ProcessingEventBriefInfo> event) {
                    ProcessingEvent pe =
                        ((ProcessingEventBriefInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).pevent;
                    AdapterBase.openForm(
                        new FormInput(
                            new ProcessingEventAdapter(null,
                                new ProcessingEventWrapper(SessionManager
                                    .getAppService(), pe))),
                        ProcessingEventEntryForm.ID);
                }
            });
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }

}
