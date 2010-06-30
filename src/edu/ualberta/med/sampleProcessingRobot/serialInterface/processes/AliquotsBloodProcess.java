package edu.ualberta.med.sampleProcessingRobot.serialInterface.processes;

import edu.ualberta.med.sampleProcessingRobot.serialInterface.RobotException;
import edu.ualberta.med.sampleProcessingRobot.serialInterface.RobotInterface.FluidLevelType;
import edu.ualberta.med.sampleProcessingRobot.serialInterface.RobotInterface.SourceTubeSize;

public class AliquotsBloodProcess extends RobotProcess {

    public AliquotsBloodProcess(SourceTubeSize sourceTubeSize,
        int sourceTubeNumber) {
        super(sourceTubeSize, FluidLevelType.FLUID_TYPE_BLOOD, sourceTubeNumber);
    }

    @Override
    protected boolean executeRoutine() {
        gripSourceTube();
        newPipetteTip();

        int sampleTubeIndex = 1;
        // Whittle down most of the liquid
        while (currentVolume > 2.0) {
            // Get some liquid
            try {
                currentVolume = robot.aspirateSample(800);
            } catch (RobotException cbsre) {
                openError("Robot error", "Error while aspirating sample", cbsre);
                done(robot);
                return false;
            }
            // Dispense to samples
            try {
                robot.dispensePallet(1, sampleTubeIndex++, 400);
                robot.dispensePallet(1, sampleTubeIndex++, 400);
            } catch (RobotException cbsre) {
                openError("Robot error",
                    "Error while dispensing blood into pallet", cbsre);
                done(robot);
                return false;
            }
        }
        // Clean up
        try {
            robot.disposePipetteTip();
            robot.returnSourceTube();
            robot.homeArm();
        } catch (RobotException cbsre) {
            openError("Robot Error", "Error while cleaning up", cbsre);
            done(robot);
            return false;
        }
        return true;
    }
}
