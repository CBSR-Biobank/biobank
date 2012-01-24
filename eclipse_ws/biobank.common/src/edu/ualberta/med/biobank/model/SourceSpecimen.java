package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class SourceSpecimen  implements IBiobankModel
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
	
	public Boolean needOriginalVolume;
	/**
	* Retrieves the value of the needOriginalVolume attribute
	* @return needOriginalVolume
	**/

	public Boolean getNeedOriginalVolume(){
		return needOriginalVolume;
	}

	/**
	* Sets the value of needOriginalVolume attribute
	**/

	public void setNeedOriginalVolume(Boolean needOriginalVolume){
		this.needOriginalVolume = needOriginalVolume;
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
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof SourceSpecimen) 
		{
			SourceSpecimen c =(SourceSpecimen)obj; 			 
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
