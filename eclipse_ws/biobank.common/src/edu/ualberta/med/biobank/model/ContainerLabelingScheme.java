package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class ContainerLabelingScheme  implements IBiobankModel
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
	
	public Integer minChars;
	/**
	* Retrieves the value of the minChars attribute
	* @return minChars
	**/

	public Integer getMinChars(){
		return minChars;
	}

	/**
	* Sets the value of minChars attribute
	**/

	public void setMinChars(Integer minChars){
		this.minChars = minChars;
	}
	
	/**
	* 
	**/
	
	public Integer maxChars;
	/**
	* Retrieves the value of the maxChars attribute
	* @return maxChars
	**/

	public Integer getMaxChars(){
		return maxChars;
	}

	/**
	* Sets the value of maxChars attribute
	**/

	public void setMaxChars(Integer maxChars){
		this.maxChars = maxChars;
	}
	
	/**
	* 
	**/
	
	public Integer maxRows;
	/**
	* Retrieves the value of the maxRows attribute
	* @return maxRows
	**/

	public Integer getMaxRows(){
		return maxRows;
	}

	/**
	* Sets the value of maxRows attribute
	**/

	public void setMaxRows(Integer maxRows){
		this.maxRows = maxRows;
	}
	
	/**
	* 
	**/
	
	public Integer maxCols;
	/**
	* Retrieves the value of the maxCols attribute
	* @return maxCols
	**/

	public Integer getMaxCols(){
		return maxCols;
	}

	/**
	* Sets the value of maxCols attribute
	**/

	public void setMaxCols(Integer maxCols){
		this.maxCols = maxCols;
	}
	
	/**
	* 
	**/
	
	public Integer maxCapacity;
	/**
	* Retrieves the value of the maxCapacity attribute
	* @return maxCapacity
	**/

	public Integer getMaxCapacity(){
		return maxCapacity;
	}

	/**
	* Sets the value of maxCapacity attribute
	**/

	public void setMaxCapacity(Integer maxCapacity){
		this.maxCapacity = maxCapacity;
	}
	
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof ContainerLabelingScheme) 
		{
			ContainerLabelingScheme c =(ContainerLabelingScheme)obj; 			 
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
