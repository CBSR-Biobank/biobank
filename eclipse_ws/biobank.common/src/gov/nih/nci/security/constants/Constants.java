/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.nih.nci.security.constants;

/**
 *
 *<!-- LICENSE_TEXT_START -->
 *
 *The NCICB Common Security Module (CSM) Software License, Version 3.0 Copyright
 *2004-2005 Ekagra Software Technologies Limited ('Ekagra')
 *
 *Copyright Notice.  The software subject to this notice and license includes both
 *human readable source code form and machine readable, binary, object code form
 *(the 'CSM Software').  The CSM Software was developed in conjunction with the
 *National Cancer Institute ('NCI') by NCI employees and employees of Ekagra.  To
 *the extent government employees are authors, any rights in such works shall be
 *subject to Title 17 of the United States Code, section 105.    
 *
 *This CSM Software License (the 'License') is between NCI and You.  'You (or
 *'Your') shall mean a person or an entity, and all other entities that control,
 *are controlled by, or are under common control with the entity.  'Control' for
 *purposes of this definition means (i) the direct or indirect power to cause the
 *direction or management of such entity, whether by contract or otherwise, or
 *(ii) ownership of fifty percent (50%) or more of the outstanding shares, or
 *(iii) beneficial ownership of such entity.  
 *
 *This License is granted provided that You agree to the conditions described
 *below.  NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 *no-charge, irrevocable, transferable and royalty-free right and license in its
 *rights in the CSM Software to (i) use, install, access, operate, execute, copy,
 *modify, translate, market, publicly display, publicly perform, and prepare
 *derivative works of the CSM Software; (ii) distribute and have distributed to
 *and by third parties the CSM Software and any modifications and derivative works
 *thereof; and (iii) sublicense the foregoing rights set out in (i) and (ii) to
 *third parties, including the right to license such rights to further third
 *parties.  For sake of clarity, and not by way of limitation, NCI shall have no
 *right of accounting or right of payment from You or Your sublicensees for the
 *rights granted under this License.  This License is granted at no charge to You.
 *
 *1.    Your redistributions of the source code for the Software must retain the
 *above copyright notice, this list of conditions and the disclaimer and
 *limitation of liability of Article 6 below.  Your redistributions in object code
 *form must reproduce the above copyright notice, this list of conditions and the
 *disclaimer of Article 6 in the documentation and/or other materials provided
 *with the distribution, if any.
 *2.    Your end-user documentation included with the redistribution, if any, must
 *include the following acknowledgment: 'This product includes software developed
 *by Ekagra and the National Cancer Institute.'  If You do not include such
 *end-user documentation, You shall include this acknowledgment in the Software
 *itself, wherever such third-party acknowledgments normally appear.
 *
 *3.    You may not use the names 'The National Cancer Institute', 'NCI' 'Ekagra
 *Software Technologies Limited' and 'Ekagra' to endorse or promote products
 *derived from this Software.  This License does not authorize You to use any
 *trademarks, service marks, trade names, logos or product names of either NCI or
 *Ekagra, except as required to comply with the terms of this License.
 *
 *4.    For sake of clarity, and not by way of limitation, You may incorporate this
 *Software into Your proprietary programs and into any third party proprietary
 *programs.  However, if You incorporate the Software into third party proprietary
 *programs, You agree that You are solely responsible for obtaining any permission
 *from such third parties required to incorporate the Software into such third
 *party proprietary programs and for informing Your sublicensees, including
 *without limitation Your end-users, of their obligation to secure any required
 *permissions from such third parties before incorporating the Software into such
 *third party proprietary software programs.  In the event that You fail to obtain
 *such permissions, You agree to indemnify NCI for any claims against NCI by such
 *third parties, except to the extent prohibited by law, resulting from Your
 *failure to obtain such permissions.
 *
 *5.    For sake of clarity, and not by way of limitation, You may add Your own
 *copyright statement to Your modifications and to the derivative works, and You
 *may provide additional or different license terms and conditions in Your
 *sublicenses of modifications of the Software, or any derivative works of the
 *Software as a whole, provided Your use, reproduction, and distribution of the
 *Work otherwise complies with the conditions stated in this License.
 *
 *6.    THIS SOFTWARE IS PROVIDED 'AS IS,' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 *(INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 *NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED.  IN NO
 *EVENT SHALL THE NATIONAL CANCER INSTITUTE, EKAGRA, OR THEIR AFFILIATES BE LIABLE
 *FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 *TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *<!-- LICENSE_TEXT_END -->
 *
 */

