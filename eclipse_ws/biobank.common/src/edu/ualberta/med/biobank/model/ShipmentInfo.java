package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class ShipmentInfo  implements IBiobankModel
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
	
	public java.util.Date receivedAt;
	/**
	* Retrieves the value of the receivedAt attribute
	* @return receivedAt
	**/

	public java.util.Date getReceivedAt(){
		return receivedAt;
	}

	/**
	* Sets the value of receivedAt attribute
	**/

	public void setReceivedAt(java.util.Date receivedAt){
		this.receivedAt = receivedAt;
	}
	
	/**
	* 
	**/
	
	public java.util.Date packedAt;
	/**
	* Retrieves the value of the packedAt attribute
	* @return packedAt
	**/

	public java.util.Date getPackedAt(){
		return packedAt;
	}

	/**
	* Sets the value of packedAt attribute
	**/

	public void setPackedAt(java.util.Date packedAt){
		this.packedAt = packedAt;
	}
	
	/**
	* 
	**/
	
	public String waybill;
	/**
	* Retrieves the value of the waybill attribute
	* @return waybill
	**/

	public String getWaybill(){
		return waybill;
	}

	/**
	* Sets the value of waybill attribute
	**/

	public void setWaybill(String waybill){
		this.waybill = waybill;
	}
	
	/**
	* 
	**/
	
	public String boxNumber;
	/**
	* Retrieves the value of the boxNumber attribute
	* @return boxNumber
	**/

	public String getBoxNumber(){
		return boxNumber;
	}

	/**
	* Sets the value of boxNumber attribute
	**/

	public void setBoxNumber(String boxNumber){
		this.boxNumber = boxNumber;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.ShippingMethod object
	**/
			
	private ShippingMethod shippingMethod;
	/**
	* Retrieves the value of the shippingMethod attribute
	* @return shippingMethod
	**/
	
	public ShippingMethod getShippingMethod(){
		return shippingMethod;
	}
	/**
	* Sets the value of shippingMethod attribute
	**/

	public void setShippingMethod(ShippingMethod shippingMethod){
		this.shippingMethod = shippingMethod;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof ShipmentInfo) 
		{
			ShipmentInfo c =(ShipmentInfo)obj; 			 
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
