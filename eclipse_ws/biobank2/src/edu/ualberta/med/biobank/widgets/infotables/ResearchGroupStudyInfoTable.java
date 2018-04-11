package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.permission.study.StudyDeletePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyReadPermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Allows the user to show the Studies associated with a Research Group
 * Used in ResearchGroupViewForm class
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupStudyInfoTable extends InfoTableWidget<StudyCountInfo> {
    public static final I18n i18n = I18nFactory.getI18n(ResearchGroupStudyInfoTable.class);

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        HasName.PropertyName.NAME.toString(),
        HasNameShort.PropertyName.NAME_SHORT.toString(),
        i18n.tr("Status") };

    public ResearchGroupStudyInfoTable(Composite parent, List<StudyCountInfo> studies) {
        super(parent, studies, HEADINGS, 10, null);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                StudyCountInfo info = (StudyCountInfo) ((BiobankCollectionModel) element).o;

                switch (columnIndex) {
                case 0:
                    return info.getStudy().getName();
                case 1:
                    return info.getStudy().getNameShort();
                case 2:
                    return (info.getStudy().getActivityStatus() != null) ? info
                        .getStudy().getActivityStatus().getName()
                        : StringUtil.EMPTY_STRING;
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    protected boolean isEditMode() {
        return false;
    }

    @Override
    public void reload() {
    }

    @Override
    public StudyCountInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null) return null;
        return (StudyCountInfo) item.o;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof StudyCountInfo
                    && o2 instanceof StudyCountInfo) {
                    StudyCountInfo p1 = (StudyCountInfo) o1;
                    StudyCountInfo p2 = (StudyCountInfo) o2;
                    return p1.getStudy().getNameShort()
                        .compareTo(p2.getStudy().getNameShort());
                }
                return super.compare(01, o2);
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Boolean canEdit(StudyCountInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new StudyUpdatePermission(target.getStudy().getId()));
    }

    @Override
    protected Boolean canDelete(StudyCountInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new StudyDeletePermission(target.getStudy().getId()));
    }

    @Override
    protected Boolean canView(StudyCountInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new StudyReadPermission(target.getStudy().getId()));
    }
}