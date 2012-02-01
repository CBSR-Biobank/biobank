/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.JasperTemplate;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.JasperTemplatePeer;

public class JasperTemplateBaseWrapper extends ModelWrapper<JasperTemplate> {

    public JasperTemplateBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public JasperTemplateBaseWrapper(WritableApplicationService appService,
        JasperTemplate wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<JasperTemplate> getWrappedClass() {
        return JasperTemplate.class;
    }

    @Override
    public Property<Integer, ? super JasperTemplate> getIdProperty() {
        return JasperTemplatePeer.ID;
    }

    @Override
    protected List<Property<?, ? super JasperTemplate>> getProperties() {
        return JasperTemplatePeer.PROPERTIES;
    }

    public String getName() {
        return getProperty(JasperTemplatePeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(JasperTemplatePeer.NAME, trimmed);
    }

    public String getXml() {
        return getProperty(JasperTemplatePeer.XML);
    }

    public void setXml(String xml) {
        String trimmed = xml == null ? null : xml.trim();
        setProperty(JasperTemplatePeer.XML, trimmed);
    }

}
