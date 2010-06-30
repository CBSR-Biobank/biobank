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

import java.text.DecimalFormat;

import gnu.io.SerialPort;

/**
 * @author Mike Sokolsky
 * 
 */
public class RobotInterface {

    /**
     * The optical fluid level sensor has three settings, this defines which one
     * to use
     */
    public enum FluidLevelType {
        FLUID_TYPE_BLOOD, FLUID_TYPE_PLASMA, FLUID_TYPE_OTHER
    };

    /**
     * The size of the source tube to be used on this run.
     */
    public enum SourceTubeSize {
        TEN_MIL, SIX_MIL, THREE_MIL
    };

    /**
     * The maximum valid source tube number. Although there are 50 spaces, at
     * least for the larger diameter tubes, the final row (closest to the door)
     * has some issues so it is limited to 45 for now.
     */
    public static final int MAX_SOURCE_TUBE_NUM = 45;

    /**
     * The maximum volume of fluid the pipetter can hold at one time.
     */
    public static final int MAX_ASPIRATE_VOLUME = 1000;

    /**
     * The number of pallets available to dispense to
     */
    public static final int MAX_PALLET_NUM = 3;

    /**
     * The number of tubes available in a pallet
     */
    public static final int MAX_TUBE_INDEX = 96;

    /**
     * The number of stepper steps in a uL, from converting from mL to steps.
     * The datasheet for the stepper specifies 763uL/step, or 1.310 steps/uL.
     * You can adjust this number to compensate for lost liquid in the pipette
     * tip, or even get really fancy and add a non-linear scaler if that
     * improves accuracy. The pipetter also has a limited range, so increasing
     * this too far and requesting 1mL aspirations may result in less liquid
     * being aspirated than expected.
     */
    private static final double STEPS_PER_UL = 1.310;

    /**
     * The number of uL per mm of fluid in a source tube. Defined for the two
     * diameters of source tube.
     */
    private static final double UL_PER_MM_WIDE = 133.33333;
    private static final double UL_PER_MM_NARROW = 0;

    /**
     * ASCII carriage return.
     */
    public static final byte CR = 13;

    private SourceTubeSize tubeSize;
    // private FluidLevelType levelType;

    private SerialInterface xselPort;

    /**
     * COM port name to try and connect to the X-SEL serial interface on. This
     * is to connect to the COM2 port on the X-SEL, not the control port
     */
    private static final String COMM_PORT = "COM5";

    private DecimalFormat oneDigit, twoDigit, fourDigit;

