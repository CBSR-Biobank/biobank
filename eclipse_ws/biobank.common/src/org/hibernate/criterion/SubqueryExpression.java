//$Id: SubqueryExpression.java,v 1.8 2005/07/04 02:40:28 oneovthafew Exp $
// This file is here to override the class in the hibernate*.jar because
// this class has a bug that does not allow DetachedCriteria to use joins
// properly. See
// http://opensource.atlassian.com/projects/hibernate/browse/HHH-952 for the bug.
package org.hibernate.criterion;

import java.util.HashMap;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.Type;

/**
 * @author Gavin King
 */
@SuppressWarnings("serial")
public abstract class SubqueryExpression implements Criterion {

    private CriteriaImpl criteriaImpl;
    private String quantifier;
    private String op;
    private QueryParameters params;
    private Type[] types;
    private CriteriaQueryTranslator innerQuery;

    protected Type[] getTypes() {
        return types;
    }

    protected SubqueryExpression(String op, String quantifier,
        DetachedCriteria dc) {
        this.criteriaImpl = dc.getCriteriaImpl();
        this.quantifier = quantifier;
        this.op = op;
    }

    protected abstract String toLeftSqlString(Criteria criteria,
        CriteriaQuery outerQuery);

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
        throws HibernateException {

        final SessionFactoryImplementor factory =
            extractSessionFactoryImplementor(
                criteria, criteriaQuery);

        final OuterJoinLoadable persister = (OuterJoinLoadable) factory
            .getEntityPersister(criteriaImpl.getEntityOrClassName());
        createAndSetInnerQuery(criteriaQuery, factory);

        // String filter = persister.filterFragment(
        // innerQuery.getRootSQLALias(), session.getEnabledFilters() );

        // this old way didn't generate joins
        // String sql = new Select( factory.getDialect() )
        // .setWhereClause( innerQuery.getWhereCondition() )
        // .setGroupByClause( innerQuery.getGroupBy() )
        // .setSelectClause( innerQuery.getSelect() )
        // .setFromClause(
        // persister.fromTableFragment( innerQuery.getRootSQLALias() ) +
        // persister.fromJoinFragment( innerQuery.getRootSQLALias(), true, false
        // )
        // )
        // .toStatementString();

        // patch to generate joins on subqueries
        // stolen from CriteriaLoader
        @SuppressWarnings("rawtypes")
        CriteriaJoinWalker walker = new CriteriaJoinWalker(persister,
            innerQuery, factory, criteriaImpl,
            criteriaImpl.getEntityOrClassName(), new HashMap()) {
            // need to override default of "this_" to whatever the innerQuery is
            // using
            @Override
            protected String generateRootAlias(final String description) {
                return innerQuery.getRootSQLALias();
            }
        };

        String sql = walker.getSQLString();
        // end join patch

        final StringBuffer buf = new StringBuffer().append(toLeftSqlString(
            criteria, criteriaQuery));
        if (op != null)
            buf.append(' ').append(op).append(' ');
        if (quantifier != null)
            buf.append(quantifier).append(' ');
        return buf.append('(').append(sql).append(')').toString();
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria,
        CriteriaQuery criteriaQuery) throws HibernateException {
        // the following two lines were added to ensure that this.params is not
        // null, which
        // can happen with two-deep nested subqueries
        SessionFactoryImplementor factory = extractSessionFactoryImplementor(
            criteria, criteriaQuery);
        createAndSetInnerQuery(criteriaQuery, factory);

        Type[] ppTypes = params.getPositionalParameterTypes();
        Object[] ppValues = params.getPositionalParameterValues();
        TypedValue[] tv = new TypedValue[ppTypes.length];
        for (int i = 0; i < ppTypes.length; i++) {
            tv[i] = new TypedValue(ppTypes[i], ppValues[i], EntityMode.POJO);
        }
        return tv;
    }

    /**
     * Creates the inner query used to extract some useful information about
     * types, since it is needed in both methods.
     * 
     * @param criteriaQuery
     * @param factory
     */
    private void createAndSetInnerQuery(CriteriaQuery criteriaQuery,
        final SessionFactoryImplementor factory) {
        if (innerQuery == null) {
            // with two-deep subqueries, the same alias would get generated for
            // both using the old method (criteriaQuery.generateSQLAlias()), so
            // that is now used as a fallback if the main criteria alias isn't
            // set
            String alias;
            if (this.criteriaImpl.getAlias() == null) {
                alias = criteriaQuery.generateSQLAlias();
            } else {
                alias = this.criteriaImpl.getAlias() + "_"; //$NON-NLS-1$
            }

            innerQuery = new CriteriaQueryTranslator(factory, criteriaImpl,
                criteriaImpl.getEntityOrClassName(), // implicit polymorphism
                                                     // not supported (would
                                                     // need a union)
                alias, criteriaQuery);

            params = innerQuery.getQueryParameters();
            types = innerQuery.getProjectedTypes();
        }
    }

    /**
     * Determines the SessionFactoryImplementer based on the common parameters
     * of both primary methods. Currently assumes that the factory will be the
     * same for all calls within a chain. The previous code tried to get the
     * factory by casting the supplier Criteria to a CriteriaImpl, getting the
     * SessionImplementor from that, and then getting the factory. However,
     * since the SessionImplementor is never used except to get the factory,
     * this seemed like an easier alternative. Also, for subqueries nested
     * two-deep, the methods were being called with Criteria with null sessions
     * (because they were created from DetachedCriteria).
     * 
     * @param criteria
     * @param criteriaQuery
     * @return
     */
    private SessionFactoryImplementor extractSessionFactoryImplementor(
        Criteria criteria,
        CriteriaQuery criteriaQuery) {
        return criteriaQuery.getFactory();

        // the following code was originally used to get around one problem with
        // two-deep
        // subqueries where the Criteria passed in was a
        // CriteriaImpl.Subcriteria (causing
        // a ClassCastException), but after getting passed that it was
        // determined that
        // not all Criteria passed in would have session set, thus the line
        // above
        // NOTE: callers may get NullPointerException, but that was the case
        // before this change
        // due to a null this.params
        // SessionImplementor session = null;
        // SessionFactoryImplementor factory = null;
        // if ( criteria instanceof CriteriaImpl ) {
        // session = ( (CriteriaImpl) criteria ).getSession(); //ugly!
        // } else if ( criteria instanceof CriteriaImpl.Subcriteria ) {
        // CriteriaImpl temp = (CriteriaImpl) ((CriteriaImpl.Subcriteria)
        // criteria).getParent();
        // session = temp.getSession();
        // }
        //
        // if ( session != null ) {
        // factory = session.getFactory();
        // }
        // return factory;
    }

}