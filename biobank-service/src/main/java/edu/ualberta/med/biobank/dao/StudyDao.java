package edu.ualberta.med.biobank.dao;

import org.springframework.stereotype.Repository;

import edu.ualberta.med.biobank.model.study.Study;

@Repository("StudyDao")
public interface StudyDao extends GenericDao<Study> {

}
