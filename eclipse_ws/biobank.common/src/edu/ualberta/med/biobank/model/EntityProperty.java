package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class EntityProperty  implements IBiobankModel
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
	* 
	**/
	
	public String property;
	/**
	* Retrieves the value of the property attribute
	* @return property
	**/

	public String getProperty(){
		return property;
	}

	/**
	* Sets the value of property attribute
	**/

	public void setProperty(String property){
		this.property = property;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.EntityColumn object's collection 
	**/
			
	private Collection<EntityColumn> entityColumnCollection = new HashSet<EntityColumn>();

	/**
	* Retrieves the value of the entityColumnCollection attribute
	* @return entityColumnCollection
	**/

	public Collection<EntityColumn> getEntityColumnCollection(){
		return entityColumnCollection;
	}

	/**
	* Sets the value of entityColumnCollection attribute
	**/

	public void setEntityColumnCollection(Collection<EntityColumn> entityColumnCollection){
		this.entityColumnCollection = entityColumnCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.PropertyType object
	**/
			
	private PropertyType propertyType;
	/**
	* Retrieves the value of the propertyType attribute
	* @return propertyType
	**/
	
	public PropertyType getPropertyType(){
		return propertyType;
	}
	/**
	* Sets the value of propertyType attribute
	**/

	public void setPropertyType(PropertyType propertyType){
		this.propertyType = propertyType;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.EntityFilter object's collection 
	**/
			
	private Collection<EntityFilter> entityFilterCollection = new HashSet<EntityFilter>();

	/**
	* Retrieves the value of the entityFilterCollection attribute
	* @return entityFilterCollection
	**/

	public Collection<EntityFilter> getEntityFilterCollection(){
		return entityFilterCollection;
	}

	/**
	* Sets the value of entityFilterCollection attribute
	**/

	public void setEntityFilterCollection(Collection<EntityFilter> entityFilterCollection){
		this.entityFilterCollection = entityFilterCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof EntityProperty) 
		{
			EntityProperty c =(EntityProperty)obj; 			 
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
