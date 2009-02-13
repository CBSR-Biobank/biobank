package edu.ualberta.med.biobank.treeview;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class NodeTransfer extends ByteArrayTransfer {
	private static final NodeTransfer INSTANCE =
		new NodeTransfer();

	public static NodeTransfer getInstance() {
		return INSTANCE;
	}
	
	private NodeTransfer() {
		super();
	}

	private static final String TYPE_NAME =
		"favorites-transfer-format:" + System.currentTimeMillis() + ":" 
		+ INSTANCE.hashCode();

	private static final int TYPEID = registerType(TYPE_NAME);

	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}
	
	protected void javaToNative(Object data, TransferData transferData) {

		if (!(data instanceof Node[])) return;
		Node[] items = (Node[]) data;

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
				Node item = items[i];
				dataOut.writeUTF("" + item.getId());
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
			List<Node> items = new ArrayList<Node>(count);
			for (int i = 0; i < count; i++) {
				String typeId = in.readUTF();
				String info = in.readUTF();
				items.add(new Node(null, new Integer(typeId).intValue(), info));
			}
			return (Node[]) items.toArray(new Node[items.size()]);
		}
		catch (IOException e) {
			return null;
		}
	}


}
