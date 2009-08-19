package edu.ualberta.med.sampleProcessingRobot.serialInterface;

public class RobotException extends Exception {

	/**
	 * The (negative) integer error code that was the reason for
	 * this exception
	 */
	private final int errorCode;
	
	/**
	 * These define the integer values for the various errors.  These
	 * are created by the X-SEL controller should not be changed unless
	 * they are changed on the controller as well.
	 */
	public static final int CAP_GRIP_ERROR = -1;
	public static final int SOURCE_TUBE_GRIP_ERROR = -2;
	public static final int PIPETTE_TIP_ERROR = -3;
	public static final int SOURCE_TUBE_NUM_ERROR = -4;
	public static final int BARCODE_READ_ERROR = -5;
	public static final int LEVEL_READ_ERROR = -6;
	public static final int BAD_STATE_ERROR = -7;
	public static final int PIPETTE_COM_ERROR = -8;

	
	private static final String[] robotErrors = 
	{	"Invalid robot error code", // Error 0
		"Error gripping the source tube cap",
		"Error with a pipetter tip",
		"Error - no source tube",
		"Error - invalid source tube number",
		"Error gripping source tube",
		"Error communicating with the pipetter",
		"Error reading source tube barcode",
		"Error reading liquid level in source tube",
		"Error - robot is not in the correct state to execute requested action"
	};
	
	public RobotException(int err) throws IndexOutOfBoundsException {
		super( (err > 0 || err < -robotErrors.length) ? robotErrors[0] :
			robotErrors[-err]);
		errorCode = err;
	}
		
	public RobotException(String msg) {
		super(msg);
		errorCode = 0;
	}
}
