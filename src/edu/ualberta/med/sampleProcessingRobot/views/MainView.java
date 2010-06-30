package edu.ualberta.med.sampleProcessingRobot.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.sampleProcessingRobot.serialInterface.RobotInterface.SourceTubeSize;
import edu.ualberta.med.sampleProcessingRobot.serialInterface.processes.AliquotsBloodProcess;

public class MainView extends ViewPart {

    public static final String ID = "edu.ualberta.med.sampleProcessingRobot.views.MainView";

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        Label label = new Label(parent, SWT.NONE);
        label.setText("Number of source tubes:");
        final Text sourceTubeNumberText = new Text(parent, SWT.BORDER);

        Button startButton = new Button(parent, SWT.PUSH);
        startButton.setText("Start default process");
        startButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SourceTubeSize sourceTubeSize = SourceTubeSize.TEN_MIL;
                int sourceTubeNumber = Integer.valueOf(sourceTubeNumberText
                    .getText());
                System.out.println("SourceTubeNumber = " + sourceTubeNumber);
                BusyIndicator.showWhile(Display.getDefault(),
                    new AliquotsBloodProcess(sourceTubeSize, sourceTubeNumber));
            }
        });
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
    }
}