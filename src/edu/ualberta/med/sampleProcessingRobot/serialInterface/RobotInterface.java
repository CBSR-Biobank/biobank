/**
 * Java code for interacting with the Canadian BioSample Repository 
 * processing robot.  Provides an interface to the AIA SCARA IX arm
 * and related peripherals - currently the barcode scanners.  All
 * communications with the micropipetter are handled by the X-SEL
 * robot controller directly.
 * 
 * This provides primarily lower-level functionality, based around 
 * the ability to call individual programs on the X-SEL controller.
 * These programs are actions like 'Pick up source tube #5' or
 * 'Aspirate 1mL of fluid from the source tube'.
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
public class RobotInterface {

	/**
	 * The optical fluid level sensor has three settings, this defines
	 * which one to use
	 */
	public enum FluidLevelType {FLUID_TYPE_BLOOD, FLUID_TYPE_PLASMA, FLUID_TYPE_OTHER};
	
	/**
	 * The size of the source tube to be used on this run.
	 */
	public enum SourceTubeSize {TEN_MIL, SIX_MIL, THREE_MIL};
		
	/**
	 * The maximum valid source tube number.  Although there are 50 spaces, at
	 * least for the larger diameter tubes, the final row (closest to the door)
	 * has some issues so it is limited to 45 for now.
	 */
	public static final int MAX_SOURCE_TUBE_NUM = 45;
	
	/**
	 * The maximum volume of fluid the pipetter can hold at one time in mL.
	 */
	public static final double MAX_ASPIRATE_VOLUME = 1.0;

	/**
	 * The number of pallets available to dispense to
	 */
	public static final int MAX_PALLET_NUM = 3;

	/**
	 * The number of tubes available in a pallet
	 */
	public static final int MAX_TUBE_INDEX = 96;
	
	private SourceTubeSize tubeSize;
	private FluidLevelType levelType;
	
	public RobotInterface() {
		
	}
	
	/**
	 * Moves the Robot to its home position
	 * 
	 * @throws RobotException
	 */
	public void homeArm() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Sets up a new run with given source tube and sample parameters
	 * This will throw an exception if the X-SEL controller believes it is either
	 * holding a source tube, cap, gripping a source tube, or has a pipette tip.
	 * To override internal state, use hardReset();
	 * @param size - Size of the source tube for this run
	 * @param fluid - Type of optical level sensor fluid to use for this run
	 * @throws RobotException
	 */
	public void initialize(SourceTubeSize size, FluidLevelType fluid) throws RobotException {
		RobotState state = getRobotState();
		if(state.isGrippingSourceTube() ||
				state.isHavePipetteTip() ||
				state.isHoldingCap() ||
				state.isHoldingSourceTube())
			throw new RobotInitException("Cannot initialize because the robot" +
					"thinks it is holding something.");
		if(size == null)
			throw new NullPointerException("The source tube size is null!");
		if(fluid == null)
			throw new NullPointerException("The level sense fluid type is null!");
		tubeSize = size;
		levelType = fluid;
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Resets the internal state of the X-SEL controller.
	 * WARNING!  This function should not be called unless the operator has
	 * confirmed that none of the robot grippers are holding anything and that
	 * the pipetter does not have a tip attached.  Calling this method in any
	 * other state can and will result in damage.
	 */
	public void hardReset() {
		return;
	}
	
	/**
	 * Tells the Robot to pick up the source tube indicated by num
	 * @param num - Number of the source tube to pick up, 1 is far left corner,
	 * 5 is far right corner, 6 is adjacent to 1.
	 * @throws RobotException
	 */
	public void pickupSourceTube(int num) throws RobotException {
		if(num < 1 || num > MAX_SOURCE_TUBE_NUM)
			throw new IllegalArgumentException("Source tube " + num + " is an invalid source tube number");
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Moves the source tube currently held by the arm to the gripper on the 
	 * table and removes the cap 
	 * @throws RobotException
	 */
	public void gripSourceTube() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Re-caps the source tube currently gripped on the table, and returns
	 * it to its previous location in the source tube pallet
	 * @throws RobotException
	 */
	public void returnSourceTube() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Picks up a new pipette tip
	 * @throws RobotException
	 */
	public void newPipetteTip() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Disposes of the current pipette tip
	 * @throws RobotException
	 */
	public void disposePipetteTip() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Aspirates the indicated volume of liquid from the source tube
	 * currently gripped on the table.
	 * @param volume - amount of sample to aspirate in mL
	 * @return the volume left in the source tube after aspiration
	 * @throws RobotException
	 */
	public double aspirateSample(double volume) throws RobotException {
		if(volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
			throw new IllegalArgumentException(volume + " is an invalid volume of liquid to aspirate");
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Aspirates the indicated volume of liquid from the Pentaspan container
	 * @param volume - amount of pentaspan to aspirate in mL
	 * @throws RobotException
	 */
	public void aspiratePentaspan(double volume) throws RobotException {
		if(volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
			throw new IllegalArgumentException(volume + " is an invalid volume of liquid to aspirate");
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Aspirates the indicated volume of liquid from the PBS container
	 * @param volume - amount of PBS to aspirate in mL
	 * @throws RobotException
	 */
	public void aspiratePBS(double volume) throws RobotException {
		if(volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
			throw new IllegalArgumentException(volume + " is an invalid volume of liquid to aspirate");
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Dispenses a given volume of fluid from the pipetter into the source tube
	 * @param volume - amount of liquid to dispense in mL
	 * @throws RobotException
	 */
	public void dispenseSource(double volume) throws RobotException {
		if(volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
			throw new IllegalArgumentException(volume + " is an invalid volume of liquid to dispense");
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Dispense a given volume of fluid from the pipetter into the specified
	 * pallet and tube
	 * @param palletNum - the index of the pallet to dispense to, pallet 1 is the farthest from the door
	 * @param tubeIndex - the tube in the pallet to dispense to, 1 is A1, 12 is A12, 13 is B1
	 * @param volume - the volume of fluid to dispense into the tube in mL
	 * @throws RobotException
	 */
	public void dispensePallet(int palletNum, int tubeIndex, double volume) throws RobotException {
		if(palletNum < 1 || palletNum > MAX_PALLET_NUM)
			throw new IllegalArgumentException(palletNum + " is an invalid pallet number");
		if(tubeIndex < 1 || tubeIndex > MAX_TUBE_INDEX)
			throw new IllegalArgumentException(tubeIndex + " is an invalid tube index");
		if(volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
			throw new IllegalArgumentException(volume + " is an invalid volume of liquid to dispense");
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Dispenses 0.4mL of fluid from the pipetter onto the next free FTA card
	 * @return the barcode of the FTA card that was just filled
	 * @throws RobotException
	 */
	public String dispenseFTA() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Moves the source tube to the barcode reader, starts scanning for a code
	 * and rotates the tube 360 degrees.
	 * 
	 * @return Source tube's barcode, or empty string if barcode was not read.
	 * @throws RobotException
	 */
	public String scanSourceTubeBarcode() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Scan the fluid level for the source tube the Robot is currently holding
	 * @return fluid level in mL, this is approximate
	 * @throws RobotException
	 */
	public double scanSourceTubeLevel() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * Queries the current state of the robot and peripherals
	 * @return an immutable object with the current state of the Robot
	 * @throws RobotException
	 */
	public RobotState getRobotState() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	/**
	 * This will clean up anything left, including disposing of a pipette
	 * tip, capping the source tube, and returning it to the rack.  It will
	 * leave the robot in the default position.
	 * @throws RobotException
	 */
	public void allDone() throws RobotException {
		throw new RobotException("Function not Implemented");
	}
	
	private boolean connectToXSEL() {
		return false;
	}
	
	private boolean connectToSourceTubeScanner() {
		return false;
	}
	
	private boolean connectToFTAScanner() {
		return false;
	}
	
	
}
