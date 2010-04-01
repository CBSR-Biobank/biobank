package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class PatientViewNodeSearchVisitor extends NodeSearchVisitor {

    public PatientViewNodeSearchVisitor(ModelWrapper<?> wrapper) {
        super(wrapper);
    }

    @Override
    public AdapterBase visit(AbstractTodayNode todayNode) {
        if (wrapper instanceof StudyWrapper || wrapper instanceof ClinicWrapper) {
            return todayNode.getChild(wrapper.getId(), true);
        }
        return visitChildren(todayNode);
    }

    @Override
    public AdapterBase visit(AbstractSearchedNode searchedNode) {
        if (wrapper instanceof StudyWrapper || wrapper instanceof ClinicWrapper) {
            return searchedNode.getChild(wrapper.getId(), true);
        }
        return visitChildren(searchedNode);
    }
}
