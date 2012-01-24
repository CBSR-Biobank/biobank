package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class AliquotedSpecimen  implements IBiobankModel
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
	
	public Integer quantity;
	/**
	* Retrieves the value of the quantity attribute
	* @return quantity
	**/

	public Integer getQuantity(){
		return quantity;
	}

	/**
	* Sets the value of quantity attribute
	**/

	public void setQuantity(Integer quantity){
		this.quantity = quantity;
	}
	
	/**
	* 
	**/
	
	public Double volume;
	/**
	* Retrieves the value of the volume attribute
	* @return volume
	**/

	public Double getVolume(){
		return volume;
	}

	/**
	* Sets the value of volume attribute
	**/

	public void setVolume(Double volume){
		this.volume = volume;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.SpecimenType object
	**/
			
	private SpecimenType specimenType;
	/**
	* Retrieves the value of the specimenType attribute
	* @return specimenType
	**/
	
	public SpecimenType getSpecimenType(){
		return specimenType;
	}
	/**
	* Sets the value of specimenType attribute
	**/

	public void setSpecimenType(SpecimenType specimenType){
		this.specimenType = specimenType;
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
	* An associated edu.ualberta.med.biobank.model.ActivityStatus object
	**/
			
	private ActivityStatus activityStatus;
	/**
	* Retrieves the value of the activityStatus attribute
	* @return activityStatus
	**/
	
	public ActivityStatus getActivityStatus(){
		return activityStatus;
	}
	/**
	* Sets the value of activityStatus attribute
	**/

	public void setActivityStatus(ActivityStatus activityStatus){
		this.activityStatus = activityStatus;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof AliquotedSpecimen) 
		{
			AliquotedSpecimen c =(AliquotedSpecimen)obj; 			 
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
