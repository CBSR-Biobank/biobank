package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class ReportFilter  implements IBiobankModel
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
	
	public Integer position;
	/**
	* Retrieves the value of the position attribute
	* @return position
	**/

	public Integer getPosition(){
		return position;
	}

	/**
	* Sets the value of position attribute
	**/

	public void setPosition(Integer position){
		this.position = position;
	}
	
	/**
	* 
	**/
	
	public Integer operator;
	/**
	* Retrieves the value of the operator attribute
	* @return operator
	**/

	public Integer getOperator(){
		return operator;
	}

	/**
	* Sets the value of operator attribute
	**/

	public void setOperator(Integer operator){
		this.operator = operator;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.ReportFilterValue object's collection 
	**/
			
	private Collection<ReportFilterValue> reportFilterValueCollection = new HashSet<ReportFilterValue>();

	/**
	* Retrieves the value of the reportFilterValueCollection attribute
	* @return reportFilterValueCollection
	**/

	public Collection<ReportFilterValue> getReportFilterValueCollection(){
		return reportFilterValueCollection;
	}

	/**
	* Sets the value of reportFilterValueCollection attribute
	**/

	public void setReportFilterValueCollection(Collection<ReportFilterValue> reportFilterValueCollection){
		this.reportFilterValueCollection = reportFilterValueCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.EntityFilter object
	**/
			
	private EntityFilter entityFilter;
	/**
	* Retrieves the value of the entityFilter attribute
	* @return entityFilter
	**/
	
	public EntityFilter getEntityFilter(){
		return entityFilter;
	}
	/**
	* Sets the value of entityFilter attribute
	**/

	public void setEntityFilter(EntityFilter entityFilter){
		this.entityFilter = entityFilter;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof ReportFilter) 
		{
			ReportFilter c =(ReportFilter)obj; 			 
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
