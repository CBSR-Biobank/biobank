package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class PatientViewNodeSearchVisitor extends NodeSearchVisitor {

    public PatientViewNodeSearchVisitor(ModelWrapper<?> wrapper) {
        super(wrapper);
    }

    @Override
    public AdapterBase visit(AbstractTodayNode todayNode) {
        if (wrapper instanceof StudyWrapper) {
            return todayNode.getChild(wrapper, true);
        }
        return visitChildren(todayNode);
    }

    @Override
    public AdapterBase visit(AbstractSearchedNode searchedNode) {
        if (wrapper instanceof StudyWrapper) {
            return searchedNode.getChild(wrapper, true);
        }
        return visitChildren(searchedNode);
    }

    @Override
    public AdapterBase visit(StudyAdapter study) {
        if (wrapper instanceof PatientWrapper) {
            return study.getChild(wrapper, true);
        }
        return null;
    }
}
