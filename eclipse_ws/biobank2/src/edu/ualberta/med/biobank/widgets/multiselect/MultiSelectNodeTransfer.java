package edu.ualberta.med.biobank.widgets.multiselect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class MultiSelectNodeTransfer extends ByteArrayTransfer {
	private static final MultiSelectNodeTransfer INSTANCE =
		new MultiSelectNodeTransfer();

	public static MultiSelectNodeTransfer getInstance() {
		return INSTANCE;
	}
	
	private MultiSelectNodeTransfer() {
		super();
	}

	private static final String TYPE_NAME =
		"favorites-transfer-format:" + System.currentTimeMillis() + ":"  //$NON-NLS-1$ //$NON-NLS-2$
		+ INSTANCE.hashCode();

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

		if (!(data instanceof MultiSelectNode[])) return;
		MultiSelectNode[] items = (MultiSelectNode[]) data;

		/**
		 * The serialization format is:
		 *  (int) number of items
		 * Then, the following for each item:
		 *  (String) the id
		 *  (String) the name
		 */
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(out);
			dataOut.writeInt(items.length);
			for (int i = 0; i < items.length; i++) {
				MultiSelectNode item = items[i];
				dataOut.writeUTF("" + item.getId()); //$NON-NLS-1$
				dataOut.writeUTF(item.getName());
			}
			dataOut.close();
			out.close();
			super.javaToNative(out.toByteArray(), transferData);
		}
		catch (IOException e) {
			// Send nothing if there were problems.
		}
	}
	@Override
	protected Object nativeToJava(TransferData transferData) {
		/**
		 * The serialization format is:
		 *  (int) number of items
		 * Then, the following for each item:
		 *  (String) id
		 *  (String) name
		 */
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		if (bytes == null)
			return null;
		DataInputStream in =new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			int count = in.readInt();
			List<MultiSelectNode> items = new ArrayList<MultiSelectNode>(count);
			for (int i = 0; i < count; i++) {
				String typeId = in.readUTF();
				String info = in.readUTF();
				items.add(new MultiSelectNode(null, new Integer(typeId).intValue(), info));
			}
			return items.toArray(new MultiSelectNode[items.size()]);
		}
		catch (IOException e) {
			return null;
		}
	}


}
