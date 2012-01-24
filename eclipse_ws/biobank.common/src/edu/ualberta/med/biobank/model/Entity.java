package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class Entity  implements IBiobankModel
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
	
	public String className;
	/**
	* Retrieves the value of the className attribute
	* @return className
	**/

	public String getClassName(){
		return className;
	}

	/**
	* Sets the value of className attribute
	**/

	public void setClassName(String className){
		this.className = className;
	}
	
	/**
	* 
	**/
	
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
	* An associated edu.ualberta.med.biobank.model.Report object's collection 
	**/
			
	private Collection<Report> reportCollection = new HashSet<Report>();

	/**
	* Retrieves the value of the reportCollection attribute
	* @return reportCollection
	**/

	public Collection<Report> getReportCollection(){
		return reportCollection;
	}

	/**
	* Sets the value of reportCollection attribute
	**/

	public void setReportCollection(Collection<Report> reportCollection){
		this.reportCollection = reportCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.EntityProperty object's collection 
	**/
			
	private Collection<EntityProperty> entityPropertyCollection = new HashSet<EntityProperty>();

	/**
	* Retrieves the value of the entityPropertyCollection attribute
	* @return entityPropertyCollection
	**/

	public Collection<EntityProperty> getEntityPropertyCollection(){
		return entityPropertyCollection;
	}

	/**
	* Sets the value of entityPropertyCollection attribute
	**/

	public void setEntityPropertyCollection(Collection<EntityProperty> entityPropertyCollection){
		this.entityPropertyCollection = entityPropertyCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Entity) 
		{
			Entity c =(Entity)obj; 			 
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
