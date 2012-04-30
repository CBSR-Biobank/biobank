package gov.nih.nci.security.authorization.domainobjects;

import java.io.Serializable;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationServiceImpl;

/**
 * Copy of original class from CSM4.1 There are bugs in the hashCode, compareTo
 * and equals methods that are solved here. This class is supposed to be the one
 * taken by the classloader of the war file.
 * 
 * The correction code has been copied from the CSM4.2 source class. TODO: can
 * we use CSM4.2 without any problem ? Do we need migration for that ?
 * 
 * @see BiobankApplicationServiceImpl
 * 
 */
@SuppressWarnings("rawtypes")
public class ProtectionElementPrivilegeContext implements Comparable,
    Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1284454348966855754L;
    /**
     * Collection of privileges for this protection element
     */
    private java.util.Set privileges;
    /**
     * The protection element for which privileges are assigned.
     */
    private ProtectionElement protectionElement;

    /**
     * Default constructor
     */
    public ProtectionElementPrivilegeContext() {
    }

    /**
     * Collection of privileges for this protection element
     */
    public java.util.Set getPrivileges() {
        return privileges;
    }

    /**
     * Collection of privileges for this protection element
     * 
     * @param newVal
     * 
     */
    public void setPrivileges(java.util.Set newVal) {
        privileges = newVal;
    }

    /**
     * The protection element for which privileges are assigned.
     */
    public ProtectionElement getProtectionElement() {
        return protectionElement;
    }

    /**
     * The protection element for which privileges are assigned.
     * 
     * @param newVal
     * 
     */
    public void setProtectionElement(ProtectionElement newVal) {
        protectionElement = newVal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProtectionElementPrivilegeContext) {
            ProtectionElementPrivilegeContext other =
                (ProtectionElementPrivilegeContext) obj;
            if (null == this) {
                return false;
            }
            if (null == other.getProtectionElement()
                || null == this.getProtectionElement()) {
                return false;
            }
            if (this
                .getProtectionElement()
                .getProtectionElementName()
                .equals(other.getProtectionElement().getProtectionElementName())) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof ProtectionElementPrivilegeContext) {
            ProtectionElementPrivilegeContext a =
                (ProtectionElementPrivilegeContext) object;
            return this
                .getProtectionElement()
                .getProtectionElementName()
                .compareToIgnoreCase(
                    a.getProtectionElement().getProtectionElementName());
        }
        return 0;
    }

    @Override
    public int hashCode() {
        int intNumber = 57 * 5;
        intNumber = intNumber
            + ((null == protectionElement ? 0 : protectionElement
                .getProtectionElementId().intValue()));
        return intNumber;
    }

}
