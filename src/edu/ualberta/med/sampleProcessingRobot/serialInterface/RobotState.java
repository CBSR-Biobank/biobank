/**
 * This class holds the state variables for the Canadian BioSample Repository
 * processing robot.  It is a combination of various barcodes, the state of 
 * the robot arm, and the internal state of the X-SEL controller.
 * 
 * Created by: Mike Sokolsky
 * Created on: 29 July, 2009
 * Last update: 9 August, 2009
 */
package edu.ualberta.med.sampleProcessingRobot.serialInterface;

/**
 * @author Mike Sokolsky
 *
 */
public class RobotState {

	private final boolean holdingCap;
	private final boolean holdingSourceTube;
	private final boolean grippingSourceTube;
	private final boolean havePipetteTip;
	private final String sourceTubeBarcode;
	private final int sourceTubeNumber;
	
	public RobotState(boolean haveCap, boolean holdST, boolean gripST, 
			boolean haveTip, String barcodeST, int numST) {
		holdingCap = haveCap;
		holdingSourceTube = holdST;
		grippingSourceTube = gripST;
		havePipetteTip = haveTip;
		sourceTubeBarcode = barcodeST;
		sourceTubeNumber = numST;
	}
	
	/**
	 * @return true if the Robot arm is holding a tube cap
	 */
	public boolean isHoldingCap() {
		return holdingCap;
	}

	/**
	 * @return true if the Robot arm is holding a source tube
	 */
	public boolean isHoldingSourceTube() {
		return holdingSourceTube;
	}

	/**
	 * @return true if the Robot is gripping a source tube on the table
	 */
	public boolean isGrippingSourceTube() {
		return grippingSourceTube;
	}

	/**
	 * @return true if the the Robot pipetter has a tip on
	 */
	public boolean isHavePipetteTip() {
		return havePipetteTip;
	}

	/**
	 * @return the source tube barcode value
	 */
	public String getSourceTubeBarcode() {
		return sourceTubeBarcode;
	}

	/**
	 * @return the current source tube number
	 */
	public int getSourceTubeNumber() {
		return sourceTubeNumber;
	}
	
}
