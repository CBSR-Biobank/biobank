package edu.ualberta.med.sampleProcessingRobot.serialInterface.processes;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.sampleProcessingRobot.serialInterface.RobotException;
import edu.ualberta.med.sampleProcessingRobot.serialInterface.RobotInterface;
import edu.ualberta.med.sampleProcessingRobot.serialInterface.RobotInterface.FluidLevelType;
import edu.ualberta.med.sampleProcessingRobot.serialInterface.RobotInterface.SourceTubeSize;

public abstract class RobotProcess implements Runnable {

    public static final Logger logger = Logger.getLogger(RobotProcess.class
        .getName());

    protected RobotInterface robot;

    protected FluidLevelType fluidLevelType;

    private int sourceTubeNumber;

    private String currentBarcode;

    protected double currentVolume;

    public RobotProcess(SourceTubeSize sourceTubeSize,
        FluidLevelType fluidLevelType, int sourceTubeNumber) {
        robot = new RobotInterface();
        this.sourceTubeNumber = sourceTubeNumber;
        this.fluidLevelType = fluidLevelType;
        try {
            robot.initialize(sourceTubeSize, fluidLevelType);
        } catch (RobotException e) {
            openError("Initialization Error",
                "Cannot initialize robot interface", e);
        }
    }

    public void run() {
        for (int sourceTubeIndex = 0; sourceTubeIndex < sourceTubeNumber; sourceTubeIndex++) {
            boolean res = getTube(sourceTubeIndex);
            if (!res)
                return;
            // readBarcode();
            res = scanSourceTubeLevel();
            if (!res)
                return;
            res = executeRoutine();
            if (res) {
                System.out.println("Success");
            } else {
                System.out.println("Process with error");
            }
        }
    }

    protected abstract boolean executeRoutine();

    /**
     * Get tube at position index
     * 
     * @return return ok if action is well done.
     */
    protected boolean getTube(int index) {
        try {
            robot.pickupSourceTube(index);
            return true;
        } catch (RobotException cbsre) {
            // FIXME do something better thant that
            openError("Robot error", "Error while getting the tube", cbsre);
            done(robot);
            return false;
        }
    }

    /**
     * Read barcode for current source tube
     */
    protected void readBarcode() {
        try {
            currentBarcode = robot.scanSourceTubeBarcode();
        } catch (RobotException cbsre) {
            // FIXME
            System.out.println(cbsre);
            done(robot);
            System.exit(1);
        }
        if (currentBarcode == "") {
            // FIXME
            System.out.println("Couldn't read source tube barcode");
            done(robot);
            System.exit(1);
        }

    }

    /**
     * Get fluid level
     */
    protected boolean scanSourceTubeLevel() {
        try {
            currentVolume = robot.scanSourceTubeLevel();
            return true;
        } catch (RobotException cbsre) {
            openError("Robot error", "error while scanning source tube level",
                cbsre);
            done(robot);
            return false;
        }
    }

    /**
     * Grip source tube
     */
    protected boolean gripSourceTube() {
        try {
            robot.gripSourceTube();
            return true;
        } catch (RobotException cbsre) {
            openError("Robot error", "Error while gripping the source tube",
                cbsre);
            done(robot);
            return false;
        }
    }

    /**
     * Get pipette tip
     */
    protected boolean newPipetteTip() {
        try {
            robot.newPipetteTip();
            return true;
        } catch (RobotException cbsre) {
            openError("Robot Error", "Error while getting a new pipette", cbsre);
            done(robot);
            return false;
        }
    }

    protected void done(RobotInterface robot) {
        try {
            robot.allDone();
        } catch (RobotException cbse) {
            openError("Robot error", "Could not cleanly shutdown robot!", cbse);
        }
    }

    /**
     * Display an error message and log the error
     */
    public void openError(String title, String message, Exception ex) {
        MessageDialog.openError(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), title, message);
        logger.log(Level.SEVERE, message, ex);
    }
}
