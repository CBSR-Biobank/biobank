package edu.ualberta.med.biobank.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.PeListViewForm;
import edu.ualberta.med.biobank.forms.SpecimenListViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;

public enum SearchType {
    INVENTORY_ID(Messages.SearchType_inventoryid_label) {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            List<SpecimenWrapper> res = new ArrayList<SpecimenWrapper>();
            SpecimenWrapper specimen = SpecimenWrapper.getSpecimen(
                SessionManager.getAppService(), searchString);
            if (specimen != null) {
                res.add(specimen);
            }
            return res;
        }

    },

    SPECIMEN_POSITION(Messages.SearchType_position_spec_label) {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper)
                return SpecimenWrapper.getSpecimensInSiteWithPositionLabel(
                    SessionManager.getAppService(), (SiteWrapper) center,
                    searchString);
            return Collections.emptyList();
        }
    },

    SPECIMEN_NON_ACTIVE(Messages.SearchType_nonactive_spec_label) {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            List<SpecimenWrapper> specimens = SpecimenWrapper
                .getSpecimensNonActiveInCenter(SessionManager.getAppService(),
                    center);
            return specimens;
        }

        @Override
        public void processResults(List<? extends ModelWrapper<?>> res) {
            Assert.isNotNull(res);
            FormInput input = new FormInput(res,
                Messages.SearchType_specimens_list_label);
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, SpecimenListViewForm.ID, false);
            } catch (PartInitException e) {
                logger.error(NLS.bind(CAN_T_OPEN_FORM_WITH_ID_MSG,
                    SpecimenListViewForm.ID), e);
            }
        }

        @Override
        protected void openResult(ModelWrapper<?> wrapper) {
        }
    },

    CONTAINER_LABEL(Messages.SearchType_label_cont_label) {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper)
                return ContainerWrapper.getContainersInSite(
                    SessionManager.getAppService(), (SiteWrapper) center,
                    searchString);
            return Collections.emptyList();
        }
    },

    CONTAINER_PRODUCT_BARCODE(Messages.SearchType_barcode_cont_label) {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper) {
                ContainerWrapper container = ContainerWrapper
                    .getContainerWithProductBarcodeInSite(
                        SessionManager.getAppService(), (SiteWrapper) center,
                        searchString);
                if (container != null) {
                    return Arrays.asList(container);
                }
            }
            return null;
        }
    },

    WORKSHEET(Messages.SearchType_worksheet_label) {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            List<ProcessingEventWrapper> pvs = ProcessingEventWrapper
                .getProcessingEventsWithWorksheet(
                    SessionManager.getAppService(), searchString);

            if (pvs == null)
                return null;
            return pvs;

        }

        @Override
        public void processResults(List<? extends ModelWrapper<?>> res) {
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

    private static final String CAN_T_OPEN_FORM_WITH_ID_MSG = "Can''t open form with id {0}"; //$NON-NLS-1$

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

    public abstract List<? extends ModelWrapper<?>> search(String searchString,
        CenterWrapper<?> center) throws Exception;

    public void processResults(List<? extends ModelWrapper<?>> res) {
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

    protected void openResult(ModelWrapper<?> wrapper) {
        AdapterBase adapter = AdapterFactory.getAdapter(wrapper);
        if (adapter != null) {
            adapter.performDoubleClick();
        }
    }

}