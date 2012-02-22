package edu.ualberta.med.biobank.test.action;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;

public class TestPermissionEnum extends TestAction {
    @Test
    public void testPersistedId() {
        Transaction tx = session.beginTransaction();
        
        String name = getMethodNameR();

        Role role = new Role();
        role.setName(name);

        Integer roleId = (Integer) session.save(role);

        for (PermissionEnum permissionEnum : PermissionEnum.values()) {
            role.getPermissionCollection().clear();
            role.getPermissionCollection().add(permissionEnum);

            session.update(role);
            session.flush();

            Integer permissionEnumId =
                (Integer) session
                    .createSQLQuery(
                        "select permission_id from role_permission where id = ?")
                    .setParameter(0, roleId)
                    .list().iterator().next();

            Assert.assertTrue(
                "stored id " + permissionEnumId
                    + " does not match PermissionEnum.getId() "
                    + permissionEnum.getId() + " of PermissionEnum "
                    + permissionEnum.name(),
                permissionEnumId.intValue() == permissionEnum.getId());
        }
        
        tx.rollback();
    }
}
