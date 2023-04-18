package hzt.service;

import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import hzt.service.inteface.IAnimationService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Collection;

import static hzt.model.AppConstants.INIT_FRAME_DURATION;
import static javafx.animation.Animation.INDEFINITE;

public class AnimationService implements IAnimationService {

    private final Timeline animationTimeline;
    private final Timeline statisticsTimeline;

    public AnimationService() {
        animationTimeline = setupTimeline();
        statisticsTimeline = setupTimeline();
    }

    private static Timeline setupTimeline() {
        Timeline t = new Timeline();
        t.setCycleCount(INDEFINITE);
        return t;
    }

    public void addLoopHandlersToTimelines(boolean start,
                                           EventHandler<ActionEvent> animationLoop,
                                           EventHandler<ActionEvent> statisticsLoop) {
        animationTimeline.getKeyFrames().add(new KeyFrame(INIT_FRAME_DURATION, "Animation keyframe", animationLoop));
        statisticsTimeline.getKeyFrames().add(new KeyFrame(INIT_FRAME_DURATION, "Statistics keyframe", statisticsLoop));
        if (start) {
            animationTimeline.play();
            statisticsTimeline.play();
        }
    }

    public void run(Collection<DoublePendulum> doublePendulums, MovableCameraPlatform cameraPlatform) {
        doublePendulums.forEach(DoublePendulum::update);
        cameraPlatform.update(animationTimeline.getCycleDuration());
    }

    public void startAnimationTimeline() {
        animationTimeline.play();
    }

    public void pauseAnimationTimeline() {
        animationTimeline.pause();
    }

}
