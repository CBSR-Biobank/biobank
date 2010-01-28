application-config-client.xml :
add interceptor to bean ApplicationService to intercept validation exceptions.

application-config-security.xml :
in securityInterceptor bean, add gov.nih.nci.system.applicationservice.WritableApplicationService.*=DUMMYVALUE in objectDefinitionSource. 
We are using a Custom ApplicationService that ineherit from WritableApplicationService. It seems we need to add the intermediate class in hierarchy to be sure the method called on it has security enabled.

application-config.xml :
add interceptor to bean ApplicationService to intercept validation exceptions.

build.properties = modify the ApplicationService used :
extension.api.interface=edu.ualberta.med.biobank.server.BiobankApplicationService
extension.api.impl=edu.ualberta.med.biobank.server.BiobankApplicationServiceImpl
extension.dao.impl=edu.ualberta.med.biobank.server.BiobankORMDAOImpl


validator-extension-Config.xml :
Add validator description for different model classes. Will add hibernate annotations in the generated classes.

