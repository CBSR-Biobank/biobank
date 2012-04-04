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

public class MultiSelectNodeTransfer extends ByteArrayTransfer {
    private static final MultiSelectNodeTransfer INSTANCE = new MultiSelectNodeTransfer();

    public static MultiSelectNodeTransfer getInstance() {
        return INSTANCE;
    }

    private MultiSelectNodeTransfer() {
        super();
    }

    private static final String TYPE_NAME = "favorites-transfer-format:"  
        + System.currentTimeMillis() + ":" + INSTANCE.hashCode(); 

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

        if (!(data instanceof MultiSelectNode<?>[]))
            return;
        MultiSelectNode<?>[] items = (MultiSelectNode<?>[]) data;

        /**
         * The serialization format is: (int) number of items Then, the
         * following for each item: (String) the id (String) the model wrapper
         * class name
         */
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(out);
            dataOut.writeInt(items.length);
            for (int i = 0; i < items.length; i++) {
                MultiSelectNode<?> item = items[i];
                if (item.getNodeObject() instanceof ModelWrapper<?>) {
                    ModelWrapper<?> mw = (ModelWrapper<?>) item.getNodeObject();
                    dataOut.writeUTF(mw.getId().toString());
                    dataOut.writeUTF(mw.getClass().getName());
                }
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
         * following for each item: (String) id (String) class name
         */
        byte[] bytes = (byte[]) super.nativeToJava(transferData);
        if (bytes == null)
            return null;
        DataInputStream in = new DataInputStream(
            new ByteArrayInputStream(bytes));
        try {
            int count = in.readInt();
            List<MultiSelectNode<?>> items = new ArrayList<MultiSelectNode<?>>(
                count);
            for (int i = 0; i < count; i++) {
                Integer id = Integer.valueOf(in.readUTF());
                String clazz = in.readUTF();
                ModelWrapper<?> wrapper;
                wrapper = getModelWrapper(clazz, id);
                items.add(new MultiSelectNode<ModelWrapper<?>>(null, wrapper));
            }
            return items.toArray(new MultiSelectNode<?>[items.size()]);
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
                .getDeclaredMethod("setId", Integer.class); 
            setIdMethod.setAccessible(true);
            setIdMethod.invoke(wrapper, id);
            wrapper.reload();
            return wrapper;
        }
        return null;
    }

}
