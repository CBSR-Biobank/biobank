package edu.ualberta.med.biobank.model.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.study.Study;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "STUDY_MEMBERSHIP", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"PRINCIPAL_ID", "STUDY_ID" }) })
@Unique(properties = { "principal", "study" }, groups = PrePersist.class)
public class StudyMembership extends Membership<StudyPermission, StudyRole> {
	private static final long serialVersionUID = 1L;

	private Study study;
	private Set<StudyRole> roles = new HashSet<StudyRole>(0);

	@NotNull(message = "{StudyMembership.study.NotNull}")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STUDY_ID", nullable = false)
	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	@Override
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "STUDY_MEMBERSHIP_ROLE", joinColumns = { @JoinColumn(name = "STUDY_MEMBERSHIP_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "STUDY_ROLE_ID", nullable = false, updatable = false) })
	public Set<StudyRole> getRoles() {
		return this.roles;
	}

	@Override
	public void setRoles(Set<StudyRole> roles) {
		this.roles = roles;
	}
}