/**
 * @author Kunal Modi (Ekagra Software Technologies Ltd.)
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Constants {
    public static final String INITIAL_CONTEXT = "com.sun.jndi.ldap.LdapCtxFactory"; //$NON-NLS-1$
    public static final String LDAP_HOST = "ldapHost"; //$NON-NLS-1$
    public static final String LDAP_SEARCHABLE_BASE = "ldapSearchableBase"; //$NON-NLS-1$
    public static final String LDAP_USER_ID_LABEL = "ldapUserIdLabel"; //$NON-NLS-1$
    public static final String LDAP_ADMIN_USER_NAME = "ldapAdminUserName"; //$NON-NLS-1$
    public static final String LDAP_ADMIN_PASSWORD = "ldapAdminPassword"; //$NON-NLS-1$

    public static final String USER_LOGIN_ID = "USER_LOGIN_ID"; //$NON-NLS-1$
    public static final String USER_PASSWORD = "USER_PASSWORD"; //$NON-NLS-1$
    public static final String USER_FIRST_NAME = "USER_FIRST_NAME"; //$NON-NLS-1$
    public static final String USER_LAST_NAME = "USER_LAST_NAME"; //$NON-NLS-1$
    public static final String USER_EMAIL_ID = "USER_EMAIL_ID"; //$NON-NLS-1$

    public static final String TABLE_NAME = "TABLE_NAME"; //$NON-NLS-1$

    public static final String CSM_EXECUTE_PRIVILEGE = "EXECUTE"; //$NON-NLS-1$
    public static final String CSM_ACCESS_PRIVILEGE = "ACCESS"; //$NON-NLS-1$
    public static final String CSM_READ_PRIVILEGE = "READ"; //$NON-NLS-1$
    public static final String CSM_WRITE_PRIVILEGE = "WRITE"; //$NON-NLS-1$
    public static final String CSM_UPDATE_PRIVILEGE = "UPDATE"; //$NON-NLS-1$
    public static final String CSM_DELETE_PRIVILEGE = "DELETE"; //$NON-NLS-1$
    public static final String CSM_CREATE_PRIVILEGE = "CREATE"; //$NON-NLS-1$

    public static final String AUTHENTICATION = "authentication"; //$NON-NLS-1$
    public static final String AUTHORIZATION = "authorization"; //$NON-NLS-1$

    public static final String FILE_NAME_SUFFIX = ".csm.new.hibernate.cfg.xml"; //$NON-NLS-1$
    public static final String APPLICATION_SECURITY_CONFIG_FILE = "ApplicationSecurityConfig.xml"; //$NON-NLS-1$
    public static final String YES = "YES"; //$NON-NLS-1$

    public static final String ENCRYPTION_ENABLED = "encryption-enabled"; //$NON-NLS-1$

    public static final String LOCKOUT_TIME = "1800000"; //$NON-NLS-1$
    public static final String ALLOWED_LOGIN_TIME = "60000"; //$NON-NLS-1$
    public static final String ALLOWED_ATTEMPTS = "6"; //$NON-NLS-1$

    public static final String HIBERNATE_MYSQL_DIALECT = "org.hibernate.dialect.MySQLDialect"; //$NON-NLS-1$
    public static final String CSM_FILTER_ALIAS = "z_csm_filter_alias_z"; //$NON-NLS-1$
    public static final String CSM_FILTER_USER_QUERY_PART_ONE = "( select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.user_id = u.user_id and ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= '"; //$NON-NLS-1$
    public static final String CSM_FILTER_USER_QUERY_PART_TWO = "' and p.privilege_name='READ' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID"; //$NON-NLS-1$
    public static final String CSM_FILTER_GROUP_QUERY_PART_ONE = "( select distinct pe.attribute_value from csm_protection_group pg,    csm_protection_element pe,  csm_pg_pe pgpe, csm_user_group_role_pg ugrpg,   csm_group g,    csm_role_privilege rp,  csm_role r,     csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.group_id = g.group_id and ugrpg.protection_group_id = any ( select pg1.protection_group_id from csm_protection_group pg1  where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id) ) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= '"; //$NON-NLS-1$
    public static final String CSM_FILTER_GROUP_QUERY_PART_TWO = "' and p.privilege_name='READ' and g.group_name IN (:GROUP_NAMES ) and pe.application_id=:APPLICATION_ID"; //$NON-NLS-1$

    public static final String ENABLE = "enable"; //$NON-NLS-1$

}