    public RobotInterface() {
        oneDigit = new DecimalFormat("0");
        twoDigit = new DecimalFormat("00");
        fourDigit = new DecimalFormat("0000");
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
     * Sets up a new run with given source tube and sample parameters This will
     * throw an exception if the X-SEL controller believes it is either holding
     * a source tube, cap, gripping a source tube, or has a pipette tip. To
     * override internal state, use hardReset();
     * 
     * @param size - Size of the source tube for this run
     * @param fluid - Type of optical level sensor fluid to use for this run
     * @throws RobotException
     */
    public void initialize(SourceTubeSize size, FluidLevelType fluid)
        throws RobotException {
        if (!connectToXSEL())
            throw new RobotInitException("Unable to connect to the robot");
        /*
         * CBSRobotState state = getRobotState();
         * if(state.isGrippingSourceTube() || state.isHavePipetteTip() ||
         * state.isHoldingCap() || state.isHoldingSourceTube()) throw new
         * CBSRobotInitException("Cannot initialize because the" +
         * "robot thinks it is holding something.");
         */
        if (size == null)
            throw new NullPointerException("The source tube size is null!");
        if (fluid == null)
            throw new NullPointerException(
                "The level sense fluid type is null!");
        tubeSize = size;
        // levelType = fluid;
    }

    /**
     * Resets the internal state of the X-SEL controller. WARNING! This function
     * should not be called unless the operator has confirmed that none of the
     * robot grippers are holding anything and that the pipetter does not have a
     * tip attached. Calling this method in any other state can and will result
     * in damage.
     */
    public void hardReset() {
        return;
    }

    /**
     * Tells the Robot to pick up the source tube indicated by num
     * 
     * @param num - Number of the source tube to pick up, 1 is far left corner,
     *            5 is far right corner, 6 is adjacent to 1.
     * @throws RobotException
     */
    public void pickupSourceTube(int num) throws RobotException {
        if (num < 1 || num > MAX_SOURCE_TUBE_NUM)
            throw new IllegalArgumentException("Source tube " + num + " is an"
                + "invalid source tube number");
        runCommand("RUN SelST " + twoDigit.format(num));
    }

    /**
     * Moves the source tube currently held by the arm to the gripper on the
     * table and removes the cap
     * 
     * @throws RobotException
     */
    public void gripSourceTube() throws RobotException {
        runCommand("RUN GrpST");
    }

    /**
     * Re-caps the source tube currently gripped on the table, and returns it to
     * its previous location in the source tube pallet
     * 
     * @throws RobotException
     */
    public void returnSourceTube() throws RobotException {
        runCommand("RUN RetST");
    }

    /**
     * Picks up a new pipette tip
     * 
     * @throws RobotException
     */
    public void newPipetteTip() throws RobotException {
        runCommand("RUN GtTip");
    }

    /**
     * Disposes of the current pipette tip
     * 
     * @throws RobotException
     */
    public void disposePipetteTip() throws RobotException {
        runCommand("RUN DsTip");
    }

    /**
     * Aspirates the indicated volume of liquid from the source tube currently
     * gripped on the table.
     * 
     * @param volume - amount of sample to aspirate in uL
     * @return the volume left in the source tube after aspiration
     * @throws RobotException
     */
    public int aspirateSample(int volume) throws RobotException {
        if (volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
            throw new IllegalArgumentException(volume + " is an invalid"
                + "volume of liquid to aspirate");
        int steps = (int) (volume * STEPS_PER_UL);
        runCommand("RUN AspST " + fourDigit.format(steps));
        String response = xselPort.readString(CR);
        if (!response.startsWith("LVL"))
            throw new RobotComException(
                "Robot did not correctly report source tube fluid volume");
        int level = Integer.parseInt(response.substring(4));
        if (tubeSize == SourceTubeSize.TEN_MIL)
            return (int) (level * UL_PER_MM_WIDE);
        else
            return (int) (level * UL_PER_MM_NARROW);
    }

    /**
     * Aspirates the indicated volume of liquid from the Pentaspan container
     * 
     * @param volume - amount of pentaspan to aspirate in uL
     * @throws RobotException
     */
    public void aspiratePentaspan(int volume) throws RobotException {
        if (volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
            throw new IllegalArgumentException(volume + " is an invalid"
                + "volume of liquid to aspirate");
        int steps = (int) (volume * STEPS_PER_UL);
        runCommand("RUN AspPT " + fourDigit.format(steps));
    }

    /**
     * Aspirates the indicated volume of liquid from the PBS container
     * 
     * @param volume - amount of PBS to aspirate in uL
     * @throws RobotException
     */
    public void aspiratePBS(int volume) throws RobotException {
        if (volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
            throw new IllegalArgumentException(volume + " is an invalid"
                + "volume of liquid to aspirate");
        int steps = (int) (volume * STEPS_PER_UL);
        runCommand("RUN AspPB " + fourDigit.format(steps));
    }

    /**
     * Dispenses a given volume of fluid from the pipetter into the source tube
     * 
     * @param volume - amount of liquid to dispense in uL
     * @throws RobotException
     */
    public void dispenseSource(int volume) throws RobotException {
        if (volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
            throw new IllegalArgumentException(volume + " is an invalid"
                + "volume of liquid to dispense");
        int steps = (int) Math.floor(volume * STEPS_PER_UL);
        runCommand("RUN DspST " + fourDigit.format(steps));
    }

    /**
     * Dispense a given volume of fluid from the pipetter into the specified
     * pallet and tube
     * 
     * @param palletNum - the index of the pallet to dispense to, pallet 1 is
     *            the farthest from the door
     * @param tubeIndex - the tube in the pallet to dispense to, 1 is A1, 12 is
     *            A12, 13 is B1
     * @param volume - the volume of fluid to dispense into the tube in uL
     * @throws RobotException
     */
    public void dispensePallet(int palletNum, int tubeIndex, int volume)
        throws RobotException {
        if (palletNum < 1 || palletNum > MAX_PALLET_NUM)
            throw new IllegalArgumentException(palletNum
                + " is an invalid pallet number");
        if (tubeIndex < 1 || tubeIndex > MAX_TUBE_INDEX)
            throw new IllegalArgumentException(tubeIndex
                + " is an invalid tube index");
        if (volume <= 0 || volume > MAX_ASPIRATE_VOLUME)
            throw new IllegalArgumentException(volume
                + " is an invalid volume of liquid to dispense");
        int steps = (int) Math.floor(volume * STEPS_PER_UL);
        runCommand("RUN DspCT " + oneDigit.format(palletNum) + " "
            + twoDigit.format(tubeIndex) + " " + fourDigit.format(steps));
    }

    /**
     * Dispenses 0.4mL of fluid from the pipetter onto the next free FTA card
     * 
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
     * 
     * @return fluid level in mL, this is approximate
     * @throws RobotException
     */
    public double scanSourceTubeLevel() throws RobotException {
        throw new RobotException("Function not Implemented");
    }

    /**
     * Queries the current state of the robot and peripherals
     * 
     * @return an immutable object with the current state of the Robot
     * @throws RobotException
     */
    public RobotState getRobotState() throws RobotException {
        throw new RobotException("Function not Implemented");
    }

    /**
     * This will clean up anything left, including disposing of a pipette tip,
     * capping the source tube, and returning it to the rack. It will leave the
     * robot in the default position.
     * 
     * @throws RobotException
     */
    public void allDone() throws RobotException {
        // throw new CBSRobotException("Function not Implemented");
        disconnectFromXSEL();
    }

    /**
     * This listens for up to 1.5 seconds for the controller to send the "READY"
     * signal,
     * 
     * @param command
     * @throws RobotException
     */
    private void runCommand(String command) throws RobotException {
        final int LOOPS = 10;
        for (int i = 0; i < LOOPS; i++) {
            if (xselPort.readString(CR).equals("READY")) {
                xselPort.writeString(command);
                break;
            }
            if (i == LOOPS - 1) {
                throw new RobotComException(
                    "Robot isn't ready for a new command");
            }
            try {
                Thread.sleep(150);
            } catch (Exception e) {
            }
            ;
        }
        String response = xselPort.readString();
        if (!response.startsWith("RET")) {
            throw new RobotComException("Unexpected response from robot");
        }
        int retVal = Integer.parseInt(response.substring(4));
        if (retVal != 1)
            throw new RobotException(retVal);
        return;
    }

    private boolean connectToXSEL() {
        if ((xselPort != null) && xselPort.isOpen())
            return true;
        xselPort = new SerialInterface(COMM_PORT, 9600, SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        if (!xselPort.isOpen()) {
            return false;
        }
        String result = xselPort.readString(CR);
        if (result == null) return true;
        
        if (result.equals("SYNCH")) {        	
            xselPort.writeString("START\r");
            if (xselPort.readString(CR).equals("READY"))
                ;
            return true;
        }
        xselPort.close();
        return false;
    }

    private void disconnectFromXSEL() {
        if (xselPort.isOpen()) {
            for (int i = 0; i < 10; i++) {
                if (xselPort.readString(CR).equals("READY")) {
                    xselPort.writeString("RST");
                    break;
                }
                try {
                    Thread.sleep(150);
                } catch (Exception e) {
                }
                ;
            }
            xselPort.close();
        }
    }

    @SuppressWarnings("unused")
    private boolean connectToSourceTubeScanner() {
        // FIXME: remove suppress warning when code is completed
        return false;
    }

    @SuppressWarnings("unused")
    private boolean connectToFTAScanner() {
        // FIXME: remove suppress warning when code is completed
        return false;
    }

}
