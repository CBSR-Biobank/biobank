package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class Report  implements IBiobankModel
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
	
	public String description;
	/**
	* Retrieves the value of the description attribute
	* @return description
	**/

	public String getDescription(){
		return description;
	}

	/**
	* Sets the value of description attribute
	**/

	public void setDescription(String description){
		this.description = description;
	}
	
	/**
	* 
	**/
	
	public Integer userId;
	/**
	* Retrieves the value of the userId attribute
	* @return userId
	**/

	public Integer getUserId(){
		return userId;
	}

	/**
	* Sets the value of userId attribute
	**/

	public void setUserId(Integer userId){
		this.userId = userId;
	}
	
	/**
	* 
	**/
	
	public Boolean isPublic;
	/**
	* Retrieves the value of the isPublic attribute
	* @return isPublic
	**/

	public Boolean getIsPublic(){
		return isPublic;
	}

	/**
	* Sets the value of isPublic attribute
	**/

	public void setIsPublic(Boolean isPublic){
		this.isPublic = isPublic;
	}
	
	/**
	* 
	**/
	
	public Boolean isCount;
	/**
	* Retrieves the value of the isCount attribute
	* @return isCount
	**/

	public Boolean getIsCount(){
		return isCount;
	}

	/**
	* Sets the value of isCount attribute
	**/

	public void setIsCount(Boolean isCount){
		this.isCount = isCount;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.ReportColumn object's collection 
	**/
			
	private Collection<ReportColumn> reportColumnCollection = new HashSet<ReportColumn>();

	/**
	* Retrieves the value of the reportColumnCollection attribute
	* @return reportColumnCollection
	**/

	public Collection<ReportColumn> getReportColumnCollection(){
		return reportColumnCollection;
	}

	/**
	* Sets the value of reportColumnCollection attribute
	**/

	public void setReportColumnCollection(Collection<ReportColumn> reportColumnCollection){
		this.reportColumnCollection = reportColumnCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Entity object
	**/
			
	private Entity entity;
	/**
	* Retrieves the value of the entity attribute
	* @return entity
	**/
	
	public Entity getEntity(){
		return entity;
	}
	/**
	* Sets the value of entity attribute
	**/

	public void setEntity(Entity entity){
		this.entity = entity;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.ReportFilter object's collection 
	**/
			
	private Collection<ReportFilter> reportFilterCollection = new HashSet<ReportFilter>();

	/**
	* Retrieves the value of the reportFilterCollection attribute
	* @return reportFilterCollection
	**/

	public Collection<ReportFilter> getReportFilterCollection(){
		return reportFilterCollection;
	}

	/**
	* Sets the value of reportFilterCollection attribute
	**/

	public void setReportFilterCollection(Collection<ReportFilter> reportFilterCollection){
		this.reportFilterCollection = reportFilterCollection;
	}
		
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Report) 
		{
			Report c =(Report)obj; 			 
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
