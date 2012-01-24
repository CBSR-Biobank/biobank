package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class RequestSpecimen  implements IBiobankModel
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
	
	public Integer state;
	/**
	* Retrieves the value of the state attribute
	* @return state
	**/

	public Integer getState(){
		return state;
	}

	/**
	* Sets the value of state attribute
	**/

	public void setState(Integer state){
		this.state = state;
	}
	
	/**
	* 
	**/
	
	public String claimedBy;
	/**
	* Retrieves the value of the claimedBy attribute
	* @return claimedBy
	**/

	public String getClaimedBy(){
		return claimedBy;
	}

	/**
	* Sets the value of claimedBy attribute
	**/

	public void setClaimedBy(String claimedBy){
		this.claimedBy = claimedBy;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.Specimen object
	**/
			
	private Specimen specimen;
	/**
	* Retrieves the value of the specimen attribute
	* @return specimen
	**/
	
	public Specimen getSpecimen(){
		return specimen;
	}
	/**
	* Sets the value of specimen attribute
	**/

	public void setSpecimen(Specimen specimen){
		this.specimen = specimen;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Request object
	**/
			
	private Request request;
	/**
	* Retrieves the value of the request attribute
	* @return request
	**/
	
	public Request getRequest(){
		return request;
	}
	/**
	* Sets the value of request attribute
	**/

	public void setRequest(Request request){
		this.request = request;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof RequestSpecimen) 
		{
			RequestSpecimen c =(RequestSpecimen)obj; 			 
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
