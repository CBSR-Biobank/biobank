package edu.ualberta.med.biobank.model.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import edu.ualberta.med.biobank.model.util.CustomEnumType;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "STUDY_ROLE")
@Unique(properties = "name", groups = PrePersist.class)
@NotUsed(by = StudyMembership.class, property = "roles", groups = PreDelete.class)
public class StudyRole
    extends Role<StudyPermission> {
    private static final long serialVersionUID = 1L;

    private Set<StudyPermission> perms = new HashSet<StudyPermission>(0);

    @Override
    @ElementCollection(targetClass = StudyPermission.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "STUDY_ROLE_PERMISSION", joinColumns = @JoinColumn(name = "STUDY_ROLE_ID"))
    @Type(
        type = "edu.ualberta.med.biobank.model.util.CustomEnumType",
        parameters = {
            @Parameter(
                name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.securityStudyPermission"
            )
        })
    @Column(name = "STUDY_PERMISSION_ID", nullable = false)
    public Set<StudyPermission> getPermissions() {
        return this.perms;
    }

    @Override
    public void setPermissions(Set<StudyPermission> permissions) {
        this.perms = permissions;
    }
}
