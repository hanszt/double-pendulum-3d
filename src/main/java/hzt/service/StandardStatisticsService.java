package hzt.service;

import hzt.service.inteface.StatisticsService;
import javafx.animation.AnimationTimer;

public class StandardStatisticsService implements StatisticsService {

    private final SimpleFrameRateMeter simpleFrameRateMeter;

    public StandardStatisticsService() {
        simpleFrameRateMeter = new SimpleFrameRateMeter();
        simpleFrameRateMeter.initialize();
    }

    public static class SimpleFrameRateMeter implements StatisticsService.ISimpleFramerateMeter {

        private final long[] frameTimes = new long[100];
        private int frameTimeIndex = 0;
        private boolean arrayFilled = false;
        private double frameRate;

        public SimpleFrameRateMeter() {
            frameRate = StandardAnimationService.INIT_FRAME_RATE;
        }

        private void initialize() {
            new AnimationTimer() {

                public void handle(long now) {
                    long oldFrameTime = frameTimes[frameTimeIndex];
                    frameTimes[frameTimeIndex] = now;
                    frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                    if (frameTimeIndex == 0) arrayFilled = true;
                    if (arrayFilled) {
                        var elapsedNanos = now - oldFrameTime;
                        var elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                        frameRate = 1.0e9 / elapsedNanosPerFrame;
                    }
                }
            }.start();
        }

        public double getFrameRate() {
            return frameRate;
        }
    }

    public SimpleFrameRateMeter getSimpleFrameRateMeter() {
        return simpleFrameRateMeter;
    }
}
