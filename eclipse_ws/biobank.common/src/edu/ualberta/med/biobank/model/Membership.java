package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class Membership  implements IBiobankModel
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

        private Integer version;

	
	/**
	* 
	**/
	
	public Integer id;
	/**
	* Retrieves the value of the id attribute
	* @return id
	**/

	public Integer getId(){
		return id;
	}

	/**
	* Sets the value of id attribute
	**/

	public void setId(Integer id){
		this.id = id;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.Permission object's collection 
	**/
			
	private Collection<Permission> permissionCollection = new HashSet<Permission>();

	/**
	* Retrieves the value of the permissionCollection attribute
	* @return permissionCollection
	**/

	public Collection<Permission> getPermissionCollection(){
		return permissionCollection;
	}

	/**
	* Sets the value of permissionCollection attribute
	**/

	public void setPermissionCollection(Collection<Permission> permissionCollection){
		this.permissionCollection = permissionCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Center object
	**/
			
	private Center center;
	/**
	* Retrieves the value of the center attribute
	* @return center
	**/
	
	public Center getCenter(){
		return center;
	}
	/**
	* Sets the value of center attribute
	**/

	public void setCenter(Center center){
		this.center = center;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Role object's collection 
	**/
			
	private Collection<Role> roleCollection = new HashSet<Role>();

	/**
	* Retrieves the value of the roleCollection attribute
	* @return roleCollection
	**/

	public Collection<Role> getRoleCollection(){
		return roleCollection;
	}

	/**
	* Sets the value of roleCollection attribute
	**/

	public void setRoleCollection(Collection<Role> roleCollection){
		this.roleCollection = roleCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Study object
	**/
			
	private Study study;
	/**
	* Retrieves the value of the study attribute
	* @return study
	**/
	
	public Study getStudy(){
		return study;
	}
	/**
	* Sets the value of study attribute
	**/

	public void setStudy(Study study){
		this.study = study;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Principal object
	**/
			
	private Principal principal;
	/**
	* Retrieves the value of the principal attribute
	* @return principal
	**/
	
	public Principal getPrincipal(){
		return principal;
	}
	/**
	* Sets the value of principal attribute
	**/

	public void setPrincipal(Principal principal){
		this.principal = principal;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Membership) 
		{
			Membership c =(Membership)obj; 			 
			if(getId() != null && getId().equals(c.getId()))
				return true;
		}
		return false;
	}
		
	/**
	* Returns hash code for the primary key of the object
	**/
	public int hashCode()
	{
		if(getId() != null)
			return getId().hashCode();
		return 0;
	}
	
}
