package hzt.controller.sub_pane;

import hzt.controller.FXMLController;
import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import hzt.service.StatisticsService;
import hzt.service.inteface.IMouseControlService;
import hzt.service.inteface.IStatisticsService;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.Duration;

import static java.lang.String.format;

public class StatisticsController extends FXMLController {

    private final IStatisticsService statisticsService = new StatisticsService();

    @FXML
    private Label upperMassXLabel;
    @FXML
    private Label upperMassYLabel;
    @FXML
    private Label lowerMassXLabel;
    @FXML
    private Label lowerMassYLabel;
    @FXML
    private Label angle1Label;
    @FXML
    private Label angle2Label;
    @FXML
    private Label vAngular1Label;
    @FXML
    private Label vAngular2Label;
    @FXML
    private Label aAngular1Label;
    @FXML
    private Label aAngular2Label;
    @FXML
    private Label frictionLabel;

    @FXML
    private Label frameRateStatsLabel;
    @FXML
    private Label runTimeLabel;
    @FXML
    private Label upperPendulumLengthLabel;
    @FXML
    private Label lowerPendulumLengthLabel;
    @FXML
    private Label upperMassLabel;
    @FXML
    private Label lowerMassLabel;
    @FXML
    private Label traceSizeLabel;
    @FXML
    private Label positionXLabel;
    @FXML
    private Label positionYLabel;
    @FXML
    private Label positionZLabel;
    @FXML
    private Label rotationXLabel;
    @FXML
    private Label rotationYLabel;
    @FXML
    private Label positionCameraXLabel;
    @FXML
    private Label positionCameraYLabel;
    @FXML
    private Label positionCameraZLabel;
    @FXML
    private Label velocityCameraLabel;
    @FXML
    private Label fieldOfViewCameraLabel;

    public StatisticsController() throws IOException {
        super("statisticsPane.fxml");
    }

    private static final String TWO_DEC_DOUBLE = "%-4.2f";

    public void updateGlobalStatistics(Duration runTimeSim) {
        frameRateStatsLabel.setText(format(TWO_DEC_DOUBLE + " f/s", statisticsService.getSimpleFrameRateMeter().getFrameRate()));
        runTimeLabel.setText(format("%d.%03d seconds", runTimeSim.toSeconds(), runTimeSim.toMillisPart()));
    }

    public void updateGroupStatistics(DoublePendulum doublePendulum, IMouseControlService mouseControlService) {
        updatePendulumPropertyStatistics(doublePendulum);
        updatePendulumPositionStatistics(doublePendulum);
        updatePendulumAngleStatistics(doublePendulum);
        frictionLabel.setText(format("%-4.3f", doublePendulum.getFriction()));
        traceSizeLabel.setText(format("%d", doublePendulum.traceSize()));

        positionXLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateX()));
        positionYLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateY()));
        positionZLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateZ()));

        rotationXLabel.setText(format(TWO_DEC_DOUBLE + " deg", mouseControlService.getAngleX()));
        rotationYLabel.setText(format(TWO_DEC_DOUBLE + " deg", mouseControlService.getAngleY()));
    }

    private void updatePendulumAngleStatistics(DoublePendulum doublePendulum) {
        angle1Label.setText(format(TWO_DEC_DOUBLE, doublePendulum.getAngle1Deg()));
        angle2Label.setText(format(TWO_DEC_DOUBLE, doublePendulum.getAngle2Deg()));
        vAngular1Label.setText(format(TWO_DEC_DOUBLE, doublePendulum.getVAngular1deg()));
        vAngular2Label.setText(format(TWO_DEC_DOUBLE, doublePendulum.getVAngular2Deg()));
        aAngular1Label.setText(format(TWO_DEC_DOUBLE, doublePendulum.getAAngular1Deg()));
        aAngular2Label.setText(format(TWO_DEC_DOUBLE, doublePendulum.getAAngular2Deg()));
    }

    private void updatePendulumPropertyStatistics(DoublePendulum doublePendulum) {
        upperPendulumLengthLabel.setText(format(TWO_DEC_DOUBLE, doublePendulum.getUpperArmLength()));
        lowerPendulumLengthLabel.setText(format(TWO_DEC_DOUBLE, doublePendulum.getLowerArmLength()));
        upperMassLabel.setText(format(TWO_DEC_DOUBLE, doublePendulum.upperPendulum.getMass()));
        lowerMassLabel.setText(format(TWO_DEC_DOUBLE, doublePendulum.lowerPendulum.getMass()));
    }

    private void updatePendulumPositionStatistics(DoublePendulum doublePendulum) {
        Point2D upperMassPosition = doublePendulum.upperPendulum.getPositionXY();
        upperMassXLabel.setText(format(TWO_DEC_DOUBLE, upperMassPosition.getX()));
        upperMassYLabel.setText(format(TWO_DEC_DOUBLE, upperMassPosition.getY()));
        Point2D lowerMassPosition = doublePendulum.lowerPendulum.getPositionXY();
        lowerMassXLabel.setText(format(TWO_DEC_DOUBLE, lowerMassPosition.getY()));
        lowerMassYLabel.setText(format(TWO_DEC_DOUBLE, lowerMassPosition.getY()));
    }

    public void updateCameraPlatformStatistics(MovableCameraPlatform cameraPlatform) {
        positionCameraXLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateX()));
        positionCameraYLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateY()));
        positionCameraZLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateZ()));

        velocityCameraLabel.setText(format(TWO_DEC_DOUBLE + " /s", cameraPlatform.getVelocity()));
        fieldOfViewCameraLabel.setText(format(TWO_DEC_DOUBLE + " deg", cameraPlatform.getCamera().getFieldOfView()));
    }
}
