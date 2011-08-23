package edu.ualberta.med.biobank.widgets.multiselect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class NewMultiSelectNodeTransfer extends ByteArrayTransfer {
    private static final NewMultiSelectNodeTransfer INSTANCE = new NewMultiSelectNodeTransfer();

    public static NewMultiSelectNodeTransfer getInstance() {
        return INSTANCE;
    }

    private NewMultiSelectNodeTransfer() {
        super();
    }

    private static final String TYPE_NAME = "favorites-transfer-format:" //$NON-NLS-1$ 
        + System.currentTimeMillis() + ":" + INSTANCE.hashCode(); //$NON-NLS-1$

    private static final int TYPEID = registerType(TYPE_NAME);

    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    @Override
    protected void javaToNative(Object data, TransferData transferData) {
        if (!(data instanceof Object[]))
            return;
        Object[] items = (Object[]) data;

        /**
         * The serialization format is: (int) number of items Then, the
         * following for each item: (String) the id (String) the name
         */
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(out);
            dataOut.writeInt(items.length);
            for (int i = 0; i < items.length; i++) {
                ModelWrapper<?> item = (ModelWrapper<?>) items[i];
                dataOut.writeUTF(item.getId().toString());
                dataOut.writeUTF(item.getClass().getName());
            }
            dataOut.close();
            out.close();
            super.javaToNative(out.toByteArray(), transferData);
        } catch (IOException e) {
            // Send nothing if there were problems.
        }
    }

    @Override
    protected Object nativeToJava(TransferData transferData) {
        /**
         * The serialization format is: (int) number of items Then, the
         * following for each item: (String) id (String) name
         */
        byte[] bytes = (byte[]) super.nativeToJava(transferData);
        if (bytes == null)
            return null;
        DataInputStream in = new DataInputStream(
            new ByteArrayInputStream(bytes));
        try {
            int count = in.readInt();
            List<ModelWrapper<?>> items = new ArrayList<ModelWrapper<?>>(count);
            for (int i = 0; i < count; i++) {
                Integer id = Integer.valueOf(in.readUTF());
                String clazz = in.readUTF();
                ModelWrapper<?> wrapper;
                wrapper = getModelWrapper(clazz, id);
                items.add(wrapper);

            }
            return items.toArray(new ModelWrapper<?>[items.size()]);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private ModelWrapper<?> getModelWrapper(String clazzName, Integer id)
        throws Exception {
        Class<ModelWrapper<?>> clazz = (Class<ModelWrapper<?>>) Class
            .forName(clazzName);
        Constructor<ModelWrapper<?>> constructor = clazz
            .getConstructor(WritableApplicationService.class);

        ModelWrapper<?> wrapper = constructor.newInstance(SessionManager
            .getAppService());
        while (clazz != null && !clazz.equals(ModelWrapper.class)) {
            clazz = (Class<ModelWrapper<?>>) clazz.getSuperclass();
        }
        if (clazz != null) {
            Method setIdMethod = clazz
                .getDeclaredMethod("setId", Integer.class); //$NON-NLS-1$
            setIdMethod.setAccessible(true);
            setIdMethod.invoke(wrapper, id);
            wrapper.reload();
            return wrapper;
        }
        return null;
    }
}
