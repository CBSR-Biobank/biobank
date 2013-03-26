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

import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.study.Study;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "CENTER_MEMBERSHIP", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"PRINCIPAL_ID", "CENTER_ID" }) })
@Unique(properties = { "principal", "center" }, groups = PrePersist.class)
public class CenterMembership extends Membership<CenterPermission, CenterRole> {
	private static final long serialVersionUID = 1L;

	private Center center;
	private Set<CenterRole> roles = new HashSet<CenterRole>(0);
	private Set<Study> studies = new HashSet<Study>(0);

	@NotNull(message = "{CenterMembership.study.NotNull}")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CENTER_ID", nullable = false)
	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	@Override
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CENTER_MEMBERSHIP_ROLE", joinColumns = { @JoinColumn(name = "CENTER_MEMBERSHIP_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "CENTER_ROLE_ID", nullable = false, updatable = false) })
	public Set<CenterRole> getRoles() {
		return this.roles;
	}

	@Override
	public void setRoles(Set<CenterRole> roles) {
		this.roles = roles;
	}

	/**
	 * Allows the {@link Study}s a {@link Principal} can work with to be
	 * restricted.
	 * 
	 * @return the list of studies that this {@link CenterMemebership} is
	 *         limited to, or empty if it applies to all {@link Study}s.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CENTER_MEMBERSHIP_STUDY", joinColumns = { @JoinColumn(name = "CENTER_MEMBERSHIP_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) })
	public Set<Study> getStudies() {
		return studies;
	}

	public void setStudies(Set<Study> studies) {
		this.studies = studies;
	}

}
