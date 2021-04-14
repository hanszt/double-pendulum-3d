package hzt.controller.sub_pane;

import hzt.controller.FXMLController;
import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import hzt.service.IMouseControlService;
import hzt.service.IStatisticsService;
import hzt.service.MouseControlService;
import hzt.service.StatisticsService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Set;

import static java.lang.String.format;

public class StatisticsController extends FXMLController {

    private final IStatisticsService statisticsService = new StatisticsService();

    @FXML
    private Label frameRateStatsLabel;
    @FXML
    private Label runTimeLabel;
    @FXML
    private Label animationIncrementLabel;
    @FXML
    private Label groupSizeLabel;
    @FXML
    private Label highlightingLabel;
    @FXML
    private Label turnFractionLabel;
    @FXML
    private Label distributionLabel;
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
    private Label accelerationCameraLabel;
    @FXML
    private Label fieldOfViewCameraLabel;

    public StatisticsController() throws IOException {
        super("statisticsPane.fxml");
    }

    private static final String TWO_DEC_DOUBLE = "%-4.2f";

    public void updateGlobalStatistics(Duration runTimeSim, double animationIncrement) {
        frameRateStatsLabel.setText(format(TWO_DEC_DOUBLE + " f/s", statisticsService.getSimpleFrameRateMeter().getFrameRate()));
        runTimeLabel.setText(format("%-4.3f seconds", runTimeSim.toSeconds()));
        animationIncrementLabel.setText(format("%-4.2e increment/s", animationIncrement));
    }

    public void updateGroupStatistics(DoublePendulum doublePendulum, IMouseControlService iMouseControlService) {

        MouseControlService mouseControlService = (MouseControlService) iMouseControlService;
        positionXLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateX()));
        positionYLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateY()));
        positionZLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateZ()));

        rotationXLabel.setText(format(TWO_DEC_DOUBLE + " deg", mouseControlService.getAngleX()));
        rotationYLabel.setText(format(TWO_DEC_DOUBLE + " deg", mouseControlService.getAngleY()));
    }

    public void updateCameraPlatformStatistics(MovableCameraPlatform cameraPlatform) {
        positionCameraXLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateX()));
        positionCameraYLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateY()));
        positionCameraZLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateZ()));

        velocityCameraLabel.setText(format(TWO_DEC_DOUBLE + " /s", cameraPlatform.getVelocity()));
        accelerationCameraLabel.setText(format(TWO_DEC_DOUBLE + " /s^2", cameraPlatform.getAcceleration()));
        fieldOfViewCameraLabel.setText(format(TWO_DEC_DOUBLE + " deg", cameraPlatform.getCamera().getFieldOfView()));
    }

    public StatisticsController getBean() {
        return this;
    }
}
