package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/
	

public class SpecimenType  implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
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
	* 
	**/
	
	@NotEmpty
	public String name;
	/**
	* Retrieves the value of the name attribute
	* @return name
	**/

	public String getName(){
		return name;
	}

	/**
	* Sets the value of name attribute
	**/

	public void setName(String name){
		this.name = name;
	}
	
	/**
	* 
	**/
	
	@NotEmpty
	public String nameShort;
	/**
	* Retrieves the value of the nameShort attribute
	* @return nameShort
	**/

	public String getNameShort(){
		return nameShort;
	}

	/**
	* Sets the value of nameShort attribute
	**/

	public void setNameShort(String nameShort){
		this.nameShort = nameShort;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.ContainerType object's collection 
	**/
			
	private Collection<ContainerType> containerTypeCollection = new HashSet<ContainerType>();
	/**
	* Retrieves the value of the containerTypeCollection attribute
	* @return containerTypeCollection
	**/

	public Collection<ContainerType> getContainerTypeCollection(){
		return containerTypeCollection;
	}

	/**
	* Sets the value of containerTypeCollection attribute
	**/

	public void setContainerTypeCollection(Collection<ContainerType> containerTypeCollection){
		this.containerTypeCollection = containerTypeCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.SpecimenType object's collection 
	**/
			
	private Collection<SpecimenType> parentSpecimenTypeCollection = new HashSet<SpecimenType>();
	/**
	* Retrieves the value of the parentSpecimenTypeCollection attribute
	* @return parentSpecimenTypeCollection
	**/

	public Collection<SpecimenType> getParentSpecimenTypeCollection(){
		return parentSpecimenTypeCollection;
	}

	/**
	* Sets the value of parentSpecimenTypeCollection attribute
	**/

	public void setParentSpecimenTypeCollection(Collection<SpecimenType> parentSpecimenTypeCollection){
		this.parentSpecimenTypeCollection = parentSpecimenTypeCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof SpecimenType) 
		{
			SpecimenType c =(SpecimenType)obj; 			 
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
	
	private Collection<SpecimenType> childSpecimenTypeCollection = new HashSet<SpecimenType>();
	public Collection<SpecimenType> getChildSpecimenTypeCollection(){
		return childSpecimenTypeCollection;
	}
	public void setChildSpecimenTypeCollection(Collection<SpecimenType> childSpecimenTypeCollection){
		this.childSpecimenTypeCollection = childSpecimenTypeCollection;
	}
}
