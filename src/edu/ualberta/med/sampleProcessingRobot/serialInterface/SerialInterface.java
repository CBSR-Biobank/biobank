package edu.ualberta.med.sampleProcessingRobot.serialInterface;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author Mike Sokolsky
 * 
 * A simple wrapper for the java serial library (here supplied by gnu's RXTX)
 * 
 * Provides basic communication to and from the serial port, as well as some
 * useful related methods
 *
 */
public class SerialInterface {
	
	// When set to true debugging information is printed to stderr
	private static final boolean DEBUG_OUTPUT = true;
	private static final int DEFAULT_TIMEOUT = 500;
	
	private SerialPort port;
	private InputStream in;
	private OutputStream out;
	private final String portName;
	private final int baudRate;
	private final int dataBits;
	private final int stopBits;
	private final int parity;
	
	private int timeout = DEFAULT_TIMEOUT;
	
	/**
	 * Creates a new SerialInterface with the specific settings
	 * 
	 * @param portName String with the name of the COM port (i.e. "COM6")
	 * @param baudRate Communication baud rate
	 * @param dataBits See SerialPort.DATABITS
	 * @param stopBits See SerialPort.STOPBITS
	 * @param parity See SerialPort.PARITY
	 */
	public SerialInterface(String _portName, int _baudRate, int _dataBits,
			int _stopBits, int _parity) {
		portName = _portName;
		baudRate = _baudRate;
		dataBits = _dataBits;
		stopBits = _stopBits;
		parity = _parity;
		open();
	}
	
	/**
	 * Sets the readString timeout length in milliseconds.  0 is block forever.
	 * 
	 * @param _timeout time in milliseconds
	 */
	public void setTimeout(int _timeout) {
		if(timeout < 0)
			return;
		timeout = _timeout;
	}
	/**
	 * 
	 * @return true if the serial port referenced by this class is open
	 */
	public boolean isOpen() {
		if(port != null)
			return true;
		else
			return false;
	}
	
	/**
	 * Tries to open the port specified when this instance was created.
	 * @return true on success, false on failure
	 */
	public boolean open() {
		if(isOpen()) {
			print("Port already open");
			return true;
		}
		print("Opening port " + portName);
		try {
			CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(portName);
			CommPort cp = cpi.open(this.getClass().getName(), 2000);
			if(cp instanceof SerialPort) {
				port = (SerialPort) cp;
				port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				port.setDTR(true);
				port.setRTS(true);
				port.setSerialPortParams(
						baudRate, 
						dataBits, 
						stopBits,
						parity);
				port.setDTR(true);
				port.setRTS(true);
				in = port.getInputStream();
				//(new Thread(new SerialReader(in))).start();
				out = port.getOutputStream();
				System.out.println("Opened port " + portName);
				return true;
			}
			else {
				port = null;
				print("Port " + portName + " is not a COM port!");
			}
		} catch (NoSuchPortException e) {
			port = null;
			print("Port " + portName + " does not exist!");
		} catch (PortInUseException e) {
			port = null;
			print("Port " + portName + " is in use!");
		} catch (UnsupportedCommOperationException e) {
			port.close();
			port = null;
			print("Could not open " + portName + 
					" with correct settings.");
		} catch (IOException e) {
			port = null;
			print("Could not open " + portName + 
					" io streams.");
		}
		return false;
	}
	
	public void close() {
		if(!isOpen()) {
			print("Port " + portName + " is not open!");
			port.close();
			port = null;
		}
	}
	
	/**
	 * Reads one byte from the serial buffer
	 * @return a byte from the serial buffer, -1 if error or no byte available
	 */
	public int readByte() {
		try {
			return in.read();
		} catch (IOException e) {
			print("Error reading byte from " + portName);
		}
		return -1;
	}
	
	/**
	 * Reads an array of bytes from the serial buffer, returning as many as
	 * are available in the buffer, up to the size of dest.
	 * 
	 * @param dest byte array to read data into
	 * @return number of bytes read, -1 on failure
	 */
	public int read(byte[] dest) {
		try {
			return in.read(dest);
		} catch (IOException e) {
			print("Error reading bytes from " + portName);
		}
		return -1;
	}
	
	/**
	 * Reads an ASCII encoded string of fixed length from the serial buffer
	 * Blocks for the current timeout value or until len bytes are read.
	 * 
	 * @param len the number of characters to read
	 * @return the String containing the characters read, null on error
	 */
	public String readString(int len) {
		final int BUF_SIZE = 100;
		byte[] byteDest = new byte[BUF_SIZE];
		String ret = null;
		int num;
		long startTime = System.currentTimeMillis();
		try {
			while(len > 0) {
				if((num = in.read(byteDest, 0, Math.max(len, BUF_SIZE))) > 0) {
					len -= num;
					assert(len >= 0);
					ret += new String(byteDest, 0, num);
					// Don't sleep if there are more characters available.
					if(num == BUF_SIZE)
						continue;
				}
				try { 
					Thread.sleep(10);
				}
				catch (InterruptedException e) {
					print("Error sleeping");
				}
				if(timeout > 0 && System.currentTimeMillis() > startTime +
						timeout)
					break;
			}
		} catch (IOException e) {
			print("Error reading String from " + portName);
		}
		return ret;
	}
	
	/**
	 * Reads an ASCII encoded string from the serial port.
	 * 
	 * @return A String with as many characters as were ready in the buffer.
	 */
	public String readString() {
		final int BUF_SIZE = 100;
		byte[] byteDest = new byte[BUF_SIZE];
		String ret = null;
		int num = BUF_SIZE;
		try {
			while(num == BUF_SIZE) {
				if((num = in.read(byteDest)) > 0) {
					ret += new String(byteDest, 0, num);
				}
			}
		} catch (IOException e) {
			print("Error reading String from " + portName);
		}
		return ret;
	}
	
	/**
	 * Writes a single byte to the serial buffer
	 * 
	 * @param src byte to write out the serial port
	 * @return 0 on success, -1 on failure
	 */
	public int writeByte(byte src) {
		try {
			out.write(src);
			return 0;
		} catch (IOException e) {
			print("Error writing byte to " + portName);
		}
		return -1;
	}
	
	/**
	 * Writes an array of bytes to the serial buffer
	 * 
	 * @param src byte array to write
	 * @return 0 on success, -1 on failure
	 */
	public int write(byte[] src) {
		try {
			out.write(src);
			return 0;
		} catch (IOException e) {
			print("Error writing bytes to " + portName);
		}
		return -1;
	}
	
	/**
	 * Writes a String to the serial buffer as ASCII encoded characters
	 * 
	 * @param src the String to write
	 * @return 0 on success, -1 on failure
	 */
	public int writeString(String src) {
		// Convert the argument strings to ASCII
		byte[] srcAsBytes;
		try {
			srcAsBytes = src.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			print("US-ASCII is an unsupported character encoding.");
			print("Unless you are running this on a very strange computer");
			print("You should never get this error.");
			return -1;
		}
		return write(srcAsBytes);
	}
	
	private void print(String s) {
		if(DEBUG_OUTPUT == true)
			System.err.println(s);
	}
}
