package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.util.NotAProxy;

/**
 * represent a protection element
 * 
 */
@Deprecated
public class ProtectionElement implements Serializable, NotAProxy {

    private static final long serialVersionUID = 1L;

    /**
     * This is the CSM protection element object id (for instance, can be
     * 'edu.ualberta.med.biobank.model.Clinic')
     */
    private String type;

    /**
     * If the CSM protection element define a attribute "id", then this is the
     * value of this attribute. (for instance, this is used for sites objects,
     * to set different accesses depending on the site id)
     */
    private String id;

    public ProtectionElement(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public ProtectionElement(String type, Integer id) {
        this(type, id == null ? null : id.toString());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getType() + "/" + getId(); //$NON-NLS-1$
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProtectionElement) {
            ProtectionElement pep = (ProtectionElement) obj;
            boolean sameType = getType() != null && pep.getType() != null
                && getType().equals(pep.getType());
            boolean sameId = (getId() == null && pep.getId() == null)
                || (getId() != null && pep.getId() != null && getId().equals(
                    pep.getId()));
            return sameType && sameId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (getType() + getId()).hashCode();
    }
}
