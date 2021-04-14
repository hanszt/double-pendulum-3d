package hzt.service;

import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface IAnimationService {

    void run(DoublePendulum fibonacciGroup, MovableCameraPlatform cameraPlatform);

    void addLoopHandlersToTimelines(boolean start,
                                    EventHandler<ActionEvent> animationLoop, EventHandler<ActionEvent> statisticsLoop);
    void pauseAnimationTimeline();

    void startAnimationTimeline();
}
