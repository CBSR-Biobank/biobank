@TypeDefs({
    @TypeDef(
        name = "attributeValueType",
        typeClass = CustomEnumType.class,
        defaultForType = AttributeValueType.class,
        parameters = {
            @Parameter(name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.type.AttributeValueType")
        }),
    @TypeDef(
        name = "dispatchSpecimenState",
        typeClass = CustomEnumType.class,
        defaultForType = DispatchSpecimenState.class,
        parameters = {
            @Parameter(name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.type.DispatchSpecimenState")
        }),
    @TypeDef(
        name = "dispatchState",
        typeClass = CustomEnumType.class,
        defaultForType = DispatchState.class,
        parameters = {
            @Parameter(name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.type.DispatchState")
        }),
    @TypeDef(
        name = "permissionEnum",
        typeClass = CustomEnumType.class,
        defaultForType = PermissionEnum.class,
        parameters = {
            @Parameter(name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.type.PermissionEnum")
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

import edu.ualberta.med.biobank.model.type.AttributeValueType;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.model.type.PermissionEnum;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.model.util.CustomEnumType;

