application-config-client.xml :
add interceptor to bean ApplicationService to intercept validation exceptions.

application-config-security.xml :
in securityInterceptor bean, add gov.nih.nci.system.applicationservice.WritableApplicationService.*=DUMMYVALUE in objectDefinitionSource. 
We are using a Custom ApplicationService that ineherit from WritableApplicationService. It seems we need to add the intermediate class in hierarchy to be sure the method called on it has security enabled.

application-config.xml :
add interceptor to bean ApplicationService to intercept validation exceptions.

build.properties = modify the ApplicationService used :

extension.api.interface=edu.ualberta.med.biobank.server.CustomApplicationService
extension.api.impl=edu.ualberta.med.biobank.server.CustomApplicationServiceImpl
extension.dao.impl=edu.ualberta.med.biobank.server.CustomORMDAOImpl
#extension.api.interface=gov.nih.nci.system.applicationservice.WritableApplicationService
#extension.api.impl=gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl
#extension.dao.impl=gov.nih.nci.system.dao.orm.WritableORMDAOImpl


validator-extension-Config.xml :
Add validator description for different model classes. Will add hibernate annotations in the generated classes.

