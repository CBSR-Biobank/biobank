@TypeDefs({
    @TypeDef(
        name = "shipmentState",
        typeClass = CustomEnumType.class,
        defaultForType = ShipmentState.class,
        parameters = {
            @Parameter(name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.type.ShipmentState")
        }),
    @TypeDef(
        name = "shipmentItemState",
        typeClass = CustomEnumType.class,
        defaultForType = ShipmentItemState.class,
        parameters = {
            @Parameter(name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.type.ShipmentItemState")
        }),
    @TypeDef(
        name = "requestSpecimenState",
        typeClass = CustomEnumType.class,
        defaultForType = RequestSpecimenState.class,
        parameters = {
            @Parameter(name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.type.RequestSpecimenState")
        })
})
package edu.ualberta.med.biobank.model;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.model.type.ShipmentItemState;
import edu.ualberta.med.biobank.model.type.ShipmentState;
import edu.ualberta.med.biobank.model.util.CustomEnumType;

