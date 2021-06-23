package hzt.service.inteface;

import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Collection;

public interface IAnimationService {

    void run(Collection<DoublePendulum> doublePendulums, MovableCameraPlatform cameraPlatform);

    void addLoopHandlersToTimelines(boolean start,
                                    EventHandler<ActionEvent> animationLoop, EventHandler<ActionEvent> statisticsLoop);
    void pauseAnimationTimeline();

    void startAnimationTimeline();
}
