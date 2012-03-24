package edu.ualberta.med.biobank.utils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.search.ContainerByBarcodeSearchAction;
import edu.ualberta.med.biobank.common.action.search.ContainerByLabelSearchAction;
import edu.ualberta.med.biobank.common.action.search.PEventByWSSearchAction;
import edu.ualberta.med.biobank.common.action.search.SpecimenByInventorySearchAction;
import edu.ualberta.med.biobank.common.action.search.SpecimenByPositionSearchAction;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.PeListViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public enum SearchType {
    INVENTORY_ID(Messages.SearchType_inventoryid_label) {
        @Override
        public List<ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            SpecimenByInventorySearchAction action =
                new SpecimenByInventorySearchAction(searchString,
                    center.getId());
            return wrapIds(SessionManager.getAppService()
                .doAction(action).getList(), SpecimenWrapper.class);
        }

    },

    SPECIMEN_POSITION(Messages.SearchType_position_spec_label) {
        @Override
        public List<ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper) {
                SpecimenByPositionSearchAction action =
                    new SpecimenByPositionSearchAction(searchString,
                        center.getId());
                return wrapIds(SessionManager.getAppService()
                    .doAction(action).getList(), SpecimenWrapper.class);
            }
            return Collections.emptyList();
        }
    },

    CONTAINER_LABEL(Messages.SearchType_label_cont_label) {
        @Override
        public List<ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper) {
                ContainerByLabelSearchAction action =
                    new ContainerByLabelSearchAction(searchString,
                        center.getId());
                List<ModelWrapper<?>> list =
                        new ArrayList<ModelWrapper<?>>(
                            ModelWrapper.wrapModelCollection(SessionManager
                                .getAppService(),
                                SessionManager.getAppService().doAction(action)
                                    .getList(),
                                ContainerWrapper.class));
                return list;
            }
            return Collections.emptyList();
        }
    },

    CONTAINER_PRODUCT_BARCODE(Messages.SearchType_barcode_cont_label) {
        @Override
        public List<ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper) {
                ContainerByBarcodeSearchAction action =
                    new ContainerByBarcodeSearchAction(searchString,
                        center.getId());
                List<ModelWrapper<?>> list =
                    new ArrayList<ModelWrapper<?>>(
                        ModelWrapper.wrapModelCollection(SessionManager
                            .getAppService(),
                            SessionManager.getAppService().doAction(action)
                                .getList(),
                            ContainerWrapper.class));
                return list;
            }
            return null;
        }
    },

    WORKSHEET(Messages.SearchType_worksheet_label) {
        @Override
        public List<ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            PEventByWSSearchAction action =
                new PEventByWSSearchAction(searchString,
                    center.getId());
            return wrapIds(SessionManager.getAppService()
                .doAction(action).getList(), ProcessingEventWrapper.class);
        }

        @Override
        public void processResults(List<ModelWrapper<?>> res) {
            Assert.isNotNull(res);
            FormInput input = new FormInput(res,
                Messages.SearchType_pEvent_list_title);
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, PeListViewForm.ID, false);
            } catch (PartInitException e) {
                logger
                    .error(NLS.bind(CAN_T_OPEN_FORM_WITH_ID_MSG,
                        PeListViewForm.ID), e);
            }
        }
    };

    private static final String CAN_T_OPEN_FORM_WITH_ID_MSG =
        "Can''t open form with id {0}"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger.getLogger(SearchType.class
        .getName());

    private String label;

    private SearchType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public abstract List<ModelWrapper<?>> search(String searchString,
        CenterWrapper<?> center) throws Exception;

    public void processResults(List<ModelWrapper<?>> res) {
        Assert.isNotNull(res);
        int size = res.size();
        if (size == 1) {
            openResult(res.get(0));
        } else {
            boolean open = MessageDialog.openQuestion(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                Messages.SearchType_question_title,
                NLS.bind(Messages.SearchType_question_msg, size));
            if (open) {
                for (ModelWrapper<?> wrapper : res) {
                    openResult(wrapper);
                }
            }
        }
    }

    private static List<ModelWrapper<?>> wrapIds(List<Integer> ids,
        Class<?> wrapperKlazz) throws Exception {
        List<ModelWrapper<?>> list = new ArrayList<ModelWrapper<?>>();
        for (Integer id : ids) {
            Constructor<?> c =
                wrapperKlazz.getConstructor(WritableApplicationService.class);
            ModelWrapper<?> wrapper =
                (ModelWrapper<?>) c.newInstance(SessionManager.getAppService());
            wrapper.setId(id);
            list.add(wrapper);
        }
        return list;
    }

    protected void openResult(ModelWrapper<?> wrapper) {
        AbstractAdapterBase adapter = AdapterFactory.getAdapter(wrapper);
        if (adapter != null) {
            adapter.performDoubleClick();
        }
    }

}