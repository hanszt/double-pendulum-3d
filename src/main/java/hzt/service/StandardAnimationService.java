package hzt.service;

import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import hzt.service.inteface.AnimationService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.Collection;

import static javafx.animation.Animation.INDEFINITE;

public class StandardAnimationService implements AnimationService {

    public static final int INIT_FRAME_RATE = 30; // f/s
    public static final Duration INIT_FRAME_DURATION = Duration.seconds(1. / INIT_FRAME_RATE); // s/f

    private final Timeline animationTimeline;
    private final Timeline statisticsTimeline;

    public StandardAnimationService() {
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
